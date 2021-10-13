package com.main.chatFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.main.chatAdapters.RecentChatAdapter;
import com.main.chatModel.ChatMessage;
import com.main.chatModel.User;
import com.main.chatUtils.Store;
import com.main.chatapplication.ChatScreenActivity;
import com.main.chatapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecentConversationsFragment extends Fragment implements RecentChatAdapter.ClickListener {

    List<ChatMessage> conversations;
    RecyclerView recyclerView;
    RecentChatAdapter adapter;
    CollectionReference conversationsCollection;

    /*
    TODO
     */


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_layout , container , false);
        init(view);
        listenConversations();
        return view;
    }

    private void init(View view) {
        conversationsCollection = FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_CONVERSATIONS);
        recyclerView = view.findViewById(R.id.order_recycler);
        conversations = new ArrayList<>();
    }

    private void listenConversations() {
        conversationsCollection.whereEqualTo(Store.KEY_SENDER_ID , Store.CURRENT_USER_ID).addSnapshotListener(eventListener);
        conversationsCollection.whereEqualTo(Store.KEY_RECEIVER_ID , Store.CURRENT_USER_ID).addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(error != null || value == null) return;

            for(DocumentChange documentChange : value.getDocumentChanges()) {
                String senderID = documentChange.getDocument().getString(Store.KEY_SENDER_ID);
                String receiverID = documentChange.getDocument().getString(Store.KEY_RECEIVER_ID);

                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage message = new ChatMessage();
                    message.senderID = senderID;
                    message.receiverID = receiverID;

                    if(Store.CURRENT_USER_ID.equals(message.senderID)) {
                        message.recentID = receiverID;
                        message.recentImage = documentChange.getDocument().getString(Store.KEY_RECEIVER_IMAGE);
                        message.recentName = documentChange.getDocument().getString(Store.KEY_RECEIVER_NAME);
                    } else {
                        message.recentID = senderID;
                        message.recentImage = documentChange.getDocument().getString(Store.KEY_SENDER_IMAGE);
                        message.recentName = documentChange.getDocument().getString(Store.KEY_SENDER_NAME);
                    }

                    message.message = documentChange.getDocument().getString(Store.KEY_LAST_MESSAGE);
                    message.timeStamp = getReadableDate(documentChange.getDocument().getDate(Store.KEY_TIME_STAMP));
                    conversations.add(message);
                } else if(documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for(int i=0; i<conversations.size(); i++) {
                        if(conversations.get(i).senderID.equals(senderID) && conversations.get(i).receiverID.equals(receiverID)) {
                            conversations.get(i).message = documentChange.getDocument().getString(Store.KEY_LAST_MESSAGE);
                            conversations.get(i).timeStamp = getReadableDate(documentChange.getDocument().getDate(Store.KEY_TIME_STAMP));
                            break;
                        }
                    }
                }
            }
            buildRecycler();
        }
    };

    private void buildRecycler() {
        Collections.sort(conversations, (a, b) -> a.date.compareTo(b.date));

        adapter = new RecentChatAdapter(conversations);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(this);
    }

    private String getReadableDate(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    public void onClickListener(String userID) {
        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    Intent intent = new Intent(requireActivity() , ChatScreenActivity.class);
                    intent.putExtra(Store.KEY_CHAT_WITH_USER , user);
                    startActivity(intent);
                });
    }
}
