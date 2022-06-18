package com.thresholdsoft.apollofeedback.offersnow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivityOffersNowBinding;
import com.thresholdsoft.apollofeedback.itemspayment.ItemsPaymentActivity;
import com.thresholdsoft.apollofeedback.offersnow.model.GetOffersNowResponse;

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
        getController().getOffersNowApiCall();
    }

    @Override
    public void onClickSkip() {
        startActivity(ItemsPaymentActivity.getStartIntent(this));
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void onSuccesGetOffersNowApi(GetOffersNowResponse getOffersNowResponse) {
        if (getOffersNowResponse != null && getOffersNowResponse.getOffersNow() != null && getOffersNowResponse.getOffersNow().size() > 0) {
            for (GetOffersNowResponse.OffersNow offersNow : getOffersNowResponse.getOffersNow()) {
                if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 0) {
                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowOne);
                } else if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 1) {
                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowTwo);
                } else if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 2) {
                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowThree);
                } else if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 3) {
                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowFour);
                }
            }
        }
    }

    @Override
    public void onFailureMessage(String message) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private OffersNowActivityController getController() {
        return new OffersNowActivityController(this, this);
    }
}