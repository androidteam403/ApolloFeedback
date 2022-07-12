package com.thresholdsoft.apollofeedback.ui.offersnow;

import android.content.Context;
import android.widget.Toast;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemRequest;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationRequest;
import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.DcOffersNowRequest;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.DcOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;
import com.thresholdsoft.apollofeedback.utils.AppConstants;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;
import com.thresholdsoft.apollofeedback.utils.NetworkUtils;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OffersNowActivityController {
    private Context mContext;
    private OffersNowActivityCallback mCallback;

    public OffersNowActivityController(Context mContext, OffersNowActivityCallback mCallback) {
        this.mContext = mContext;
        this.mCallback = mCallback;
    }

    public void getOffersNowApiCall() {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            CommonUtils.showDialog(mContext, "Please wait...");

            ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
            Call<GetOffersNowResponse> call = apiInterface.GET_OFFERS_NOW_API_CALL();
            call.enqueue(new Callback<GetOffersNowResponse>() {
                @Override
                public void onResponse(Call<GetOffersNowResponse> call, Response<GetOffersNowResponse> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        mCallback.onSuccesGetOffersNowApi(response.body());
                    }
                    CommonUtils.hideDialog();
                }

                @Override
                public void onFailure(Call<GetOffersNowResponse> call, Throwable t) {
                    CommonUtils.hideDialog();
                    mCallback.onFailureMessage(t.getMessage());
                }
            });
        } else {
            mCallback.onFailureMessage("Something went wrong.");
        }
    }

    public void feedbakSystemApiCall() {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            FeedbackSystemRequest feedbackSystemRequest = new FeedbackSystemRequest();
            feedbackSystemRequest.setSiteId(new SessionManager(mContext).getSiteId());
            feedbackSystemRequest.setTerminalId(new SessionManager(mContext).getTerminalId());
            feedbackSystemRequest.setISFeedback(0);
            feedbackSystemRequest.setFeedbackRate("0");
            ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
            Call<FeedbackSystemResponse> call = apiInterface.FEEDBACK_SYSTEM_API_CALL(feedbackSystemRequest);
            call.enqueue(new Callback<FeedbackSystemResponse>() {
                @Override
                public void onResponse(Call<FeedbackSystemResponse> call, Response<FeedbackSystemResponse> response) {
                    CommonUtils.hideDialog();
                    if (response.isSuccessful() && response.code() == 200) {
//                        Toast.makeText(mContext, "IsPamentScreen=================="+response.body().getIspaymentScreen(), Toast.LENGTH_SHORT).show();
                        if (mCallback != null) {
                            mCallback.onSuccessFeedbackSystemApiCall(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(Call<FeedbackSystemResponse> call, Throwable t) {
                    CommonUtils.hideDialog();
                    if (mCallback != null) {
                        mCallback.onFailureMessage(t.getMessage());
                    }
                }
            });
        } else {
            if (mCallback != null) {
                mCallback.onFailureMessage("Something went wrong.");
            }
        }
    }

    public void getDcOffersNowApi(String dcCode) {
        CommonUtils.showDialog(mContext, "Loading…");
        ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
        DcOffersNowRequest dcOffersNowRequest = new DcOffersNowRequest();
        dcOffersNowRequest.setDcCode(dcCode);

        Call<DcOffersNowResponse> call = apiInterface.GET_DCOFFERSNOW_API(dcOffersNowRequest);
        call.enqueue(new Callback<DcOffersNowResponse>() {
            @Override
            public void onResponse(Call<DcOffersNowResponse> call, Response<DcOffersNowResponse> response) {
                if (response.isSuccessful() && response.body() !=null && response.body().getSuccess()) {
                    CommonUtils.hideDialog();
                    mCallback.onSuccesDcOffersNowApi(response.body());
                }else{
                    mCallback.onFailureDcOffersNowApi();
                }

            }


            @Override
            public void onFailure(Call<DcOffersNowResponse> call, Throwable t) {
            CommonUtils.hideDialog();
            mCallback.onFailureMessage(t.getMessage());
            }
        });
    }

}

