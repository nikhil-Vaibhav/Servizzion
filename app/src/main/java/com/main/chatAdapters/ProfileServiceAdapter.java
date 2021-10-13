package com.main.chatAdapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatModel.Service;
import com.main.chatListeners.IntentListener;
import com.main.chatUtils.Store;
import com.main.chatapplication.HelperActivity;
import com.main.chatapplication.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileServiceAdapter extends RecyclerView.Adapter<ProfileServiceAdapter.ViewHolder> {

    IntentListener intentListener ;
    List<Service> services ;

    public ProfileServiceAdapter(List<Service> services , IntentListener listener ) {
        this.services = services;
        this.intentListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_item_layout , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProfileServiceAdapter.ViewHolder holder, int position) {
        holder.setUserData(services.get(position) , intentListener );
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title_tv , short_desc_tv ;
        AppCompatImageView serviceImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.buy_service_title);
            short_desc_tv = itemView.findViewById(R.id.buy_service_desc_view);
            serviceImage = itemView.findViewById(R.id.userImageView);
            itemView.findViewById(R.id.buy_author_view).setVisibility(View.GONE);
        }

        private void setUserData(Service service , IntentListener intentListener) {
            title_tv.setText(service.title);
            short_desc_tv.setText(service.short_description);
            short_desc_tv.setLines(3);
            if(service.serviceImages.size() > 0) {
                Picasso.get()
                        .load(service.serviceImages.get(0))
                        .fit()
                        .transform(new RoundedTransformationBuilder()
                                .borderColor(Color.BLACK)
                                .cornerRadiusDp(8)
                                .oval(false)
                                .build())
                        .centerCrop()
                        .into(serviceImage);
            }
            itemView.setOnClickListener(v -> {
                intentListener.startActivity(service);
            });

        }
    }
}
