package com.thresholdsoft.apollofeedback.ui.offersnow;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.databinding.ActivityOffersNowBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogSuccessFaceRecogBinding;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.ui.itemspayment.ItemsPaymentActivity;
import com.thresholdsoft.apollofeedback.ui.offersnow.adapter.ImageSlideAdapter;
import com.thresholdsoft.apollofeedback.ui.offersnow.dialog.AccessKeyDialog;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.DcOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.ZeroCodeApiModelResponse;
import com.thresholdsoft.apollofeedback.ui.storesetup.StoreSetupActivity;
import com.thresholdsoft.apollofeedback.utils.AppConstants;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OffersNowActivity extends BaseActivity implements OffersNowActivityCallback {
    int currentIndex = 0;
    boolean isAutoScrolling = true;
    LinearLayoutManager layoutManager;
    private ActivityOffersNowBinding offersNowBinding;
    private FeedbackSystemResponse feedbackSystemResponse;
    Button b;
    private CameraDevice cameraDevice;


    private Camera camera;
    private CameraCaptureSession cameraCaptureSessions;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
  private com.google.mlkit.vision.face.FaceDetector faceDetector;
    private Handler frameCaptureHandler;
    private final long FRAME_CAPTURE_DELAY = 1000;
    private Size previewSize;


    public static Intent getStartIntent(Context mContext) {
        Intent intent = new Intent(mContext, OffersNowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    private String mobileNumber;
    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {


        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(width,height );
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Handle surface texture size change if needed
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if(camera!=null){
                camera.stopPreview();
                camera.release();
            }

            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // Update your view here if needed
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            // This is called when the camera is open
            cameraDevice = camera;
            createCameraPreviews();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {

        }
        // Implement other methods like onDisconnected, onError, etc.
    };

    protected void createCameraPreviews() {
        try {
            SurfaceTexture texture = offersNowBinding.textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(1920, 1080);
            Surface surface = new Surface(texture);
            final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    // The camera is already closed
                    if (cameraDevice == null) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview(captureRequestBuilder);
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
                // Implement other methods like onConfigureFailed
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void updatePreview(CaptureRequest.Builder captureRequestBuilder) {
        if (cameraDevice == null) {
            return;
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(int textureViewWidth, int textureViewHeight) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // Here, you should choose the camera ID (front or back)
            String cameraIds = manager.getCameraIdList()[0];
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }
                // For still image captures, we use the largest available size.
                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());

                // Calculate the aspect ratio of the TextureView for the chosen size
                int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
                // Swap dimensions based on device orientation
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 90 ||
                                characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 0 ||
                                characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e("CameraActivity", "Display rotation is invalid: " + displayRotation);
                }

                int rotatedWidth = textureViewWidth;
                int rotatedHeight = textureViewHeight;

                if (swappedDimensions) {
                    rotatedWidth = textureViewHeight;
                    rotatedHeight = textureViewWidth;
                }

                Size aspectRatio = new Size(rotatedWidth, rotatedHeight);

                previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), textureViewWidth, textureViewHeight, aspectRatio);
                configureTransform(textureViewWidth, textureViewHeight);
                break;
            }

            // Check the stream configuration map, you might want to configure the sizes
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.openCamera(cameraIds, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the TextureView
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the TextureView
        List<Size> notBigEnough = new ArrayList<>();
        int width = aspectRatio.getWidth();
        int height = aspectRatio.getHeight();

        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                bigEnough.add(option);
            } else {
                notBigEnough.add(option);
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e("CameraActivity", "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplication won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offersNowBinding = DataBindingUtil.setContentView(this, R.layout.activity_offers_now);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setUp();

    }

    private void setUp() {
        offersNowBinding.setCallback(this);
        if (getDataManager().getSiteId().equalsIgnoreCase("") && getDataManager().getTerminalId().equalsIgnoreCase("")) {
            offersNowBinding.setIsConfigurationAvailable(true);
            onClickSettingIcon();
        } else {
            offersNowBinding.setIsConfigurationAvailable(false);
        }





//        getController().getOffersNowApiCall();
//        getController().feedbakSystemApiCall();
        getController().getDcOffersNowApi(getDataManager().getDcCode());
//        Intent serviceIntent = new Intent(this, FaceDetectors.class);
//         faceDetectors = new FaceDetectors(getApplicationContext(), this);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted, request it
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 999);
//        } else {
//            // Permission is already granted, you can start camera operations
//            faceDetectors.onCreate();
//            faceDetectors.onStartCommand(serviceIntent, 0, 1);
//        }

        assert offersNowBinding.textureView != null;
        offersNowBinding.textureView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        offersNowBinding.textureView.setSurfaceTextureListener(textureListener);

       setupFaceDetector();

    }




    private void setupFaceDetector() {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .build();

        faceDetector = FaceDetection.getClient(options);
    }

    private void captureAndProcessFrame() {
        if (offersNowBinding.textureView == null || !offersNowBinding.textureView.isAvailable()) {
            return;
        }
        if (offersNowBinding.textureView.getWidth() == 0 || offersNowBinding.textureView.getHeight() == 0) {
            Toast.makeText(this, "TextureView not ready", Toast.LENGTH_SHORT).show();
            return; // TextureView not ready
        }

        Bitmap bitmap = offersNowBinding.textureView.getBitmap();
        if (bitmap != null) {
//            BitmapFactory.Options bitmapFatoryOptions=new BitmapFactory.Options();
//            bitmapFatoryOptions.inPreferredConfig=Bitmap.Config.RGB_565;
//            Bitmap mybitmapss=BitmapFactory.decodeResource(getResources(), R.drawable.human_face,bitmapFatoryOptions);
//            Toast.makeText(this, ""+bitmap, Toast.LENGTH_SHORT).show();
           processFrame(bitmap);

        }
    }

    private void startFrameCapture() {
        frameCaptureHandler = new Handler(Looper.getMainLooper());
        frameCaptureHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                captureAndProcessFrame();
                frameCaptureHandler.postDelayed(this, FRAME_CAPTURE_DELAY);
            }
        }, FRAME_CAPTURE_DELAY);
    }



    private void stopFrameCapture() {
        if (frameCaptureHandler != null) {
            frameCaptureHandler.removeCallbacksAndMessages(null);
        }
    }


    private void processFrame(Bitmap bitmap) {
//        Toast.makeText(this, "ProcessFrame"+ bitmap, Toast.LENGTH_SHORT).show();
        InputImage image = InputImage.fromBitmap(bitmap, 0); // Assuming no rotation needed
        faceDetector.process(image)
                .addOnSuccessListener(faces -> {
                    if(faces.size()==0){
//                        Toast.makeText(this, "No faces found"+ faces.size(), Toast.LENGTH_SHORT).show();
                    }
                    for (Face face : faces) {
                        Rect boundingBox = face.getBoundingBox();
                        Bitmap croppedBitmap = cropBitmap(bitmap, boundingBox);
                        runOnUiThread(() -> {; // Initialize your bitmap

// Specify the output file path. Example for internal storage:
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                            String filename = "JPEG_" + timeStamp + ".jpg";
                            File outputFile = new File(getApplicationContext().getFilesDir(), filename); // context is your Activity or Application context

                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(outputFile);

                                // Compress the bitmap to JPEG format and write it to the output stream
                                // Quality is set to 100 (highest)
                                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                String name = "";
                                if(!name.isEmpty()){
                                    getController().zeroCodeApiCall(outputFile, name, croppedBitmap);
                                }else{
                                    getController().zeroCodeApiCallWithoutName(outputFile, croppedBitmap);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (out != null) {
                                        out.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            // Assuming you have an ImageView with the ID imageView
//                            openDialogBox(croppedBitmap);
                            stopFrameCapture();

                        });
//                        Toast.makeText(this, "No. of Faces found: "+ faces.size(), Toast.LENGTH_SHORT).show();
                        // Handle detected faces
                        // You can crop the bitmap around the face.getBoundingBox() if needed
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "unable to find faces", Toast.LENGTH_SHORT).show();
                    // Handle any errors
                });
    }

    private Bitmap cropBitmap(Bitmap bitmap, Rect rect) {
        // Ensure the cropping area is within the bounds of the bitmap
        int x = Math.max(0, rect.left);
        int y = Math.max(0, rect.top);
        int width = Math.min(rect.width(), bitmap.getWidth() - x);
        int height = Math.min(rect.height(), bitmap.getHeight() - y);

        return Bitmap.createBitmap(bitmap, x, y, width, height);
    }
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == offersNowBinding.textureView || null == this) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        offersNowBinding.textureView.setTransform(matrix);
    }

