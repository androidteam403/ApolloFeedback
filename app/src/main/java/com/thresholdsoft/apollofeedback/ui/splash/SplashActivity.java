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

public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding splashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        splashBinding.appLogo.setAnimation(animZoomOut);
        new Handler().postDelayed(() -> {
            startActivity(OffersNowActivity.getStartIntent(SplashActivity.this));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            finish();
        }, 1500);
    }
}
