package com.thresholdsoft.apollofeedback.ui.feedback;

import android.content.Context;
import android.graphics.Bitmap;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemRequest;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.ZeroCodeApiModelResponse;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;
import com.thresholdsoft.apollofeedback.utils.NetworkUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
            Call<FeedbackSystemResponse> call = apiInterface.FEEDBACK_SYSTEM_API_CALL();
//            Call<FeedbackSystemResponse> call = apiInterface.FEEDBACK_SYSTEM_API_CALL(feedbackSystemRequest);
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
    public void zeroCodeApiCall(File file, String name, Bitmap croppedBitmap) {
        if (NetworkUtils.isNetworkConnected(mContext)) {
//            CommonUtils.showDialog(mContext, "Please wait...");

//            // Create RequestBody for the image file
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), image);
//            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", image.getName(), requestBody);
//
//            // Create RequestBody for the 'name' field
//            RequestBody nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name);

            ApiInterface apiInterface = ApiClient.getApiService("http://20.197.55.11:5000/");

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), name);


            Call<ZeroCodeApiModelResponse> call = apiInterface.ZERO_CODE_FILE_UPLOADS(body, description);

            call.enqueue(new Callback<ZeroCodeApiModelResponse>() {
                @Override
                public void onResponse(Call<ZeroCodeApiModelResponse> call, Response<ZeroCodeApiModelResponse> response) {
//                    CommonUtils.hideDialog();
                    if (response.isSuccessful()) {
                        mCallback.onSuccessMultipartResponse(response.body(), croppedBitmap, file);
                    } else {
//                        mCallback.onFailureMessage("Response not successful");
                        CommonUtils.hideDialog();

                    }
                }

                @Override
                public void onFailure(Call<ZeroCodeApiModelResponse> call, Throwable t) {
                    CommonUtils.hideDialog();
                    mCallback.onFailureMessage(t.getMessage());
                }
            });
        } else {
            mCallback.onFailureMessage("No network connection.");
        }
    }

}
