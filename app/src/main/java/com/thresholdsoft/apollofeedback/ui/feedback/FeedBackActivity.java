package com.thresholdsoft.apollofeedback.ui.feedback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivityFeedBackBinding;

public class FeedBackActivity extends BaseActivity implements FeedBackActivityCallBack {
    private ActivityFeedBackBinding activityFeedBackBinding;

    public static Intent getStartIntent(Context mContext) {
        Intent intent = new Intent(mContext, FeedBackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFeedBackBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed_back);


        activityFeedBackBinding.poor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            }
        });


        activityFeedBackBinding.poorTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                activityFeedBackBinding.poorTick.setVisibility(View.GONE);
                activityFeedBackBinding.poor.setVisibility(View.VISIBLE);
                activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);
            }
        });


        activityFeedBackBinding.fair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

            }
        });
        activityFeedBackBinding.fairtick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                activityFeedBackBinding.payment.setVisibility(View.VISIBLE);

                activityFeedBackBinding.fairtick.setVisibility(View.GONE);
                activityFeedBackBinding.fair.setVisibility(View.VISIBLE);
                activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);

            }
        });
        activityFeedBackBinding.average.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


            }
        });
        activityFeedBackBinding.averagetick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                activityFeedBackBinding.payment.setVisibility(View.VISIBLE);

                activityFeedBackBinding.averagetick.setVisibility(View.GONE);
                activityFeedBackBinding.average.setVisibility(View.VISIBLE);
                activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);

            }
        });

        activityFeedBackBinding.happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

            }
        });
        activityFeedBackBinding.happytick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                activityFeedBackBinding.payment.setVisibility(View.VISIBLE);

                activityFeedBackBinding.happytick.setVisibility(View.GONE);
                activityFeedBackBinding.happy.setVisibility(View.VISIBLE);
                activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);

            }
        });
        activityFeedBackBinding.excellent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            }
        });
        activityFeedBackBinding.excellenttick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                activityFeedBackBinding.excellenttick.setVisibility(View.GONE);
                activityFeedBackBinding.excellent.setVisibility(View.VISIBLE);
                activityFeedBackBinding.feedbackthanku.setVisibility(View.GONE);

            }
        });

    }


}