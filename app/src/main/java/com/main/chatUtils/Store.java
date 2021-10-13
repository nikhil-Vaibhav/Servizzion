package com.main.chatUtils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.main.chatModel.User;

import java.util.HashMap;

public class Store {

    public static boolean FIRST_VISIT = true;
    public static String CURRENT_USER_ID = "current userId";
    public static User CURRENT_USER = new User();
    public static final CollectionReference userCollection = FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER);

    /*
    TODO: Change These Constants values
     */
    public static final String OWNER_EMAIL = "developernisanj@gmail.com";
    public static final String OWNER_PASSWORD = "sanjeev_nidhi_2116";

    public static final String CHAT_IMAGE_STORAGE = "chatImages";
    public static final String KEY_SHARED_PREFS = "shared data";
    public static final String KEY_SHARED_PREFS_CHOSEN_FILTER = "chosen filter";
    public static final String KEY_SHARED_PREFS_USER = "shared user details";
    public static final String KEY_COLLECTION_USER = "users";
    public static final String KEY_COLLECTION_SUGGESTIONS = "suggestions";
    public static final String KEY_COLLECTION_HELP = "help";
    public static final String KEY_COLLECTION_ORDER_REQUESTS = "order requests";
    public static final String KEY_COLLECTION_USER_SERVICES = "user services";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_COLLECTION_ALL_SERVICES = "all services";
    public static final String KEY_COLLECTION_MY_ORDERS = "my orders";
    public static final String KEY_COLLECTION_PENDING_ORDERS = "pending orders";
    public static final String KEY_COLLECTION_DELIVERED_ORDERS = "delivered orders";
    public static final String KEY_COLLECTION_ACTIVE_CHAT = "active chat";
    public static final String KEY_EXTRA_INTENT_SERVICE = "intent service";
    public static final String KEY_PROFILE_IMAGE_UPLOADS = "profileImage uploads";
    public static final String KEY_SERVICE_IMAGE_UPLOADS = "serviceImage uploads";
    public static String CURRENT_USER_FOLDER_REFERENCE = "";
    public static final String KEY_AVAILABILITY = "availability";
    public static final int TASK_MY_SERVICES = 1;
    public static final int TASK_MY_ORDERS = 2;
    public static final int TASK_ORDERS_PENDING = 3;
    public static final int TASK_THIS_USER_SERVICES = 4;
    public static final int TASK_VIEW_PROFILE = 5;
    public static final int TASK_DELIVERED_ORDERS = 6;
    public static final int TASK_ACCOUNT_UPDATE = 7;
    public static final String KEY_ORDER_ID = "orderID";
    public static final String EXTRA_SELECTED_TASK = "activity class";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_USERNAME = "userName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USERBIO = "userBio";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER_PROFILE_PICTURE_PATH = "profilePicturePath";
    public static final String KEY_CHAT_WITH_USER = "Chat with";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIME_STAMP = "timeStamp";
    public static final String KEY_IMAGE_IN_MESSAGE_PATH = "imageInMessage";
    public static final String KEY_SENDER_ID = "senderID";
    public static final String KEY_RECEIVER_ID = "receiverID";
    public static final String KEY_SERVICE_ID = "serviceID";
    public static final String KEY_SERVICE_CATEGORY = "category";
    public static final String KEY_SERVICE_TITLE = "service_title";
    public static final String KEY_SERVICE_SHORT_DESC = "service_short_desc";
    public static final String KEY_SERVICE_LONG_DESC = "service_long_desc";
    public static final String KEY_SERVICE_BY_USER = "servedByUser";
    public static final String KEY_SERVICE_IMAGE_LIST = "serviceImages";
    public static final String KEY_USER_RATINGS = "userRatings";
    public static final String KEY_USER_DESCRIPTION = "userDescription";
    public static final String KEY_USER_ID = "userID";
    public static final String KEY_INTENT_FRAGMENT = "intent fragment";
    public static final String CHANNEL_ORDER_NOTIFICATIONS = "order notifications";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "last message";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_TO = "to";
    public static final String REMOTE_MESSAGE_TYPE = "remote message type";
    public static final String REMOTE_MESSAGE_CHAT = "chat message";
    public static final String REMOTE_MESSAGE_ORDER_REQUEST = "order delivery request";
    public static final String REMOTE_MESSAGE_ORDER_PLACED = "order placed";

    public static final String NOTIF_TITLE = "notify title";
    public static final String NOTIF_MESSAGE = "notify message";

    public static HashMap<String , String> headerMap = null;
    public static HashMap<String, String> getHeaderMap() {
        if(headerMap == null) {
            headerMap = new HashMap<>();
            headerMap.put(REMOTE_MSG_AUTHORIZATION , "key=AAAAsZ7usJ0:APA91bEGDP13DYurpB1YxNshPAHdYDJaRCI7voJyToGkcmED52Zvy746baOar1WmlPb-Taukp4DWkA8OLz_Y0mcp07X4BZR5uBT7O8tay9vtkWT_vEk3ji2-6nBYyKFzyX0abk5CECk_");
            headerMap.put(REMOTE_MSG_CONTENT_TYPE , "application/json");
        }
        return headerMap;
    }
}
