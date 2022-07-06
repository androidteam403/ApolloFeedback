package com.thresholdsoft.apollofeedback.db;
/*
 * Created on : jun 17, 2022.
 * Author : NAVEEN.M
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.thresholdsoft.apollofeedback.ui.storesetup.StoreSetupActivityMvpView;

public class SessionManager  {
    SharedPreferences preferences;
//    public static final StoreSetupActivityMvpView INSTANCE = ;


    //Preff keys
    private static final String PREF_KEY_LOGIN_TOKEN = "PREF_KEY_LOGIN_TOKEN";
    private static final String PREF_KEY_TERMINAL_ID = "PREF_KEY_TERMINAL_ID";
    private static final String PREF_KEY_SITE_ID = "PREF_KEY_SITE_ID";
    private static final String PREF_KEY_EPOS_URL = "PREF_KEY_EPOS_URL";

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


    public void setSiteId(String siteId) {
        preferences.edit().putString(PREF_KEY_SITE_ID, siteId).apply();
    }

    public String getSiteId() {
        return preferences.getString(PREF_KEY_SITE_ID, "");
    }

    public void setTerminalId(String terminalId){
        preferences.edit().putString(PREF_KEY_TERMINAL_ID, terminalId).apply();
    }

    public String getTerminalId(){
        return preferences.getString(PREF_KEY_TERMINAL_ID, "");
    }

    public void setEposUrl(String epoUrl){
        preferences.edit().putString(PREF_KEY_EPOS_URL, epoUrl).apply();
    }

    public String getEposUrl(){
        return preferences.getString(PREF_KEY_EPOS_URL, "");
    }



}
