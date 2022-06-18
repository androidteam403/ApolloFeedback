package com.thresholdsoft.apollofeedback.network;

import com.thresholdsoft.apollofeedback.itemspayment.model.GetAdvertisementResponse;
import com.thresholdsoft.apollofeedback.offersnow.model.GetOffersNowResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/*
 * Created on : jun 17, 2022.
 * Author : NAVEEN.M
 */
public interface ApiInterface {
    @GET("http://dev.thresholdsoft.com/apollo_feedback_assets/offers.json")
    Call<GetOffersNowResponse> GET_OFFERS_NOW_API_CALL();

    @GET("http://dev.thresholdsoft.com/apollo_feedback_assets/advertisements.json")
    Call<GetAdvertisementResponse> GET_ADVERTISEMENT_API_CALL();
}