package com.qzc.mobiledlsearch.util;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class ToastUtil {

    public static void showText(final Activity activity, final String message) {
        if ("main".equals(Thread.currentThread().getName())) {
            Log.e("ToastUtil", "in the main thread");
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("ToastUtil", "not in the main thread");
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
