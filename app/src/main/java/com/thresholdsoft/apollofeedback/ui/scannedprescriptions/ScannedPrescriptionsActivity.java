package com.thresholdsoft.apollofeedback.ui.scannedprescriptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.adapter.PrescriptionListAdapter;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.model.KioskSelfCheckOutTransactionRequest;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.model.KioskSelfCheckOutTransactionResponse;
import com.thresholdsoft.apollofeedback.ui.whyscanprescription.epsonscan.EpsonScanActivity;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ScannedPrescriptionsActivity extends BaseActivity implements ScannedPrescriptionsActivityCallback {

    ActivityScannedPrescriptionsBinding scannedPrescriptionsBinding;
    private List<String> scannedPrescriptionsPathList;
    private PrescriptionListAdapter prescriptionListAdapter;
    private FeedbackSystemResponse feedbackSystemResponse;
    private boolean isFeedbackScreen = false;
    private boolean isPrescriptionsUploaded = false;
    private boolean isOnClickUploadPrescriptions = false;
    private static final String BITMAP_IMAGE = "BITMAP_IMAGE";
    private String bitmapImage;
    private static final String IS_TRAINED = "IS_TRAINED";
    private boolean isTrained = false;
    private static final String FILE_NAME = "FILE_NAME";

    public static Intent getStartActivity(Context context, String filePath, String bitmapImage, boolean isTrained, String fileName) {
        Intent intent = new Intent(context, ScannedPrescriptionsActivity.class);
        intent.putExtra("FILE_PATH", filePath);
        intent.putExtra(IS_TRAINED, isTrained);
        intent.putExtra(BITMAP_IMAGE, bitmapImage);
        intent.putExtra(FILE_NAME, fileName);
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
//        getController().feedbakSystemApiCall();
        String filePath = null;
        if (getIntent() != null) {
            filePath = (String) getIntent().getStringExtra("FILE_PATH");
        }
        if (getSessionManager().getScannedPrescriptionsPath() != null && getSessionManager().getScannedPrescriptionsPath().size() > 0) {
            this.scannedPrescriptionsPathList = getSessionManager().getScannedPrescriptionsPath();
            if (filePath != null && !filePath.isEmpty()) scannedPrescriptionsPathList.add(filePath);
            getSessionManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
        } else {
            scannedPrescriptionsPathList = new ArrayList<>();
            if (filePath != null && !filePath.isEmpty()) scannedPrescriptionsPathList.add(filePath);
            getSessionManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
        }
        scannedPrescriptionsBinding.setIsScanAgain(scannedPrescriptionsPathList.size() <= 6);
        prescriptionListAdapter();

        if (getIntent() != null) {
            bitmapImage = (String) getIntent().getStringExtra(BITMAP_IMAGE);
            isTrained = (boolean) getIntent().getBooleanExtra(IS_TRAINED, false);
            fileName = (String) getIntent().getStringExtra(FILE_NAME);
            try {
                Bitmap src = BitmapFactory.decodeStream(openFileInput("customer.jpg"));
                scannedPrescriptionsBinding.customerImage.setImageBitmap(src);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
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
//                startActivity(OffersNowActivity.getStartIntent(ScannedPrescriptionsActivity.this));
//                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//                finish();
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
//                            if (!isFeedbackScreen) {
//                                isFeedbakIdleHandler.removeCallbacks(isFeedbakIdlRunnable);
//                                isFeedbakIdleHandler.postDelayed(isFeedbakIdlRunnable, 1000 * 60);
//                            }
                            if (!this.isFeedbackScreen) {
                                feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
                                this.isFeedbackScreen = true;
//                            if (this.isPrescriptionsUploaded) {
//                                startActivity(FeedBackActivity.getStartIntent(ScannedPrescriptionsActivity.this, feedbackSystemResponse));
//                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//                                finish();
//                            }
                                scannedPrescriptionsBinding.setIsScanAgain(false);
                                if (scannedPrescriptionsPathList != null && scannedPrescriptionsPathList.size() > 0) {
                                    onClickUploadPrescriptions();
                                } else {
                                    startActivity(FeedBackActivity.getStartIntent(ScannedPrescriptionsActivity.this, feedbackSystemResponse, bitmapImage, isTrained));
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    finish();
                                }
                            }
                        });
                        qrCodeDialog.show();
                    }
                }
