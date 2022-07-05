package com.thresholdsoft.apollofeedback.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;

import com.thresholdsoft.apollofeedback.R;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

/*
 * Created on : jun 17, 2022.
 * Author : NAVEEN.M
 */
public class CommonUtils {
    private static SpotsDialog spotsDialog;

    public static void showDialog(Context mContext, String strMessage) {
        try {
            if (spotsDialog != null) {
                if (spotsDialog.isShowing()) {
                    spotsDialog.dismiss();
                }
            }
            spotsDialog = new SpotsDialog(mContext, strMessage, R.style.Custom, false, dialog -> {

            });
            Objects.requireNonNull(spotsDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            spotsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideDialog() {
        try {
            if (spotsDialog.isShowing())
                spotsDialog.dismiss();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isLoadingShowing() {
        return spotsDialog.isShowing();
    }
}
