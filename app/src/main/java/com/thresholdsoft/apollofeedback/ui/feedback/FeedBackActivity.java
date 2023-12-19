package com.thresholdsoft.apollofeedback.ui.feedback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.thresholdsoft.apollofeedback.utils.AppConstants;

public class FeedBackActivity extends BaseActivity implements FeedBackActivityCallBack {
    private ActivityFeedBackBinding activityFeedBackBinding;

    public static Intent getStartIntent(Context mContext, FeedbackSystemResponse feedbackSystemResponse) {
        Intent intent = new Intent(mContext, FeedBackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(AppConstants.FEEDBACK_SYSTEM_RESPONSE, feedbackSystemResponse);
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
            FeedbackSystemResponse feedbackSystemResponse = (FeedbackSystemResponse) getIntent().getSerializableExtra(AppConstants.FEEDBACK_SYSTEM_RESPONSE);
            if (feedbackSystemResponse != null)
                activityFeedBackBinding.setModel(feedbackSystemResponse);
        }

        activityFeedBackBinding.poor.setOnClickListener(v -> {
            poor();
        });


//        activityFeedBackBinding.poorTick.setOnClickListener(v -> {
//            activityFeedBackBinding.poorTick.setVisibility(View.GONE);
//            activityFeedBackBinding.poor.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//        });


        activityFeedBackBinding.fair.setOnClickListener(v -> {
            fair();
        });
//        activityFeedBackBinding.fairtick.setOnClickListener(v -> {
//            activityFeedBackBinding.payment.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.fairtick.setVisibility(View.GONE);
//            activityFeedBackBinding.fair.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });
        activityFeedBackBinding.average.setOnClickListener(v -> {
            average();
        });
//        activityFeedBackBinding.averagetick.setOnClickListener(v -> {
//            activityFeedBackBinding.payment.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.averagetick.setVisibility(View.GONE);
//            activityFeedBackBinding.average.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });

        activityFeedBackBinding.happy.setOnClickListener(v -> {
            happy();
        });

//        activityFeedBackBinding.happytick.setOnClickListener(v -> {
//            activityFeedBackBinding.payment.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.happytick.setVisibility(View.GONE);
//            activityFeedBackBinding.happy.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });
        activityFeedBackBinding.excellent.setOnClickListener(v -> {
            excellent();
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
//                    startActivity(OffersNowActivity.getStartIntent(FeedBackActivity.this));
//                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//                    finish();
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
}