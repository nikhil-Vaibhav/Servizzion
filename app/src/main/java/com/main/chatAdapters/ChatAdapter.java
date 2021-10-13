package com.main.chatAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.chatModel.ChatMessage;
import com.main.chatapplication.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_SENT = 1;
    public static final int TYPE_RECEIVE = 2;

    private final String senderID;
    private final List<ChatMessage> messages;

    public ChatAdapter(String senderID, List<ChatMessage> messages) {
        this.senderID = senderID;
        this.messages = messages;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_RECEIVE) {
            return new ReceiveMessageViewHolder(inflater.inflate(R.layout.message_recieved_layout , parent , false));
        } else {
            return new SentMessageViewHolder(inflater.inflate(R.layout.message_sent_layout , parent , false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_SENT ){
            ((SentMessageViewHolder) holder).setUpMessage(messages.get(position));
        } else {
            ((ReceiveMessageViewHolder) holder).setUpMessage(messages.get(position));
        }
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).senderID.equals(senderID)) return TYPE_SENT;
        else return TYPE_RECEIVE;
    }

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView main_message_view;
        private final TextView time_stamp_view;
        public SentMessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            main_message_view = itemView.findViewById(R.id.main_message);
            time_stamp_view = itemView.findViewById(R.id.time_stamp);
        }

        private void setUpMessage(ChatMessage message) {
            main_message_view.setText(message.message);
            time_stamp_view.setText(message.timeStamp);
        }
    }

    public static class ReceiveMessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView main_message_view;
        private final TextView time_stamp_view;
        private final ImageView imageView;

        public ReceiveMessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            main_message_view = itemView.findViewById(R.id.main_message);
            time_stamp_view = itemView.findViewById(R.id.time_stamp);
            imageView = itemView.findViewById(R.id.chat_image_view);
        }


        private void setUpMessage(ChatMessage message) {
            main_message_view.setText(message.message);
            time_stamp_view.setText(message.timeStamp);
            if(message.imageInMessage != null) {
                imageView.setVisibility(View.VISIBLE);
                main_message_view.setVisibility(View.GONE);
                Picasso.get()
                        .load(message.imageInMessage)
                        .fit()
                        .centerCrop()
                        .into(imageView);
            }
        }
    }
}
