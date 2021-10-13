package com.main.chatapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatModel.ChatMessage;
import com.main.chatModel.User;
import com.main.chatAdapters.ChatAdapter;
import com.main.chatUtils.ApiClient;
import com.main.chatUtils.MessagingAPI;
import com.main.chatUtils.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatScreenActivity extends AppCompatActivity {


    SharedPreferences prefs;
    ChatAdapter adapter;
    RecyclerView chatRecycler;
    List<ChatMessage> messages = new ArrayList<>();
    FirebaseFirestore database;

    private String conversationID = null;
    private TextInputEditText new_message_editText;
    private TextView send_text_button, send_image_button;
    private String receiverID;
    private User receiverUser;
    private boolean isReceiverAvailable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        init();
        loadReceiverDetails();
        setUpToolbar();
        buildRecycler();
        setListeners();
        listenMessages();
    }

    private void setUpToolbar() {
        setTitle(receiverUser.userName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent i = new Intent(this, HelperActivity.class);
        i.removeExtra(Store.EXTRA_SELECTED_TASK);
        i.putExtra(Store.EXTRA_SELECTED_TASK, item.getItemId());
        i.putExtra(Store.KEY_CHAT_WITH_USER, receiverUser);
        startActivity(i);
        return true;
    }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Store.KEY_CHAT_WITH_USER);
        receiverID = receiverUser.userID;
    }

    private void init() {
        prefs = getSharedPreferences(Store.KEY_SHARED_PREFS_USER, MODE_PRIVATE);
        database = FirebaseFirestore.getInstance();
        new_message_editText = findViewById(R.id.new_message_et);
        send_text_button = findViewById(R.id.send_button_tv);
        send_image_button = findViewById(R.id.send_Image_tv);
        chatRecycler = findViewById(R.id.chat_recycler);
    }

    private void buildRecycler() {
        adapter = new ChatAdapter(Store.CURRENT_USER_ID, messages);
        chatRecycler.setHasFixedSize(true);
        chatRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        chatRecycler.setAdapter(adapter);
    }

    private void setListeners() {
        send_text_button.setOnClickListener(v -> {
            if(new_message_editText.getText().toString().trim().isEmpty()) return;
            sendMessage();
        });
        send_image_button.setOnClickListener(v -> {
            toast("Loading");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            imageLauncher.launch(intent);
        });
        database.collection(Store.KEY_COLLECTION_USER).document(receiverID)
                .addSnapshotListener(this, (value, error) -> {
                     if(error != null || value == null) return;
                     isReceiverAvailable = Integer.parseInt(String.valueOf(value.get(Store.KEY_AVAILABILITY))) == 1;
                });
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    ActivityResultLauncher<Intent> imageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if(uri == null) {
                        return;
                    }

                    String filePath = System.currentTimeMillis() + receiverUser.userID + getFileExtension(uri);
                    FirebaseStorage.getInstance().getReference(Store.CHAT_IMAGE_STORAGE).child(filePath).putFile(uri)
                            .addOnSuccessListener(taskSnapshot -> {
                                if (taskSnapshot.getMetadata() != null) {
                                    if (taskSnapshot.getMetadata().getReference() != null) {
                                        Task<Uri> imageResult = taskSnapshot.getStorage().getDownloadUrl();
                                        imageResult.addOnSuccessListener(uri1 -> {
                                            sendImage(uri1.toString());
                                        });
                                    }
                                }
                            });
                }
            });

    private void sendImage(String imagePath) {
        Map<String, Object> message = new HashMap<>();
        message.put(Store.KEY_MESSAGE, Objects.requireNonNull(new_message_editText.getText()).toString());
        message.put(Store.KEY_TIME_STAMP, new Date());
        message.put(Store.KEY_SENDER_ID, Store.CURRENT_USER_ID);
        message.put(Store.KEY_RECEIVER_ID, receiverID);
        message.put(Store.KEY_IMAGE_IN_MESSAGE_PATH, imagePath);

        toast("Success");
        database.collection(Store.KEY_COLLECTION_CHAT)
                .add(message);
    }

    private void sendMessage() {
        Map<String, Object> message = new HashMap<>();
        message.put(Store.KEY_MESSAGE, Objects.requireNonNull(new_message_editText.getText()).toString());
        message.put(Store.KEY_TIME_STAMP, new Date());
        message.put(Store.KEY_SENDER_ID, Store.CURRENT_USER_ID);
        message.put(Store.KEY_RECEIVER_ID, receiverID);

        database.collection(Store.KEY_COLLECTION_CHAT)
                .add(message);

        if(conversationID == null) {
            Map<String, Object> conversation = new HashMap<>();
            conversation.put(Store.KEY_LAST_MESSAGE, Objects.requireNonNull(new_message_editText.getText()).toString());
            conversation.put(Store.KEY_TIME_STAMP, new Date());
            conversation.put(Store.KEY_SENDER_ID, Store.CURRENT_USER_ID);
            conversation.put(Store.KEY_SENDER_NAME, Store.CURRENT_USER.userName);
            conversation.put(Store.KEY_SENDER_IMAGE, Store.CURRENT_USER.profilePicturePath);
            conversation.put(Store.KEY_RECEIVER_ID, receiverID);
            conversation.put(Store.KEY_RECEIVER_NAME, receiverUser.userName);
            conversation.put(Store.KEY_RECEIVER_IMAGE, receiverUser.profilePicturePath);
            addConversation(conversation);
        } else {
            updateConversation(Objects.requireNonNull(new_message_editText.getText()).toString());
        }

        if (!isReceiverAvailable) {
            try {
                String title = "New Message\n";
                String text = Store.CURRENT_USER.userName + ": " + new_message_editText.getText().toString() + "\n";

                // [Accessing token from the shared prefs]
                JSONArray tokens = new JSONArray();
                tokens.put(prefs.getString(Store.KEY_FCM_TOKEN, null));

                JSONObject data = new JSONObject();
                data.put(Store.KEY_FCM_TOKEN, Store.CURRENT_USER.fcmToken);
                data.put(Store.NOTIF_MESSAGE, text);
                data.put(Store.NOTIF_TITLE , title);
                data.put(Store.REMOTE_MESSAGE_TYPE, Store.REMOTE_MESSAGE_CHAT);

                JSONObject body = new JSONObject();
                body.put(Store.REMOTE_MSG_DATA, data);
                body.put(Store.REMOTE_MSG_TO, tokens.get(0));

                sendNotification(body.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        new_message_editText.setText("");
    }

    private void listenMessages() {
        database.collection(Store.KEY_COLLECTION_CHAT)
                .whereEqualTo(Store.KEY_RECEIVER_ID, receiverID)
                .whereEqualTo(Store.KEY_SENDER_ID, Store.CURRENT_USER_ID)
                .addSnapshotListener(eventListener);

        database.collection(Store.KEY_COLLECTION_CHAT)
                .whereEqualTo(Store.KEY_RECEIVER_ID, Store.CURRENT_USER_ID)
                .whereEqualTo(Store.KEY_SENDER_ID, receiverID)
                .addSnapshotListener(eventListener);

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

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            Toast.makeText(this, "Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (value != null) {
            int count = messages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = documentChange.getDocument().getString(Store.KEY_SENDER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Store.KEY_MESSAGE);
                    chatMessage.receiverID = documentChange.getDocument().getString(Store.KEY_RECEIVER_ID);
                    chatMessage.imageInMessage = documentChange.getDocument().getString(Store.KEY_IMAGE_IN_MESSAGE_PATH);
                    chatMessage.date = documentChange.getDocument().getDate(Store.KEY_TIME_STAMP);
                    chatMessage.timeStamp = getReadableDate(documentChange.getDocument().getDate(Store.KEY_TIME_STAMP));
                    messages.add(chatMessage);
                }
            }

            Collections.sort(messages, (a, b) -> a.date.compareTo(b.date));
            if (count == 0) adapter.notifyDataSetChanged();
            else {
                adapter.notifyItemRangeInserted(messages.size(), messages.size());
                chatRecycler.smoothScrollToPosition(messages.size() - 1);
            }
        }

        if(conversationID == null) {
            checkRecents();
        }
    };

    private String getReadableDate(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversation(Map<String, Object> message) {
        database.collection(Store.KEY_COLLECTION_CONVERSATIONS).add(message).addOnSuccessListener(documentReference -> conversationID = documentReference.getId());
    }

    private void updateConversation(String message) {
        database.collection(Store.KEY_COLLECTION_CONVERSATIONS).document(conversationID)
                .update(Store.KEY_LAST_MESSAGE , message);
    }

    private void checkRecents() {
        if(messages.size() != 0) {
            checkForRecent(Store.CURRENT_USER_ID , receiverUser.userID);
            checkForRecent(receiverUser.userID , Store.CURRENT_USER_ID);
        }
    }

    private void checkForRecent(String senderID , String receiverID) {
        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Store.KEY_SENDER_ID , senderID)
                .whereEqualTo(Store.KEY_RECEIVER_ID , receiverID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots.isEmpty()) return;
                    conversationID = queryDocumentSnapshots.getDocuments().get(0).getId();
                });
    }
}