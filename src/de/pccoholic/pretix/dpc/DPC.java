package de.pccoholic.pretix.dpc;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.ImageView;

public class DPC extends Activity {
    private static DPC ins;
    private DevicePolicyManager dpm;
    private ActivityManager am;
    private SharedPreferences prefs;
    private ComponentName deviceAdmin;
    private BroadcastReceiver batteryReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        setPowerConnection(PowerConnectionReceiver.getBatteryStatus(this));
        setBatteryLevel(BatteryReceiver.getBatteryLevel(this));

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, filter);

        prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        AdminActivities.becomeHomeActivity(this);
        startLockTask();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (IsTaskLockActive()) {
            startService(new Intent(this, StatusbarService.class));

            Intent i = new Intent("android.intent.action.MAIN");
            //ToDo: Try/Catch if app is not installed
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

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(batteryReceiver);
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

    public static DPC getInstance(){
        return ins;
    }

    public void setBatteryLevel(final int level) {
        DPC.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView batteryLevel = findViewById(R.id.batteryLevel);
                batteryLevel.setImageLevel(level);
            }
        });
    }

    public void setPowerConnection(final String connection) {
        DPC.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView batteryLevel = findViewById(R.id.batteryLevel);
                if (connection.equals(Intent.ACTION_POWER_CONNECTED)) {
                    batteryLevel.setImageResource(R.drawable.stat_sys_battery_charge);
                } else if (connection.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                    batteryLevel.setImageResource(R.drawable.stat_sys_battery);
                } else {
                    batteryLevel.setImageResource(R.drawable.ic_battery_unknown);
                }
            }
        });
    }

    public void stopLockTaskAndDie() {
        DPC.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopLockTask();
                AdminActivities.restoreLauncher(DPC.getInstance());
                finishAndRemoveTask();
            }
        });
    }
}