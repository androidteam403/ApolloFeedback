package com.thresholdsoft.apollofeedback.ui.whyscanprescription.epsonscan;

import android.content.Context;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemRequest;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.ScannedPrescriptionsActivityCallback;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.model.KioskSelfCheckOutTransactionRequest;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.model.KioskSelfCheckOutTransactionResponse;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;
import com.thresholdsoft.apollofeedback.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EpsonScanActivityController {
    private Context mContext;
    private EpsonScanActivityCallback mCallback;

    public EpsonScanActivityController(Context mContext, EpsonScanActivityCallback mCallback) {
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
            Call<FeedbackSystemResponse> call = apiInterface.FEEDBACK_SYSTEM_API_CALL(feedbackSystemRequest);
            call.enqueue(new Callback<FeedbackSystemResponse>() {
                @Override
                public void onResponse(Call<FeedbackSystemResponse> call, Response<FeedbackSystemResponse> response) {
                    CommonUtils.hideDialog();
                    if (response.isSuccessful() && response.code() == 200) {
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
}
