package com.thresholdsoft.apollofeedback.db;
/*
 * Created on : jun 17, 2022.
 * Author : NAVEEN.M
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionManager {
    SharedPreferences preferences;

    //Preff keys
    private static final String PREF_KEY_LOGIN_TOKEN = "PREF_KEY_LOGIN_TOKEN";

    public SessionManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void clearAllSharedPreferences() {
        preferences.edit().clear().apply();
    }

    public void setLoginToken(String loginLoken) {
        preferences.edit().putString(PREF_KEY_LOGIN_TOKEN, loginLoken).apply();
    }

    public String getLoginToken() {
        return preferences.getString(PREF_KEY_LOGIN_TOKEN, "");
    }
}
