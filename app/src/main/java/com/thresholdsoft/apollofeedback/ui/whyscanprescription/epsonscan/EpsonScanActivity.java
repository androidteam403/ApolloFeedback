package com.thresholdsoft.apollofeedback.ui.whyscanprescription.epsonscan;
/**
 * Created by naveen.m on Nov 10, 2021.
 */

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.epson.epsonscansdk.EpsonPDFCreator;
import com.epson.epsonscansdk.EpsonScanner;
import com.epson.epsonscansdk.ErrorCode;
import com.epson.epsonscansdk.usb.UsbProfile;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivityEpsonScanBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogScanStatusBinding;
import com.thresholdsoft.apollofeedback.ui.epsonsdk.FindScannerCallback;
import com.thresholdsoft.apollofeedback.ui.epsonsdk.FindUsbScannerTask;
import com.thresholdsoft.apollofeedback.ui.epsonsdk.FolderUtility;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.ScannedPrescriptionsActivity;

import java.util.ArrayList;
import java.util.List;

public class EpsonScanActivity extends BaseActivity implements FindScannerCallback {
    private ActivityEpsonScanBinding epsonScanBinding;
    private final int REQUEST_CODE = 1000;
    private List<UsbProfile> usbDevices;
    EpsonScanner scanner;

    FolderUtility folderUtility = new FolderUtility(this);
    EpsonPDFCreator pdfCreator = new EpsonPDFCreator();
    public static Intent getStartActivity(Context context) {
        Intent intent = new Intent(context, EpsonScanActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        epsonScanBinding = DataBindingUtil.setContentView(this, R.layout.activity_epson_scan);
        setUp();
    }

    private void setUp() {
        {
            // Android 6, API 23以上でパーミッションの確認
            if (Build.VERSION.SDK_INT >= 23) {
                String[] permissions = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                };
                checkPermission(permissions, REQUEST_CODE);
            }
        }
        FindUsbScannerTask task = new FindUsbScannerTask(EpsonScanActivity.this, EpsonScanActivity.this);
        task.execute();
        onScanClick();
    }

    private void scanDialog(String devicePath) {
        if (pdfCreator.initFilePath(folderUtility.getPDFFileName()) == false) {
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage("pdfCreator init fails")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
            return;
        }

        if (scanner.init(true, this) == false) {
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage("epson scan library init fails")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
            return;
        }

        ErrorCode err = scanner.open();
        if (err != ErrorCode.kEPSErrorNoError) {
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage("fails to open scanner code : " + err.getCode())
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .show();
            return;
        }
    }

    public void checkPermission(final String[] permissions, final int request_code) {
        ActivityCompat.requestPermissions(this, permissions, request_code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        Toast toast = Toast.makeText(this,
//                                "Added Permission: " + permissions[i], Toast.LENGTH_SHORT);
//                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(this,
                                "Rejected Permission: " + permissions[i], Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                break;
            default:
                break;
        }
    }

    String devicePathh = null;

    @Override
    public void onFindUsbDevices(List<UsbProfile> devices) {
        String devicePath = null;
        for (int idx = 0; idx < devices.size(); idx++) {
            UsbProfile device = (UsbProfile) devices.get(idx);
            String productName = device.getProductName();
//            https:
//d1xsr68o6znzvt.cloudfront.net/videos/FOO0093.mp4
            if (productName.equalsIgnoreCase("ES-60W")) {
                devicePath = device.getDevicePath();
                devicePathh = devicePath;
                scanner = new EpsonScanner();
                scanner.setDevicePath(devicePath);
                scanDialog(devicePath);
                break;
            }
        }
    }

    @Override
    public void onNoUsbDevicesFound() {
        Dialog dialog = new Dialog(this);
        DialogScanStatusBinding scanStatusBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_scan_status, null, false);
        dialog.setContentView(scanStatusBinding.getRoot());
        scanStatusBinding.tittle.setText("Scanner Not Available!!! ");
        scanStatusBinding.message.setText("Please contact Store Executive");
        scanStatusBinding.OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.show();
    }

    private void onScanClick() {
        epsonScanBinding.scan.setOnClickListener(v -> {
            // do Scan
            ScanTask scanTask = new ScanTask(EpsonScanActivity.this, scanner, folderUtility.getTempImageStoreDir());
            scanTask.execute();
            scanTask.SetOnFinishedListener(new OnFinishedListener() {
                @Override
                public void onFinished(ArrayList<String> arrayFileNames) {
                    for (String fileName : arrayFileNames) {
                        if (fileName.endsWith(".jpg")) {
                            if (pdfCreator.addJpegFile(fileName, 200, 200) == false) {
                                new AlertDialog.Builder(EpsonScanActivity.this)
                                        .setTitle("Alert")
                                        .setMessage("pdfCreator add fails")
                                        .setPositiveButton("OK", (dialog, which) -> finish())
                                        .show();
                                return;
                            }
                        } else {
                            if (pdfCreator.addPNMFile(fileName, 200, 200) == false) {
                                new AlertDialog.Builder(EpsonScanActivity.this)
                                        .setTitle("Alert")
                                        .setMessage("pdfCreator add fails")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .show();
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onImageStored(String filePath) {
                    if (scanner != null) {
                        scanner.close();
                        scanner.destory();
                    }
                    if (pdfCreator != null)
                        pdfCreator.destory();
                    startActivity(ScannedPrescriptionsActivity.getStartActivity(EpsonScanActivity.this, filePath));
                    finish();
//                    Intent intent = new Intent(EpsonScanActivity.this, InsertPrescriptionActivityNew.class);
//                    intent.putExtra("filePath", filePath);
//                    startActivity(intent);
//                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                    finish();
                }

                @Override
                public void onScannerNotAvailable() {
                    if (scanner != null) {
                        scanner.close();
                        scanner.destory();
                    }
                    if (pdfCreator != null)
                        pdfCreator.destory();
                    finish();
                }
            });
        });
    }

    @Override
    protected void onPause() {
        if (scanner != null) {
            scanner.close();
            scanner.destory();
        }
        if (pdfCreator != null)
            pdfCreator.destory();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (scanner != null) {
            scanner.close();
            scanner.destory();
        }
        if (pdfCreator != null)
            pdfCreator.destory();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanner != null) {
            scanner.close();
            scanner.destory();
        }
        if (pdfCreator != null)
            pdfCreator.destory();
    }
}
