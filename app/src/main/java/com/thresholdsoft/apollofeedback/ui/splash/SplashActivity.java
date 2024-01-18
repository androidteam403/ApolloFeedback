package com.thresholdsoft.apollofeedback.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivitySplashBinding;
import com.thresholdsoft.apollofeedback.ui.offersnow.OffersNowActivity;
import com.thresholdsoft.apollofeedback.ui.offersnow.dialog.AccessKeyDialog;
import com.thresholdsoft.apollofeedback.ui.storesetup.StoreSetupActivity;
import com.thresholdsoft.apollofeedback.utils.AppConstants;

public class SplashActivity extends BaseActivity implements SplashActivityCallback {
    private AccessKeyDialog accesskeyDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding splashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        splashBinding.appLogo.setAnimation(animZoomOut);


        new Handler().postDelayed(() -> {
//            if (getDataManager().getSiteId().equalsIgnoreCase("") && getDataManager().getTerminalId().equalsIgnoreCase("")) {
//                accesskeyDialog = new AccessKeyDialog(SplashActivity.this);
//                accesskeyDialog.setSplashCallback(this);
//                accesskeyDialog.onClickSubmit(v1 -> {
//                    accesskeyDialog.listener();
//                    if (accesskeyDialog.validate()) {
//                startActivity(StoreSetupActivity.getStartIntent(SplashActivity.this));
//                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//                        accesskeyDialog.dismiss();
//                finish();
//                    }
//                });
//                accesskeyDialog.show();
//            } else {
            if (getDataManager().getSiteId().equalsIgnoreCase("") && getDataManager().getTerminalId().equalsIgnoreCase("")) {
                onClickSettingIcon();

            } else {
                startActivity(OffersNowActivity.getStartIntent(SplashActivity.this));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            }

            /*startActivity(OffersNowActivity.getStartIntent(SplashActivity.this));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            finish();*/
//            }
        }, 1500);
    }

    public void onClickSettingIcon() {
        AccessKeyDialog accesskeyDialog = new AccessKeyDialog(this);
        accesskeyDialog.setSplashCallback(this);
        accesskeyDialog.onClickSubmit(v1 -> {
            accesskeyDialog.listener();
            if (accesskeyDialog.validate()) {
                startActivityForResult(StoreSetupActivity.getStartIntent(this), AppConstants.STORE_SETUP_ACTIVITY_CODE);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                accesskeyDialog.dismiss();
                finish();
            }
        });
        accesskeyDialog.show();
    }

    @Override
    public void onAccessDialogDismiss() {
        finish();
        if (accesskeyDialog != null)
            accesskeyDialog.dismiss();

    }
}
