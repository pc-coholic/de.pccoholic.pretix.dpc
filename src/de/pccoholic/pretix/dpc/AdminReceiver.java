package de.pccoholic.pretix.dpc;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        Common.showToast(context, "[Device Admin enabled]");
        Common.becomeHomeActivity(context);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "Warning: Device Admin is going to be disabled.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Common.showToast(context, "[Device Admin disabled]");
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent,
            String pkg) {
        Common.showToast(context, "[Kiosk Mode enabled]");
    }

    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        Common.showToast(context, "[Kiosk Mode disabled]");
    }
}
