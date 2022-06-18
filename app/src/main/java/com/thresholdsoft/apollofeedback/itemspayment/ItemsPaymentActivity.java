package com.thresholdsoft.apollofeedback.itemspayment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.databinding.ActivityItemsPaymentBinding;

public class ItemsPaymentActivity extends BaseActivity implements ItemsPaymentActivityCallback {

    private ActivityItemsPaymentBinding itemsPaymentBinding;


    public static Intent getStartIntent(Context mContext) {
        Intent intent = new Intent(mContext, ItemsPaymentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemsPaymentBinding = DataBindingUtil.setContentView(this, R.layout.activity_items_payment);
    }
}