//                new Handler().postDelayed(() -> getController().feedbakSystemApiCall(), 5000);
                feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
                feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
            } else if (feedbackSystemResponse.getIsfeedbackScreen()) {
//                if (!isFeedbackScreen) {
//                    isFeedbakIdleHandler.removeCallbacks(isFeedbakIdlRunnable);
//                    isFeedbakIdleHandler.postDelayed(isFeedbakIdlRunnable, 1000 * 60);
//                }
                if (!this.isFeedbackScreen) {
                    feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
                    this.isFeedbackScreen = true;
//                if (this.isPrescriptionsUploaded) {
//                    List<String> scannedPrescriptionsPathList = new ArrayList<>();
//                    getSessionManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
//                    startActivity(FeedBackActivity.getStartIntent(ScannedPrescriptionsActivity.this, feedbackSystemResponse));
//                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//                    finish();
//                }
//                feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
//                feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
                    scannedPrescriptionsBinding.setIsScanAgain(false);
                    if (scannedPrescriptionsPathList != null && scannedPrescriptionsPathList.size() > 0) {
                        onClickUploadPrescriptions();
                    } else {
                        startActivity(FeedBackActivity.getStartIntent(ScannedPrescriptionsActivity.this, feedbackSystemResponse, bitmapImage, isTrained));
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        finish();
                    }
                }
            } else {
//                new Handler().postDelayed(() -> getController().feedbakSystemApiCall(), 5000);
                feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
                feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);

            }
        }

    }

    Handler feedbakSystemApiCallHandler = new Handler();
    Runnable feedbakSystemApiCallRunnable = new Runnable() {
        @Override
        public void run() {
//            if (!feedbackSystemResponse.getIsPrescriptionScan()) {
            getController().feedbakSystemApiCall();
//            }
        }
    };
