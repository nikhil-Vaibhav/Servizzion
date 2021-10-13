package com.main.chatAdapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatModel.Service;
import com.main.chatModel.User;
import com.main.chatUtils.Store;
import com.main.chatapplication.MainActivity;
import com.main.chatapplication.R;
import com.main.chatapplication.ViewServiceActivity;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<Service> services;

    public UserAdapter(List<Service> services) {
        this.services = services;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_item_layout, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.ViewHolder holder, int position) {
        holder.setUserData(services.get(position));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        TextView service_title_view , author_name_view , service_desc_view , tag_view;
        AppCompatImageView imageView;
        public ViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            service_title_view = itemView.findViewById(R.id.buy_service_title);
            service_desc_view = itemView.findViewById(R.id.buy_service_desc_view);
            author_name_view = itemView.findViewById(R.id.buy_author_view);
            tag_view = itemView.findViewById(R.id.buy_service_tag);
            imageView = itemView.findViewById(R.id.userImageView);
        }

        private void setUserData(Service service) {
            service_title_view.setText(service.title);
            service_desc_view.setText(service.short_description);
            author_name_view.setText(service.servedByUser.userName);
            tag_view.setText(service.category);
            tag_view.setVisibility(View.VISIBLE);

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
                        .into(imageView);
            }
            itemView.setOnClickListener(v -> {
                addUserToSharedPrefs(service.servedByUser);
                Intent intent = new Intent(MainActivity.context , ViewServiceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //[Flag for starting new activity from outside activity]
                intent.putExtra(Store.KEY_EXTRA_INTENT_SERVICE , service);
                MainActivity.context.startActivity(intent);
            });
        }

        private void addUserToSharedPrefs(User user) {
            SharedPreferences prefs = context.getSharedPreferences(Store.KEY_SHARED_PREFS_USER , Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(user.userID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        editor.putString(Store.KEY_FCM_TOKEN , documentSnapshot.getString(Store.KEY_FCM_TOKEN));
                        editor.apply();
                    });


        }
    }

}
