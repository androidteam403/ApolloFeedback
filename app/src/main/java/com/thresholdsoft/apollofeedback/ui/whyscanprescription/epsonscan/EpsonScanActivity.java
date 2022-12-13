package com.thresholdsoft.apollofeedback.ui.whyscanprescription.epsonscan;
/**
 * Created by naveen.m on Nov 10, 2021.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.epson.epsonscansdk.EpsonPDFCreator;
import com.epson.epsonscansdk.EpsonScanner;
import com.epson.epsonscansdk.ErrorCode;
import com.epson.epsonscansdk.usb.UsbProfile;
import com.google.zxing.WriterException;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.databinding.ActivityEpsonScanBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogQrcodeBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogScanStatusBinding;
import com.thresholdsoft.apollofeedback.ui.epsonsdk.FindScannerCallback;
import com.thresholdsoft.apollofeedback.ui.epsonsdk.FindUsbScannerTask;
import com.thresholdsoft.apollofeedback.ui.epsonsdk.FolderUtility;
import com.thresholdsoft.apollofeedback.ui.feedback.FeedBackActivity;
import com.thresholdsoft.apollofeedback.ui.itemspayment.ItemsPaymentActivity;
import com.thresholdsoft.apollofeedback.ui.offersnow.OffersNowActivity;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.ScannedPrescriptionsActivity;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.ScannedPrescriptionsActivityController;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class EpsonScanActivity extends BaseActivity implements FindScannerCallback,EpsonScanActivityCallback {
    private ActivityEpsonScanBinding epsonScanBinding;
    private final int REQUEST_CODE = 1000;
    private List<UsbProfile> usbDevices;
    EpsonScanner scanner;
    private FeedbackSystemResponse feedbackSystemResponse;

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

        getController().feedbakSystemApiCall();
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

    @SuppressLint("MissingSuperCall")
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
//        DialogScanStatusBinding scanStatusBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_scan_status, null, false);
//        dialog.setContentView(scanStatusBinding.getRoot());
//        scanStatusBinding.tittle.setText("Scanner Not Available!!! ");
//        scanStatusBinding.message.setText("Please contact Store Executive");
//        scanStatusBinding.OK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        dialog.show();
    }
    private EpsonScanActivityController getController() {
        return new EpsonScanActivityController(this, this);
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
    private void qrCodeGeneration(Object qrCodeData, DialogQrcodeBinding dialogQrcodeBinding, Context context) {
        // below line is for getting
        // the windowmanager service.
        WindowManager manager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();
        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);
        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;
        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;
        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        QRGEncoder qrgEncoder = new QRGEncoder((String) qrCodeData, null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            if (qrCodeData != null) {
                //generatePhonepeQrcodeBinding.qrlogo.setVisibility(View.VISIBLE);
                dialogQrcodeBinding.qrCodeImage.setImageBitmap(bitmap);
                //generatePhonepeQrcodeBinding.loadingPanel.setVisibility(View.GONE);
                //ActivityUtils.hideDialog();
            }
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
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
    boolean isDialogShow = false;

    @Override
    public void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse) {
        if (feedbackSystemResponse != null) {


            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("##,##0.00");
            String amounttobeCollected = "";
            String discountAmount = "";
            String collectedAmount = "";
            String giftAmount = "";
            if (feedbackSystemResponse.getCustomerScreen() != null) {
                if (feedbackSystemResponse.getCustomerScreen().getPayment().getAmouttobeCollected() != null && !feedbackSystemResponse.getCustomerScreen().getPayment().getAmouttobeCollected().isEmpty())
                    amounttobeCollected = formatter.format(Double.valueOf(feedbackSystemResponse.getCustomerScreen().getPayment().getAmouttobeCollected()));

                if (feedbackSystemResponse.getCustomerScreen().getPayment().getDiscountValue() != null && !feedbackSystemResponse.getCustomerScreen().getPayment().getDiscountValue().isEmpty())
                    discountAmount = formatter.format(Double.valueOf(feedbackSystemResponse.getCustomerScreen().getPayment().getDiscountValue()));

                if (feedbackSystemResponse.getCustomerScreen().getPayment().getCollectedAmount() != null && !feedbackSystemResponse.getCustomerScreen().getPayment().getCollectedAmount().isEmpty())
                    collectedAmount = formatter.format(Double.valueOf(feedbackSystemResponse.getCustomerScreen().getPayment().getCollectedAmount()));

                if (feedbackSystemResponse.getCustomerScreen().getPayment().getGiftAmount() != null && !feedbackSystemResponse.getCustomerScreen().getPayment().getGiftAmount().isEmpty())
                    giftAmount = formatter.format(Double.valueOf(feedbackSystemResponse.getCustomerScreen().getPayment().getGiftAmount()));


                feedbackSystemResponse.getCustomerScreen().getPayment().setAmouttobeCollected(amounttobeCollected);
                feedbackSystemResponse.getCustomerScreen().getPayment().setCollectedAmount(collectedAmount);
                feedbackSystemResponse.getCustomerScreen().getPayment().setDiscountValue(discountAmount);
                feedbackSystemResponse.getCustomerScreen().getPayment().setGiftAmount(giftAmount);
            }
            this.feedbackSystemResponse = feedbackSystemResponse;//remove this line after testing
            if (feedbackSystemResponse.getIscustomerScreen()) {
                startActivity(OffersNowActivity.getStartIntent(EpsonScanActivity.this));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            } else if ((feedbackSystemResponse.getIspaymentScreen())) {
                epsonScanBinding.setModel(feedbackSystemResponse);
                if (feedbackSystemResponse.getCustomerScreen().getPayment().getQrCode() != null && !feedbackSystemResponse.getCustomerScreen().getPayment().getQrCode().isEmpty()) {
                    if (!isDialogShow) {
                        isDialogShow = true;
                        Dialog qrCodeDialog = new Dialog(this, R.style.dialogcustomstyle);
                        DialogQrcodeBinding dialogQrcodeBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_qrcode, null, false);
                        dialogQrcodeBinding.setModel(feedbackSystemResponse);
                        qrCodeDialog.setContentView(dialogQrcodeBinding.getRoot());
                        qrCodeDialog.setCancelable(false);
                        dialogQrcodeBinding.closeIcon.setOnClickListener(view -> qrCodeDialog.dismiss());
                        qrCodeGeneration(feedbackSystemResponse.getCustomerScreen().getPayment().getQrCode(), dialogQrcodeBinding, this);
                        dialogQrcodeBinding.qrCodeImage.setOnClickListener(view -> {
                            qrCodeDialog.dismiss();
                            startActivity(FeedBackActivity.getStartIntent(EpsonScanActivity.this, feedbackSystemResponse));
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            finish();
                        });
                        qrCodeDialog.show();
                    }
                }
                new Handler().postDelayed(() -> getController().feedbakSystemApiCall(), 5000);
            } else if (feedbackSystemResponse.getIsfeedbackScreen()) {
                startActivity(FeedBackActivity.getStartIntent(EpsonScanActivity.this, feedbackSystemResponse));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            } else {
                new Handler().postDelayed(() -> getController().feedbakSystemApiCall(), 5000);
            }
        }

    }

    @Override
    public void onFailureMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }
}
