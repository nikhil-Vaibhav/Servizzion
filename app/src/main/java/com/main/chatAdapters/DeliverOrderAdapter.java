package com.main.chatAdapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.main.chatListeners.IntentListener;
import com.main.chatModel.Order;
import com.main.chatUtils.Store;
import com.main.chatapplication.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DeliverOrderAdapter extends RecyclerView.Adapter<DeliverOrderAdapter.ViewHolder> {

    IntentListener listener;
    ArrayList<Order> orders;

    public void setOnClickListener(IntentListener listener) {
        this.listener = listener;
    }
    public DeliverOrderAdapter(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_item_layout , parent , false);
        return new ViewHolder(view , parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DeliverOrderAdapter.ViewHolder holder, int position) {
        holder.setData(orders.get(position) , listener);
    }
    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleTv;
        private final TextView userNameTv;
        private final TextView statusView;
        private final Context context;
        private final AppCompatImageView imageView;
        private final Handler handler;

        public ViewHolder(@NonNull @NotNull View itemView , Context context) {
            super(itemView);
            this.context = context;
            handler = new Handler();
            titleTv = itemView.findViewById(R.id.buy_service_title);
            userNameTv = itemView.findViewById(R.id.buy_service_desc_view);
            statusView = itemView.findViewById(R.id.buy_author_view);
            imageView = itemView.findViewById(R.id.userImageView);

            statusView.setText(context.getString(R.string.status_delivered));
            statusView.setTextColor(context.getResources().getColor(R.color.green));
        }

        private void setData(Order order , IntentListener listener) {
            titleTv.setText(order.service.title);
            userNameTv.setText(order.sellerUser.userName);
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
