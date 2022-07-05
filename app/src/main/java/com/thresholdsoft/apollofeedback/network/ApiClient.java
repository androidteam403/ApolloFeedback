package com.thresholdsoft.apollofeedback.network;
/*
 * Created on : jun 17, 2022.
 * Author : NAVEEN.M
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thresholdsoft.apollofeedback.BuildConfig;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {


    public static ApiInterface getApiService() {
        return getRetrofitInstance().create(ApiInterface.class);
    }

    private static Retrofit getRetrofitInstance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    public static ApiInterface getApiService2() {
        return getRetrofitInstance().create(ApiInterface.class);
    }
}
