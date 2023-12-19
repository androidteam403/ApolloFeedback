package com.thresholdsoft.apollofeedback.ui.offersnow.hiddencamera;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class HiddenCamera extends Service {
    private CameraDevice cameraDevice;
    private ImageReader imageReader;

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start Foreground Service Notification
        startForeground(1, buildForegroundNotification());

        // Setup Camera
        setupCamera(getApplicationContext());

        return START_NOT_STICKY;
    }
    public void initialize() {
        startBackgroundThread();
        // Other initialization code...
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
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


    public void cleanup() {
        stopBackgroundThread();
        // Other cleanup code...
    }
//    private void setupCamera() {
//        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        try {
//            String cameraId = manager.getCameraIdList()[0];
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                // Handle permission not granted case
//                handleCameraPermissionNotGranted();
//                return;
//            }
//            manager.openCamera(cameraId, stateCallback, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }

    private void setupCamera(Context context) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[0]; // Assuming you're using the first back-facing camera
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            // Handle device and camera sensor orientation
            int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            int deviceOrientation = windowManager.getDefaultDisplay().getRotation();
            boolean isLandscape = deviceOrientation == Surface.ROTATION_90 || deviceOrientation == Surface.ROTATION_270;
            int totalRotation = (sensorOrientation + deviceOrientation + 360) % 360;
            boolean swapDimension = totalRotation == 90 || totalRotation == 270;

            // Calculate the optimal size for camera preview
            Size[] jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    .getOutputSizes(ImageFormat.JPEG);

            // Choose optimal size based on width and height
            // (Modify this logic based on your requirement)
            int width = 640; // Default width
            int height = 480; // Default height
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            if (swapDimension) {
                int temp = width;
                width = height;
                height = temp;
            }

            // Configure ImageReader
            imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            // Set the OnImageAvailableListener and background handler here
            // ...

            // Open the camera
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Handle permission not granted case
                return;
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        int rotationCompensation = (sensorOrientation + deviceOrientation + 360) % 360;

        // Return the corresponding Surface rotation
        return ORIENTATIONS.get(rotationCompensation);
    }

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }


    private void handleCameraPermissionNotGranted() {
        // Send a broadcast or start an activity to request permission
        Intent intent = new Intent(this, PermissionRequestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Optionally stop the service
        stopSelf();
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            setupImageReader();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {

        }
    };

    private void setupImageReader() {
        // Set the ImageReader size and format
        imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 1);
        imageReader.setOnImageAvailableListener(reader -> {
            Image image = null;
            Bitmap bitmap = null;
            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

                    // Process the bitmap here
                    processBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (image != null) {
                    image.close();
                }
            }
        }, null);

        // Create a camera capture session to capture images
        createCameraCaptureSession();
    }

    private void processBitmap(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .build();
        FaceDetector detector = FaceDetection.getClient(options);

        detector.process(image)
                .addOnSuccessListener(faces -> {
                    Log.d("ImageProcessing", "Faces detected: " + faces.size());
                    // Process faces
                })
                .addOnFailureListener(e -> Log.e("ImageProcessing", "Face detection failed: " + e.getMessage()));
    }




    private void createCameraCaptureSession() {
        try {
            // Prepare the surfaces for the capture session
            Surface imageReaderSurface = imageReader.getSurface();

            // Create a list of surfaces to be used in capture session
            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(imageReaderSurface);

            // Create a capture session for the surfaces
            cameraDevice.createCaptureSession(surfaces,
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The session is ready to start capturing
                            try {
                                CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                                captureRequestBuilder.addTarget(imageReaderSurface);

                                // Set auto focus and flash if necessary
                                captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                                // Send a repeating capture request
                                cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Log.e("CameraService", "Configuration failed for CaptureSession");
                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private Notification buildForegroundNotification() {
        // Build and return a notification for the foreground service
        return null;
    }
}
