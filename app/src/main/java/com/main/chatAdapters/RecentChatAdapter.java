package com.main.chatAdapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.main.chatModel.ActiveUser;
import com.main.chatModel.ChatMessage;
import com.main.chatModel.User;
import com.main.chatapplication.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatAdapter.ViewHolder> {

    List<ChatMessage> messages;
    ClickListener listener;

    public RecentChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void setOnClickListener(ClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_item_layout , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecentChatAdapter.ViewHolder holder, int position) {
        holder.setUserData(messages.get(position) , listener);
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTv , lastMessageView;
        AppCompatImageView imageView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            userNameTv = itemView.findViewById(R.id.buy_service_title);
            imageView = itemView.findViewById(R.id.userImageView);
            lastMessageView = itemView.findViewById(R.id.buy_service_desc_view);
            itemView.findViewById(R.id.buy_author_view).setVisibility(View.GONE);
        }

        private void setUserData(ChatMessage message , ClickListener listener) {
            userNameTv.setText(message.recentName);
            lastMessageView.setText(message.message);

            Picasso.get()
                    .load(message.recentImage)
                    .fit()
                    .centerCrop()
                    .transform(new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .cornerRadiusDp(40)
                            .oval(false)
                            .build())
                    .into(imageView);

            itemView.setOnClickListener(v -> {
                listener.onClickListener(message.recentID);
            });
        }
    }

    public interface ClickListener {
        void onClickListener(String userID);
    }
}
