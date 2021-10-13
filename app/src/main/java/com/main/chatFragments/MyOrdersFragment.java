package com.main.chatFragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.chatAdapters.OrderAdapter;
import com.main.chatListeners.IntentListener;
import com.main.chatModel.Order;
import com.main.chatModel.Service;
import com.main.chatModel.User;
import com.main.chatUtils.Store;
import com.main.chatapplication.R;
import com.main.chatapplication.ViewServiceActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyOrdersFragment extends Fragment implements IntentListener {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    Handler handler;
    private final CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER);


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_layout, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        handler = new Handler();
        recyclerView = view.findViewById(R.id.order_recycler);
    }

    @Override
    public void onStart() {
        super.onStart();
        LoadOrders.start();
    }

    Thread LoadOrders = new Thread(() -> {
        usersCollection.document(Store.CURRENT_USER_ID).collection(Store.KEY_COLLECTION_MY_ORDERS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Order> orders = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Order order = documentSnapshot.toObject(Order.class);
                        orders.add(order);
                    }
                    handler.post(() -> {
                        showOrders(orders);
                    });
                });
    });

    private void showOrders(ArrayList<Order> orderArrayList) {
        adapter = new OrderAdapter(orderArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(this);
    }

    @Override
    public void startActivity(Service service) {
        Intent intent = new Intent(requireActivity(), ViewServiceActivity.class);
        intent.putExtra(Store.KEY_EXTRA_INTENT_SERVICE, service);
        startActivity(intent);
    }
}
