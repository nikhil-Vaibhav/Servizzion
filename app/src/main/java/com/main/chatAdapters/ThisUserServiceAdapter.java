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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatListeners.IntentListener;
import com.main.chatModel.Order;
import com.main.chatModel.Payment;
import com.main.chatModel.Service;
import com.main.chatModel.User;
import com.main.chatUtils.Store;
import com.main.chatapplication.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ThisUserServiceAdapter extends RecyclerView.Adapter<ThisUserServiceAdapter.ViewHolder> {

    IntentListener listener;
    List<Service> services ;
    public void setOnClickListener(IntentListener listener) {
        this.listener = listener;
    }

    public ThisUserServiceAdapter(List<Service> services) {
        this.services = services;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_item_layout , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ThisUserServiceAdapter.ViewHolder holder, int position) {
        holder.setUserData(services.get(position) , listener);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title_tv , desc_view;
        AppCompatImageView imageView;

        public ViewHolder(@NonNull @NotNull View itemView ) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.buy_service_title);
            desc_view = itemView.findViewById(R.id.buy_service_desc_view);
            imageView = itemView.findViewById(R.id.userImageView);
            itemView.findViewById(R.id.buy_author_view).setVisibility(View.GONE);
        }

        private void setUserData(Service service , IntentListener listener) {

            title_tv.setText(service.title);
            desc_view.setText(service.short_description);
            Picasso.get()
                    .load(service.serviceImages.get(0))
                    .fit()
                    .transform(new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .cornerRadiusDp(40)
                            .oval(false)
                            .build())
                    .centerCrop()
                    .into(imageView);
            itemView.setOnClickListener(view -> {
                listener.startActivity(service);
            });
        }
    }
}
