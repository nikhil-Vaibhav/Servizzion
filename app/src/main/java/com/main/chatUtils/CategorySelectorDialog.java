package com.main.chatUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.main.chatAdapters.CategoryAdapter;
import com.main.chatListeners.ObjectPassListener;
import com.main.chatapplication.R;
import com.main.chatModel.*;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategorySelectorDialog extends AppCompatDialogFragment implements ObjectPassListener {

    RecyclerView catRecycler;
    CategoryAdapter adapter;
    List<String> categories;

    String selectedFilter;
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.custom_dialog_layout , null);
        init(view);
        builder.setView(view)
                .setTitle("Select A Category")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    SharedPreferences prefs = requireActivity().getSharedPreferences(Store.KEY_SHARED_PREFS , Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Store.KEY_SHARED_PREFS_CHOSEN_FILTER , selectedFilter);
                    editor.apply();
                    Toast.makeText(requireActivity(), selectedFilter, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {});

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        buildRecycler();
    }

    private void init(View view) {
        categories = new ArrayList<>();
        catRecycler = view.findViewById(R.id.category_recycler);
    }

    private void buildRecycler() {
        categories = Arrays.asList(requireActivity().getResources().getStringArray(R.array.categoryList));
        adapter = new CategoryAdapter(categories , this);
        catRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        catRecycler.setHasFixedSize(true);
        catRecycler.setAdapter(adapter);
    }

    @Override
    public void passObject(Object... data) {
        selectedFilter = (String) data[0];
    }
}
