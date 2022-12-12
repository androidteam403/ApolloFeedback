package com.thresholdsoft.apollofeedback.ui.whyscanprescription;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivityWhyScanPrescriptionBinding;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.ui.whyscanprescription.epsonscan.EpsonScanActivity;

import java.util.ArrayList;
import java.util.List;

public class WhyScanPrescriptionActivity extends BaseActivity implements WhyScanPrescriptionActivityCallback {
    private ActivityWhyScanPrescriptionBinding whyScanPrescriptionBinding;

    public static Intent getStartIntent(Context mContext) {
        Intent intent = new Intent(mContext, WhyScanPrescriptionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whyScanPrescriptionBinding = DataBindingUtil.setContentView(this, R.layout.activity_why_scan_prescription);
        whyScanPrescriptionBinding.setCallback(this);
        setUp();
    }

    private void setUp() {
        List<String> scannedPrescriptionsPathList = new ArrayList<>();
        getSessionManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
    }

    private SessionManager getSessionManager() {
        return new SessionManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClickScanPrescription() {
        startActivity(EpsonScanActivity.getStartActivity(this));
    }
}