//    Handler isFeedbakIdleHandler = new Handler();
//    Runnable isFeedbakIdlRunnable = new Runnable() {
//        @Override
//        public void run() {
//            List<String> scannedPrescriptionsPathList = new ArrayList<>();
//            getSessionManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
//            startActivity(FeedBackActivity.getStartIntent(ScannedPrescriptionsActivity.this, feedbackSystemResponse));
//            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//            finish();
//        }
//    };

    @Override
    protected void onPause() {
//        isFeedbakIdleHandler.removeCallbacks(isFeedbakIdlRunnable);
        feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
//        if (isFeedbackScreen) {
//            isFeedbakIdleHandler.removeCallbacks(isFeedbakIdlRunnable);
//            isFeedbakIdleHandler.postDelayed(isFeedbakIdlRunnable, 1000 * 60);
//        }
        feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
//        feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
        getController().feedbakSystemApiCall();
        super.onResume();
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
        startActivity(EpsonScanActivity.getStartActivity(this, true, bitmapImage, isTrained, fileName));
        finish();
    }

    @Override
    public void onClickPrescription(String prescriptionPath) {
        Dialog prescriptionZoomDialog = new Dialog(this, R.style.fadeinandoutcustomDialog);
        DialogPrescriptionFullviewBinding prescriptionFullviewBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_prescription__fullview, null, false);
        prescriptionZoomDialog.setContentView(prescriptionFullviewBinding.getRoot());
        File imgFile = new File(prescriptionPath);
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
                    scannedPrescriptionsBinding.setIsScanAgain(scannedPrescriptionsPathList.size() <= 6);
                    prescriptionListAdapter.notifyDataSetChanged();
                } else {
                    finish();
                }
            }
        });
        dialogCustomAlertBinding.dialogButtonNO.setOnClickListener(v12 -> dialog.dismiss());
    }

    @Override
    public void onSuccessKioskSelfCheckOutTransactionApiCAll(KioskSelfCheckOutTransactionResponse kioskSelfCheckOutTransactionResponse, int prescriptionPos) {
        if (kioskSelfCheckOutTransactionResponse.getRequestStatus() == 0) {
            if (prescriptionPos == (scannedPrescriptionsPathList.size() - 1)) {
                this.isPrescriptionsUploaded = true;
                CommonUtils.hideDialog();
                Toast.makeText(this, kioskSelfCheckOutTransactionResponse.getReturnMessage(), Toast.LENGTH_SHORT).show();
                if (isFeedbackScreen) {
                    List<String> scannedPrescriptionsPathList = new ArrayList<>();
                    getSessionManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
                    startActivity(FeedBackActivity.getStartIntent(ScannedPrescriptionsActivity.this, feedbackSystemResponse, bitmapImage, isTrained));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    finish();
                }

            } else {
                KioskSelfCheckOutTransactionRequest kioskSelfCheckOutTransactionRequest = new KioskSelfCheckOutTransactionRequest();
                kioskSelfCheckOutTransactionRequest.setPrescString(encodeImage(scannedPrescriptionsPathList.get(prescriptionPos + 1)));
                kioskSelfCheckOutTransactionRequest.setPrescId(getSessionManager().getSiteId() + "-" + CommonUtils.getCurrentTimeStamp());
                kioskSelfCheckOutTransactionRequest.setFromdate(CommonUtils.getCurrentDateTime());
                kioskSelfCheckOutTransactionRequest.setTodate("");
                kioskSelfCheckOutTransactionRequest.setStoreid(getSessionManager().getSiteId());
                kioskSelfCheckOutTransactionRequest.setKioskid(getSessionManager().getSiteId() + "_" + getSessionManager().getTerminalId());
                if (feedbackSystemResponse != null && feedbackSystemResponse.getCustomerofferScreen() != null && feedbackSystemResponse.getCustomerofferScreen().getCustomerName() != null) {
                    kioskSelfCheckOutTransactionRequest.setCustomername(feedbackSystemResponse.getCustomerofferScreen().getCustomerName());
                }
                kioskSelfCheckOutTransactionRequest.setMobileno(Objects.requireNonNull(feedbackSystemResponse).getCustomerScreen().getBillNumber());
                kioskSelfCheckOutTransactionRequest.setKiosklink(scannedPrescriptionsPathList.get(prescriptionPos + 1).substring(scannedPrescriptionsPathList.get(prescriptionPos + 1).indexOf("-") + 1).replaceAll("/", "_"));
                kioskSelfCheckOutTransactionRequest.setCreateddate(CommonUtils.getCurrentDateTime());
                kioskSelfCheckOutTransactionRequest.setStatusid(1);
                kioskSelfCheckOutTransactionRequest.setRequesttype("INSERTPRESCRIPTION");
                getController().kioskSelfCheckOutTransactionApiCAll(kioskSelfCheckOutTransactionRequest, prescriptionPos + 1);
            }

        }
    }

    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.NO_WRAP);
        //Base64.de
        return encImage;

    }


    @Override
    public void onClickUploadPrescriptions() {
        if (!isOnClickUploadPrescriptions) {
            isOnClickUploadPrescriptions = true;
            if (feedbackSystemResponse != null) {
                if (scannedPrescriptionsPathList != null && scannedPrescriptionsPathList.size() > 0) {
                    KioskSelfCheckOutTransactionRequest kioskSelfCheckOutTransactionRequest = new KioskSelfCheckOutTransactionRequest();

//            File imagefile = new File(scannedPrescriptionsPathList.get(0));
//
//            InputStream inputStream = null; // You can get an inputStream using any I/O API
//            try {
//                inputStream = new FileInputStream(imagefile);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            byte[] bytes;
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//
//            try {
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    output.write(buffer, 0, bytesRead);
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            bytes = output.toByteArray();
//            String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);

                    kioskSelfCheckOutTransactionRequest.setPrescString(encodeImage(scannedPrescriptionsPathList.get(0)));

//            kioskSelfCheckOutTransactionRequest.setPrescString(encodeImage(scannedPrescriptionsPathList.get(0)));

//            kioskSelfCheckOutTransactionRequest.setPrescString(Base64.encodeToString(scannedPrescriptionsPathList.get(0).getBytes(), Base64.DEFAULT));
                    kioskSelfCheckOutTransactionRequest.setPrescId(getSessionManager().getSiteId() + "-" + CommonUtils.getCurrentTimeStamp());
                    kioskSelfCheckOutTransactionRequest.setFromdate(CommonUtils.getCurrentDateTime());
                    kioskSelfCheckOutTransactionRequest.setTodate("");
                    kioskSelfCheckOutTransactionRequest.setStoreid(getSessionManager().getSiteId());
                    kioskSelfCheckOutTransactionRequest.setKioskid(getSessionManager().getSiteId() + "_" + getSessionManager().getTerminalId());
                    if (feedbackSystemResponse != null && feedbackSystemResponse.getCustomerofferScreen() != null && feedbackSystemResponse.getCustomerofferScreen().getCustomerName() != null) {
                        kioskSelfCheckOutTransactionRequest.setCustomername(feedbackSystemResponse.getCustomerofferScreen().getCustomerName());
                    }
                    kioskSelfCheckOutTransactionRequest.setMobileno(Objects.requireNonNull(feedbackSystemResponse).getCustomerScreen().getBillNumber());
                    kioskSelfCheckOutTransactionRequest.setKiosklink(scannedPrescriptionsPathList.get(0).substring(scannedPrescriptionsPathList.get(0).indexOf("-") + 1).replaceAll("/", "_"));
                    kioskSelfCheckOutTransactionRequest.setCreateddate(CommonUtils.getCurrentDateTime());
                    kioskSelfCheckOutTransactionRequest.setStatusid(1);
                    kioskSelfCheckOutTransactionRequest.setRequesttype("INSERTPRESCRIPTION");
                    CommonUtils.showDialog(this, "Please Wait...");
                    getController().kioskSelfCheckOutTransactionApiCAll(kioskSelfCheckOutTransactionRequest, 0);
                }
            }
        }
    }

    //..............................................................................................
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    boolean mStartPlaying = true;
    private static String fileName = null;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean isRecording = false;

    private void startRecording() {

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            isRecording = true;
            scannedPrescriptionsBinding.startRecord.setVisibility(View.GONE);
            scannedPrescriptionsBinding.startRecordGif.setVisibility(View.VISIBLE);
            recorder.prepare();
        } catch (IOException e) {
            Log.e("OFFERS_NOW_ACTIVITY", "prepare() failed");
        }

        recorder.start();