//    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing) {
//        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
////        int rotationCompensation = ORIENTATIONS.get(deviceRotation);
//
//        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
//        int sensorOrientation = 0;
//        try {
//            sensorOrientation = cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SENSOR_ORIENTATION);
//        } catch (CameraAccessException e) {
//            throw new RuntimeException(e);
//        }
//
//        if (isFrontFacing) {
//            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
//        } else { // back-facing
//            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
//        }
//
//        return rotationCompensation;
//    }








    @Override
    public void onClickSkip() {
        startActivity(ItemsPaymentActivity.getStartIntent(this, mobileNumber));
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        finish();
    }

    GetOffersNowResponse getOffersNowResponse;

    @Override
    public void onSuccesGetOffersNowApi(GetOffersNowResponse getOffersNowResponse) {
        this.getOffersNowResponse = getOffersNowResponse;
//        if (getOffersNowResponse != null && getOffersNowResponse.getOffersNow() != null && getOffersNowResponse.getOffersNow().size() > 0) {
//            for (GetOffersNowResponse.OffersNow offersNow : getOffersNowResponse.getOffersNow()) {
//                if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 0) {
//                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowOne);
//                } else if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 1) {
//                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowTwo);
//                } else if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 2) {
//                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowThree);
//                } else if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 3) {
//                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowFour);
//                }
//            }
//        }
    }

    @Override
    public void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse) {
        this.feedbackSystemResponse = feedbackSystemResponse;
        if (feedbackSystemResponse != null) {
            if (feedbackSystemResponse.getCustomerScreen() != null && feedbackSystemResponse.getCustomerScreen().getBillNumber() != null) {
                this.mobileNumber = feedbackSystemResponse.getCustomerScreen().getBillNumber();
            }
            if (Objects.requireNonNull(feedbackSystemResponse).getIspaymentScreen()) {
                startActivity(ItemsPaymentActivity.getStartIntent(this, mobileNumber));
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
            if (feedbackSystemResponse != null) {
                if (!feedbackSystemResponse.getIsPrescriptionScan()) {
                    getController().feedbakSystemApiCall();
                }
            } else {
                getController().feedbakSystemApiCall();
            }
        }
    };

    @Override
    protected void onPause() {
        recyclerViewScrollerHandler.removeCallbacks(recyclerViewScrollerRunnable);
        feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
        super.onPause();
        stopFrameCapture();
//        faceDetectors.stopBackgroundThread();
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    protected void onResume() {
        List<String> scannedPrescriptionsPathList = new ArrayList<>();
        getDateManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
        offersNowBinding.setStoreName(getDataManager().getStoreName());
        feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
//        feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
        getController().feedbakSystemApiCall();
        super.onResume();
        startFrameCapture();
//        faceDetectors.startBackgroundThread();
    }

    private SessionManager getDateManager() {
        return new SessionManager(this);
    }

    @Override
    public void onClickRefreshIcon() {
        CommonUtils.showDialog(this, "Please Wait...");
        getController().feedbakSystemApiCall();
    }

    @Override
    public void onClickSettingIcon() {
        AccessKeyDialog accesskeyDialog = new AccessKeyDialog(OffersNowActivity.this);
        accesskeyDialog.setOffersNowCallback(this);
        accesskeyDialog.onClickSubmit(v1 -> {
            accesskeyDialog.listener();
            if (accesskeyDialog.validate()) {
                startActivityForResult(StoreSetupActivity.getStartIntent(OffersNowActivity.this), AppConstants.STORE_SETUP_ACTIVITY_CODE);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                accesskeyDialog.dismiss();
            }
        });
        accesskeyDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 999) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, you can start camera operations
//                initializeCamera();
            } else {
                // Permission denied, you can disable the functionality that depends on this permission.
            }
        }
    }

    @Override
    public void onAccessDialogDismiss() {
        if (getDataManager().getSiteId().equalsIgnoreCase("") && getDataManager().getTerminalId().equalsIgnoreCase("")) {
            finish();
        }
    }


    DcOffersNowResponse dcOffersNowResponse;
    List<String> imagesList;

    @Override
    public void onSuccesDcOffersNowApi(DcOffersNowResponse dcOffersNowResponse) {
        this.dcOffersNowResponse = dcOffersNowResponse;
        imagesList = new ArrayList<String>();
        for (DcOffersNowResponse.Data.ListData.Row rows : dcOffersNowResponse.getData().getListData().getRows()) {
            String excepSites = rows.getExceptionSites();
            String k[] = excepSites.split(",");
            boolean isStoreid = false;
            for (int i = 0; i < k.length; i++) {
                if (getDataManager().getSiteId().equals(k[i])) {
                    isStoreid = true;
                }
            }
            if (!isStoreid) {
                for (DcOffersNowResponse.Data.ListData.Row.PosMediaLibrary posMedia : rows.getPosMediaLibrary()) {
                    for (DcOffersNowResponse.Data.ListData.Row.PosMediaLibrary.File filePath : posMedia.getFile()) {
                        imagesList.add(filePath.getPath());
                    }
                }
            }
        }

        if (imagesList != null && imagesList.size() > 0) {
            ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(OffersNowActivity.this, imagesList);
            offersNowBinding.imagesRcv.setAdapter(imageSlideAdapter);
            layoutManager = new LinearLayoutManager(OffersNowActivity.this, LinearLayoutManager.HORIZONTAL, false);
            offersNowBinding.imagesRcv.setLayoutManager(layoutManager);
            if (imagesList.size() > 4) {
                recyclerViewScrollerHandler.removeCallbacks(recyclerViewScrollerRunnable);
                recyclerViewScrollerHandler.postDelayed(recyclerViewScrollerRunnable, 5000);
            }


           /* offersNowBinding.imagesRcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int itemCount = layoutManager.getItemCount();
                    for (int i = 0; i < itemCount; i++) {
                        View view = layoutManager.findViewByPosition(i);
                        if (view != null) {
                            if (i == lastVisibleItemPosition || i == firstVisibleItemPosition) {
                                view.setScaleX(0.8f);
                                view.setScaleY(0.8f);
                                view.setAlpha(0.3f);
                            } else {
                                view.setScaleX(1.0f);
                                view.setScaleY(1.0f);
                                view.setAlpha(1.0f);
                            }
                        }
                    }
                }
            });

            Handler handler = new Handler();
            Runnable scrollRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isAutoScrolling) {
                        if (currentIndex < imagesList.size() - 1) {
                            currentIndex++;
                        } else {
                            currentIndex = 0;
                        }
                        smoothScrollToPosition(currentIndex);
                    }
                    handler.postDelayed(this, 10000);
                }
            };
            handler.postDelayed(scrollRunnable, 10000);*/

            /*offersNowBinding.offersNowOne.setVisibility(View.INVISIBLE);
            offersNowBinding.offersNowTwo.setVisibility(View.INVISIBLE);
            offersNowBinding.offersNowThree.setVisibility(View.INVISIBLE);
            offersNowBinding.offersNowFour.setVisibility(View.INVISIBLE);
            for (int i = 0; i < imagesList.size(); i++) {
                if (i == 0) {
                    Glide.with(this).load(Uri.parse(AppConstants.DC_CODE_IMAGE_BASEURL + imagesList.get(i))).into(offersNowBinding.offersNowOne);
                    offersNowBinding.offersNowOne.setVisibility(View.VISIBLE);
                } else if (i == 1) {
                    Glide.with(this).load(Uri.parse(AppConstants.DC_CODE_IMAGE_BASEURL + imagesList.get(i))).into(offersNowBinding.offersNowTwo);
                    offersNowBinding.offersNowTwo.setVisibility(View.VISIBLE);
                } else if (i == 2) {
                    Glide.with(this).load(Uri.parse(AppConstants.DC_CODE_IMAGE_BASEURL + imagesList.get(i))).into(offersNowBinding.offersNowThree);
                    offersNowBinding.offersNowThree.setVisibility(View.VISIBLE);
                } else if (i == 3) {
                    Glide.with(this).load(Uri.parse(AppConstants.DC_CODE_IMAGE_BASEURL + imagesList.get(i))).into(offersNowBinding.offersNowFour);
                    offersNowBinding.offersNowFour.setVisibility(View.VISIBLE);
                }
            }*/
        } else {
//            offersNowBinding.offersNowOne.setVisibility(View.INVISIBLE);
//            offersNowBinding.offersNowTwo.setVisibility(View.INVISIBLE);
//            offersNowBinding.offersNowThree.setVisibility(View.INVISIBLE);
//            offersNowBinding.offersNowFour.setVisibility(View.INVISIBLE);
        }
    }

    Handler recyclerViewScrollerHandler = new Handler();
    Runnable recyclerViewScrollerRunnable = new Runnable() {
        @Override
        public void run() {
            LinearLayoutManager layoutManager = (LinearLayoutManager) offersNowBinding.imagesRcv.getLayoutManager();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if ((lastVisibleItemPosition + 1) < imagesList.size()) {
                smoothScrollToPosition(lastVisibleItemPosition+1);
                recyclerViewScrollerHandler.removeCallbacks(recyclerViewScrollerRunnable);
                recyclerViewScrollerHandler.postDelayed(recyclerViewScrollerRunnable, 5000);
            } else {
                smoothScrollToPosition(0);
                recyclerViewScrollerHandler.removeCallbacks(recyclerViewScrollerRunnable);
                recyclerViewScrollerHandler.postDelayed(recyclerViewScrollerRunnable, 10000);
            }
        }
    };

    private void smoothScrollToPosition(int currentIndex) {
        CustomSmoothScroller customSmoothScroller = new CustomSmoothScroller(OffersNowActivity.this);
        customSmoothScroller.setTargetPosition(currentIndex);
        layoutManager.startSmoothScroll(customSmoothScroller);
    }

    @Override
    public void onFailureDcOffersNowApi() {

    }
    Dialog dialog;
    public void openDialogBox(Bitmap image, ZeroCodeApiModelResponse response, File file){

         dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogSuccessFaceRecogBinding feedBackbinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_success_face_recog, null, false);
        dialog.setContentView(feedBackbinding.getRoot());
