package com.main.chatAdapters;

import android.content.Context;
import android.graphics.Color;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatListeners.IntentListener;
import com.main.chatModel.ActiveUser;
import com.main.chatModel.Order;
import com.main.chatModel.User;
import com.main.chatServices.NotificationService;
import com.main.chatUtils.ApiClient;
import com.main.chatUtils.MessagingAPI;
import com.main.chatUtils.Store;
import com.main.chatapplication.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.ViewHolder> {

    IntentListener listener;
    private static ArrayList<Order> orders;


    public void setOnClickListener(IntentListener listener) {
        this.listener = listener;
    }

    public PendingOrderAdapter(ArrayList<Order> o) {
        orders = o;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PendingOrderAdapter.ViewHolder holder, int position) {
        holder.setData(orders.get(position), listener );
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView serviceTitle , orderForUserName;
        private final Context context;
        private final AppCompatImageView profileImageView;
        private final Button markDeliverBtn;

        public ViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            orderForUserName = itemView.findViewById(R.id.order_for_user);
            serviceTitle = itemView.findViewById(R.id.pending_service_title);
            profileImageView = itemView.findViewById(R.id.user_profile_image);
            markDeliverBtn = itemView.findViewById(R.id.markDeliverButton);
        }

        private void setData(Order order, IntentListener listener) {
            serviceTitle.setText(order.service.title);
            orderForUserName.setText(order.buyerUser.userName);
            Picasso.get()
                    .load(order.service.serviceImages.get(0))
                    .fit()
                    .transform(new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .cornerRadiusDp(40)
                            .oval(false)
                            .build())
                    .centerCrop()
                    .into(profileImageView);

            markDeliverBtn.setOnClickListener(v -> {
                markDeliverBtn.setClickable(false);
                sendRequestToBuyer(order);
            });

            itemView.setOnClickListener(view -> {
                listener.startActivity(order.service);
            });
        }

        private void sendRequestToBuyer(Order order) {
            /* TODO: Send notification to buyer */
            //Add to requests of buyer
            FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(order.buyerUser.userID).collection(Store.KEY_COLLECTION_ORDER_REQUESTS)
                    .document(order.orderID).set(order);

            //Remove from pending of seller
            FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(Store.CURRENT_USER_ID).collection(Store.KEY_COLLECTION_PENDING_ORDERS)
                    .document(order.orderID).delete().addOnSuccessListener(unused -> {
                        markDeliverBtn.setText(R.string.requested);
            });

            int pos = getBindingAdapterPosition();
            orders.remove(pos);
            if (getBindingAdapter() != null) {
                getBindingAdapter().notifyItemRemoved(pos);
            }

        }

    }

}
