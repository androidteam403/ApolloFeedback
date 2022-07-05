package com.thresholdsoft.apollofeedback.network;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemRequest;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationRequest;
import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationResponse;
import com.thresholdsoft.apollofeedback.ui.storesetup.model.StoreListResponseModel;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.GetAdvertisementResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/*
 * Created on : jun 17, 2022.
 * Author : NAVEEN.M
 */
public interface ApiInterface {
    @POST("SalesTransactionService.svc/FeedbackSystemV2API")
    Call<FeedbackSystemResponse> FEEDBACK_SYSTEM_API_CALL(@Body FeedbackSystemRequest feedbackSystemRequest);

    @GET("http://dev.thresholdsoft.com/apollo_feedback_assets/offers.json")
    Call<GetOffersNowResponse> GET_OFFERS_NOW_API_CALL();

    @GET("http://dev.thresholdsoft.com/apollo_feedback_assets/advertisements.json")
    Call<GetAdvertisementResponse> GET_ADVERTISEMENT_API_CALL();

    @GET("http://lms.apollopharmacy.org:8033/APK/apollompos/Self/STORELIST")
    Call<StoreListResponseModel> GET_STORES_LIST();

    @POST("http://lms.apollopharmacy.org:8033/APK/apollompos/Self/Registration")
    Call<DeviceRegistrationResponse> deviceRegistration(@Body DeviceRegistrationRequest deviceRegistrationRequest);

}