//        startRecord.setText("Recording is inprogress...");
    }

    private void stopRecording() {
        isRecording = false;
        scannedPrescriptionsBinding.startRecordGif.setVisibility(View.GONE);
        scannedPrescriptionsBinding.startRecord.setVisibility(View.VISIBLE);
        recorder.stop();
        recorder.release();
        recorder = null;
//        startRecord.setText("Start Record");
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    onPlay(mStartPlaying);
                    if (mStartPlaying) {
//                        startPlay.setText("Stop playing");
                    } else {
//                        startPlay.setText("Start playing");
                    }
                    mStartPlaying = !mStartPlaying;
                }

            });
        } catch (IOException e) {
            Log.e("OFFERS_NOW_ACTIVITY", "prepare() failed");
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
            stopPlayingHandler.removeCallbacks(stopPlayingRunnable);
            stopPlayingHandler.postDelayed(stopPlayingRunnable, 30000);
        } else {
            stopPlaying();
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    @Override
    public void onCLickStartRecord() {
        if (!isRecording) {
            // Record to the external cache directory for visibility
            fileName = getExternalCacheDir().getAbsolutePath();
            fileName += "/audiorecordtest.3gp";
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startRecording();
                stopRecordHandler.removeCallbacks(stopRecordRunnable);
                stopRecordHandler.postDelayed(stopRecordRunnable, 30000);
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
            }
        }
    }

    @Override
    public void onClickPlayorStop() {
        onPlay(mStartPlaying);
        if (mStartPlaying) {
            scannedPrescriptionsBinding.play.setImageResource(R.drawable.stop_icon);
        } else {
            scannedPrescriptionsBinding.play.setImageResource(R.drawable.play_icon);
        }
        mStartPlaying = !mStartPlaying;
    }


    Handler stopRecordHandler = new Handler();
    Runnable stopRecordRunnable = new Runnable() {
        @Override
        public void run() {
            stopRecording();
        }
    };

    Handler stopPlayingHandler = new Handler();
    Runnable stopPlayingRunnable = new Runnable() {
        @Override
        public void run() {
            mStartPlaying = true;
            scannedPrescriptionsBinding.play.setImageResource(R.drawable.play_icon);
            stopPlaying();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }
}
