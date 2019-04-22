package com.nisco.family.common.utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 自定义Toast
 *
 * @author visen
 */
public class CustomToast {
    private static Toast mToast;

    private static Handler mhandler = new Handler();

    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void showToast(Context context, String text) {
        mhandler.removeCallbacks(r);
        if (null != mToast) {
            mToast.setText(text);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mhandler.postDelayed(r, 3000);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public static void showToast(Context context, String text, int duration)
    {
        mhandler.removeCallbacks(r);
        if (null != mToast)
        {
            mToast.setText(text);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        else
        {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mhandler.postDelayed(r, 3000);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    private static void showToast(Context context, int strId) {
        showToast(context, context.getString(strId));
    }
}
