package com.thresholdsoft.apollofeedback.ui.whyscanprescription.epsonscan;
/**
 * Created by naveen.m on Nov 10, 2021.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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
import androidx.core.content.ContextCompat;
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
import com.thresholdsoft.apollofeedback.ui.offersnow.OffersNowActivity;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.ScannedPrescriptionsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class EpsonScanActivity extends BaseActivity implements FindScannerCallback, EpsonScanActivityCallback {
    private ActivityEpsonScanBinding epsonScanBinding;
    private final int REQUEST_CODE = 1000;
    private final int IMAGES_AUDIO_VIDEO_REQUEST_CODE = 1001;
    private final int READ_WRITE_REQUEST_CODE = 1002;
    private List<UsbProfile> usbDevices;
    EpsonScanner scanner;
    private FeedbackSystemResponse feedbackSystemResponse;
    private String devicePath = null;
    FolderUtility folderUtility = new FolderUtility(this);
    EpsonPDFCreator pdfCreator = new EpsonPDFCreator();
    private boolean isCameFromScannedPrescription;

    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private boolean isNewActivity = true;
    private static final String BITMAP_IMAGE = "BITMAP_IMAGE";
    private String bitmapImage;
    private static final String IS_TRAINED = "IS_TRAINED";
    private boolean isTrained;
    private static final String FILE_NAME = "FILE_NAME";

    public static Intent getStartActivity(Context context, boolean isCameFromScannedPrescription, String bitmapImage, boolean isTrained, String fileName) {
        Intent intent = new Intent(context, EpsonScanActivity.class);
        intent.putExtra("IS_CAME_FROM_SCANNED_PRESCRIPTION", isCameFromScannedPrescription);
        intent.putExtra(IS_TRAINED, isTrained);
        intent.putExtra(BITMAP_IMAGE, bitmapImage);
        intent.putExtra(FILE_NAME, fileName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        epsonScanBinding = DataBindingUtil.setContentView(this, R.layout.activity_epson_scan);
        setUp();
    }

    private void setUp() {
        epsonScanBinding.setCallback(this);
        if (getIntent() != null) {
            isCameFromScannedPrescription = (Boolean) getIntent().getBooleanExtra("IS_CAME_FROM_SCANNED_PRESCRIPTION", false);
            if (getIntent() != null) {
                bitmapImage = (String) getIntent().getStringExtra(BITMAP_IMAGE);
                isTrained = (boolean) getIntent().getBooleanExtra(IS_TRAINED, false);
                fileName = (String) getIntent().getStringExtra(FILE_NAME);
                try {
                    Bitmap src = BitmapFactory.decodeStream(openFileInput("customer.jpg"));
                    epsonScanBinding.customerImage.setImageBitmap(src);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }
//        getController().feedbakSystemApiCall();
        {
            // Android 6, API 23以上でパーミッションの確認
            /*if (Build.VERSION.SDK_INT >= 23) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,};
                checkPermission(permissions, REQUEST_CODE);
            }*/

           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                String[] permissions = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO};
                checkPermission(permissions, IMAGES_AUDIO_VIDEO_REQUEST_CODE);
            } else {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                checkPermission(permissions, READ_WRITE_REQUEST_CODE);
            }
