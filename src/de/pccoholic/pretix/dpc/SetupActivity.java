package de.pccoholic.pretix.dpc;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SetupActivity  extends Activity {
    private DevicePolicyManager dpm;
    private ComponentName deviceAdmin;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setup);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdmin = new ComponentName(this, AdminReceiver.class);
        prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        TextView title = findViewById(R.id.setupErrorTitle);
        TextView expl = findViewById(R.id.setupErrorExpl);
        Button actionButton = findViewById(R.id.setupAction);

        if (!dpm.isDeviceOwnerApp(getPackageName())) {
            title.setText(R.string.setup_not_device_owner);
            expl.setText(R.string.setup_not_device_owner_expl);
        } else if (!dpm.isAdminActive(deviceAdmin)) {
            title.setText(R.string.setup_not_device_admin);
            expl.setText(R.string.setup_not_device_admin_expl);
        } else if (!prefs.getBoolean("isProvisioned", false)) {
            title.setText(R.string.setup_not_provisioned);
            expl.setText(R.string.setup_not_provisioned_expl);
        } else if (true == false) {
            // ToDo: Check if Kiosk-App is installed
            title.setText(R.string.setup_no_kioskapp);
            expl.setText(R.string.setup_no_kioskapp_expl);
            actionButton.setText(R.string.setup_no_kioskapp_button);
            actionButton.setVisibility(View.VISIBLE);

            actionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // ToDo: Trigger the installation of the Kiosk-App
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