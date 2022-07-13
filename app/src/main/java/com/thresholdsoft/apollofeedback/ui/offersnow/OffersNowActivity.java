package com.thresholdsoft.apollofeedback.ui.offersnow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.databinding.ActivityOffersNowBinding;
import com.thresholdsoft.apollofeedback.ui.itemspayment.ItemsPaymentActivity;
import com.thresholdsoft.apollofeedback.ui.offersnow.dialog.AccessKeyDialog;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.DcOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.storesetup.StoreSetupActivity;
import com.thresholdsoft.apollofeedback.utils.AppConstants;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
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
        if (getDataManager().getSiteId().equalsIgnoreCase("") && getDataManager().getTerminalId().equalsIgnoreCase("")) {
            onClickSettingIcon();
        }

        offersNowBinding.skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent
            }
        });

//        getController().getOffersNowApiCall();
        getController().feedbakSystemApiCall();
        getController().getDcOffersNowApi(getDataManager().getDcCode());
    }

    @Override
    public void onClickSkip() {
        startActivity(ItemsPaymentActivity.getStartIntent(this));
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        finish();
    }

    GetOffersNowResponse getOffersNowResponse;

    @Override
    public void onSuccesGetOffersNowApi(GetOffersNowResponse getOffersNowResponse) {
        this.getOffersNowResponse = getOffersNowResponse;
//        if (getOffersNowResponse != null && getOffersNowResponse.getOffersNow() != null && getOffersNowResponse.getOffersNow().size() > 0) {
//            for (GetOffersNowResponse.OffersNow offersNow : getOffersNowResponse.getOffersNow()) {
//                if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 0) {
//                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowOne);
//                } else if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 1) {
//                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowTwo);
//                } else if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 2) {
//                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowThree);
//                } else if (getOffersNowResponse.getOffersNow().indexOf(offersNow) == 3) {
//                    Glide.with(this).load(Uri.parse(offersNow.getImage())).into(offersNowBinding.offersNowFour);
//                }
//            }
//        }
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
        accesskeyDialog.setOffersNowCallback(this);
        accesskeyDialog.onClickSubmit(v1 -> {
            accesskeyDialog.listener();
            if (accesskeyDialog.validate()) {
                startActivityForResult(StoreSetupActivity.getStartIntent(OffersNowActivity.this), AppConstants.STORE_SETUP_ACTIVITY_CODE);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                accesskeyDialog.dismiss();
            }
        });
        accesskeyDialog.show();
    }

    @Override
    public void onAccessDialogDismiss() {
        if (getDataManager().getSiteId().equalsIgnoreCase("") && getDataManager().getTerminalId().equalsIgnoreCase("")) {
            finish();
        }
    }


    DcOffersNowResponse dcOffersNowResponse;

    @Override
    public void onSuccesDcOffersNowApi(DcOffersNowResponse dcOffersNowResponse) {
        this.dcOffersNowResponse = dcOffersNowResponse;
        List<String> imagesList = new ArrayList<String>();
        for (DcOffersNowResponse.Data.ListData.Row rows : dcOffersNowResponse.getData().getListData().getRows()) {
            String excepSites = rows.getExceptionSites();
            String k[] = excepSites.split(",");
            boolean isStoreid = false;
            for (int i = 0; i < k.length; i++) {
                if (getDataManager().getSiteId().equals(k[i])) {
                    isStoreid = true;
                }
            }
            if (!isStoreid) {
                for (DcOffersNowResponse.Data.ListData.Row.PosMediaLibrary posMedia : rows.getPosMediaLibrary()) {
                    for (DcOffersNowResponse.Data.ListData.Row.PosMediaLibrary.File filePath : posMedia.getFile()) {
                        imagesList.add(filePath.getFullPath());
                    }

                }
            }
        }
//        if (!isStoreid) {
//            for (DcOffersNowResponse.Data.ListData.Row rows : dcOffersNowResponse.getData().getListData().getRows()) {
//                for (DcOffersNowResponse.Data.ListData.Row.PosMediaLibrary posMedia : rows.getPosMediaLibrary()) {
//                    for (DcOffersNowResponse.Data.ListData.Row.PosMediaLibrary.File filePath : posMedia.getFile()) {
//                        imagesList.add(filePath.getFullPath());
//                    }
//
//                }
//            }
        if (imagesList != null && imagesList.size() > 0) {
            for (int i = 0; i < imagesList.size(); i++) {
                if (i == 0) {
                    Glide.with(this).load(Uri.parse(imagesList.get(i))).into(offersNowBinding.offersNowOne);
                } else if (i == 1) {
                    Glide.with(this).load(Uri.parse(imagesList.get(i))).into(offersNowBinding.offersNowTwo);
                } else if (i == 2) {
                    Glide.with(this).load(Uri.parse(imagesList.get(i))).into(offersNowBinding.offersNowThree);
                } else if (i == 3) {
                    Glide.with(this).load(Uri.parse(imagesList.get(i))).into(offersNowBinding.offersNowFour);
                }
            }


//            }
        } else {

            offersNowBinding.offersNowOne.setImageBitmap(null);
            offersNowBinding.offersNowTwo.setImageBitmap(null);
            offersNowBinding.offersNowThree.setImageBitmap(null);
            offersNowBinding.offersNowFour.setImageBitmap(null);
        }

    }

    @Override
    public void onFailureDcOffersNowApi() {

    }

    @Override
    public void onFailureMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.STORE_SETUP_ACTIVITY_CODE) {
                if (getDataManager().getSiteId().equalsIgnoreCase("") && getDataManager().getTerminalId().equalsIgnoreCase("")) {
                    finish();
                } else {
                    getController().feedbakSystemApiCall();
                    getController().getDcOffersNowApi(getDataManager().getDcCode());
                }
            }
        }
    }

    private OffersNowActivityController getController() {
        return new OffersNowActivityController(this, this);
    }
}