*/

        }
        FindUsbScannerTask task = new FindUsbScannerTask(EpsonScanActivity.this, EpsonScanActivity.this);
        task.execute();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO}, IMAGES_AUDIO_VIDEO_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_WRITE_REQUEST_CODE);
        }
        onScanClick();
    }

    private void scanDialog(String devicePath) {
        if (pdfCreator.initFilePath(folderUtility.getPDFFileName()) == false) {
            new AlertDialog.Builder(this).setCancelable(false).setTitle("Alert").setMessage("pdfCreator init fails").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            return;
        }

        if (scanner.init(true, this) == false) {
            new AlertDialog.Builder(this).setCancelable(false).setTitle("Alert").setMessage("epson scan library init fails").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            return;
        }

        ErrorCode err = scanner.open();
        if (err != ErrorCode.kEPSErrorNoError) {
            new AlertDialog.Builder(this).setCancelable(false).setTitle("Alert").setMessage("fails to open scanner code : " + err.getCode()).setPositiveButton("OK", (dialog, which) -> finish()).show();
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
                        if (devicePath != null && !devicePath.isEmpty()) {
                            scanDialog(devicePath);
                        }
//                        Toast toast = Toast.makeText(this,
//                                "Added Permission: " + permissions[i], Toast.LENGTH_SHORT);
//                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(this, "Rejected Permission: " + permissions[i], Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                break;
            case READ_WRITE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        if (devicePath != null && !devicePath.isEmpty()) {
                            scanDialog(devicePath);
                        }
                    } else {
                        Toast.makeText(this, "Rejected Permission: " + permissions[0] + "," + permissions[1], Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case IMAGES_AUDIO_VIDEO_REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        if (devicePath != null && !devicePath.isEmpty()) {
                            scanDialog(devicePath);
                        }
                    } else {
                        Toast.makeText(this, "Rejected Permission: " + permissions[0] + "," + permissions[1] + "," + permissions[2], Toast.LENGTH_SHORT).show();
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
//                scanDialog(devicePath);
                break;
            }
        }
    }

    @Override
    public void onNoUsbDevicesFound() {
        Dialog dialog = new Dialog(this);
        DialogScanStatusBinding scanStatusBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_scan_status, null, false);
        dialog.setContentView(scanStatusBinding.getRoot());
        dialog.setCancelable(false);
        scanStatusBinding.tittle.setText("Scanner Not Available!!! ");
        scanStatusBinding.message.setText("Please contact Store Executive");
        scanStatusBinding.OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCameFromScannedPrescription) {
                    startActivity(ScannedPrescriptionsActivity.getStartActivity(EpsonScanActivity.this, null, bitmapImage, isTrained, fileName));
                    finish();
                } else {
                    finish();
                }
            }
        });
        dialog.show();
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
                                new AlertDialog.Builder(EpsonScanActivity.this).setCancelable(false).setTitle("Alert").setMessage("pdfCreator add fails").setPositiveButton("OK", (dialog, which) -> finish()).show();
                                return;
                            }
                        } else {
                            if (pdfCreator.addPNMFile(fileName, 200, 200) == false) {
                                new AlertDialog.Builder(EpsonScanActivity.this).setCancelable(false).setTitle("Alert").setMessage("pdfCreator add fails").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
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
                    if (pdfCreator != null) pdfCreator.destory();
                    // Get length of file in bytes
                    long fileSizeInBytes = filePath.length();
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    long fileSizeInMB = fileSizeInKB / 1024;

                    startActivity(ScannedPrescriptionsActivity.getStartActivity(EpsonScanActivity.this, saveBitmapToFile(filePath).getAbsolutePath(), bitmapImage, isTrained, fileName));
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
                    if (pdfCreator != null) pdfCreator.destory();
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
        feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
        if (scanner != null) {
            scanner.close();
            scanner.destory();
        }
        if (pdfCreator != null) pdfCreator.destory();
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        //
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
        super.onStop();
    }

    private static String TAG = "EPSON_SCAN_ACTIVITY";

    public class PhoneUnlockedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNewActivity) {
                if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    Log.d(TAG, "Phone unlocked");
                    isNewActivity = false;
                    Intent test = getIntent();
                    finish();
                    startActivity(test);
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    Log.d(TAG, "Phone locked");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (scanner != null) {
            scanner.close();
            scanner.destory();
        }
        if (pdfCreator != null) pdfCreator.destory();
        finish();
    }

    @Override
    protected void onStart() {
        receiver = new PhoneUnlockedReceiver();
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
        super.onStart();
    }

    @Override
    protected void onResume() {
        receiver = new PhoneUnlockedReceiver();
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
        feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
//        feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
        getController().feedbakSystemApiCall();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanner != null) {
            scanner.close();
            scanner.destory();
        }
        if (pdfCreator != null) pdfCreator.destory();
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
                            startActivity(FeedBackActivity.getStartIntent(EpsonScanActivity.this, feedbackSystemResponse, bitmapImage, isTrained));
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            finish();
                        });
                        qrCodeDialog.show();
                    }
                }
//                new Handler().postDelayed(() -> getController().feedbakSystemApiCall(), 5000);
                feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
                feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
            } else if (feedbackSystemResponse.getIsfeedbackScreen()) {
                startActivity(FeedBackActivity.getStartIntent(EpsonScanActivity.this, feedbackSystemResponse, bitmapImage, isTrained));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
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

    @Override
    public void onFailureMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    public File saveBitmapToFile(String imgfile) {
        try {
            File file = new File(imgfile + "/1.jpg");
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int checkCallingOrSelfPermission(String permission) {
        return super.checkCallingOrSelfPermission(permission);
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
            epsonScanBinding.startRecord.setVisibility(View.GONE);
            epsonScanBinding.startRecordGif.setVisibility(View.VISIBLE);
            recorder.prepare();
        } catch (IOException e) {
            Log.e("OFFERS_NOW_ACTIVITY", "prepare() failed");
        }

        recorder.start();
//        startRecord.setText("Recording is inprogress...");
    }

    private void stopRecording() {
        isRecording = false;
        epsonScanBinding.startRecordGif.setVisibility(View.GONE);
        epsonScanBinding.startRecord.setVisibility(View.VISIBLE);
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
            epsonScanBinding.play.setImageResource(R.drawable.stop_icon);
        } else {
            epsonScanBinding.play.setImageResource(R.drawable.play_icon);
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
            epsonScanBinding.play.setImageResource(R.drawable.play_icon);
            stopPlaying();
        }
    };


}
