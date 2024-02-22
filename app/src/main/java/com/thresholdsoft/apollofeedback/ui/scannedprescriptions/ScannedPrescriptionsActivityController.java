package com.thresholdsoft.apollofeedback.ui.scannedprescriptions;

import android.content.Context;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemRequest;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.model.KioskSelfCheckOutTransactionRequest;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.model.KioskSelfCheckOutTransactionResponse;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;
import com.thresholdsoft.apollofeedback.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannedPrescriptionsActivityController {

    private Context mContext;
    private ScannedPrescriptionsActivityCallback mCallback;

    public ScannedPrescriptionsActivityController(Context mContext, ScannedPrescriptionsActivityCallback mCallback) {
        this.mContext = mContext;
        this.mCallback = mCallback;
    }

    public void feedbakSystemApiCall() {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            FeedbackSystemRequest feedbackSystemRequest = new FeedbackSystemRequest();
            feedbackSystemRequest.setSiteId(new SessionManager(mContext).getSiteId());
            feedbackSystemRequest.setTerminalId(new SessionManager(mContext).getTerminalId());
            feedbackSystemRequest.setISFeedback(0);
            feedbackSystemRequest.setFeedbackRate("0");
            ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
//            Call<FeedbackSystemResponse> call = apiInterface.FEEDBACK_SYSTEM_API_CALL();
            Call<FeedbackSystemResponse> call = apiInterface.FEEDBACK_SYSTEM_API_CALL(feedbackSystemRequest);
            call.enqueue(new Callback<FeedbackSystemResponse>() {
                @Override
                public void onResponse(Call<FeedbackSystemResponse> call, Response<FeedbackSystemResponse> response) {
//                    CommonUtils.hideDialog();
                    if (response.isSuccessful() && response.code() == 200) {
                        if (mCallback != null) {
                            mCallback.onSuccessFeedbackSystemApiCall(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(Call<FeedbackSystemResponse> call, Throwable t) {
//                    CommonUtils.hideDialog();
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

    public void kioskSelfCheckOutTransactionApiCAll(KioskSelfCheckOutTransactionRequest kioskSelfCheckOutTransactionRequest, int prescriptionPos) {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
            Call<KioskSelfCheckOutTransactionResponse> call = apiInterface.KIOSK_SELF_CHECK_OUT_TRANSACTION_API_CALL("application/json", kioskSelfCheckOutTransactionRequest);
            call.enqueue(new Callback<KioskSelfCheckOutTransactionResponse>() {
                @Override
                public void onResponse(Call<KioskSelfCheckOutTransactionResponse> call, Response<KioskSelfCheckOutTransactionResponse> response) {
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        if (mCallback != null) {
                            mCallback.onSuccessKioskSelfCheckOutTransactionApiCAll(response.body(), prescriptionPos);
                        }
                    } else {
                        CommonUtils.hideDialog();
                    }
                }

                @Override
                public void onFailure(Call<KioskSelfCheckOutTransactionResponse> call, Throwable t) {
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
}

