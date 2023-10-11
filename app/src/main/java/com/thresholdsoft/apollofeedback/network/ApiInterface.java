package com.thresholdsoft.apollofeedback.network;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemRequest;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.CrossShellRequest;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.CrossShellResponse;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.GetAdvertisementResponse;
import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationRequest;
import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.DcOffersNowRequest;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.DcOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.model.KioskSelfCheckOutTransactionRequest;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.model.KioskSelfCheckOutTransactionResponse;
import com.thresholdsoft.apollofeedback.ui.storesetup.model.StoreListResponseModel;
import com.thresholdsoft.apollofeedback.utils.fileupload.FileDownloadRequest;
import com.thresholdsoft.apollofeedback.utils.fileupload.FileDownloadResponse;
import com.thresholdsoft.apollofeedback.utils.fileupload.FileUploadResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

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

    @GET("https://online.apollopharmacy.org/MAPPS/apollompos/Self/STORELIST")
//http://lms.apollopharmacy.org:8033/APK/apollompos/Self/STORELIST
    Call<StoreListResponseModel> GET_STORES_LIST();

    @POST("https://online.apollopharmacy.org/MAPPS/apollompos/Self/Registration")
//http://lms.apollopharmacy.org:8033/APK/apollompos/Self/Registration
    Call<DeviceRegistrationResponse> deviceRegistration(@Body DeviceRegistrationRequest deviceRegistrationRequest);

    @POST("https://signage.apollopharmacy.app/zc-v3.1-user-svc/2.0/ads/api/pos_offer/list/get-pos-offers-by-dc")
    Call<DcOffersNowResponse> GET_DCOFFERSNOW_API(@Body DcOffersNowRequest dcOffersNowRequest);

    @POST("SalesTransactionService.svc/GetUpSellingProduct")
    Call<CrossShellResponse> Get_CROSSSHELL_API(@Body CrossShellRequest crossShellRequest);

    @POST("SalesTransactionService.svc/KioskSelfCheckOutTransaction")
    Call<KioskSelfCheckOutTransactionResponse> KIOSK_SELF_CHECK_OUT_TRANSACTION_API_CALL(@Header ("Content-Type") String contentType, @Body KioskSelfCheckOutTransactionRequest kioskSelfCheckOutTransactionRequest);


    @Multipart
    @POST
    Call<FileUploadResponse> FILE_UPLOAD_API_CALL(@Url String url, @Header("TYPE") String type, @Header("token") String token,
                                                  @Part MultipartBody.Part file);

    @POST
    Call<FileDownloadResponse> FILE_DOWNLOAD_API_CALL(@Url String url, @Header("token") String token, @Body FileDownloadRequest fileDownloadRequest);
}