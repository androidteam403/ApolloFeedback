package com.thresholdsoft.apollofeedback.ui.offersnow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.databinding.ActivityOffersNowBinding;
import com.thresholdsoft.apollofeedback.ui.splash.SplashActivity;
import com.thresholdsoft.apollofeedback.ui.storesetup.StoreSetupActivity;
import com.thresholdsoft.apollofeedback.ui.itemspayment.ItemsPaymentActivity;
import com.thresholdsoft.apollofeedback.ui.offersnow.dialog.AccessKeyDialog;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;

import java.util.Objects;

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
        getController().feedbakSystemApiCall();
    }

    @Override
    public void onClickSkip() {
        startActivity(ItemsPaymentActivity.getStartIntent(this));
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        finish();
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
            offersNowBinding.skipButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse) {
        if (feedbackSystemResponse != null) {
            if (Objects.requireNonNull(feedbackSystemResponse).getIspaymentScreen()) {
                startActivity(ItemsPaymentActivity.getStartIntent(this));
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
    public void onClickSettingIcon() {
        AccessKeyDialog accesskeyDialog = new AccessKeyDialog(OffersNowActivity.this);
        accesskeyDialog.onClickSubmit(v1 -> {
            accesskeyDialog.listener();
            if (accesskeyDialog.validate()) {
                startActivity(StoreSetupActivity.getStartIntent(OffersNowActivity.this));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                accesskeyDialog.dismiss();
            }
        });
        accesskeyDialog.show();
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

    private OffersNowActivityController getController() {
        return new OffersNowActivityController(this, this);
    }
}