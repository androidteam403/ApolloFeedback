package com.thresholdsoft.apollofeedback.ui.scannedprescriptions;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.WriterException;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.databinding.ActivityScannedPrescriptionsBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogCustomAlertBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogPrescriptionFullviewBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogQrcodeBinding;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.ui.feedback.FeedBackActivity;
import com.thresholdsoft.apollofeedback.ui.itemspayment.ItemsPaymentActivity;
import com.thresholdsoft.apollofeedback.ui.itemspayment.ItemsPaymentActivityController;
import com.thresholdsoft.apollofeedback.ui.offersnow.OffersNowActivity;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.adapter.PrescriptionListAdapter;
import com.thresholdsoft.apollofeedback.ui.whyscanprescription.epsonscan.EpsonScanActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ScannedPrescriptionsActivity extends BaseActivity implements ScannedPrescriptionsActivityCallback {

    ActivityScannedPrescriptionsBinding scannedPrescriptionsBinding;
    private List<String> scannedPrescriptionsPathList;
    private PrescriptionListAdapter prescriptionListAdapter;
    private FeedbackSystemResponse feedbackSystemResponse;

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
        getController().feedbakSystemApiCall();
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

    private ScannedPrescriptionsActivityController getController() {
        return new ScannedPrescriptionsActivityController(this, this);
    }

    private SessionManager getSessionManager() {
        return new SessionManager(this);
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
                startActivity(OffersNowActivity.getStartIntent(ScannedPrescriptionsActivity.this));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            } else if ((feedbackSystemResponse.getIspaymentScreen())) {
                scannedPrescriptionsBinding.setModel(feedbackSystemResponse);
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
                            startActivity(FeedBackActivity.getStartIntent(ScannedPrescriptionsActivity.this, feedbackSystemResponse));
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            finish();
                        });
                        qrCodeDialog.show();
                    }
                }
                new Handler().postDelayed(() -> getController().feedbakSystemApiCall(), 5000);
            } else if (feedbackSystemResponse.getIsfeedbackScreen()) {
                startActivity(FeedBackActivity.getStartIntent(ScannedPrescriptionsActivity.this, feedbackSystemResponse));
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
