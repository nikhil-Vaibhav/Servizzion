package com.main.chatServices;

import android.app.Notification;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.main.chatModel.Order;
import com.main.chatUtils.Store;
import com.main.chatapplication.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessagingService extends FirebaseMessagingService {

    public static final String TAG = "message api";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sendNotificationForOrderPlaced(remoteMessage.getData());
    }

    /*
    TODO: A system for sending notification as payload so that I can connect pending intent to specific activities
     */

    private void sendNotificationForOrderPlaced(Map<String , String> message) {
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);

        String title = message.get(Store.NOTIF_TITLE);
        String text = message.get(Store.NOTIF_MESSAGE);

        Notification notification = new NotificationCompat.Builder(this, Store.CHANNEL_ORDER_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[]{0,200,700,300,700,300})
                .build();

        managerCompat.notify(1, notification);
    }
}
