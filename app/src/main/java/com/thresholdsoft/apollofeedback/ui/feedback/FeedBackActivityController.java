package com.thresholdsoft.apollofeedback.ui.feedback;

import android.content.Context;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemRequest;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;
import com.thresholdsoft.apollofeedback.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedBackActivityController {
    private Context mContext;
    private FeedBackActivityCallBack mCallback;

    public FeedBackActivityController(Context mContext, FeedBackActivityCallBack mCallback) {
        this.mContext = mContext;
        this.mCallback = mCallback;
    }

    public void feedbakSystemApiCall(String feedbackRate, int isFeedback) {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            if (isFeedback == 1)
                CommonUtils.showDialog(mContext, "Please Wait...");
            FeedbackSystemRequest feedbackSystemRequest = new FeedbackSystemRequest();
            feedbackSystemRequest.setSiteId(new SessionManager(mContext).getSiteId());
            feedbackSystemRequest.setTerminalId(new SessionManager(mContext).getTerminalId());
            feedbackSystemRequest.setISFeedback(isFeedback);
            feedbackSystemRequest.setFeedbackRate(feedbackRate);
            ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
            Call<FeedbackSystemResponse> call = apiInterface.FEEDBACK_SYSTEM_API_CALL(feedbackSystemRequest);
            call.enqueue(new Callback<FeedbackSystemResponse>() {
                @Override
                public void onResponse(Call<FeedbackSystemResponse> call, Response<FeedbackSystemResponse> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        if (isFeedback == 0) {
                            if (mCallback != null) {
                                mCallback.onSuccessFeedbackSystemApiContinousCall(response.body(), isFeedback);
                            }
                        } else {
                            if (mCallback != null) {
                                mCallback.onSuccessFeedbackSystemApiCall(response.body());
                            }
                        }
                    }
                    CommonUtils.hideDialog();
                }

                @Override
                public void onFailure(Call<FeedbackSystemResponse> call, Throwable t) {
                    if (mCallback != null) {
                        mCallback.onFailureMessage(t.getMessage());
                    }
                    CommonUtils.hideDialog();
                }
            });
        } else {
            if (mCallback != null) {
                mCallback.onFailureMessage("Something went wrong.");
            }
        }
    }
}
