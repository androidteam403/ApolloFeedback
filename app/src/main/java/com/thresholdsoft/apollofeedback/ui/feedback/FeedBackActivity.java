package com.thresholdsoft.apollofeedback.ui.feedback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.databinding.ActivityFeedBackBinding;
import com.thresholdsoft.apollofeedback.ui.offersnow.OffersNowActivity;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.ZeroCodeApiModelResponse;
import com.thresholdsoft.apollofeedback.utils.AppConstants;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedBackActivity extends BaseActivity implements FeedBackActivityCallBack {
    private ActivityFeedBackBinding activityFeedBackBinding;
    private FeedbackSystemResponse feedbackSystemResponse;
    private static final String BITMAP_IMAGE = "BITMAP_IMAGE";
    private String bitmapImage;
    private static final String IS_TRAINED = "IS_TRAINED";
    private boolean isTrained;

    public static Intent getStartIntent(Context mContext, FeedbackSystemResponse feedbackSystemResponse, String bitmapImage, boolean isTrained) {
        Intent intent = new Intent(mContext, FeedBackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(AppConstants.FEEDBACK_SYSTEM_RESPONSE, feedbackSystemResponse);
        intent.putExtra(IS_TRAINED, isTrained);
        intent.putExtra(BITMAP_IMAGE, bitmapImage);
        return intent;
    }

    Handler feedbackEditboxHandler = new Handler();
    final Runnable feedbackEditboxRunnable = new Runnable() {
        @Override
        public void run() {
            String poor = getDataManager().getPoorKey();
            String fair = getDataManager().getFairKey();
            String average = getDataManager().getAverageKey();
            String happy = getDataManager().getHappyKey();
            String excellent = getDataManager().getExcellentKey();
            if (poor.equals(feedbackRatingFromPhysical)) {
                feedbackRatingFromPhysical = "";
                feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
//                    getController().feedbakSystemApiCall("1", 1);
                poor();
            } else if (fair.equals(feedbackRatingFromPhysical)) {
                feedbackRatingFromPhysical = "";
                feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
//                    getController().feedbakSystemApiCall("2", 1);
                fair();
            } else if (average.equals(feedbackRatingFromPhysical)) {
                feedbackRatingFromPhysical = "";
                feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
//                    getController().feedbakSystemApiCall("3", 1);
                average();
            } else if (happy.equals(feedbackRatingFromPhysical)) {
                feedbackRatingFromPhysical = "";
                feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
//                    getController().feedbakSystemApiCall("4", 1);
                happy();
            } else if (excellent.equals(feedbackRatingFromPhysical)) {
                feedbackRatingFromPhysical = "";
                feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
//                    getController().feedbakSystemApiCall("5", 1);
                excellent();
            }


            /*switch (feedbackRatingFromPhysical) {
                case "7":
                    feedbackRatingFromPhysical = "";
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
//                    getController().feedbakSystemApiCall("2", 1);
                    fair();
                    break;
                case "b":
                    feedbackRatingFromPhysical = "";
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
//                    getController().feedbakSystemApiCall("3", 1);
                    average();
                    break;
                case "6":
                    feedbackRatingFromPhysical = "";
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
//                    getController().feedbakSystemApiCall("4", 1);
                    happy();
                    break;
                case "=":
                    feedbackRatingFromPhysical = "";
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
//                    getController().feedbakSystemApiCall("5", 1);
                    excellent();
                    break;
                default:

            }*/
        }
    };
    private String feedbackRatingFromPhysical = "";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFeedBackBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed_back);
        onFeedbackSecreteEditboxListener();
        if (getIntent() != null) {
            feedbackSystemResponse = (FeedbackSystemResponse) getIntent().getSerializableExtra(AppConstants.FEEDBACK_SYSTEM_RESPONSE);
            if (feedbackSystemResponse != null)
                activityFeedBackBinding.setModel(feedbackSystemResponse);
            bitmapImage = (String) getIntent().getStringExtra(BITMAP_IMAGE);
            isTrained = (boolean) getIntent().getBooleanExtra(IS_TRAINED, false);
            if (!isTrained) {
                trainImage();
            }
            try {
                Bitmap src = BitmapFactory.decodeStream(openFileInput("customer.jpg"));
                activityFeedBackBinding.customerImage.setImageBitmap(src);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        activityFeedBackBinding.poorIc.setOnClickListener(v -> {
//            poor();
            String poor = getDataManager().getPoorKey();
            activityFeedBackBinding.feedbackSecretEditbox.setText(poor);
        });


//        activityFeedBackBinding.poorTick.setOnClickListener(v -> {
//            activityFeedBackBinding.poorTick.setVisibility(View.GONE);
//            activityFeedBackBinding.poor.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//        });


        activityFeedBackBinding.fairIc.setOnClickListener(v -> {
//            fair();
            String fair = getDataManager().getFairKey();
            activityFeedBackBinding.feedbackSecretEditbox.setText(fair);
        });
//        activityFeedBackBinding.fairtick.setOnClickListener(v -> {
//            activityFeedBackBinding.payment.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.fairtick.setVisibility(View.GONE);
//            activityFeedBackBinding.fair.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });
        activityFeedBackBinding.averageIc.setOnClickListener(v -> {
//            average();
            String average = getDataManager().getAverageKey();
            activityFeedBackBinding.feedbackSecretEditbox.setText(average);
        });
//        activityFeedBackBinding.averagetick.setOnClickListener(v -> {
//            activityFeedBackBinding.payment.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.averagetick.setVisibility(View.GONE);
//            activityFeedBackBinding.average.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });

        activityFeedBackBinding.happyIc.setOnClickListener(v -> {
//            happy();
            String happy = getDataManager().getHappyKey();
            activityFeedBackBinding.feedbackSecretEditbox.setText(happy);
        });

//        activityFeedBackBinding.happytick.setOnClickListener(v -> {
//            activityFeedBackBinding.payment.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.happytick.setVisibility(View.GONE);
//            activityFeedBackBinding.happy.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });
        activityFeedBackBinding.excellentIc.setOnClickListener(v -> {
//            excellent();
            String excellent = getDataManager().getExcellentKey();
            activityFeedBackBinding.feedbackSecretEditbox.setText(excellent);
        });
//        activityFeedBackBinding.excellenttick.setOnClickListener(v -> {
//            activityFeedBackBinding.excellenttick.setVisibility(View.GONE);
//            activityFeedBackBinding.excellent.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });
        getController().feedbakSystemApiCall("0", 0);

        new Handler().postDelayed(() -> {
            getController().feedbakSystemApiCall("0", 1);
        }, 120000);
    }

    private void poor() {
        activityFeedBackBinding.setIsFeedbackEnabled(true);
        activityFeedBackBinding.fairtick.setVisibility(View.GONE);
        activityFeedBackBinding.fair.setVisibility(View.VISIBLE);
        activityFeedBackBinding.averagetick.setVisibility(View.GONE);
        activityFeedBackBinding.average.setVisibility(View.VISIBLE);
        activityFeedBackBinding.happytick.setVisibility(View.GONE);
        activityFeedBackBinding.happy.setVisibility(View.VISIBLE);
        activityFeedBackBinding.excellenttick.setVisibility(View.GONE);
        activityFeedBackBinding.excellent.setVisibility(View.VISIBLE);
        activityFeedBackBinding.poorTick.setVisibility(View.VISIBLE);
        activityFeedBackBinding.poor.setVisibility(View.GONE);
        activityFeedBackBinding.feedbackthanku.setVisibility(View.VISIBLE);

//        activityFeedBackBinding.fair.setImageDrawable(getResources().getDrawable(R.drawable.dull_fair));
//        activityFeedBackBinding.average.setImageDrawable(getResources().getDrawable(R.drawable.dull_average));
//        activityFeedBackBinding.happy.setImageDrawable(getResources().getDrawable(R.drawable.dull_happy));
//        activityFeedBackBinding.excellent.setImageDrawable(getResources().getDrawable(R.drawable.dull_excellent));

        getController().feedbakSystemApiCall("1", 1);
    }

    private void fair() {
        activityFeedBackBinding.setIsFeedbackEnabled(true);
        activityFeedBackBinding.poorTick.setVisibility(View.GONE);
        activityFeedBackBinding.poor.setVisibility(View.VISIBLE);
        activityFeedBackBinding.averagetick.setVisibility(View.GONE);
        activityFeedBackBinding.average.setVisibility(View.VISIBLE);
        activityFeedBackBinding.happytick.setVisibility(View.GONE);
        activityFeedBackBinding.happy.setVisibility(View.VISIBLE);
        activityFeedBackBinding.excellenttick.setVisibility(View.GONE);
        activityFeedBackBinding.excellent.setVisibility(View.VISIBLE);
        activityFeedBackBinding.fairtick.setVisibility(View.VISIBLE);
        activityFeedBackBinding.fair.setVisibility(View.GONE);
        activityFeedBackBinding.feedbackthanku.setVisibility(View.VISIBLE);

//        activityFeedBackBinding.poor.setImageDrawable(getResources().getDrawable(R.drawable.dull_poor));
//        activityFeedBackBinding.average.setImageDrawable(getResources().getDrawable(R.drawable.dull_average));
//        activityFeedBackBinding.happy.setImageDrawable(getResources().getDrawable(R.drawable.dull_happy));
//        activityFeedBackBinding.excellent.setImageDrawable(getResources().getDrawable(R.drawable.dull_excellent));

        getController().feedbakSystemApiCall("2", 1);
    }

    private void average() {
        activityFeedBackBinding.setIsFeedbackEnabled(true);
        activityFeedBackBinding.poorTick.setVisibility(View.GONE);
        activityFeedBackBinding.poor.setVisibility(View.VISIBLE);
        activityFeedBackBinding.fairtick.setVisibility(View.GONE);
        activityFeedBackBinding.fair.setVisibility(View.VISIBLE);
        activityFeedBackBinding.happytick.setVisibility(View.GONE);
        activityFeedBackBinding.happy.setVisibility(View.VISIBLE);
        activityFeedBackBinding.excellenttick.setVisibility(View.GONE);
        activityFeedBackBinding.excellent.setVisibility(View.VISIBLE);
        activityFeedBackBinding.averagetick.setVisibility(View.VISIBLE);
        activityFeedBackBinding.average.setVisibility(View.GONE);
        activityFeedBackBinding.feedbackthanku.setVisibility(View.VISIBLE);

//        activityFeedBackBinding.poor.setImageDrawable(getResources().getDrawable(R.drawable.dull_poor));
//        activityFeedBackBinding.fair.setImageDrawable(getResources().getDrawable(R.drawable.dull_fair));
//        activityFeedBackBinding.happy.setImageDrawable(getResources().getDrawable(R.drawable.dull_happy));
//        activityFeedBackBinding.excellent.setImageDrawable(getResources().getDrawable(R.drawable.dull_excellent));

        getController().feedbakSystemApiCall("3", 1);
    }

    private void happy() {
        activityFeedBackBinding.setIsFeedbackEnabled(true);
        activityFeedBackBinding.poorTick.setVisibility(View.GONE);
        activityFeedBackBinding.poor.setVisibility(View.VISIBLE);
        activityFeedBackBinding.fairtick.setVisibility(View.GONE);
        activityFeedBackBinding.fair.setVisibility(View.VISIBLE);
        activityFeedBackBinding.averagetick.setVisibility(View.GONE);
        activityFeedBackBinding.average.setVisibility(View.VISIBLE);
        activityFeedBackBinding.excellenttick.setVisibility(View.GONE);
        activityFeedBackBinding.excellent.setVisibility(View.VISIBLE);
        activityFeedBackBinding.happytick.setVisibility(View.VISIBLE);
        activityFeedBackBinding.happy.setVisibility(View.GONE);
        activityFeedBackBinding.feedbackthanku.setVisibility(View.VISIBLE);

//        activityFeedBackBinding.poor.setImageDrawable(getResources().getDrawable(R.drawable.dull_poor));
//        activityFeedBackBinding.fair.setImageDrawable(getResources().getDrawable(R.drawable.dull_fair));
//        activityFeedBackBinding.average.setImageDrawable(getResources().getDrawable(R.drawable.dull_average));
//        activityFeedBackBinding.excellent.setImageDrawable(getResources().getDrawable(R.drawable.dull_excellent));

        getController().feedbakSystemApiCall("4", 1);
    }

    private void excellent() {
        activityFeedBackBinding.setIsFeedbackEnabled(true);
        activityFeedBackBinding.poorTick.setVisibility(View.GONE);
        activityFeedBackBinding.poor.setVisibility(View.VISIBLE);
        activityFeedBackBinding.fairtick.setVisibility(View.GONE);
        activityFeedBackBinding.fair.setVisibility(View.VISIBLE);
        activityFeedBackBinding.averagetick.setVisibility(View.GONE);
        activityFeedBackBinding.average.setVisibility(View.VISIBLE);
        activityFeedBackBinding.happytick.setVisibility(View.GONE);
        activityFeedBackBinding.happy.setVisibility(View.VISIBLE);
        activityFeedBackBinding.excellenttick.setVisibility(View.VISIBLE);
        activityFeedBackBinding.excellent.setVisibility(View.GONE);
        activityFeedBackBinding.feedbackthanku.setVisibility(View.VISIBLE);

//        activityFeedBackBinding.poor.setImageDrawable(getResources().getDrawable(R.drawable.dull_poor));
//        activityFeedBackBinding.fair.setImageDrawable(getResources().getDrawable(R.drawable.dull_fair));
//        activityFeedBackBinding.average.setImageDrawable(getResources().getDrawable(R.drawable.dull_average));
//        activityFeedBackBinding.happy.setImageDrawable(getResources().getDrawable(R.drawable.dull_happy));

        getController().feedbakSystemApiCall("5", 1);
    }

    @Override
    public void onFailureMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse) {
        if (feedbackSystemResponse != null) {
            if (feedbackSystemResponse.getStatus()) {
                new Handler().postDelayed(() -> {
                    startActivity(OffersNowActivity.getStartIntent(FeedBackActivity.this));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    finish();
                }, 3000);
            } else {
                if (feedbackSystemResponse != null && feedbackSystemResponse.getMessage() != null)
                    Toast.makeText(this, feedbackSystemResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSuccessFeedbackSystemApiContinousCall(FeedbackSystemResponse feedbackSystemResponse, int isFeedback) {
        if (isFeedback == 0) {
            if (feedbackSystemResponse != null) {
                if (!feedbackSystemResponse.getIsfeedbackScreen()) {
                    getController().feedbakSystemApiCall("0", 1);
                } else {
                    new Handler().postDelayed(() -> {
                        getController().feedbakSystemApiCall("0", 0);
                    }, 10000);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onPause() {
        feedbackRatingFromPhysical = "";
        feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
        super.onPause();
    }

    private FeedBackActivityController getController() {
        return new FeedBackActivityController(this, this);
    }

    private void onFeedbackSecreteEditboxListener() {
        activityFeedBackBinding.feedbackSecretEditbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().isEmpty()) {
                    feedbackRatingFromPhysical = s.toString();
                    activityFeedBackBinding.feedbackSecretEditbox.setText("");
                    feedbackEditboxHandler.removeCallbacks(feedbackEditboxRunnable);
                    feedbackEditboxHandler.postDelayed(feedbackEditboxRunnable, 1000);
                }
            }
        });
    }

    File outputFile;

    private void trainImage() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String filename = "JPEG_" + timeStamp + ".jpg";
        outputFile = new File(getApplicationContext().getFilesDir(), filename); // context is your Activity or Application context

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);

            // Compress the bitmap to JPEG format and write it to the output stream
            // Quality is set to 100 (highest)
            Bitmap bitmap = BitmapFactory.decodeStream(openFileInput("customer.jpg"));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            CommonUtils.showDialog(this, "Please Wait...");
            getController().zeroCodeApiCall(outputFile, feedbackSystemResponse.getCustomerofferScreen().getCustomerName() + "-" + feedbackSystemResponse.getCustomerScreen().getBillNumber(), bitmap);

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

    }

    public void onSuccessMultipartResponse(ZeroCodeApiModelResponse response, Bitmap image, File file) {
        CommonUtils.hideDialog();
        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            /*if ((response.getMessage().equals("Image match found")) || (response.getMessage().equals("Image, Added to traning data"))) {
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
            }*/
    }
}
