package de.pccoholic.pretix.dpc;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.BatteryManager;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusbarService extends Service {
    private WindowManager windowManager;
    private ImageView chatHead;
    private View statusBar;

    @Override public void onCreate() {
        super.onCreate();

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

        TextView t = statusBar.findViewById(R.id.textView);

        BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        t.setText("Battery level: " + String.valueOf(batLevel) + "%");

        //windowManager.addView(chatHead, params);
        windowManager.addView(statusBar, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
