package de.pccoholic.pretix.dpc;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Settings;

public class AdminActivities {
    static String getHomeActivity(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ComponentName cn = intent.resolveActivity(pm);
        if (cn != null)
            return cn.flattenToShortString();
        else
            return "none";
    }

    static void becomeHomeActivity(Context context) {
        ComponentName deviceAdmin = new ComponentName(context, AdminReceiver.class);
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (!dpm.isAdminActive(deviceAdmin)) {
            //Common.showToast(context, "This app is not a device admin!");
            return;
        }
        if (!dpm.isDeviceOwnerApp(context.getPackageName())) {
            //Common.showToast(context, "This app is not the device owner!");
            return;
        }
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName activity = new ComponentName(context, SplashActivity.class);
        dpm.addPersistentPreferredActivity(deviceAdmin, intentFilter, activity);
        //Common.showToast(context, "Home activity: " + getHomeActivity(context));
    }

    static void restoreLauncher(Context c) {
        ComponentName deviceAdmin = new ComponentName(c, AdminReceiver.class);
        DevicePolicyManager dpm = (DevicePolicyManager) c.getSystemService(Context.DEVICE_POLICY_SERVICE);

        dpm.clearPackagePersistentPreferredActivities(deviceAdmin,
                c.getPackageName());
        //Common.showToast(c, "Home activity: " + getHomeActivity(c));
    }

    static void allowNonMarketAppsInstallation(Context context) {
        ComponentName deviceAdmin = new ComponentName(context, AdminReceiver.class);
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        dpm.setSecureSetting(deviceAdmin, Settings.Secure.INSTALL_NON_MARKET_APPS, "1");
    }

    static void setLockTaskApplications(Context context) {
        ComponentName deviceAdmin = new ComponentName(context, AdminReceiver.class);
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        dpm.setLockTaskPackages(deviceAdmin,
                new String[] { context.getPackageName() });
    }
}
