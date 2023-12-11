package com.thresholdsoft.apollofeedback.ui.offersnow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Build.VERSION;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.thresholdsoft.apollofeedback.R;

import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CameraActivity extends AppCompatActivity {
    @Nullable
    private File imageFromCameraFile;
    private String fileNameForCompressedImage;

    @Nullable
    public final File getImageFromCameraFile() {
        return this.imageFromCameraFile;
    }

    public final void setImageFromCameraFile(@Nullable File var1) {
        this.imageFromCameraFile = var1;
    }

    @SuppressLint("ResourceType")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_camera_activityy);
        if (!checkPermission()) {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissions(999);
            }
        } else {

            openCamera();

        }
    }

    private final boolean checkPermission() {
        return VERSION.SDK_INT >= 33 ? ContextCompat.checkSelfPermission((Context)this, "android.permission.CAMERA") == 0 && ContextCompat.checkSelfPermission((Context)this, "android.permission.READ_MEDIA_IMAGES") == 0 && ContextCompat.checkSelfPermission((Context)this, "android.permission.READ_MEDIA_AUDIO") == 0 && ContextCompat.checkSelfPermission((Context)this, "android.permission.READ_MEDIA_VIDEO") == 0 : ContextCompat.checkSelfPermission((Context)this, "android.permission.READ_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission((Context)this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission((Context)this, "android.permission.CAMERA") == 0;
    }

    @RequiresApi(23)
    private final void askPermissions(int PermissonCode) {
        if (VERSION.SDK_INT >= 33) {
            this.requestPermissions(new String[]{"android.permission.READ_MEDIA_AUDIO", "android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.CAMERA"}, PermissonCode);
        } else {
            this.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, PermissonCode);
        }

    }

    @SuppressLint("ResourceType")
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        Intrinsics.checkNotNullParameter(grantResults, "grantResults");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 999:
                boolean var6 = false;
                if (grantResults.length != 0 && grantResults[0] == 0) {
                    this.openCamera();
                } else {
                    Context var10000 = this.getApplicationContext();
                    Context var10001 = this.getApplicationContext();
                    Intrinsics.checkNotNullExpressionValue(var10001, "applicationContext");
                    Resources var7 = var10001.getResources();
                    Toast.makeText(var10000, (CharSequence)(var7 != null ? var7.getString(1900034) : null), 0).show();
                }
            default:
        }
    }

    private final void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Context var10003 = this.getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(var10003, "applicationContext");
        this.imageFromCameraFile = new File(var10003.getCacheDir(), System.currentTimeMillis() + ".jpg");
        this.fileNameForCompressedImage = System.currentTimeMillis() + ".jpg";
        if (VERSION.SDK_INT < 24) {
            intent.putExtra("output", (Parcelable)Uri.fromFile(this.imageFromCameraFile));
        } else {
            Context var10000 = this.getApplicationContext();
            StringBuilder var10001 = new StringBuilder();
            Context var10002 = this.getApplicationContext();
            Intrinsics.checkNotNullExpressionValue(var10002, "applicationContext");
            String var3 = var10001.append(var10002.getPackageName()).append(".provider").toString();
            File var4 = this.imageFromCameraFile;
            Intrinsics.checkNotNull(var4);
            Uri photoUri = FileProvider.getUriForFile(var10000, var3, var4);
            intent.putExtra("output", (Parcelable)photoUri);
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivityForResult(intent, 999);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @RequiresApi(29)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && this.imageFromCameraFile != null && resultCode == -1) {
            Toast.makeText((Context)this, (CharSequence)"Got Image camera file", Toast.LENGTH_SHORT).show();
        } else if (resultCode == 0) {
            Toast.makeText(this.getApplicationContext(), (CharSequence)"Cancelled",Toast.LENGTH_SHORT).show();
        }

    }
}