//            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);

        feedBackbinding.nameF.setVisibility(View.GONE);
        feedBackbinding.phoneNoF.setVisibility(View.GONE);
        feedBackbinding.userName.setVisibility(View.VISIBLE);
        feedBackbinding.userPhoneNo.setVisibility(View.VISIBLE);
        feedBackbinding.warningImage.setVisibility(View.GONE);
        feedBackbinding.yourDetailsNotFound.setVisibility(View.GONE);
        feedBackbinding.warningImage.setVisibility(View.GONE);
        feedBackbinding.verifiedYourDetails.setVisibility(View.VISIBLE);
        feedBackbinding.fillYourDetailsText.setVisibility(View.GONE);

//            feedBackbinding.closeWhiteRating.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                }
//            });



        feedBackbinding.personImage.setImageBitmap(image);

        feedBackbinding.onClickContinueF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offersNowBinding.textureViewLayout.setVisibility(View.VISIBLE);
                offersNowBinding.offersLayout.setVisibility(View.GONE);
                dialog.dismiss();

//                offersNowBinding.textureView.setVisibility(View.GONE);
//                offersNowBinding.offersLayout.setVisibility(View.VISIBLE);
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//                startFrameCapture();
                if(feedBackbinding.phoneNoF.getText().toString().isEmpty() || feedBackbinding.nameF.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    getController().zeroCodeApiCall(file, feedBackbinding.nameF.getText().toString(), image);
//                    feedBackbinding.nameF.setVisibility(View.GONE);
//                    feedBackbinding.phoneNoF.setVisibility(View.GONE);
//                    feedBackbinding.userName.setVisibility(View.VISIBLE);
//                    feedBackbinding.userPhoneNo.setVisibility(View.VISIBLE);
//                    feedBackbinding.warningImage.setVisibility(View.GONE);
//                    feedBackbinding.yourDetailsNotFound.setVisibility(View.GONE);
//                    feedBackbinding.warningImage.setVisibility(View.GONE);
//                    feedBackbinding.verifiedYourDetails.setVisibility(View.VISIBLE);
//                    feedBackbinding.fillYourDetailsText.setVisibility(View.GONE);

//                    dialog.dismiss();
                }
            }
        });

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
        dialog.show();

    }

    @Override
    public void onClickCapture() {
        offersNowBinding.textureViewLayout.setVisibility(View.VISIBLE);
        offersNowBinding.offersLayout.setVisibility(View.GONE);

    }

    @Override
    public void onSuccessMultipartResponse(ZeroCodeApiModelResponse response, Bitmap image, File file) {
        if(response!=null){
            Toast.makeText(this ,response.getMessage(), Toast.LENGTH_SHORT).show();
           Toast.makeText(this ,response.getName(), Toast.LENGTH_SHORT).show();


           openDialogBox(image, response, file);
        }
        CommonUtils.hideDialog();
    }
