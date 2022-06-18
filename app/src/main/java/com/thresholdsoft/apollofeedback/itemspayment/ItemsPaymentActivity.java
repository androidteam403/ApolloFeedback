package com.thresholdsoft.apollofeedback.itemspayment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivityItemsPaymentBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogQrcodeBinding;
import com.thresholdsoft.apollofeedback.itemspayment.model.GetAdvertisementResponse;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ItemsPaymentActivity extends BaseActivity implements ItemsPaymentActivityCallback {

    private ActivityItemsPaymentBinding itemsPaymentBinding;


    public static Intent getStartIntent(Context mContext) {
        Intent intent = new Intent(mContext, ItemsPaymentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemsPaymentBinding = DataBindingUtil.setContentView(this, R.layout.activity_items_payment);
        setUp();
    }

    private void setUp() {
        itemsPaymentBinding.setCallback(this);
        getController().getAdvertisementApiCall();

    }

    @Override
    public void onClickContinuePayment() {
        Dialog qrCodeDialog = new Dialog(this, R.style.dialogcustomstyle);
        DialogQrcodeBinding dialogQrcodeBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_qrcode, null, false);
        qrCodeDialog.setContentView(dialogQrcodeBinding.getRoot());
        qrCodeDialog.setCancelable(false);
        dialogQrcodeBinding.closeIcon.setOnClickListener(view -> qrCodeDialog.dismiss());
        qrCodeGeneration("upi://pay?pa=APOLLOPREPROD@ybl&pn=APOLLOPREPROD&am=4.00&mam=4.00&tr=160021&tn=Payment%20for%20160021&mc=5311&mode=04&purpose=00", dialogQrcodeBinding, this);
        qrCodeDialog.show();
    }

    @Override
    public void onSuccessGetAdvertisementApi(GetAdvertisementResponse getAdvertisementResponse) {
        if (getAdvertisementResponse != null && getAdvertisementResponse.getAdvertisement() != null && getAdvertisementResponse.getAdvertisement().size() > 0) {
            for (GetAdvertisementResponse.Advertisement advertisement : getAdvertisementResponse.getAdvertisement()) {
                if (getAdvertisementResponse.getAdvertisement().indexOf(advertisement) == 0) {
                    Glide.with(this).load(Uri.parse(advertisement.getAdvertisementImage())).into(itemsPaymentBinding.advertisementOne);
                } else if (getAdvertisementResponse.getAdvertisement().indexOf(advertisement) == 1) {
                    Glide.with(this).load(Uri.parse(advertisement.getAdvertisementImage())).into(itemsPaymentBinding.advertisementTwo);
                }
            }
        }
    }

    @Override
    public void onFailureMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private ItemsPaymentActivityController getController() {
        return new ItemsPaymentActivityController(this, this);
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
//                generatePhonepeQrcodeBinding.qrlogo.setVisibility(View.VISIBLE);
                dialogQrcodeBinding.qrCodeImage.setImageBitmap(bitmap);
//                generatePhonepeQrcodeBinding.loadingPanel.setVisibility(View.GONE);
//                ActivityUtils.hideDialog();
            }
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
    }
}
