package de.pccoholic.pretix.dpc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SetupActivity  extends Activity {
    private SharedPreferences prefs;

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

            // ToDo: Handle barcodescanning and setting preferences
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
                }
            });
        } else {
            title.setText(R.string.setup_everything_fine);
            expl.setText(R.string.setup_everything_fine_expl);
            actionButton.setText(R.string.setup_everything_fine_button);
            actionButton.setVisibility(View.VISIBLE);

            actionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finishAndRemoveTask();
                }
            });
        }


    }
}