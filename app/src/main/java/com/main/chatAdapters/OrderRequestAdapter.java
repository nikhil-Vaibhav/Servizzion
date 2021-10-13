package com.main.chatAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatListeners.IntentListener;
import com.main.chatModel.Order;
import com.main.chatServices.NotificationService;
import com.main.chatUtils.Store;
import com.main.chatapplication.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderRequestAdapter extends RecyclerView.Adapter<OrderRequestAdapter.ViewHolder> {

    IntentListener listener;
    private static ArrayList<Order> orders;


    public void setOnClickListener(IntentListener listener) {
        this.listener = listener;
    }

    public OrderRequestAdapter(ArrayList<Order> o) {
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
    public void onBindViewHolder(@NonNull @NotNull OrderRequestAdapter.ViewHolder holder, int position) {
        holder.setData(orders.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView serviceTitle, orderForUserName;
        private final Context context;
        private final AppCompatImageView profileImageView;
        private final Button markDeliverBtn;
        private final CollectionReference reqCollection;

        public ViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            reqCollection = FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(Store.CURRENT_USER_ID).collection(Store.KEY_COLLECTION_ORDER_REQUESTS);
            orderForUserName = itemView.findViewById(R.id.order_for_user);
            serviceTitle = itemView.findViewById(R.id.pending_service_title);
            profileImageView = itemView.findViewById(R.id.user_profile_image);
            markDeliverBtn = itemView.findViewById(R.id.markDeliverButton);
        }

        private void setData(Order order, IntentListener listener) {
            serviceTitle.setText(order.service.title);
            orderForUserName.setText(order.buyerUser.userName);

            if(order.service.serviceImages.size() > 0) {

                Picasso.get()
                        .load(order.service.serviceImages.get(0))
                        .fit()
                        .transform(new RoundedTransformationBuilder()
                                .borderColor(Color.BLACK)
                                .cornerRadiusDp(50)
                                .oval(false)
                                .build())
                        .centerCrop()
                        .into(profileImageView);
            }

            markDeliverBtn.setOnClickListener(v -> {
                markDeliverBtn.setText(context.getString(R.string.loading_string));
                confirmOrderDelivery(order);
            });

            itemView.setOnClickListener(view -> {
                listener.startActivity(order.service);
            });
        }

        private void confirmOrderDelivery(Order order) {
            /* TODO: Send notification to seller */
            //Add to delivered of seller
            FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(order.sellerUser.userID).collection(Store.KEY_COLLECTION_DELIVERED_ORDERS)
                    .add(order);

            //Remove from request
            reqCollection.document(order.orderID).delete();

            int pos = getBindingAdapterPosition();
            orders.remove(pos);
            notifyItemRemoved(pos);
            Toast.makeText(context, "Delivery successful", Toast.LENGTH_SHORT).show();
        }

    }

}
