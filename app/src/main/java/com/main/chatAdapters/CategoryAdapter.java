package com.main.chatAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.chatListeners.ObjectPassListener;
import com.main.chatapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    static List<String> categories;
    static ObjectPassListener listener;

    public CategoryAdapter(List<String> categoryList, ObjectPassListener objectPassListener) {
        categories = categoryList;
        listener = objectPassListener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout , parent , false);
        return new ViewHolder(view );
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryAdapter.ViewHolder holder, int position) {
        holder.titleView.setText(categories.get(position));
    }
    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RadioButton radioButton;
        TextView titleView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.cat_item_title);
            radioButton = itemView.findViewById(R.id.selected_radio);
            itemView.setOnClickListener(v -> {
                radioButton.setEnabled(!radioButton.isEnabled());
                listener.passObject(categories.get(getBindingAdapterPosition()));
            });
        }


    }

}
