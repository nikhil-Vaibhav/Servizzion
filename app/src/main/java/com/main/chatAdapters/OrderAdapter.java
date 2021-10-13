package com.main.chatAdapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.main.chatListeners.IntentListener;
import com.main.chatModel.Order;
import com.main.chatModel.User;
import com.main.chatUtils.Store;
import com.main.chatapplication.HelperActivity;
import com.main.chatapplication.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    IntentListener listener;
    ArrayList<Order> orders;

    public void setOnClickListener(IntentListener listener) {
        this.listener = listener;
    }
    public OrderAdapter(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_item_layout , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderAdapter.ViewHolder holder, int position) {
        holder.setData(orders.get(position) , listener);
    }
    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleTv;
        private final TextView userNameTv;
        private final TextView orderIdView;
        private final AppCompatImageView imageView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.buy_service_title);
            userNameTv = itemView.findViewById(R.id.buy_service_desc_view);
            imageView = itemView.findViewById(R.id.userImageView);
            orderIdView = itemView.findViewById(R.id.buy_author_view);
        }

        private void setData(Order order , IntentListener listener) {
            titleTv.setText(order.service.title);
            userNameTv.setText(order.sellerUser.userName);
            orderIdView.setText(MessageFormat.format("#{0}", order.orderID));

            if(order.service.serviceImages.size() > 0) {
                Picasso.get()
                        .load(order.service.serviceImages.get(0))
                        .fit()
                        .transform(new RoundedTransformationBuilder()
                                .borderColor(Color.BLACK)
                                .cornerRadiusDp(40)
                                .oval(false)
                                .build())
                        .centerCrop()
                        .into(imageView);
            }

            itemView.setOnClickListener(v -> {
                listener.startActivity(order.service);
            });
        }
    }

}
