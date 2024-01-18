package com.thresholdsoft.apollofeedback.ui.whyscanprescription;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
    private String bitmapImage;
    private boolean isTrained;
    private static final String BITMAP_IMAGE = "BITMAP_IMAGE";
    private static final String IS_TRAINED = "IS_TRAINED";
    private static final String FILE_NAME = "FILE_NAME";
    private static String fileName = null;

    public static Intent getStartIntent(Context mContext, String bitmapImage, boolean isTrained, String fileName) {
        Intent intent = new Intent(mContext, WhyScanPrescriptionActivity.class);
        intent.putExtra(BITMAP_IMAGE, bitmapImage);
        intent.putExtra(IS_TRAINED, isTrained);
        intent.putExtra(FILE_NAME, fileName);
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

        if (getIntent() != null) {
            bitmapImage = (String) getIntent().getStringExtra(BITMAP_IMAGE);
            isTrained = (boolean) getIntent().getBooleanExtra(IS_TRAINED, false);
            fileName = (String) getIntent().getStringExtra(FILE_NAME);
        }
    }

    Handler navigatePrescriptionHandler = new Handler();
    Runnable navigatePrescriptionRunnable = this::onClickScanPrescription;

    private SessionManager getSessionManager() {
        return new SessionManager(this);
    }

    @Override
    protected void onPause() {
        navigatePrescriptionHandler.removeCallbacks(navigatePrescriptionRunnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        navigatePrescriptionHandler.removeCallbacks(navigatePrescriptionRunnable);
        navigatePrescriptionHandler.postDelayed(navigatePrescriptionRunnable, 3000);
        super.onResume();
    }

    @Override
    public void onClickScanPrescription() {
        startActivity(EpsonScanActivity.getStartActivity(this, false, bitmapImage, isTrained, fileName));
    }
}