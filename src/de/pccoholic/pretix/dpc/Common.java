package de.pccoholic.pretix.dpc;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.device.ScanDevice;

public class Common {
    static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    static void setScannerToBroadcast() {
        ScanDevice sm = new ScanDevice();
        sm.setOutScanMode(0);
    }

    static boolean isDeviceOwner(Context context) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        return dpm.isDeviceOwnerApp(context.getPackageName());
    }

    static boolean isDeviceAdmin(Context context) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName deviceAdmin = new ComponentName(context, AdminReceiver.class);

        return dpm.isAdminActive(deviceAdmin);
    }

    static boolean isDPCProvisioned(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        return prefs.getBoolean("pref_DPC_is_provisioned", false);
    }

    static boolean isKioskAppInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        try {
            pm.getPackageInfo(prefs.getString("pref_DPC_kiosk_package", null), PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {

        }

        return false;
    }

    static boolean isDeviceReady(Context context) {
        return isDeviceOwner(context) && isDeviceAdmin(context) && isDPCProvisioned(context) && isKioskAppInstalled(context);
    }
}