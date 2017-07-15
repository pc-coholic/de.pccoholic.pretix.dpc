package de.pccoholic.pretix.dpc;

import android.content.Context;
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
}