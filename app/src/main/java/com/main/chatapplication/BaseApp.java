package com.main.chatapplication;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.main.chatModel.Order;
import com.main.chatUtils.Store;

public class BaseApp extends Application {

    NotificationManagerCompat managerCompat;

    @Override
    public void onCreate() {
        super.onCreate();

        managerCompat = NotificationManagerCompat.from(this);
        createNotificationChannels();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //Order Notification Channel
            NotificationChannel orderChannel = new NotificationChannel(
                    Store.CHANNEL_ORDER_NOTIFICATIONS ,
                    "Notifications for Orders" ,
                    NotificationManager.IMPORTANCE_HIGH
            );
            orderChannel.setDescription("This channel handles notifications related to orders placed and received by you.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(orderChannel);
        }
    }


}
