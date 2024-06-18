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
import android.hardware.usb.UsbDevice;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.herohan.uvcapp.CameraHelper;
import com.herohan.uvcapp.ICameraHelper;
import com.herohan.uvcapp.ImageCapture;
import com.serenegiant.utils.FileUtils;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.databinding.ActivityOffersNowBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogAudioRecordingBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogSuccessFaceRecogBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogUsbListBinding;
import com.thresholdsoft.apollofeedback.ui.itemspayment.ItemsPaymentActivity;
import com.thresholdsoft.apollofeedback.ui.offersnow.adapter.ImageSlideAdapter;
import com.thresholdsoft.apollofeedback.ui.offersnow.adapter.UsbWebcamAdapter;
import com.thresholdsoft.apollofeedback.ui.offersnow.dialog.AccessKeyDialog;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.DcOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.OneApolloAPITransactionResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.ZeroCodeApiModelResponse;
import com.thresholdsoft.apollofeedback.ui.storesetup.StoreSetupActivity;
import com.thresholdsoft.apollofeedback.utils.AppConstants;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import dmax.dialog.SpotsDialog;
import pl.droidsonroids.gif.GifDrawable;

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
    private FaceDetector faceDetector;
    private boolean isTrained = false;

    private boolean iswebCam;
    private int secondsRemainingSecond = 30;
    private int secondsRemainingFourth = 30;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private int lengthinSec;
    private boolean audioPlayed = false;
    private Dialog recordDialog;

    private int pauseLength;
    private boolean isFaceDetectionEnabled = true;


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
        iswebCam = getDataManager().isWebcam();
        checkReadWritePermissions();

        offersNowBinding.audioRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordingDialog();
            }
        });
        voiceRecordKeyTextChangedListener();
        offersNowBinding.voiceRecordKeyEdit.requestFocus();
        offersNowBinding.voiceRecordKeyEdit.setInputType(InputType.TYPE_NULL);
        setOnTouchListener();


    }

    /*.............................................Audio Recording................................................................*/

    private Handler handler = new Handler();
    private DialogAudioRecordingBinding dialogAudioRecordingBinding;
    private GifDrawable gifDrawable;

    public void recordingDialog() {
        isFaceDetectionEnabled = false;
        recordDialog = new Dialog(this);
        mStartPlaying = false;
        audioPlayed = false;
        dialogAudioRecordingBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_audio_recording, null, false);
        recordDialog.setContentView(dialogAudioRecordingBinding.getRoot());
        recordDialog.setCancelable(false);
        recordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogAudioRecordingBinding.setCallback(OffersNowActivity.this);
        dialogAudioRecordingBinding.progressBar.setProgress(0);
        secondsRemainingFirst = 9;
        readytoRecordDelayedHandler.postDelayed(readytoRecordDelayedRunnable, 1000);
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.sound_animation);
            dialogAudioRecordingBinding.imageGif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialogAudioRecordingBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readytoRecordDelayedHandler.removeCallbacks(readytoRecordDelayedRunnable);
                recordDialog.dismiss();
                isFaceDetectionEnabled = true;
            }
        });
        dialogAudioRecordingBinding.close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDialog.dismiss();
                stopRecording();
                handler.removeCallbacks(secondCircleRunnable);
                isFaceDetectionEnabled = true;
            }
        });
        dialogAudioRecordingBinding.close3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDialog.dismiss();
                isFaceDetectionEnabled = true;
            }
        });
        dialogAudioRecordingBinding.close4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDialog.dismiss();
                stopPlaying();
                stopAudioPlaying();
                handler.removeCallbacks(fourthCircleRunnable);
                isFaceDetectionEnabled = true;
            }
        });
        //  int durationInSec = Math.min(lengthinSec, 30 * 60);
        //String formattedTime = String.format("%02d:%02d", durationInSec / 60, durationInSec % 60);
        // dialogAudioRecordingBinding.recordingTime.setText(formattedTime);
        dialogAudioRecordingBinding.listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    dialogAudioRecordingBinding.listen.setVisibility(View.GONE);
                    dialogAudioRecordingBinding.listen2.setVisibility(View.VISIBLE);
                    mediaPlayer.pause();
                    pauseLength = mediaPlayer.getCurrentPosition();
                    handler.removeCallbacks(fourthCircleRunnable);
                    pauseGif();
                }
            }
        });
        dialogAudioRecordingBinding.listen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAudioRecordingBinding.listen.setVisibility(View.VISIBLE);
                dialogAudioRecordingBinding.listen2.setVisibility(View.GONE);
                mediaPlayer.seekTo(pauseLength);
                mediaPlayer.start();
                handler.post(fourthCircleRunnable);
                resumeGif();
            }
        });
        recordDialog.show();
    }

    private void pauseGif() {
        // dialogAudioRecordingBinding.imageGif.setFreezesAnimation(true);
        if (gifDrawable != null && gifDrawable.isPlaying()) {
            gifDrawable.pause();
        }
    }

    private void resumeGif() {
        //dialogAudioRecordingBinding.imageGif.setFreezesAnimation(false);
        if (gifDrawable != null && !gifDrawable.isPlaying()) {
            gifDrawable.start();
        }
    }


    private int secondsRemainingFirst = 9;

    private Handler readytoRecordDelayedHandler = new Handler();
    Runnable readytoRecordDelayedRunnable = new Runnable() {
        @Override
        public void run() {
            if (secondsRemainingFirst >= 0) {
                dialogAudioRecordingBinding.audioSec.setText(String.valueOf(secondsRemainingFirst));
                int progress = (int) (((float) (10 - secondsRemainingFirst) / 10) * 100);
                dialogAudioRecordingBinding.progressBar.setProgress(progress);

                if (secondsRemainingFirst > 0) {
                    secondsRemainingFirst--;
                    readytoRecordDelayedHandler.removeCallbacks(readytoRecordDelayedRunnable);
                    readytoRecordDelayedHandler.postDelayed(readytoRecordDelayedRunnable, 1000);
                } else {
                    readytoRecordDelayedHandler.removeCallbacks(readytoRecordDelayedRunnable);
                    dialogAudioRecordingBinding.progressLayout.setVisibility(View.GONE);
                    dialogAudioRecordingBinding.recordingLayout.setVisibility(View.VISIBLE);
                    /*startAudioRecording();*/
                    onCLickStartRecord();
                    mStartPlaying = false;
                    audioPlayed = false;
                    secondsRemainingSecond = 30;
                    handler.post(secondCircleRunnable);
                }
            }
        }
    };

    @Override
    public void onClickAudioPlayorStop() {
        mStartPlaying = true;
        onAudioPlay(mStartPlaying);
        if (mStartPlaying) {
            dialogAudioRecordingBinding.submitRecLayout.setVisibility(View.GONE);
            dialogAudioRecordingBinding.listeningRecLayout.setVisibility(View.VISIBLE);
            secondsRemainingFourth = 30;
            handler.post(fourthCircleRunnable);
        } else {
            dialogAudioRecordingBinding.submitRecLayout.setVisibility(View.VISIBLE);
        }
        mStartPlaying = !mStartPlaying;
    }

    private void startAudioPlaying() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                lengthinSec = Math.min(mediaPlayer.getDuration() / 1000, 30);
                // dialogAudioRecordingBinding.recordingTime.setText(String.valueOf(lengthinSec));
                if (fileName != null) {
                    File audioFile = new File(fileName);
                    long fileSizeInBytes = audioFile.length();
                    long fileSizeInKB = fileSizeInBytes / 1024;

                    long fileSizeInMB = fileSizeInKB / 1024;
                    Log.d("AudioFileSize", "File size: " + fileSizeInKB + " MB");
                    showToast("File size: " + fileSizeInKB + " MB");
                }
            }
        });
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (!audioPlayed) {
                        mStartPlaying = false;
                        onAudioPlay(mStartPlaying);
                        audioPlayed = true;
                    }
                }
            });
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            Log.e("OFFERS_NOW_ACTIVITY", "Error initializing MediaPlayer: " + e.getMessage());
            showToast("Error initializing audio playback");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void onAudioPlay(boolean start) {
        if (start) {
            startAudioPlaying();
            stopPlayingHandler.removeCallbacks(stopPlayingRunnable);
            stopPlayingHandler.postDelayed(stopPlayingRunnable, 30000);
        } else {
            stopAudioPlaying();
        }
    }

    private void stopAudioPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(null);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        audioPlayed = false;
    }


    final Runnable secondCircleRunnable = new Runnable() {
        @Override
        public void run() {
            if (secondsRemainingSecond >= 0) {
                dialogAudioRecordingBinding.sec.setText(String.valueOf(secondsRemainingSecond));

                int progress = (int) (((float) (30 - secondsRemainingSecond) / 30) * 100);
                dialogAudioRecordingBinding.secProgress.setProgress(progress);
                dialogAudioRecordingBinding.fourthProgress.setProgress(progress);
                if (secondsRemainingSecond > 0) {
                    secondsRemainingSecond--;
                    handler.postDelayed(this, 1000);
                } else {
                    handler.removeCallbacks(secondCircleRunnable);
                    dialogAudioRecordingBinding.recordingLayout.setVisibility(View.GONE);
                    dialogAudioRecordingBinding.submitRecLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    final Runnable fourthCircleRunnable = new Runnable() {
        @Override
        public void run() {
            if (secondsRemainingFourth >= 0) {

                int progress = (int) (((float) (30 - secondsRemainingFourth) / 30) * 100);
                dialogAudioRecordingBinding.fourthProgress.setProgress(progress);
                if (secondsRemainingFourth > 0) {
                    secondsRemainingFourth--;
                    handler.postDelayed(this, 1000);
                } else {
                    handler.removeCallbacks(fourthCircleRunnable);
                    stopPlaying();
                    //stopAudioPlaying();
                    // dialogAudioRecordingBinding.listeningRecLayout.setVisibility(View.GONE);
                    audioPlayed = false;
                    recordDialog.dismiss();
                }
            }
        }
    };

    @Override
    public int calculateProgressBarProgress(String recordedTime) {
        if (recordedTime != null && !recordedTime.isEmpty()) {
            int totalTimeInSeconds = 300;
            int recordedTimeInSeconds = Integer.parseInt(recordedTime);
            return (int) (((float) recordedTimeInSeconds / totalTimeInSeconds) * 100);
        } else {
            return 0;
        }
    }

    /*...........................................................................................................*/
    //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
    private void checkReadWritePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                setUp();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA}, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                setUp();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    setUp();
                }
            }
        } else if (requestCode == 2) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    setUp();
                }
            }
        }
    }

    private void setUp() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                textToSpeech.setSpeechRate(.9f);
            }
        });
        offersNowBinding.setCallback(this);
        offersNowBinding.setIsWebCam(iswebCam);
        startBackgroundThread();
        isFaceDetected = false;
        offersNowBinding.textureViewLayout.setVisibility(View.VISIBLE);
        offersNowBinding.offersLayout.setVisibility(View.VISIBLE);
        if (!iswebCam) {
            if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
                openCamera();
            } else {
                offersNowBinding.surfaceView.getHolder().addCallback(surfaceHolderCallbacks);
            }
            setupFaceDetector();
        } else {
            initWebCamViews();
            setupFaceDetectorFromWebCam();
        }

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

    }


    Handler faceDetectionforDelayingHandler = new Handler();
    Runnable faceDetectionforDelayingRunnable = new Runnable() {
        @Override
        public void run() {
            openCameraDelayed();
            /*showDialogs(OffersNowActivity.this, "Please Wait...");
            stopBackgroundThread();
            getController().zeroCodeApiCallWithoutName(outputFile, bitmapTemp);*/
        }
    };

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
        faceDetectionforDelayingHandler.removeCallbacks(faceDetectionforDelayingRunnable);
        faceDetectionforDelayingHandler.postDelayed(faceDetectionforDelayingRunnable, 5000);
    }

    private final static int FRONT_CAMERA = 1;
    private final static int BACK_CAMERA = 0;

    private void openCameraDelayed() {
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }


        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[FRONT_CAMERA];
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

                        detectFaces(bitmapImage);
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

    /*private void createCameraPreview() {
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
    }*/
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

    File outputFile;
    Bitmap bitmapTemp;

    private void detectFaces(Bitmap bitmap) {
        if (!isFaceDetectionEnabled) {
            return;
        }
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
                            outputFile = new File(getApplicationContext().getFilesDir(), filename); // context is your Activity or Application context

                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(outputFile);

                                // Compress the bitmap to JPEG format and write it to the output stream
                                // Quality is set to 100 (highest)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                bitmapTemp = bitmap;
//                                Toast.makeText(this, "Face detected", Toast.LENGTH_SHORT).show();
                                /*faceDetectionforDelayingHandler.removeCallbacks(faceDetectionforDelayingRunnable);
                                faceDetectionforDelayingHandler.postDelayed(faceDetectionforDelayingRunnable, 10000);*/
                                showDialogs(this, "Please Wait...");
                                stopBackgroundThread();
                                closeCamera();
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

    private Bitmap bitmapImage = null;
    private boolean isImageDetected = false;
    Handler imageDetectedTimeOutHandler = new Handler();
    Runnable imageDetectedTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if (!iswebCam) {
                isImageDetected = false;
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                stopBackgroundThread();
                closeCamera();
                startBackgroundThread();
                isFaceDetected = false;
                if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
                    openCamera();
                } else {
                    offersNowBinding.surfaceView.getHolder().addCallback(surfaceHolderCallbacks);
                }
            }
        }
    };

    public void openDialogBox(Bitmap image, ZeroCodeApiModelResponse responses, File file, ZeroCodeApiModelResponse response, OneApolloAPITransactionResponse oneApolloAPITransactionResponse) {
        isImageDetected = true;
        imageDetectedTimeOutHandler.removeCallbacks(imageDetectedTimeOutRunnable);
        imageDetectedTimeOutHandler.postDelayed(imageDetectedTimeOutRunnable, 20000);
        if (dialog != null) {
            dialog.dismiss();
        }
        this.bitmapImage = image;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        feedBackbinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_success_face_recog, null, false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        dialog.setContentView(feedBackbinding.getRoot());
        dialog.setCancelable(false);
//            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (oneApolloAPITransactionResponse != null
                && oneApolloAPITransactionResponse.getRequestStatus() == 0
                && oneApolloAPITransactionResponse.getOneApolloProcessResult() != null) {
            feedBackbinding.availableCreditsLayout.setVisibility(View.VISIBLE);
            feedBackbinding.availablePoints.setText(oneApolloAPITransactionResponse.getOneApolloProcessResult().getAvailablePoints());
        } else {
            feedBackbinding.availableCreditsLayout.setVisibility(View.GONE);
        }
        if ((responses.getMessage().equals("Image match found")) || (responses.getMessage().equals("Image, Added to traning data"))) {
            isTrained = true;
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
                speechMessage = "Hi " + name + ", " + CommonUtils.getTimeFromAndroid() + "Welcome to apollo pharmacy.";
                textToSpeech.speak(speechMessage, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                feedBackbinding.userName.setText(input);
                speechMessage = "Hi " + input + ", " + CommonUtils.getTimeFromAndroid() + "Welcome to apollo pharmacy.";
                textToSpeech.speak(speechMessage, TextToSpeech.QUEUE_FLUSH, null, null);
            }
            dialog.show();
        } else {
            isTrained = false;
            /*feedBackbinding.nameF.setVisibility(View.VISIBLE);
            feedBackbinding.phoneNoF.setVisibility(View.VISIBLE);*/

            /*..................................................*/
            /*feedBackbinding.userName.setVisibility(View.GONE);
            feedBackbinding.userPhoneNo.setVisibility(View.GONE);
            feedBackbinding.warningImage.setVisibility(View.VISIBLE);
            feedBackbinding.yourDetailsNotFound.setVisibility(View.VISIBLE);
            feedBackbinding.warningImage.setVisibility(View.VISIBLE);
            feedBackbinding.verifiedYourDetails.setVisibility(View.GONE);
            feedBackbinding.fillYourDetailsText.setVisibility(View.VISIBLE);*/
        }

        feedBackbinding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopBackgroundThread();
