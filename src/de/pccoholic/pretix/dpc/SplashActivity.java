package de.pccoholic.pretix.dpc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class<?> intentToLaunch = DPC.class;

        if (!Common.isDeviceReady(this)) {
            intentToLaunch = SetupActivity.class;
        }

        Intent intent = new Intent(this, intentToLaunch);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        finish();
    }
}
