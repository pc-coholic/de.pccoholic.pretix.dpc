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

        // ToDo: Broadcast the changes
        if (action.equals(Intent.ACTION_POWER_CONNECTED))
        {
            DPC.getInstace().setPowerConnection(Intent.ACTION_POWER_CONNECTED);
            StatusbarService.getInstace().setPowerConnection(Intent.ACTION_POWER_CONNECTED);
            Common.showToast(context, "Connected");
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED))
        {
            DPC.getInstace().setPowerConnection(Intent.ACTION_POWER_DISCONNECTED);
            StatusbarService.getInstace().setPowerConnection(Intent.ACTION_POWER_DISCONNECTED);
            Common.showToast(context, "Disconnected");
        } else {
            DPC.getInstace().setPowerConnection(Intent.CATEGORY_MONKEY);
            StatusbarService.getInstace().setPowerConnection(Intent.CATEGORY_MONKEY);
            Common.showToast(context, "Something happened to the power");
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
