package com.thresholdsoft.apollofeedback.ui.offersnow;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearSmoothScroller;

public class CustomSmoothScroller extends LinearSmoothScroller {
    public CustomSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return 200f / displayMetrics.densityDpi;
    }
}
