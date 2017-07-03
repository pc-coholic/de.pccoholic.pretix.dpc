package de.pccoholic.pretix.dpc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusbarService extends Service {
    private static StatusbarService ins;
    private WindowManager windowManager;
    private View statusBar;

    @Override public void onCreate() {
        super.onCreate();

        ins = this;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        int statusBarHeight = (int) Math.ceil(25 * getResources().getDisplayMetrics().density);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                statusBarHeight,
                // Allows the view to be on top of the StatusBar
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                // Keeps the button presses from going to the background window
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                // Enables the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        params.gravity =  Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        LayoutInflater inflate = (LayoutInflater) getBaseContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
        statusBar = inflate.inflate(R.layout.statusbar, null);

        windowManager.addView(statusBar, params);

        setPowerConnection(PowerConnectionReceiver.getBatteryStatus(this));
        setBatteryLevel(BatteryReceiver.getBatteryLevel(this));

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1000);
                TextView dateTimeTextView = statusBar.findViewById(R.id.dateTime);
                dateTimeTextView.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));

                ImageView wifiLevel = statusBar.findViewById(R.id.wifiLevel);

                ConnectivityManager cm = (ConnectivityManager) getBaseContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null) {
                    if (networkInfo.isConnected()) {
                        wifiLevel.setImageResource(R.drawable.stat_sys_wifi);
                    } else {
                        wifiLevel.setImageResource(R.drawable.ic_signal_wifi_off);
                    }
                } else {
                    wifiLevel.setImageResource(R.drawable.ic_signal_wifi_off);
                }


                WifiManager wifiManager = (WifiManager) getBaseContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                wifiLevel.setImageLevel( WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5));

            }
        };
        handler.postDelayed(r, 0000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (statusBar != null) {
            windowManager.removeView(statusBar);
        }
    }

     @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static StatusbarService getInstance(){
        return ins;
    }

    public void setBatteryLevel(final int level) {
        if (getInstance() != null){
            ImageView batteryLevel = statusBar.findViewById(R.id.batteryLevel);
            batteryLevel.setImageLevel(level);
        }
    }

    public void setPowerConnection(final String connection) {
        if (ins != null) {
            ImageView batteryLevel = statusBar.findViewById(R.id.batteryLevel);
            if (connection.equals(Intent.ACTION_POWER_CONNECTED)) {
                batteryLevel.setImageResource(R.drawable.stat_sys_battery_charge);
            } else if (connection.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                batteryLevel.setImageResource(R.drawable.stat_sys_battery);
            } else {
                batteryLevel.setImageResource(R.drawable.ic_battery_unknown);
            }
        }
    }
}