//                offersNowBinding.parentView.removeView(offersNowBinding.surfaceView);
                //                    if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
//                        Canvas canvas = offersNowBinding.surfaceView.getHolder().lockCanvas();
//                        canvas.drawColor(Color.BLACK); // or any other color
//                        offersNowBinding.surfaceView.getHolder().unlockCanvasAndPost(canvas);
//                    }
                closeCamera();
//                offersNowBinding.textureViewLayout.setVisibility(View.GONE);
//                offersNowBinding.offersLayout.setVisibility(View.VISIBLE);
//                offersNowBinding.imageCaptureBtn.setVisibility(View.VISIBLE);
//                offersNowBinding.imagesRcv.setVisibility(View.VISIBLE);

                dialog.dismiss();
                startBackgroundThread();
                isFaceDetected = false;
//        offersNowBinding.textureViewLayout.setVisibility(View.VISIBLE);
//        offersNowBinding.offersLayout.setVisibility(View.GONE);


                if (!iswebCam) {
                    if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
                        openCamera();
                    } else {
                        offersNowBinding.surfaceView.getHolder().addCallback(surfaceHolderCallbacks);
                    }
                } else {
                    initCameraHelper();
                }
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
//                    offersNowBinding.parentView.removeView(offersNowBinding.surfaceView);
//                    if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
//                        Canvas canvas = offersNowBinding.surfaceView.getHolder().lockCanvas();
//                        canvas.drawColor(Color.BLACK); // or any other color
//                        offersNowBinding.surfaceView.getHolder().unlockCanvasAndPost(canvas);
//                    }
                    closeCamera();
