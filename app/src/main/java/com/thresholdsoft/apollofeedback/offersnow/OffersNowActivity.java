package com.thresholdsoft.apollofeedback.offersnow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivityOffersNowBinding;
import com.thresholdsoft.apollofeedback.itemspayment.ItemsPaymentActivity;

public class OffersNowActivity extends BaseActivity implements OffersNowActivityCallback {
    private ActivityOffersNowBinding offersNowBinding;

    public static Intent getStartIntent(Context mContext) {
        Intent intent = new Intent(mContext, OffersNowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offersNowBinding = DataBindingUtil.setContentView(this, R.layout.activity_offers_now);
        setUp();
    }

    private void setUp() {
        offersNowBinding.setCallback(this);
    }

    @Override
    public void onClickSkip() {
        startActivity(ItemsPaymentActivity.getStartIntent(this));
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}