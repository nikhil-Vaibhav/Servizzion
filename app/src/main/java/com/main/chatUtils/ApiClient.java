package com.main.chatUtils;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    private static Retrofit apiClient = null;

    public static Retrofit getApiClient() {
        if(apiClient == null) {
            apiClient = new Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return apiClient;
    }
}
