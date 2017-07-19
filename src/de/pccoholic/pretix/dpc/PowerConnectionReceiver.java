package de.pccoholic.pretix.dpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_POWER_CONNECTED))
        {
            DPC.getInstance().setPowerConnection(Intent.ACTION_POWER_CONNECTED);
            StatusbarService.getInstance().setPowerConnection(Intent.ACTION_POWER_CONNECTED);
            try {
                DPC.getInstance().restartApplication();
            } catch(Exception e) {

            }
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED))
        {
            DPC.getInstance().setPowerConnection(Intent.ACTION_POWER_DISCONNECTED);
            StatusbarService.getInstance().setPowerConnection(Intent.ACTION_POWER_DISCONNECTED);
            try {
                DPC.getInstance().restartApplication();
            } catch(Exception e) {

            }
        } else {
            DPC.getInstance().setPowerConnection(Intent.CATEGORY_MONKEY);
            StatusbarService.getInstance().setPowerConnection(Intent.CATEGORY_MONKEY);
            Common.showToast(context, "Something happened to the power...");
        }
    }

    public static String getBatteryStatus(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        if (plugged == 0) {
            return Intent.ACTION_POWER_DISCONNECTED;
        } else {
            return Intent.ACTION_POWER_CONNECTED;
        }
    }

}
