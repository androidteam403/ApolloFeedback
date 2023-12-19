package com.thresholdsoft.apollofeedback.ui.offersnow;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.TextToSpeech;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
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
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class OffersNowActivity extends BaseActivity implements OffersNowActivityCallback {
    int currentIndex = 0;
    boolean isAutoScrolling = true;
    LinearLayoutManager layoutManager;
    private ActivityOffersNowBinding offersNowBinding;
    private FeedbackSystemResponse feedbackSystemResponse;
    Button b;
    private CameraDevice cameraDevice;

    private boolean isFaceDetected = false;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private ImageReader imageReader;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private TextToSpeech textToSpeech;
    private com.google.mlkit.vision.face.FaceDetector faceDetector;

    public static Intent getStartIntent(Context mContext) {
        Intent intent = new Intent(mContext, OffersNowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    private String mobileNumber;

    SurfaceHolder.Callback surfaceHolderCallbacks = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            closeCamera();
            startBackgroundThread();
            openCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopBackgroundThread();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offersNowBinding = DataBindingUtil.setContentView(this, R.layout.activity_offers_now);
        setUp();
    }

    private void setUp() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                textToSpeech.setSpeechRate(0.8f);
            }
        });
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

        SurfaceHolder surfaceHolder = offersNowBinding.surfaceView.getHolder();


        setupFaceDetector();
    }


    private void setupFaceDetector() {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        faceDetector = FaceDetection.getClient(options);
    }

    Image image;

    private void openCamera() {
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[1];
            CameraCharacteristics characteristics = null;
            try {
                characteristics = manager.getCameraCharacteristics(cameraId);
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }

            Size[] jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    .getOutputSizes(ImageFormat.JPEG);

            int width = 640; // Default width
            int height = 480; // Default height
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
//                    if(!isFaceDetected){
                    image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

                        detectFaces(bitmapImage); // Assuming you have a detectFaces method for processing
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
//                    }

                }
            };

            imageReader.setOnImageAvailableListener(readerListener, backgroundHandler);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    createCameraPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close(); // handle camera disconnection
                    closeCamera();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close(); // handle camera error
                    closeCamera();
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void createCameraPreview() {
        try {
            Surface surface = offersNowBinding.surfaceView.getHolder().getSurface();
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (cameraDevice == null) return;
                    cameraCaptureSession = session;
                    updatePreview();
                    startFaceDetection();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (cameraDevice == null) return;
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("Camera Background");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void captureImage() {
        if (cameraDevice == null) return;
        try {
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            cameraCaptureSession.capture(captureBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    // Image captured, you can process the image here
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    Handler faceDetectionHandler;

    private void startFaceDetection() {
        faceDetectionHandler = new Handler();
        Runnable faceDetectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (cameraDevice != null) {
                    captureImage(); // Capture an image for processing
                }
                // Schedule the next check after a short delay
                if (!isFaceDetected) {
                    faceDetectionHandler.postDelayed(this, 6000);
                } else {
                    stopFrameCapture();
                }
                // Adjust time delay as needed
            }
        };
        faceDetectionHandler.post(faceDetectionRunnable);
    }

    private void stopFrameCapture() {
        if (faceDetectionHandler != null) {
            faceDetectionHandler.removeCallbacksAndMessages(null);
        }
    }


    private void detectFaces(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        faceDetector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty() && !isFaceDetected) {
                        isFaceDetected = true;
                        for (Face face : faces) {
                            Rect boundingBox = face.getBoundingBox();
//                            Bitmap croppedBitmap = cropBitmap(bitmap, boundingBox);
//                            runOnUiThread(() -> {
                            ; // Initialize your bitmap

// Specify the output file path. Example for internal storage:


                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                            String filename = "JPEG_" + timeStamp + ".jpg";
                            File outputFile = new File(getApplicationContext().getFilesDir(), filename); // context is your Activity or Application context

                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(outputFile);

                                // Compress the bitmap to JPEG format and write it to the output stream
                                // Quality is set to 100 (highest)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                Toast.makeText(this, "Face detected", Toast.LENGTH_SHORT).show();
                                showDialogs(this, "Please Wait...");

                                stopBackgroundThread();
//                                    closeCamera();
                                getController().zeroCodeApiCallWithoutName(outputFile, bitmap);

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

                            stopFrameCapture();


                            // Assuming you have an ImageView with the ID imageView
//                            openDialogBox(croppedBitmap);


//                            });
//                        Toast.makeText(this, "No. of Faces found: "+ faces.size(), Toast.LENGTH_SHORT).show();
                            // Handle detected faces
                            // You can crop the bitmap around the face.getBoundingBox() if needed
                        }


                    } else {
                        captureImage();
//                        Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "ERROR!", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void onClickSkip() {
//        startActivity(ItemsPaymentActivity.getStartIntent(this, mobileNumber));
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//        finish();
    }

    Dialog dialog;
    //    Dialog detailsNotFound;
    DialogSuccessFaceRecogBinding feedBackbinding;
//    DialogDetailsNotFoundBinding dialogDetailsNotFoundBinding;


    public void openDialogBox(Bitmap image, ZeroCodeApiModelResponse responses, File file, ZeroCodeApiModelResponse response) {
        if (dialog != null) {
            dialog.dismiss();
        }

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        feedBackbinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_success_face_recog, null, false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        dialog.setContentView(feedBackbinding.getRoot());
        dialog.setCancelable(false);
//            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if ((responses.getMessage().equals("Image match found")) || (responses.getMessage().equals("Image, Added to traning data"))) {
            feedBackbinding.nameF.setVisibility(View.GONE);
            feedBackbinding.phoneNoF.setVisibility(View.GONE);
            feedBackbinding.userName.setVisibility(View.VISIBLE);
            feedBackbinding.userPhoneNo.setVisibility(View.VISIBLE);
            feedBackbinding.warningImage.setVisibility(View.GONE);
            feedBackbinding.yourDetailsNotFound.setVisibility(View.GONE);
            feedBackbinding.warningImage.setVisibility(View.GONE);
            feedBackbinding.verifiedYourDetails.setVisibility(View.VISIBLE);
            feedBackbinding.fillYourDetailsText.setVisibility(View.GONE);

            String speechMessage = "";

            String input = responses.getName();
            if (input.contains("-")) {
                String[] parts = input.split("-");
                String name = parts[0];
                String number = parts[1];
                feedBackbinding.userName.setText(name);
                feedBackbinding.userPhoneNo.setText(number);
                speechMessage = "Hi" + name + " " + CommonUtils.getTimeFromAndroid() + "Welcome to apollo pharmacy.";
                textToSpeech.speak(speechMessage, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                feedBackbinding.userName.setText(input);
                speechMessage = "Hi" + input + " " + CommonUtils.getTimeFromAndroid() + "Welcome to apollo pharmacy.";
                textToSpeech.speak(speechMessage, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        } else {
            feedBackbinding.nameF.setVisibility(View.VISIBLE);
            feedBackbinding.phoneNoF.setVisibility(View.VISIBLE);
            feedBackbinding.userName.setVisibility(View.GONE);
            feedBackbinding.userPhoneNo.setVisibility(View.GONE);
            feedBackbinding.warningImage.setVisibility(View.VISIBLE);
            feedBackbinding.yourDetailsNotFound.setVisibility(View.VISIBLE);
            feedBackbinding.warningImage.setVisibility(View.VISIBLE);
            feedBackbinding.verifiedYourDetails.setVisibility(View.GONE);
            feedBackbinding.fillYourDetailsText.setVisibility(View.VISIBLE);
        }

        feedBackbinding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopBackgroundThread();
                offersNowBinding.parentView.removeView(offersNowBinding.surfaceView);
                //                    if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
//                        Canvas canvas = offersNowBinding.surfaceView.getHolder().lockCanvas();
//                        canvas.drawColor(Color.BLACK); // or any other color
//                        offersNowBinding.surfaceView.getHolder().unlockCanvasAndPost(canvas);
//                    }
                closeCamera();
                offersNowBinding.textureViewLayout.setVisibility(View.GONE);
                offersNowBinding.offersLayout.setVisibility(View.VISIBLE);
                offersNowBinding.imageCaptureBtn.setVisibility(View.VISIBLE);
                offersNowBinding.imagesRcv.setVisibility(View.VISIBLE);

                dialog.dismiss();
            }

        });


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
                if ((responses.getMessage().equals("Image match found")) || (responses.getMessage().equals("Image, Added to training data"))
                        || (responses.getMessage().equals("Image, Added to traning data"))) {
//                    if (null != cameraDevice) {
//                        cameraDevice.close();
//                        cameraDevice = null;
//                    }
                    stopBackgroundThread();
                    offersNowBinding.parentView.removeView(offersNowBinding.surfaceView);
//                    if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
//                        Canvas canvas = offersNowBinding.surfaceView.getHolder().lockCanvas();
//                        canvas.drawColor(Color.BLACK); // or any other color
//                        offersNowBinding.surfaceView.getHolder().unlockCanvasAndPost(canvas);
//                    }
                    closeCamera();
                    offersNowBinding.textureViewLayout.setVisibility(View.GONE);
                    offersNowBinding.offersLayout.setVisibility(View.VISIBLE);
                    offersNowBinding.imageCaptureBtn.setVisibility(View.VISIBLE);
                    offersNowBinding.imagesRcv.setVisibility(View.VISIBLE);

                    dialog.dismiss();
//                    isFaceDetected=false;
                } else {
                    if (feedBackbinding.phoneNoF.getText().toString().isEmpty() || feedBackbinding.nameF.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    } else {
                        showDialogs(OffersNowActivity.this, "Please Wait...");
                        getController().zeroCodeApiCall(file, feedBackbinding.nameF.getText().toString() + "-" + feedBackbinding.phoneNoF.getText().toString(), image);
                    }

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
        hideDialogs();

    }
//    public void openDialogBoxNotFound(Bitmap image, ZeroCodeApiModelResponse responses, File file, ZeroCodeApiModelResponse response) {
//
//        detailsNotFound = new Dialog(this);
//        detailsNotFound.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogDetailsNotFoundBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_details_not_found, null, false);
//        detailsNotFound.setContentView(dialogDetailsNotFoundBinding.getRoot());
////            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        detailsNotFound.setCancelable(true);
//
//        if ((responses.getMessage().equals("Image match found")) || (responses.getMessage().equals("Image, Added to traning data"))) {
//            dialogDetailsNotFoundBinding.nameF.setVisibility(View.GONE);
//            dialogDetailsNotFoundBinding.phoneNoF.setVisibility(View.GONE);
//            dialogDetailsNotFoundBinding.userName.setVisibility(View.VISIBLE);
//            dialogDetailsNotFoundBinding.userPhoneNo.setVisibility(View.VISIBLE);
//            dialogDetailsNotFoundBinding.warningImage.setVisibility(View.GONE);
//            dialogDetailsNotFoundBinding.yourDetailsNotFound.setVisibility(View.GONE);
//            dialogDetailsNotFoundBinding.warningImage.setVisibility(View.GONE);
//            dialogDetailsNotFoundBinding.verifiedYourDetails.setVisibility(View.VISIBLE);
//            dialogDetailsNotFoundBinding.fillYourDetailsText.setVisibility(View.GONE);
//            String input = responses.getName();
//            if (input.contains("-")) {
//                String[] parts = input.split("-");
//                String name = parts[0];
//                String number = parts[1];
//                dialogDetailsNotFoundBinding.userName.setText(name);
//                dialogDetailsNotFoundBinding.userPhoneNo.setText(number);
//            } else {
//                dialogDetailsNotFoundBinding.userName.setText(input);
//            }
//
//        }
////        else {
////            feedBackbinding.nameF.setVisibility(View.VISIBLE);
////            feedBackbinding.phoneNoF.setVisibility(View.VISIBLE);
////            feedBackbinding.userName.setVisibility(View.GONE);
////            feedBackbinding.userPhoneNo.setVisibility(View.GONE);
////            feedBackbinding.warningImage.setVisibility(View.VISIBLE);
////            feedBackbinding.yourDetailsNotFound.setVisibility(View.VISIBLE);
////            feedBackbinding.warningImage.setVisibility(View.VISIBLE);
////            feedBackbinding.verifiedYourDetails.setVisibility(View.GONE);
////            feedBackbinding.fillYourDetailsText.setVisibility(View.VISIBLE);
////        }
//
//
////            feedBackbinding.closeWhiteRating.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    dialog.dismiss();
////                }
////            });
//
//
//        dialogDetailsNotFoundBinding.personImage.setImageBitmap(image);
//
//        dialogDetailsNotFoundBinding.onClickContinueF.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if ((responses.getMessage().equals("Image match found")) || (responses.getMessage().equals("Image, Added to training data"))) {
//                    if (null != cameraDevice) {
//                        cameraDevice.close();
//                        cameraDevice = null;
//                    }
//                    stopBackgroundThread();
//                    offersNowBinding.parentView.removeView(offersNowBinding.surfaceView);
//                    if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
//                        Canvas canvas = offersNowBinding.surfaceView.getHolder().lockCanvas();
//                        canvas.drawColor(Color.BLACK); // or any other color
//                        offersNowBinding.surfaceView.getHolder().unlockCanvasAndPost(canvas);
//                    }
//                    closeCamera();
//                    offersNowBinding.textureViewLayout.setVisibility(View.GONE);
//                    offersNowBinding.offersLayout.setVisibility(View.VISIBLE);
//                    offersNowBinding.imageCaptureBtn.setVisibility(View.VISIBLE);
//                    offersNowBinding.imagesRcv.setVisibility(View.GONE);
//
//                    detailsNotFound.dismiss();
//                }
////                else {
//                    if (dialogDetailsNotFoundBinding.phoneNoF.getText().toString().isEmpty() || dialogDetailsNotFoundBinding.nameF.getText().toString().isEmpty()) {
//                        Toast.makeText(getApplicationContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
//                    } else {
//                        showDialogs(getApplicationContext(), "Please Wait...");
//                        getController().zeroCodeApiCall(file, dialogDetailsNotFoundBinding.nameF.getText().toString() + "-" + dialogDetailsNotFoundBinding.phoneNoF.getText().toString(), image);
//                    }
////                }
//
//            }
//        });
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
//        hideDialogs();
//
//    }


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
//                startActivity(ItemsPaymentActivity.getStartIntent(this, mobileNumber));
//                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//                finish();
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

        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void closeCamera() {
//        clearSurfaceView();
        try {
            if (null != cameraCaptureSession) {
                cameraCaptureSession.close();
                cameraCaptureSession = null;
            }
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (null != imageReader) {
                imageReader.close();
                imageReader = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSurfaceView() {
        SurfaceHolder holder = offersNowBinding.surfaceView.getHolder();
        if (holder != null) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.BLACK); // Clear with black or any background color
                holder.unlockCanvasAndPost(canvas);
            }
        }
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
        isFaceDetected = false;
//        resetSurfaceView();
        startBackgroundThread();
        if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
            openCamera();
        } else {

            offersNowBinding.surfaceView.getHolder().addCallback(surfaceHolderCallbacks);


        }
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
                smoothScrollToPosition(lastVisibleItemPosition + 1);
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

    private void resetSurfaceView() {
        SurfaceHolder holder = offersNowBinding.surfaceView.getHolder();
        if (holder != null) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); // Clear the canvas
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void onClickCapture() {
        startBackgroundThread();
        isFaceDetected = false;
        offersNowBinding.textureViewLayout.setVisibility(View.VISIBLE);
        offersNowBinding.offersLayout.setVisibility(View.GONE);


        if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
            openCamera();
        } else {

            offersNowBinding.surfaceView.getHolder().addCallback(surfaceHolderCallbacks);

        }

//        if (!checkPermission()) {
//            askPermissions(777);
//            return;
//        } else {
//            openCameras();
//        }


    }

    ZeroCodeApiModelResponse responses;

    @Override
    public void onSuccessMultipartResponse(ZeroCodeApiModelResponse response, Bitmap image, File file) {
        if (response != null) {
            responses = response;
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, response.getName(), Toast.LENGTH_SHORT).show();

//            if((response.equals("Image match found")) || (response.equals("Image, Added to training data"))){
            openDialogBox(image, response, file, response);

        }

    }

    @Override
    public void onFailureMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
        } else {
            super.onBackPressed();
        }

        finishAffinity();
        System.exit(0);
//        isFaceDetected=false;
//        stopBackgroundThread();
//        offersNowBinding.parentView.removeView(offersNowBinding.surfaceView);
//       closeCamera();
//            offersNowBinding.textureViewLayout.setVisibility(View.GONE);
//            offersNowBinding.offersLayout.setVisibility(View.VISIBLE);
//            offersNowBinding.imageCaptureBtn.setVisibility(View.VISIBLE);
//            offersNowBinding.imagesRcv.setVisibility(View.VISIBLE);
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

    private static SpotsDialog spotsDialog;

    public static void showDialogs(Context mContext, String strMessage) {
        try {
            if (spotsDialog != null) {
                if (spotsDialog.isShowing()) {
                    spotsDialog.dismiss();
                }
            }
            spotsDialog = new SpotsDialog(mContext, strMessage, R.style.Custom, false, dialog -> {

            });
            Objects.requireNonNull(spotsDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            spotsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideDialogs() {
        try {
            if (spotsDialog != null)
                if (spotsDialog.isShowing())
                    spotsDialog.dismiss();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}