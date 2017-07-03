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

        int batLevel = getBatteryLevel(context);
        try {
            DPC.getInstance().setBatteryLevel(batLevel);
            StatusbarService.getInstance().setBatteryLevel(batLevel);
        } catch (NullPointerException e) {

        }
    }

    public static int getBatteryLevel(Context context) {
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return batLevel;
    }
}
