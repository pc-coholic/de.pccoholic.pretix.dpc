package de.pccoholic.pretix.dpc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SetupActivity  extends Activity {
    private SharedPreferences prefs;
    private BroadcastReceiver scanReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setup);

        prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        TextView title = findViewById(R.id.setupErrorTitle);
        TextView expl = findViewById(R.id.setupErrorExpl);
        final Button actionButton = findViewById(R.id.setupAction);
        final ProgressDialog progress = new ProgressDialog(this);

        if (!Common.isDeviceOwner(this)) {
            title.setText(R.string.setup_not_device_owner);
            expl.setText(R.string.setup_not_device_owner_expl);
        } else if (!Common.isDeviceAdmin(this)) {
            title.setText(R.string.setup_not_device_admin);
            expl.setText(R.string.setup_not_device_admin_expl);
        } else if (!Common.isDPCProvisioned(this)) {
            title.setText(R.string.setup_not_provisioned);
            expl.setText(R.string.setup_not_provisioned_expl);

            Common.setScannerToBroadcast();
            scanReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    byte[] barcode = intent.getByteArrayExtra("barocode"); // sic!
                    int barocodelen = intent.getIntExtra("length", 0);
                    String barcodeStr = new String(barcode, 0, barocodelen);
                    try {
                        JSONObject provisionJSON = new JSONObject(barcodeStr);
                        if (provisionJSON.has("pref_DPC_unlock_barcode")
                                && provisionJSON.has("pref_DPC_kiosk_package")
                                && provisionJSON.has("pref_DPC_kiosk_package_url")) {
                            SharedPreferences.Editor prefsEditor = prefs.edit();
                            prefsEditor.clear();

                            Iterator<String> iter = provisionJSON.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    Object value = provisionJSON.get(key);

                                    if (value instanceof Boolean) {
                                        prefsEditor.putBoolean(key, ((Boolean) value).booleanValue());
                                    } else if (value instanceof Integer) {
                                        prefsEditor.putInt(key, ((Integer) value).intValue());
                                    } else {
                                        prefsEditor.putString(key, value.toString());
                                    }

                                    prefsEditor.putBoolean("pref_DPC_is_provisioned", true);
                                    prefsEditor.commit();

                                    finish();
                                    startActivity(getIntent());
                                } catch (JSONException e){
                                    // Something went wrong...
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            IntentFilter filter = new IntentFilter();
            filter.addAction("scan.rcv.message");
            registerReceiver(scanReceiver, filter);
        } else if (!Common.isKioskAppInstalled(this)) {
            title.setText(R.string.setup_no_kioskapp);
            expl.setText(R.string.setup_no_kioskapp_expl);
            actionButton.setText(R.string.setup_no_kioskapp_button);
            actionButton.setVisibility(View.VISIBLE);

            actionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AdminActivities.allowNonMarketAppsInstallation(v.getContext());
                    InstallAPK downloadAndInstall = new InstallAPK();
                    progress.setCancelable(false);
                    progress.setMessage("Downloading...");
                    downloadAndInstall.setContext(getApplicationContext(), progress);
                    downloadAndInstall.execute(prefs.getString("pref_DPC_kiosk_package_url", ""));
                    //downloadAndInstall.execute("http://bundesnerdrichtendienst.de/pretixdroid-1.4.apk");

                    actionButton.setText(R.string.setup_continue_button);
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            finish();
                            startActivity(getIntent());
                        }
                    });
                }
            });
        } else {
            title.setText(R.string.setup_everything_fine);
            expl.setText(R.string.setup_everything_fine_expl);
            actionButton.setText(R.string.setup_continue_button);
            actionButton.setVisibility(View.VISIBLE);

            actionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finishAndRemoveTask();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // As there is no proper way to check if a Receiver is registered or not...
        try {
            unregisterReceiver(scanReceiver);
        } catch (Exception e) {

        }
    }
}