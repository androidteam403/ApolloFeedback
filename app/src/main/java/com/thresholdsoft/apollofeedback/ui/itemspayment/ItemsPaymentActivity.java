package com.thresholdsoft.apollofeedback.ui.itemspayment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.zxing.WriterException;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.databinding.ActivityItemsPaymentBinding;
import com.thresholdsoft.apollofeedback.databinding.DialogQrcodeBinding;
import com.thresholdsoft.apollofeedback.ui.feedback.FeedBackActivity;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.CrossShellResponse;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.GetAdvertisementResponse;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.UpsellCrosssellModel;
import com.thresholdsoft.apollofeedback.ui.offersnow.OffersNowActivity;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ItemsPaymentActivity extends BaseActivity implements ItemsPaymentActivityCallback {

    private ActivityItemsPaymentBinding itemsPaymentBinding;
    private static final String MOBILE_NUMBER = "MOBILE_NUMBER";

    public static Intent getStartIntent(Context mContext, String mobileNumber) {
        Intent intent = new Intent(mContext, ItemsPaymentActivity.class);
        intent.putExtra(MOBILE_NUMBER, mobileNumber);
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
//        getController().getAdvertisementApiCall();
        String mobileNumber = null;
        if (getIntent() != null) {
            mobileNumber = (String) getIntent().getStringExtra(MOBILE_NUMBER);
        }
        getController().feedbakSystemApiCall();
        getController().crossshellApiCall(mobileNumber);

    }

    private FeedbackSystemResponse feedbackSystemResponse;

    @Override
    public void onClickContinuePayment() {
//        Dialog qrCodeDialog = new Dialog(this, R.style.dialogcustomstyle);
//        DialogQrcodeBinding dialogQrcodeBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_qrcode, null, false);
//        qrCodeDialog.setContentView(dialogQrcodeBinding.getRoot());
//        qrCodeDialog.setCancelable(false);
//        dialogQrcodeBinding.closeIcon.setOnClickListener(view -> qrCodeDialog.dismiss());
//        qrCodeGeneration("upi://pay?pa=APOLLOPREPROD@ybl&pn=APOLLOPREPROD&am=4.00&mam=4.00&tr=160021&tn=Payment%20for%20160021&mc=5311&mode=04&purpose=00", dialogQrcodeBinding, this);
//        dialogQrcodeBinding.qrCodeImage.setOnClickListener(view -> {
//            qrCodeDialog.dismiss();
//            startActivity(FeedBackActivity.getStartIntent(ItemsPaymentActivity.this, feedbackSystemResponse));
//            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//            finish();
//        });
//        qrCodeDialog.show();
    }

    @Override
    public void onSuccessGetAdvertisementApi(GetAdvertisementResponse getAdvertisementResponse) {
//        if (getAdvertisementResponse != null && getAdvertisementResponse.getAdvertisement() != null && getAdvertisementResponse.getAdvertisement().size() > 0) {
//            for (GetAdvertisementResponse.Advertisement advertisement : getAdvertisementResponse.getAdvertisement()) {
//                if (getAdvertisementResponse.getAdvertisement().indexOf(advertisement) == 0) {
//                    Glide.with(this).load(Uri.parse(advertisement.getAdvertisementImage())).into(itemsPaymentBinding.advertisementOne);
//                } else if (getAdvertisementResponse.getAdvertisement().indexOf(advertisement) == 1) {
//                    Glide.with(this).load(Uri.parse(advertisement.getAdvertisementImage())).into(itemsPaymentBinding.advertisementTwo);
//                }
//            }
//        }
    }

    @Override
    public void onFailureMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                startActivity(OffersNowActivity.getStartIntent(ItemsPaymentActivity.this));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            } else if ((feedbackSystemResponse.getIspaymentScreen())) {
                itemsPaymentBinding.setModel(feedbackSystemResponse);
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
                            startActivity(FeedBackActivity.getStartIntent(ItemsPaymentActivity.this, feedbackSystemResponse));
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            finish();
                        });
                        qrCodeDialog.show();
                    }
                }
                new Handler().postDelayed(() -> getController().feedbakSystemApiCall(), 5000);
            } else if (feedbackSystemResponse.getIsfeedbackScreen()) {
                startActivity(FeedBackActivity.getStartIntent(ItemsPaymentActivity.this, feedbackSystemResponse));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            } else {
                new Handler().postDelayed(() -> getController().feedbakSystemApiCall(), 5000);
            }
        }
    }

    @Override
    public void onClickRefreshIcon() {
        CommonUtils.showDialog(this, "Please Wait...");
        getController().feedbakSystemApiCall();
    }

    @Override
    public void onSucessCrossShell(CrossShellResponse crossShellResponse) {
        List<UpsellCrosssellModel> upsellCrosssellModelList = new ArrayList<>();
        if (crossShellResponse != null && crossShellResponse.getUpselling() != null && crossShellResponse.getUpselling().size() > 0) {
            for (CrossShellResponse.Upselling upselling : crossShellResponse.getUpselling()) {
                UpsellCrosssellModel upsellCrosssellModel = new UpsellCrosssellModel();
                upsellCrosssellModel.setId(upselling.getId());
                upsellCrosssellModel.setItemid(upselling.getItemid());
                upsellCrosssellModel.setItemname(upselling.getItemname());
                upsellCrosssellModel.setReason(upselling.getReason());
                upsellCrosssellModel.setStockqty(upselling.getStockqty());
                upsellCrosssellModelList.add(upsellCrosssellModel);
            }
        }
        if (crossShellResponse != null && crossShellResponse.getCrossselling() != null && crossShellResponse.getCrossselling().size() > 0) {
            for (CrossShellResponse.Crossselling crossselling : crossShellResponse.getCrossselling()) {
                UpsellCrosssellModel upsellCrosssellModel = new UpsellCrosssellModel();
                upsellCrosssellModel.setId(crossselling.getId());
                upsellCrosssellModel.setItemid(crossselling.getItemid());
                upsellCrosssellModel.setItemname(crossselling.getItemname());
                upsellCrosssellModel.setReason(crossselling.getReason());
                upsellCrosssellModel.setStockqty(crossselling.getStockqty());
                upsellCrosssellModelList.add(upsellCrosssellModel);
            }
        }

        if (upsellCrosssellModelList != null && upsellCrosssellModelList.size() > 0) {
            itemsPaymentBinding.firstname.setText(upsellCrosssellModelList.get(0).getItemname());
            itemsPaymentBinding.firstreason.setText("- " + upsellCrosssellModelList.get(0).getReason());
        }
        if (upsellCrosssellModelList != null && upsellCrosssellModelList.size() > 1) {
            itemsPaymentBinding.secondname.setText(upsellCrosssellModelList.get(1).getItemname());
            itemsPaymentBinding.secondreason.setText("- " + upsellCrosssellModelList.get(1).getReason());
        }

        if (upsellCrosssellModelList != null && upsellCrosssellModelList.size() > 2) {
            itemsPaymentBinding.thirdname.setText(upsellCrosssellModelList.get(2).getItemname());
            itemsPaymentBinding.thirdreason.setText("- " + upsellCrosssellModelList.get(2).getReason());
        }
        if (upsellCrosssellModelList != null && upsellCrosssellModelList.size() > 3) {
            itemsPaymentBinding.fourthname.setText(upsellCrosssellModelList.get(3).getItemname());
            itemsPaymentBinding.fourthreason.setText("- " + upsellCrosssellModelList.get(3).getReason());
        }
        if (upsellCrosssellModelList != null && upsellCrosssellModelList.size() > 4) {
            itemsPaymentBinding.fifthname.setText(upsellCrosssellModelList.get(4).getItemname());
            itemsPaymentBinding.fifthreason.setText("- " + upsellCrosssellModelList.get(4).getReason());
        }
        if (upsellCrosssellModelList != null && upsellCrosssellModelList.size() > 5) {
            itemsPaymentBinding.sixthname.setText(upsellCrosssellModelList.get(5).getItemname());
            itemsPaymentBinding.sixthreason.setText("- " + upsellCrosssellModelList.get(5).getReason());
        }
        if (upsellCrosssellModelList != null && upsellCrosssellModelList.size() > 6) {
            itemsPaymentBinding.seventhname.setText(upsellCrosssellModelList.get(6).getItemname());
            itemsPaymentBinding.seventhreason.setText("- " + upsellCrosssellModelList.get(6).getReason());
        }
        if (upsellCrosssellModelList != null && upsellCrosssellModelList.size() > 7) {
            itemsPaymentBinding.eigthname.setText(upsellCrosssellModelList.get(7).getItemname());
            itemsPaymentBinding.eigthreason.setText("- " + upsellCrosssellModelList.get(7).getReason());
        }
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
}
