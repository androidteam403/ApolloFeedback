package com.thresholdsoft.apollofeedback.base;
/*
 * Created on : jun 17, 2022.
 * Author : NAVEEN.M
 */

import androidx.appcompat.app.AppCompatActivity;

import com.thresholdsoft.apollofeedback.db.SessionManager;

public abstract class BaseActivity extends AppCompatActivity {
    public SessionManager getDataManager() {
        return new SessionManager(this);
    }



}
