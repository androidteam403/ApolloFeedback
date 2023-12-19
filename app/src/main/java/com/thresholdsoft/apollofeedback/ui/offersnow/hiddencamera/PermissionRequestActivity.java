package com.thresholdsoft.apollofeedback.ui.offersnow.hiddencamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.thresholdsoft.apollofeedback.R;

public class PermissionRequestActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestCameraPermission();
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. Start the camera service again if necessary.
                startCameraService();
            } else {
                handlePermissionDenied();
                // Permission was denied. Handle the failure.
            }
        }
        finish(); // Close the activity
    }

    private void handlePermissionDenied() {
        // Show an explanation to the user
        // This should be customized based on your app's need
        new AlertDialog.Builder(this)
                .setTitle("Camera Permission Needed")
                .setMessage("This app needs the Camera permission to function. Please enable it in the app settings.")
                .setPositiveButton("App Settings", (dialog, which) -> openAppSettings())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void startCameraService() {
        Intent serviceIntent = new Intent(this, HiddenCamera.class);
        // Check if the service is not already running
        if (!isServiceRunning(HiddenCamera.class)) {
            startService(serviceIntent);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        }
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}