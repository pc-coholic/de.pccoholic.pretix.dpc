package de.pccoholic.pretix.dpc;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

public class DPC extends Activity {
    private static DPC ins;
    private ActivityManager am;
    private SharedPreferences prefs;
    private BroadcastReceiver batteryReceiver;
    private BroadcastReceiver powerconnectionReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        super.onCreate(null);
        ins = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main);
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        setPowerConnection(PowerConnectionReceiver.getBatteryStatus(this));
        setBatteryLevel(BatteryReceiver.getBatteryLevel(this));

        IntentFilter batteryReceiverFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.batteryReceiver = new BatteryReceiver();
        registerReceiver(this.batteryReceiver, batteryReceiverFilter);

        IntentFilter powerConnectionReciverFilter = new IntentFilter();
        powerConnectionReciverFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        powerConnectionReciverFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        powerconnectionReceiver = new PowerConnectionReceiver();
        registerReceiver(powerconnectionReceiver, powerConnectionReciverFilter);

        prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        Common.setScannerToBroadcast();

        AdminActivities.becomeHomeActivity(this);
        AdminActivities.setLockTaskApplications(this);

        startService(new Intent(this, StatusbarService.class));

        startLockTask();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (IsTaskLockActive()) {
            if (PowerConnectionReceiver.getBatteryStatus(this) == Intent.ACTION_POWER_DISCONNECTED) {
                Intent i = new Intent("android.intent.action.MAIN");
                i.setClassName(prefs.getString("pref_DPC_kiosk_package", null), getMainActivity(prefs.getString("pref_DPC_kiosk_package", null)).name);
                startActivity(i);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopService(new Intent(this, StatusbarService.class));

        try {
            unregisterReceiver(batteryReceiver);
        } catch (Exception e) {

        }

        try {
            unregisterReceiver(powerconnectionReceiver);
        } catch (Exception e) {

        }
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

    public void restartApplication() {
        DPC.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent mStartActivity = new Intent(DPC.getInstance(), DPC.class);
                int mPendingIntentId = ((int) System.currentTimeMillis());
                PendingIntent mPendingIntent = PendingIntent.getActivity(DPC.getInstance(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)DPC.getInstance().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
                stopLockTaskAndDie();
            }
        });
    }
}