//                    offersNowBinding.textureViewLayout.setVisibility(View.GONE);
//                    offersNowBinding.offersLayout.setVisibility(View.VISIBLE);
//                    offersNowBinding.imageCaptureBtn.setVisibility(View.VISIBLE);
//                    offersNowBinding.imagesRcv.setVisibility(View.VISIBLE);

                    dialog.dismiss();
                    startBackgroundThread();
                    isFaceDetected = false;
//        offersNowBinding.textureViewLayout.setVisibility(View.VISIBLE);
//        offersNowBinding.offersLayout.setVisibility(View.GONE);

                    if (!iswebCam) {
                        if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
                            openCamera();
                        } else {
                            offersNowBinding.surfaceView.getHolder().addCallback(surfaceHolderCallbacks);
                        }
                    }
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
                /*if (dialog != null && dialog.isShowing()) {*/
                if (bitmapImage != null && isImageDetected) {
                    startActivity(ItemsPaymentActivity.getStartIntent(this, mobileNumber, saveBitmap(bitmapImage), isTrained, fileName));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    finish();
                } else {
                    feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
                    feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
                }
                /*} else {
                    feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
                    feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
                }*/
            } else {
//                new Handler().postDelayed(() -> getController().feedbakSystemApiCall(), 5000);
                feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
                feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
            }
        }
    }

    public String saveBitmap(Bitmap bitmap) {
        String fileName = "customer.jpg";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
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
        faceDetectionforDelayingHandler.removeCallbacks(faceDetectionforDelayingRunnable);
        imageDetectedTimeOutHandler.removeCallbacks(imageDetectedTimeOutRunnable);
        closeCamera();
        stopBackgroundThread();

        //Audio record
        mStartPlaying = true;
        isRecording = false;
        stopRecordHandler.removeCallbacks(stopRecordRunnable);
        stopPlayingHandler.removeCallbacks(stopPlayingRunnable);
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
        getDataManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
        offersNowBinding.setStoreName(getDataManager().getStoreName());
        feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
//        feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
        getController().feedbakSystemApiCall();

        super.onResume();
        isFaceDetected = false;
//        resetSurfaceView();
        if (dialog != null && dialog.isShowing()) {
            stopBackgroundThread();
        } else {
            startBackgroundThread();
        }

        if (!iswebCam) {
            if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
                if (dialog != null && dialog.isShowing()) {
                    closeCamera();
                } else {
                    openCamera();
                }
            } else {
                offersNowBinding.surfaceView.getHolder().addCallback(surfaceHolderCallbacks);
            }
        }
    }

 /*public SessionManager getDataManager() {
        return new SessionManager(this);
    }*/

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
//        startBackgroundThread();
//        isFaceDetected=false;
////        offersNowBinding.textureViewLayout.setVisibility(View.VISIBLE);
////        offersNowBinding.offersLayout.setVisibility(View.GONE);
//
//
//        if (offersNowBinding.surfaceView.getHolder().getSurface().isValid()) {
//            openCamera();
//        } else {
//
//                offersNowBinding.surfaceView.getHolder().addCallback(surfaceHolderCallbacks);
//
//        }

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
            stopBackgroundThread();
            stopFrameCapture();
            responses = response;
            if ((responses.getMessage().equals("Image match found")) || (responses.getMessage().equals("Image, Added to traning data"))) {
                if (response.getName() != null && !response.getName().isEmpty()) {
                    if (response.getName().contains("-")) {
                        String[] splitName = response.getName().split("-");
                        String phoneNumber = splitName[1];
                        getController().oneApolloApiTransaction(image, file, phoneNumber);
                    } else {
                        openDialogBox(image, response, file, response, null);
                    }
                } else {
                    openDialogBox(image, response, file, response, null);
                }
            } else {
                openDialogBox(image, response, file, response, null);
            }
        }
    }

    @Override
    public void onFailureMultipartResponse(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        hideDialogs();
    }

    @Override
    public void onSuccessOneApolloApiTransaction(OneApolloAPITransactionResponse oneApolloAPITransactionResponse, Bitmap image, File file) {
        openDialogBox(image, responses, file, responses, oneApolloAPITransactionResponse);
    }

    @Override
    public void onFailureOneApolloApiTransaction(String message, Bitmap image, File file) {
        openDialogBox(image, responses, file, responses, null);
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

    //................................. Audio record.............................................................
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
            offersNowBinding.startRecord.setVisibility(View.GONE);
            offersNowBinding.startRecordGif.setVisibility(View.VISIBLE);
            recorder.prepare();
        } catch (IOException e) {
            Log.e("OFFERS_NOW_ACTIVITY", "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        isRecording = false;
        isFaceDetectionEnabled = false;
        offersNowBinding.startRecordGif.setVisibility(View.GONE);
        offersNowBinding.startRecord.setVisibility(View.VISIBLE);
        recorder.stop();
        recorder.release();
        recorder = null;
//        startRecord.setText("Start Record");
    }

    private void startPlaying() {
        player = new MediaPlayer();
        isFaceDetectionEnabled = false;
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
        if (player != null) {
            player.setOnCompletionListener(null);
            if (player.isPlaying()) {
                player.stop();
            }
            player.reset();
            player.release();
            player = null;
        }
        audioPlayed = false;
        isFaceDetectionEnabled = true;
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
    public void onSelectWebCam(UsbDevice usbDevice) {
        webCamUsbDeviceDialog.dismiss();
        selectDevice(usbDevice);
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
            offersNowBinding.play.setImageResource(R.drawable.play_icon);
            stopPlaying();
        }
    };

    @Override
    public void onClickPlayorStop() {
        onAudioPlay(mStartPlaying);
        if (mStartPlaying) {
            offersNowBinding.play.setImageResource(R.drawable.stop_icon);
        } else {
            offersNowBinding.play.setImageResource(R.drawable.play_icon);
        }
        mStartPlaying = !mStartPlaying;
    }

    @Override
    public void onStop() {
        super.onStop();

        clearCameraHelper();

        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }


    /*................................web cam.....................................*/
    private static final boolean DEBUG = true;
    private static final String TAG = OffersNowActivity.class.getSimpleName();
    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;
    private ICameraHelper mCameraHelper;
    private File webCamFile;

    private void initWebCamViews() {
//        mCameraViewMain = findViewById(R.id.svCameraViewMain);
        offersNowBinding.svCameraViewMain.setAspectRatio(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        offersNowBinding.svCameraViewMain.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (mCameraHelper != null) {
                    mCameraHelper.addSurface(holder.getSurface(), false);
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (mCameraHelper != null) {
                    mCameraHelper.removeSurface(holder.getSurface());
                }
            }
        });

    }

    private void setupFaceDetectorFromWebCam() {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        faceDetector = FaceDetection.getClient(options);
    }

    public void initCameraHelper() {
        if (DEBUG) Log.d(TAG, "initCameraHelper:");
        if (mCameraHelper == null) {
            mCameraHelper = new CameraHelper();
            mCameraHelper.setStateCallback(mStateListener);
        }
        takePictureWebCamHandler.removeCallbacks(takePictureWebCamRunnable);
        takePictureWebCamHandler.postDelayed(takePictureWebCamRunnable, 5000);
    }

    private void clearCameraHelper() {
        if (DEBUG) Log.d(TAG, "clearCameraHelper:");
        if (mCameraHelper != null) {
            mCameraHelper.release();
            mCameraHelper = null;
        }
        takePictureWebCamHandler.removeCallbacks(takePictureWebCamRunnable);
    }

    private void selectDevice(final UsbDevice device) {
        if (DEBUG) Log.v(TAG, "selectDevice:device=" + device.getDeviceName());
        mCameraHelper.selectDevice(device);
    }

    private final ICameraHelper.StateCallback mStateListener = new ICameraHelper.StateCallback() {
        @Override
        public void onAttach(UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onAttach:");
            selectDevice(device);
        }

        @Override
        public void onAttach(HashSet<UsbDevice> needNotifyDevices) {
            if (iswebCam) {
                List<UsbDevice> needNotifyDevicesList
                        = (List<UsbDevice>) needNotifyDevices.stream()
                        .collect(Collectors.toList());
                showUsbDevicesDialog(needNotifyDevicesList);
            }
        }

        @Override
        public void onDeviceOpen(UsbDevice device, boolean isFirstOpen) {
            if (DEBUG) Log.v(TAG, "onDeviceOpen:");
            mCameraHelper.openCamera();
        }

        @Override
        public void onCameraOpen(UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onCameraOpen:");

            mCameraHelper.startPreview();

            com.serenegiant.usb.Size size = mCameraHelper.getPreviewSize();
            if (size != null) {
                int width = size.width;
                int height = size.height;
                //auto aspect ratio
                offersNowBinding.svCameraViewMain.setAspectRatio(width, height);
            }

            mCameraHelper.addSurface(offersNowBinding.svCameraViewMain.getHolder().getSurface(), false);
            takePictureWebCamHandler.removeCallbacks(takePictureWebCamRunnable);
            takePictureWebCamHandler.postDelayed(takePictureWebCamRunnable, 5000);
        }

        @Override
        public void onCameraClose(UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onCameraClose:");

            if (mCameraHelper != null) {
                mCameraHelper.removeSurface(offersNowBinding.svCameraViewMain.getHolder().getSurface());
            }

            takePictureWebCamHandler.removeCallbacks(takePictureWebCamRunnable);
        }

        @Override
        public void onDeviceClose(UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onDeviceClose:");
        }

        @Override
        public void onDetach(UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onDetach:");
        }

        @Override
        public void onCancel(UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onCancel:");
        }

    };
    private Dialog webCamUsbDeviceDialog;

    private void showUsbDevicesDialog(List<UsbDevice> usbDevicesList) {
        webCamUsbDeviceDialog = new Dialog(this);
        DialogUsbListBinding dialogUsbListBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_usb_list, null, false);
        webCamUsbDeviceDialog.setContentView(dialogUsbListBinding.getRoot());
        webCamUsbDeviceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        webCamUsbDeviceDialog.setCancelable(false);
        UsbWebcamAdapter usbWebcamAdapter = new UsbWebcamAdapter(this, usbDevicesList, this);
        LinearLayoutManager mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        dialogUsbListBinding.usbCamRecyclerview.setLayoutManager(mManager);
        dialogUsbListBinding.usbCamRecyclerview.setAdapter(usbWebcamAdapter);

        webCamUsbDeviceDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initCameraHelper();
    }

    private void detectFacesFromWebCam(Bitmap bitmap) {
      /*  if (!isFaceDetectionEnabled) {
            return;
        }*/
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        faceDetector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        if (isFaceDetectionEnabled) {
                            showDialogs(this, "Please Wait...");
                            clearCameraHelper();
                            getController().zeroCodeApiCallWithoutName(webCamFile, bitmap);
                        } else {
                            takePictureWebCamHandler.removeCallbacks(takePictureWebCamRunnable);
                            takePictureWebCamHandler.postDelayed(takePictureWebCamRunnable, 5000);
                        }
                       /* for (Face face : faces) {
                            Toast.makeText(this, "face detected", Toast.LENGTH_SHORT).show();
                        }*/
                    } else {
                        /*Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show();*/
                        takePictureWebCamHandler.removeCallbacks(takePictureWebCamRunnable);
                        takePictureWebCamHandler.postDelayed(takePictureWebCamRunnable, 5000);

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "ERROR!", Toast.LENGTH_SHORT).show();
                });
    }

    Handler takePictureWebCamHandler = new Handler();
    Runnable takePictureWebCamRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCameraHelper != null) {

                webCamFile = FileUtils.getCaptureFile(OffersNowActivity.this, Environment.DIRECTORY_DCIM, ".jpg");
                ImageCapture.OutputFileOptions options =
                        new ImageCapture.OutputFileOptions.Builder(webCamFile).build();

//                ContentValues contentValues = new ContentValues();
//                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "NEW_IMAGE");
//                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//
//                ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(
//                        getContentResolver(),
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        contentValues).build();