//
//    @Override
//    public void onSuccessSavedImage(Bitmap bitmap) {
//        Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        DialogForShowingImageBinding feedBackbinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_for_showing_image, null, false);
//        dialog.setContentView(feedBackbinding.getRoot());
////            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialog.setCancelable(true);
//
////            feedBackbinding.closeWhiteRating.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    dialog.dismiss();
////                }
////            });
//
//
////        Picasso.get().load(String.valueOf(imageData)).into(feedBackbinding.personImage);
////        feedBackbinding.personImage.setImageBitmap(Bitmap.createScaledBitmap(imageData, imageData.getWidth(), imageData.getHeight(), false));
//        feedBackbinding.personImage.setImageBitmap(bitmap);
//
//
//        Window window = dialog.getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//            layoutParams.copyFrom(window.getAttributes());
//            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//            window.setAttributes(layoutParams);
//        }
//        dialog.show();
//
//    }


    public File convertByteArrayToFile(Context context, byte[] byteArray, String fileName) {
        // Create a file in the app's internal storage directory
        File file = new File(context.getFilesDir(), fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Handle this appropriately in your code
        }

        return file;
    }

    @Override
    public void onFailureMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.STORE_SETUP_ACTIVITY_CODE) {
                if (getDataManager().getSiteId().equalsIgnoreCase("") && getDataManager().getTerminalId().equalsIgnoreCase("")) {
                    finish();
                } else {
                    offersNowBinding.setIsConfigurationAvailable(false);
                    getController().feedbakSystemApiCall();
                    getController().getDcOffersNowApi(getDataManager().getDcCode());
                }
            }
        }
    }

    private OffersNowActivityController getController() {
        return new OffersNowActivityController(this, this);
    }
}