package com.thresholdsoft.apollofeedback.ui.scannedprescriptions;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivityScannedPrescriptionsBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogCustomAlertBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogPrescriptionFullviewBinding;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.adapter.PrescriptionListAdapter;
import com.thresholdsoft.apollofeedback.ui.whyscanprescription.epsonscan.EpsonScanActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScannedPrescriptionsActivity extends BaseActivity implements ScannedPrescriptionsActivityCallback {

    ActivityScannedPrescriptionsBinding scannedPrescriptionsBinding;
    private List<String> scannedPrescriptionsPathList;
    private PrescriptionListAdapter prescriptionListAdapter;

    public static Intent getStartActivity(Context context, String filePath) {
        Intent intent = new Intent(context, ScannedPrescriptionsActivity.class);
        intent.putExtra("FILE_PATH", filePath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannedPrescriptionsBinding = DataBindingUtil.setContentView(this, R.layout.activity_scanned_prescriptions);
        scannedPrescriptionsBinding.setCallback(this);
        setUp();
    }

    private void setUp() {
        String filePath = null;
        if (getIntent() != null) {
            filePath = (String) getIntent().getStringExtra("FILE_PATH");
        }
        if (getSessionManager().getScannedPrescriptionsPath() != null && getSessionManager().getScannedPrescriptionsPath().size() > 0) {
            this.scannedPrescriptionsPathList = getSessionManager().getScannedPrescriptionsPath();
            scannedPrescriptionsPathList.add(filePath);
            getSessionManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
        } else {
            scannedPrescriptionsPathList = new ArrayList<>();
            scannedPrescriptionsPathList.add(filePath);
            getSessionManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
        }
        prescriptionListAdapter();
    }

    private void prescriptionListAdapter() {
        scannedPrescriptionsBinding.setCallback(this);
        prescriptionListAdapter = new PrescriptionListAdapter(this, scannedPrescriptionsPathList, this);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        scannedPrescriptionsBinding.prescriptionListRecyclerview.setLayoutManager(mLayoutManager2);
        scannedPrescriptionsBinding.prescriptionListRecyclerview.setAdapter(prescriptionListAdapter);
    }

    private SessionManager getSessionManager() {
        return new SessionManager(this);
    }

    @Override
    public void onClickScanAgain() {
        startActivity(EpsonScanActivity.getStartActivity(this));
        finish();
    }

    @Override
    public void onClickPrescription(String prescriptionPath) {
        Dialog prescriptionZoomDialog = new Dialog(this, R.style.fadeinandoutcustomDialog);
        DialogPrescriptionFullviewBinding prescriptionFullviewBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_prescription__fullview, null, false);
        prescriptionZoomDialog.setContentView(prescriptionFullviewBinding.getRoot());
        File imgFile = new File(prescriptionPath + "/1.jpg");
        if (imgFile.exists()) {
            Uri uri = Uri.fromFile(imgFile);
            prescriptionFullviewBinding.prescriptionFullviewImg.setImageURI(uri);
        }
        prescriptionFullviewBinding.dismissPrescriptionFullview.setOnClickListener(v -> prescriptionZoomDialog.dismiss());
        prescriptionZoomDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onClickItemDelete(int position) {
        Dialog dialog = new Dialog(this);
        DialogCustomAlertBinding dialogCustomAlertBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_custom_alert, null, false);
        dialog.setContentView(dialogCustomAlertBinding.getRoot());
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialogCustomAlertBinding.title.setText("Alert");
        dialogCustomAlertBinding.dialogMessage.setText("Are you sure want to delete?");
        dialogCustomAlertBinding.dialogButtonOK.setOnClickListener(v1 -> {
            dialog.dismiss();
            if (getSessionManager().getScannedPrescriptionsPath() != null && getSessionManager().getScannedPrescriptionsPath().size() > 0) {
                this.scannedPrescriptionsPathList = getSessionManager().getScannedPrescriptionsPath();
                scannedPrescriptionsPathList.remove(position);
                getSessionManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
                if (scannedPrescriptionsPathList != null && scannedPrescriptionsPathList.size() > 0 && prescriptionListAdapter != null) {
                    prescriptionListAdapter.setPrescriptionPathList(scannedPrescriptionsPathList);
                    prescriptionListAdapter.notifyDataSetChanged();
                } else {
                    finish();
                }
            }
        });
        dialogCustomAlertBinding.dialogButtonNO.setOnClickListener(v12 -> dialog.dismiss());
    }
}
