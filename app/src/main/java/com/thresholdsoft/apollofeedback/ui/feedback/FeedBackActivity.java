package com.thresholdsoft.apollofeedback.ui.feedback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.databinding.ActivityFeedBackBinding;
import com.thresholdsoft.apollofeedback.ui.offersnow.OffersNowActivity;
import com.thresholdsoft.apollofeedback.utils.AppConstants;

import java.util.Timer;
import java.util.TimerTask;

public class FeedBackActivity extends BaseActivity implements FeedBackActivityCallBack {
    private ActivityFeedBackBinding activityFeedBackBinding;


    public static Intent getStartIntent(Context mContext, FeedbackSystemResponse feedbackSystemResponse) {
        Intent intent = new Intent(mContext, FeedBackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(AppConstants.FEEDBACK_SYSTEM_RESPONSE, feedbackSystemResponse);
        return intent;
    }


//    Handler handler=new Handler();
//    Runnable runnable;
//    int Delay=10*1000;
//    @Override
//    protected void onPause() {
//
//        FeedbackSystemResponse feedbackSystemResponse=new FeedbackSystemResponse();
//        if (feedbackSystemResponse.getIsfeedbackScreen()==false){
//            handler.removeCallbacks(runnable);
//
//        }
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        handler.postDelayed(runnable=new Runnable() {
//            @Override
//            public void run() {
//                getController().feedbakSystemApiCall("0",0);
//                onPause();
//            }
//        },Delay);
//        super.onResume();
//    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                getController().feedbakSystemApiCall("0", 0);
            }
        }, 1000, 60000);


        activityFeedBackBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed_back);
        if (getIntent() != null) {
            FeedbackSystemResponse feedbackSystemResponse = (FeedbackSystemResponse) getIntent().getSerializableExtra(AppConstants.FEEDBACK_SYSTEM_RESPONSE);
            if (feedbackSystemResponse != null)
                activityFeedBackBinding.setModel(feedbackSystemResponse);
        }


        activityFeedBackBinding.poor.setOnClickListener(v -> {
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

            activityFeedBackBinding.fair.setImageDrawable(getResources().getDrawable(R.drawable.dull_fair));
            activityFeedBackBinding.average.setImageDrawable(getResources().getDrawable(R.drawable.dull_average));
            activityFeedBackBinding.happy.setImageDrawable(getResources().getDrawable(R.drawable.dull_happy));
            activityFeedBackBinding.excellent.setImageDrawable(getResources().getDrawable(R.drawable.dull_excellent));

            getController().feedbakSystemApiCall("1", 1);
        });


//        activityFeedBackBinding.poorTick.setOnClickListener(v -> {
//            activityFeedBackBinding.poorTick.setVisibility(View.GONE);
//            activityFeedBackBinding.poor.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//        });


        activityFeedBackBinding.fair.setOnClickListener(v -> {
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

            activityFeedBackBinding.poor.setImageDrawable(getResources().getDrawable(R.drawable.dull_poor));
            activityFeedBackBinding.average.setImageDrawable(getResources().getDrawable(R.drawable.dull_average));
            activityFeedBackBinding.happy.setImageDrawable(getResources().getDrawable(R.drawable.dull_happy));
            activityFeedBackBinding.excellent.setImageDrawable(getResources().getDrawable(R.drawable.dull_excellent));

            getController().feedbakSystemApiCall("2", 1);
        });
//        activityFeedBackBinding.fairtick.setOnClickListener(v -> {
//            activityFeedBackBinding.payment.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.fairtick.setVisibility(View.GONE);
//            activityFeedBackBinding.fair.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });
        activityFeedBackBinding.average.setOnClickListener(v -> {
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

            activityFeedBackBinding.poor.setImageDrawable(getResources().getDrawable(R.drawable.dull_poor));
            activityFeedBackBinding.fair.setImageDrawable(getResources().getDrawable(R.drawable.dull_fair));
            activityFeedBackBinding.happy.setImageDrawable(getResources().getDrawable(R.drawable.dull_happy));
            activityFeedBackBinding.excellent.setImageDrawable(getResources().getDrawable(R.drawable.dull_excellent));

            getController().feedbakSystemApiCall("3", 1);
        });
//        activityFeedBackBinding.averagetick.setOnClickListener(v -> {
//            activityFeedBackBinding.payment.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.averagetick.setVisibility(View.GONE);
//            activityFeedBackBinding.average.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });

        activityFeedBackBinding.happy.setOnClickListener(v -> {
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

            activityFeedBackBinding.poor.setImageDrawable(getResources().getDrawable(R.drawable.dull_poor));
            activityFeedBackBinding.fair.setImageDrawable(getResources().getDrawable(R.drawable.dull_fair));
            activityFeedBackBinding.average.setImageDrawable(getResources().getDrawable(R.drawable.dull_average));
            activityFeedBackBinding.excellent.setImageDrawable(getResources().getDrawable(R.drawable.dull_excellent));

            getController().feedbakSystemApiCall("4", 1);
        });

//        activityFeedBackBinding.happytick.setOnClickListener(v -> {
//            activityFeedBackBinding.payment.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.happytick.setVisibility(View.GONE);
//            activityFeedBackBinding.happy.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });
        activityFeedBackBinding.excellent.setOnClickListener(v -> {
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

            activityFeedBackBinding.poor.setImageDrawable(getResources().getDrawable(R.drawable.dull_poor));
            activityFeedBackBinding.fair.setImageDrawable(getResources().getDrawable(R.drawable.dull_fair));
            activityFeedBackBinding.average.setImageDrawable(getResources().getDrawable(R.drawable.dull_average));
            activityFeedBackBinding.happy.setImageDrawable(getResources().getDrawable(R.drawable.dull_happy));

            getController().feedbakSystemApiCall("5", 1);
        });
//        activityFeedBackBinding.excellenttick.setOnClickListener(v -> {
//            activityFeedBackBinding.excellenttick.setVisibility(View.GONE);
//            activityFeedBackBinding.excellent.setVisibility(View.VISIBLE);
//            activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
//
//        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 120000);
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
                }, 5000);
            } else {
                Toast.makeText(this, feedbackSystemResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    private FeedBackActivityController getController() {
        return new FeedBackActivityController(this, this);
    }
}