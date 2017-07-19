package de.pccoholic.pretix.dpc;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AdminActivities.getHomeActivity(context).equals(new ComponentName(context, SplashActivity.class).flattenToShortString())) {
            //Common.showToast(context, "App is already HomeActivity - not launching again");
        } else {
            //Common.showToast(context, "App is NOT HomeActivity - launching");
            Intent i = new Intent(context, SplashActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            context.startActivity(i);
        }
    }
}
