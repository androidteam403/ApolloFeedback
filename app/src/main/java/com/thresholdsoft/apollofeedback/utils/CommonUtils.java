package com.thresholdsoft.apollofeedback.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;

import com.thresholdsoft.apollofeedback.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
            if (spotsDialog != null)
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

    public static String getCurrentTimeStamp() {
        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        return tsTemp.toString();
    }

    public static String getCurrentDateTime() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getTimeFromAndroid() {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        if (hours >= 1 && hours <= 12) {
            return "Good Morning ";
        } else if (hours >= 12 && hours <= 16) {
            return "Good Afternoon ";
        } else if (hours >= 16 && hours <= 21) {
            return "Good Evening ";
        } else if (hours >= 21 && hours <= 24) {
            return "Good Night ";
        }
        return "";
    }

}
