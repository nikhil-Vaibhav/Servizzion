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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.main.chatAdapters.OrderAdapter;
import com.main.chatAdapters.OrderRequestAdapter;
import com.main.chatListeners.IntentListener;
import com.main.chatModel.Order;
import com.main.chatModel.Service;
import com.main.chatUtils.Store;
import com.main.chatapplication.R;
import com.main.chatapplication.ViewServiceActivity;

import java.util.ArrayList;

public class DeliveryRequestFragment extends Fragment implements IntentListener {

    private RecyclerView recyclerView;
    private OrderRequestAdapter adapter;
    Handler handler;
    private final CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER);
    private ListenerRegistration listenerReg;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_layout , container , false);
        init(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listenerReg = usersCollection.document(Store.CURRENT_USER_ID).collection(Store.KEY_COLLECTION_ORDER_REQUESTS)
                .addSnapshotListener((value, error) -> {
                    if(value == null || error != null) return;
                    ArrayList<Order> orders = new ArrayList<>();
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        if(documentChange.getType() == DocumentChange.Type.ADDED) {
                            Order order = documentChange.getDocument().toObject(Order.class);
                            orders.add(order);
                        }
                    }
                    handler.post(() -> {
                        showOrders(orders);
                    });
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        listenerReg.remove();
    }

    private void init(View view) {
        handler = new Handler();
        recyclerView = view.findViewById(R.id.order_recycler);
    }

    private void showOrders(ArrayList<Order> orderArrayList) {
        adapter = new OrderRequestAdapter(orderArrayList);
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
