package com.main.chatFragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatAdapters.ThisUserServiceAdapter;
import com.main.chatListeners.IntentListener;
import com.main.chatModel.Service;
import com.main.chatModel.User;
import com.main.chatUtils.Store;
import com.main.chatapplication.HelperActivity;
import com.main.chatapplication.R;
import com.main.chatapplication.ViewServiceActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewThisUserServicesFragment extends Fragment implements IntentListener{

    User active_user;
    private RecyclerView recyclerView;
    private ThisUserServiceAdapter adapter;
    private List<Service> services;
    Handler handler;

    public ViewThisUserServicesFragment(User user) {
        this.active_user = user;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_this_services_layout , container , false);
        init(view);
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LoadServices.start();
    }

    private void init(View view) {
        handler = new Handler();
        recyclerView = view.findViewById(R.id.this_user_services_recycler);
        services = new ArrayList<>();
    }

    Thread LoadServices = new Thread(() -> {
        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(active_user.userID).collection(Store.KEY_COLLECTION_USER_SERVICES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Service> serviceList = new ArrayList<>();
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots ) {
                        Service service = documentSnapshot.toObject(Service.class);
                        if(service == null) service = new Service();
                        serviceList.add(service);
                    }
                    handler.post(() -> {
                        buildRecycler(serviceList);
                    });
                });
    });

    private void buildRecycler(List<Service> serviceList) {
        adapter = new ThisUserServiceAdapter(serviceList );
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(this);
    }


    @Override
    public void startActivity(Service service) {
        Intent intent = new Intent(requireActivity() , ViewServiceActivity.class);
        intent.putExtra(Store.KEY_EXTRA_INTENT_SERVICE , service);
        startActivity(intent);
    }
}
