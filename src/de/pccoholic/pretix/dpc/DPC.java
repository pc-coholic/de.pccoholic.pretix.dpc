package de.pccoholic.pretix.dpc;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class DPC extends Activity {
    private DevicePolicyManager dpm;
    private ActivityManager am;
    private SharedPreferences prefs;
    private ComponentName deviceAdmin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, this.getPackageName(), Context.MODE_PRIVATE, R.xml.preferences, true);

        setContentView(R.layout.main);
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        deviceAdmin = new ComponentName(this, AdminReceiver.class);
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (!dpm.isAdminActive(deviceAdmin)) {
            Common.showToast(this, "This app is not a device admin!");
        }
        if (dpm.isDeviceOwnerApp(getPackageName())) {
            dpm.setLockTaskPackages(deviceAdmin,
                    new String[] { getPackageName() });
        } else {
            Common.showToast(this, "This app is not the device owner!");
        }

        Intent unlockIntent = getIntent();

        prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        if (unlockIntent.getStringExtra("DPC_unlock_barcode") != null
                && unlockIntent.getStringExtra("DPC_unlock_barcode").equals(prefs.getString("pref_DPC_unlock_barcode", null))) {
            stopLockTask();
        } else {
            startLockTask();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (IsTaskLockActive()) {
            startService(new Intent(this, StatusbarService.class));

            Intent i = new Intent("android.intent.action.MAIN");
            i.setClassName(prefs.getString("pref_DPC_kiosk_package", null), getMainActivity(prefs.getString("pref_DPC_kiosk_package", null)).name);
            startActivity(i);
        } else {
            stopService(new Intent(this, StatusbarService.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopService(new Intent(this, StatusbarService.class));
    }
    public boolean IsTaskLockActive() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return am.isInLockTaskMode();
        } else {
            if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                return false;
            } else {
                return true;
            }
        }
    }

    public ActivityInfo getMainActivity(String packageName) {
        try {
            PackageManager pm = getPackageManager();
            Intent mainIntent = new Intent("android.intent.action.MAIN", null);
            mainIntent.addCategory("android.intent.category.LAUNCHER");
            for (ResolveInfo temp : pm.queryIntentActivities(mainIntent, 0)) {
                if (temp.activityInfo.packageName.equals(packageName)) {
                    return temp.activityInfo;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}