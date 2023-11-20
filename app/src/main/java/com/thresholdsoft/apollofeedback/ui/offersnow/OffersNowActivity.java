package com.thresholdsoft.apollofeedback.ui.offersnow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.base.BaseActivity;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.databinding.ActivityOffersNowBinding;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.ui.itemspayment.ItemsPaymentActivity;
import com.thresholdsoft.apollofeedback.ui.offersnow.adapter.ImageSlideAdapter;
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
    int currentIndex = 0;
    boolean isAutoScrolling = true;
    LinearLayoutManager layoutManager;
    private ActivityOffersNowBinding offersNowBinding;
    private FeedbackSystemResponse feedbackSystemResponse;
    Button b;

    public static Intent getStartIntent(Context mContext) {
        Intent intent = new Intent(mContext, OffersNowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    private String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offersNowBinding = DataBindingUtil.setContentView(this, R.layout.activity_offers_now);
        setUp();
    }

    private void setUp() {
        offersNowBinding.setCallback(this);
        if (getDataManager().getSiteId().equalsIgnoreCase("") && getDataManager().getTerminalId().equalsIgnoreCase("")) {
            offersNowBinding.setIsConfigurationAvailable(true);
            onClickSettingIcon();
        } else {
            offersNowBinding.setIsConfigurationAvailable(false);
        }


//        getController().getOffersNowApiCall();
//        getController().feedbakSystemApiCall();
        getController().getDcOffersNowApi(getDataManager().getDcCode());
    }

    @Override
    public void onClickSkip() {
        startActivity(ItemsPaymentActivity.getStartIntent(this, mobileNumber));
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
        this.feedbackSystemResponse = feedbackSystemResponse;
        if (feedbackSystemResponse != null) {
            if (feedbackSystemResponse.getCustomerScreen() != null && feedbackSystemResponse.getCustomerScreen().getBillNumber() != null) {
                this.mobileNumber = feedbackSystemResponse.getCustomerScreen().getBillNumber();
            }
            if (Objects.requireNonNull(feedbackSystemResponse).getIspaymentScreen()) {
               /* startActivity(ItemsPaymentActivity.getStartIntent(this, mobileNumber));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();*/
            } else {
//                new Handler().postDelayed(() -> getController().feedbakSystemApiCall(), 5000);
                feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
                feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
            }
        }
    }

    Handler feedbakSystemApiCallHandler = new Handler();
    Runnable feedbakSystemApiCallRunnable = new Runnable() {
        @Override
        public void run() {
            if (feedbackSystemResponse != null) {
                if (!feedbackSystemResponse.getIsPrescriptionScan()) {
                    getController().feedbakSystemApiCall();
                }
            } else {
                getController().feedbakSystemApiCall();
            }
        }
    };

    @Override
    protected void onPause() {
        recyclerViewScrollerHandler.removeCallbacks(recyclerViewScrollerRunnable);
        feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        List<String> scannedPrescriptionsPathList = new ArrayList<>();
        getDateManager().setScannedPrescriptionsPath(scannedPrescriptionsPathList);
        offersNowBinding.setStoreName(getDataManager().getStoreName());
        feedbakSystemApiCallHandler.removeCallbacks(feedbakSystemApiCallRunnable);
//        feedbakSystemApiCallHandler.postDelayed(feedbakSystemApiCallRunnable, 5000);
        getController().feedbakSystemApiCall();
        super.onResume();
    }

    private SessionManager getDateManager() {
        return new SessionManager(this);
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
    List<String> imagesList;

    @Override
    public void onSuccesDcOffersNowApi(DcOffersNowResponse dcOffersNowResponse) {
        this.dcOffersNowResponse = dcOffersNowResponse;
        imagesList = new ArrayList<String>();
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
                        imagesList.add(filePath.getPath());
                    }
                }
            }
        }

        if (imagesList != null && imagesList.size() > 0) {
            ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(OffersNowActivity.this, imagesList);
            offersNowBinding.imagesRcv.setAdapter(imageSlideAdapter);
            layoutManager = new LinearLayoutManager(OffersNowActivity.this, LinearLayoutManager.HORIZONTAL, false);
            offersNowBinding.imagesRcv.setLayoutManager(layoutManager);
            if (imagesList.size() > 4) {
                recyclerViewScrollerHandler.removeCallbacks(recyclerViewScrollerRunnable);
                recyclerViewScrollerHandler.postDelayed(recyclerViewScrollerRunnable, 5000);
            }


           /* offersNowBinding.imagesRcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int itemCount = layoutManager.getItemCount();
                    for (int i = 0; i < itemCount; i++) {
                        View view = layoutManager.findViewByPosition(i);
                        if (view != null) {
                            if (i == lastVisibleItemPosition || i == firstVisibleItemPosition) {
                                view.setScaleX(0.8f);
                                view.setScaleY(0.8f);
                                view.setAlpha(0.3f);
                            } else {
                                view.setScaleX(1.0f);
                                view.setScaleY(1.0f);
                                view.setAlpha(1.0f);
                            }
                        }
                    }
                }
            });

            Handler handler = new Handler();
            Runnable scrollRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isAutoScrolling) {
                        if (currentIndex < imagesList.size() - 1) {
                            currentIndex++;
                        } else {
                            currentIndex = 0;
                        }
                        smoothScrollToPosition(currentIndex);
                    }
                    handler.postDelayed(this, 10000);
                }
            };
            handler.postDelayed(scrollRunnable, 10000);*/

            /*offersNowBinding.offersNowOne.setVisibility(View.INVISIBLE);
            offersNowBinding.offersNowTwo.setVisibility(View.INVISIBLE);
            offersNowBinding.offersNowThree.setVisibility(View.INVISIBLE);
            offersNowBinding.offersNowFour.setVisibility(View.INVISIBLE);
            for (int i = 0; i < imagesList.size(); i++) {
                if (i == 0) {
                    Glide.with(this).load(Uri.parse(AppConstants.DC_CODE_IMAGE_BASEURL + imagesList.get(i))).into(offersNowBinding.offersNowOne);
                    offersNowBinding.offersNowOne.setVisibility(View.VISIBLE);
                } else if (i == 1) {
                    Glide.with(this).load(Uri.parse(AppConstants.DC_CODE_IMAGE_BASEURL + imagesList.get(i))).into(offersNowBinding.offersNowTwo);
                    offersNowBinding.offersNowTwo.setVisibility(View.VISIBLE);
                } else if (i == 2) {
                    Glide.with(this).load(Uri.parse(AppConstants.DC_CODE_IMAGE_BASEURL + imagesList.get(i))).into(offersNowBinding.offersNowThree);
                    offersNowBinding.offersNowThree.setVisibility(View.VISIBLE);
                } else if (i == 3) {
                    Glide.with(this).load(Uri.parse(AppConstants.DC_CODE_IMAGE_BASEURL + imagesList.get(i))).into(offersNowBinding.offersNowFour);
                    offersNowBinding.offersNowFour.setVisibility(View.VISIBLE);
                }
            }*/
        } else {
//            offersNowBinding.offersNowOne.setVisibility(View.INVISIBLE);
//            offersNowBinding.offersNowTwo.setVisibility(View.INVISIBLE);
//            offersNowBinding.offersNowThree.setVisibility(View.INVISIBLE);
//            offersNowBinding.offersNowFour.setVisibility(View.INVISIBLE);
        }
    }

    Handler recyclerViewScrollerHandler = new Handler();
    Runnable recyclerViewScrollerRunnable = new Runnable() {
        @Override
        public void run() {
            LinearLayoutManager layoutManager = (LinearLayoutManager) offersNowBinding.imagesRcv.getLayoutManager();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if ((lastVisibleItemPosition + 1) < imagesList.size()) {
                smoothScrollToPosition(lastVisibleItemPosition+1);
                recyclerViewScrollerHandler.removeCallbacks(recyclerViewScrollerRunnable);
                recyclerViewScrollerHandler.postDelayed(recyclerViewScrollerRunnable, 5000);
            } else {
                smoothScrollToPosition(0);
                recyclerViewScrollerHandler.removeCallbacks(recyclerViewScrollerRunnable);
                recyclerViewScrollerHandler.postDelayed(recyclerViewScrollerRunnable, 10000);
            }
        }
    };

    private void smoothScrollToPosition(int currentIndex) {
        CustomSmoothScroller customSmoothScroller = new CustomSmoothScroller(OffersNowActivity.this);
        customSmoothScroller.setTargetPosition(currentIndex);
        layoutManager.startSmoothScroll(customSmoothScroller);
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
                    offersNowBinding.setIsConfigurationAvailable(false);
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