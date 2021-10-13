package com.main.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatAdapters.ImagePagerAdapter;
import com.main.chatFragments.ImageSliderFragment;
import com.main.chatModel.Order;
import com.main.chatModel.Payment;
import com.main.chatModel.Service;
import com.main.chatModel.User;
import com.main.chatUtils.ApiClient;
import com.main.chatUtils.MessagingAPI;
import com.main.chatUtils.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewServiceActivity extends AppCompatActivity {

    private NotificationManagerCompat managerCompat;
    private Service service;
    private TextView titleView, shortDescView, longDescView;
    private Button chatWithSellerBtn, placeOrderBtn;
    private LinearLayout buttonLayout;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_service);

        setUpToolbar();
        init();
        setData();
        setListeners();

        setTitle(service.servedByUser.userName);

    }

    private void setUpToolbar() {
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setListeners() {

        chatWithSellerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ViewServiceActivity.this, ChatScreenActivity.class);
            intent.putExtra(Store.KEY_CHAT_WITH_USER, service.servedByUser);
            startActivity(intent);
        });

        placeOrderBtn.setOnClickListener(v -> {
            confirmOrder();
            Toast.makeText(ViewServiceActivity.this, "Order Placed.", Toast.LENGTH_SHORT).show();
        });
    }

    private void confirmOrder() {
        Order order = new Order(true, service.servedByUser, Store.CURRENT_USER, service, new Payment(
                Integer.getInteger(service.charges), null, null, null
        ));

        CollectionReference userCollection = FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER);
        userCollection.document(Store.CURRENT_USER_ID).collection(Store.KEY_COLLECTION_MY_ORDERS).add(order).addOnSuccessListener(documentReference -> {
            userCollection.document(service.servedByUser.userID).collection(Store.KEY_COLLECTION_PENDING_ORDERS).document(documentReference.getId())
                    .set(order);
        });

        try {
            String title = "Order Received";
            String text = "You have received an order from " + Store.CURRENT_USER.userName + " for your serviceID " + order.service.serviceID + ". Your OrderID is: " + order.orderID ;

            // [Accessing token from the shared prefs]
            JSONArray tokens = new JSONArray();
            tokens.put(prefs.getString(Store.KEY_FCM_TOKEN , null));

            JSONObject data = new JSONObject();
            data.put(Store.KEY_FCM_TOKEN, Store.CURRENT_USER.fcmToken);
            data.put(Store.NOTIF_MESSAGE, text);
            data.put(Store.NOTIF_TITLE , title);
            data.put(Store.REMOTE_MESSAGE_TYPE, Store.REMOTE_MESSAGE_ORDER_PLACED);

            JSONObject body = new JSONObject();
            body.put(Store.REMOTE_MSG_DATA , data);
            body.put(Store.REMOTE_MSG_TO , tokens.get(0));  /* TODO: Check with to and registration_ids */

            sendNotification(body.toString());
        } catch (Exception e) {
            toast(e.getMessage());
        }

    }

    private void init() {
        prefs = getSharedPreferences(Store.KEY_SHARED_PREFS_USER , MODE_PRIVATE);
        managerCompat = NotificationManagerCompat.from(this);
        service = (Service) getIntent().getSerializableExtra(Store.KEY_EXTRA_INTENT_SERVICE);
        titleView = findViewById(R.id.view_service_title);
        shortDescView = findViewById(R.id.view_service_short);
        longDescView = findViewById(R.id.view_service_long);
        chatWithSellerBtn = findViewById(R.id.chat_with_seller_button);
        placeOrderBtn = findViewById(R.id.place_order_button);
        buttonLayout = findViewById(R.id.buttons_layout);
    }

    private void setData() {
        if (service.servedByUser.userID.equals(Store.CURRENT_USER_ID)) {
            buttonLayout.setVisibility(View.GONE);
        }
        titleView.setText(service.title);
        shortDescView.setText(MessageFormat.format("{0}\n{1}", getResources().getString(R.string.key_highlights), service.short_description));
        longDescView.setText(service.long_description);

        Fragment fragment = new ImageSliderFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imagelist", (ArrayList<String>) service.serviceImages);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.view_images_slider_layout, fragment).commit();
    }

    private void toast(String text) {
        Toast.makeText(ViewServiceActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String message) {
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
                                        toast("Error : " + ((JSONObject) results.get(0)).getString("error"));
                                        return;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            toast("Notification sent successfully");
                        } else {
                            toast("Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        toast(t.getMessage());
                    }
                });
    }

}