package com.thresholdsoft.apollofeedback.ui.offersnow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemRequest;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.DcOffersNowRequest;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.DcOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;
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

//    public void zeroCodeApiCall(File image, String name) {
//        if (NetworkUtils.isNetworkConnected(mContext)) {
//            CommonUtils.showDialog(mContext, "Please wait...");
//            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"),image);
//            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", image.getName(), requestBody);
//
//            ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
////            Map<String,String> params = new HashMap<>();
////            if (name != null) {
////                params.put("name", name);
////            }else{
////                params.put("", "");
////            }
//            Call<ZeroCodeApiModelResponse> call = apiInterface.ZERO_CODE_FILE_UPLOADS("multipart/form-data", fileToUpload, "params");
//            call.enqueue(new Callback<ZeroCodeApiModelResponse>() {
//                @Override
//                public void onResponse(Call<ZeroCodeApiModelResponse> call, Response<ZeroCodeApiModelResponse> response) {
//                    if (response.isSuccessful()) {
//                        mCallback.onSuccessMultipartResponse(response.body());
////                        mCallback.onSuccesGetOffersNowApi(response.body());
//                    }
//                    CommonUtils.hideDialog();
//                }
//
//                @Override
//                public void onFailure(Call<ZeroCodeApiModelResponse> call, Throwable t) {
//                    CommonUtils.hideDialog();
//                    mCallback.onFailureMessage(t.getMessage());
//                }
//            });
//        } else {
//            mCallback.onFailureMessage("Something went wrong.");
//        }
//    }

//    public void zeroCodeApiCall(File image, String name) {
//        if (NetworkUtils.isNetworkConnected(mContext)) {
//            CommonUtils.showDialog(mContext, "Please wait...");
//
//            // Create RequestBody for the image file
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), image);
//            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", image.getName(), requestBody);
//
//            // Create RequestBody for the 'name' field
//            RequestBody nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name);
//
//            ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
//
//            // Call the API method with the file and name request bodies
//            Call<ZeroCodeApiModelResponse> call = apiInterface.ZERO_CODE_FILE_UPLOADS( fileToUpload, nameRequestBody);
//
//            call.enqueue(new Callback<ZeroCodeApiModelResponse>() {
//                @Override
//                public void onResponse(Call<ZeroCodeApiModelResponse> call, Response<ZeroCodeApiModelResponse> response) {
//                    if (response.isSuccessful()) {
//                        mCallback.onSuccessMultipartResponse(response.body());
//                    }
//                    CommonUtils.hideDialog();
//                }
//
//                @Override
//                public void onFailure(Call<ZeroCodeApiModelResponse> call, Throwable t) {
//                    CommonUtils.hideDialog();
//                    mCallback.onFailureMessage(t.getMessage());
//                }
//            });
//        } else {
//            mCallback.onFailureMessage("Something went wrong.");
//        }
//    }

    public void zeroCodeApiCall(File file, String name, Bitmap croppedBitmap) {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            CommonUtils.showDialog(mContext, "Please wait...");

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
                    CommonUtils.hideDialog();
                    if (response.isSuccessful()) {
                        mCallback.onSuccessMultipartResponse(response.body(), croppedBitmap , file);
                    } else {
                        mCallback.onFailureMessage("Response not successful");
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

    public void zeroCodeApiCallWithoutName(File image, Bitmap croppedBitmap) {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            CommonUtils.showDialog(mContext, "Please wait...");

            // Create RequestBody for the image file
            @SuppressLint("ResourceType") RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), image);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", image.getName(), requestBody);

            // Create RequestBody for the 'name' field
//            RequestBody nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name);

            ApiInterface apiInterface = ApiClient.getApiService("http://20.197.55.11:5000/");

            // Call the API method with the file and name request bodies
            Call<ZeroCodeApiModelResponse> call = apiInterface.ZERO_CODE_FILE_UPLOADS_WITHOUT_NAME(fileToUpload);

            call.enqueue(new Callback<ZeroCodeApiModelResponse>() {
                @Override
                public void onResponse(Call<ZeroCodeApiModelResponse> call, Response<ZeroCodeApiModelResponse> response) {
                    CommonUtils.hideDialog();
                    if (response.isSuccessful()) {
                        mCallback.onSuccessMultipartResponse(response.body(), croppedBitmap, image);
                    } else {
                        mCallback.onFailureMessage("Response not successful");
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

