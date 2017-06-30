package de.pccoholic.pretix.dpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        /*
        Common.showToast(context, "chargePlug: " + String.valueOf(chargePlug));
        Common.showToast(context, "isCharging: " + String.valueOf(isCharging));
        Common.showToast(context, "usbCharge: " + String.valueOf(usbCharge));
        Common.showToast(context, "acCharge: " + String.valueOf(acCharge));
        */
    }

}