//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(outputStream).build();

                mCameraHelper.takePicture(options, new ImageCapture.OnImageCaptureCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Bitmap bitmap = BitmapFactory.decodeFile(webCamFile.getAbsolutePath());
                        detectFacesFromWebCam(bitmap);
                    }

                    @Override
                    public void onError(int imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                        Toast.makeText(OffersNowActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    private String voiceRecordKey = "";

    private void voiceRecordKeyTextChangedListener() {
        offersNowBinding.voiceRecordKeyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && s.toString().length() > 0) {
                    voiceRecordKeyHandler.removeCallbacks(voiceRecordKeyRunnable);
                    voiceRecordKey = s.toString();
                    offersNowBinding.voiceRecordKeyEdit.setText("");
                    voiceRecordKeyHandler.postDelayed(voiceRecordKeyRunnable, 300);
                }
            }
        });
    }

    Handler voiceRecordKeyHandler = new Handler();
    Runnable voiceRecordKeyRunnable = new Runnable() {
        @Override
        public void run() {
            if (voiceRecordKey.equalsIgnoreCase(getDataManager().getVoiceRecordKey())) {
                recordingDialog();
            }
        }
    };

    private void setOnTouchListener() {
        offersNowBinding.parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                offersNowBinding.voiceRecordKeyEdit.requestFocus();
                return false;
            }
        });
    }
}
