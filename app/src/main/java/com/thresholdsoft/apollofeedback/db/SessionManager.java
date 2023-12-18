package com.thresholdsoft.apollofeedback.db;
/*
 * Created on : jun 17, 2022.
 * Author : NAVEEN.M
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    SharedPreferences preferences;
//    public static final StoreSetupActivityMvpView INSTANCE = ;


    //Preff keys
    private static final String PREF_KEY_LOGIN_TOKEN = "PREF_KEY_LOGIN_TOKEN";
    private static final String PREF_KEY_TERMINAL_ID = "PREF_KEY_TERMINAL_ID";
    private static final String PREF_KEY_SITE_ID = "PREF_KEY_SITE_ID";
    private static final String PREF_KEY_EPOS_URL = "PREF_KEY_EPOS_URL";
    private static final String PREF_STORE_ADDRESS = "PREF_STORE_ADDRESS";
    private static final String PREF_LABEL_ADDRESS = "PREF_LABEL_ADDRESS";
    private static final String PREF_KEY_DC_CODE = "PREF_KEY_DC_CODE";
    private static final String PREF_KEY_STORE_NAME = "PREF_KEY_STORE_NAME";
    private static final String PREF_KEY_SCANNED_PRESCRIPTIONS_LIST_PATH = "PREF_KEY_SCANNED_PRESCRIPTIONS_LIST_PATH";
    private static final String PREF_KEY_POOR = "PREF_KEY_POOR";
    private static final String PREF_KEY_FAIR = "PREF_KEY_FAIR";
    private static final String PREF_KEY_AVERAGE = "PREF_KEY_AVERAGE";
    private static final String PREF_KEY_HAPPY = "PREF_KEY_HAPPY";
    private static final String PREF_KEY_EXCELLENT = "PREF_KEY_EXCELLENT";


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

    public void setTerminalId(String terminalId) {
        preferences.edit().putString(PREF_KEY_TERMINAL_ID, terminalId).apply();
    }

    public String getTerminalId() {
        return preferences.getString(PREF_KEY_TERMINAL_ID, "");
    }

    public void setEposUrl(String epoUrl) {
        preferences.edit().putString(PREF_KEY_EPOS_URL, epoUrl).apply();
    }

    public String getEposUrl() {
        return preferences.getString(PREF_KEY_EPOS_URL, "http://online.apollopharmacy.org:51/EPOS/");
    }

    public void setStoreAddress(String storeAddress) {
        preferences.edit().putString(PREF_STORE_ADDRESS, storeAddress).apply();
    }

    public String getStoreAddress() {
        return preferences.getString(PREF_STORE_ADDRESS, "");
    }

    public void setLabelAddress(String labelAddress) {
        preferences.edit().putString(PREF_LABEL_ADDRESS, labelAddress).apply();
    }

    public String getLabelAddress() {
        return preferences.getString(PREF_LABEL_ADDRESS, "");
    }

    public void setDcCode(String dcCode) {
        preferences.edit().putString(PREF_KEY_DC_CODE, dcCode).apply();
    }

    public String getDcCode() {
        return preferences.getString(PREF_KEY_DC_CODE, "");
    }

    public void setStoreName(String storeName) {
        preferences.edit().putString(PREF_KEY_STORE_NAME, storeName).apply();
    }

    public String getStoreName() {
        return preferences.getString(PREF_KEY_STORE_NAME, "");
    }

    public void setScannedPrescriptionsPath(List<String> scannedPrescriptionsPathList) {
        preferences.edit().putString(PREF_KEY_SCANNED_PRESCRIPTIONS_LIST_PATH, new Gson().toJson(scannedPrescriptionsPathList)).apply();
    }

    List<String> scannedPrescriptionsPathListEmpty = new ArrayList<>();

    public List<String> getScannedPrescriptionsPath() {
        Gson gson = new Gson();
        String json = preferences.getString(PREF_KEY_SCANNED_PRESCRIPTIONS_LIST_PATH, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        if (gson.fromJson(json, type) != null) {
            return gson.fromJson(json, type);
        } else {
            return scannedPrescriptionsPathListEmpty;
        }
    }

    public void setPoorKey(String poorKey) {
        preferences.edit().putString(PREF_KEY_POOR, poorKey).apply();
    }

    public String getPoorKey() {
        return preferences.getString(PREF_KEY_POOR, "");
    }

    public void setFairKey(String fairKey) {
        preferences.edit().putString(PREF_KEY_FAIR, fairKey).apply();
    }

    public String getFairKey() {
        return preferences.getString(PREF_KEY_FAIR, "");
    }

    public void setAverageKey(String averageKey) {
        preferences.edit().putString(PREF_KEY_AVERAGE, averageKey).apply();
    }

    public String getAverageKey() {
        return preferences.getString(PREF_KEY_AVERAGE, "");
    }

    public void setHappyKey(String happyKey) {
        preferences.edit().putString(PREF_KEY_HAPPY, happyKey).apply();
    }

    public String getHappyKey() {
        return preferences.getString(PREF_KEY_HAPPY, "");
    }

    public void setExcellentKey(String excellentKey) {
        preferences.edit().putString(PREF_KEY_EXCELLENT, excellentKey).apply();
    }

    public String getExcellentKey() {
        return preferences.getString(PREF_KEY_EXCELLENT, "");
    }

}
