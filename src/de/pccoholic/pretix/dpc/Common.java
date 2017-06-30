package de.pccoholic.pretix.dpc;

import android.content.Context;
import android.widget.Toast;

public class Common {
    static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
