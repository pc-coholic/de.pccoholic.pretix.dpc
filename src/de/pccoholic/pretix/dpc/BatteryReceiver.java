package de.pccoholic.pretix.dpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;

import static android.content.Context.BATTERY_SERVICE;

public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (bundle == null) {
            return;
        }

        boolean isPresent = intent.getBooleanExtra("present", false);
        String technology = intent.getStringExtra("technology");
        int plugged = intent.getIntExtra("plugged", -1);
        int scale = intent.getIntExtra("scale", -1);
        int health = intent.getIntExtra("health", 0);
        int status = intent.getIntExtra("status", 0);
        int rawlevel = intent.getIntExtra("level", -1);
        int level = 0;
        Common.showToast(context, "rawlevel: " + String.valueOf(rawlevel));
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Common.showToast(context, "batlevel: " + String.valueOf(batLevel));
    }
}
