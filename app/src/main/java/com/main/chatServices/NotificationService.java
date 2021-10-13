package com.main.chatServices;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.main.chatUtils.ApiClient;
import com.main.chatUtils.MessagingAPI;
import com.main.chatUtils.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService {

    public static void sendNotification(Context context , String message) {

        ApiClient.getApiClient().create(MessagingAPI.class)
                .sendMessage(Store.getHeaderMap(), message)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    JSONObject responseJSON =  new JSONObject(response.body());
                                    JSONArray results = responseJSON.getJSONArray("results");
                                    if(responseJSON.getInt("failure") == 1) {
                                        Toast.makeText(context, "Error : " + ((JSONObject) results.get(0)).getString("error"), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(context, "Notification sent successfully" , Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "Error: " + response.code() , Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
