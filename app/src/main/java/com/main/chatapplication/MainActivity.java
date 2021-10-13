package com.main.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.api.Distribution;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.main.chatAdapters.UserAdapter;
import com.main.chatModel.Order;
import com.main.chatModel.Service;
import com.main.chatUtils.CategorySelectorDialog;
import com.main.chatUtils.Store;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomSheetDialog bottomSheetDialog;
    RecyclerView recyclerView;
    UserAdapter adapter;
    Button filterBtn;
    Handler handler;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    ProgressBar progressBar;
    List<Service> services;
    TextInputEditText searchBox;
    private TextView noResultView;
    private long backPressedTime;
    private ListenerRegistration listener;
    public static Context context;
    private DocumentSnapshot lastResult;
    private boolean isScrolling;
    private int scrolledOut, currentVisible, totalItems;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        init();
        setListener();
        getToken();

        if (getSharedPreferences(Store.KEY_SHARED_PREFS_USER, MODE_PRIVATE).getInt(Store.KEY_AVAILABILITY, 0) == 1) {
            Toast.makeText(this, "User Online", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.collection(Store.KEY_COLLECTION_USER).document(Store.CURRENT_USER_ID)
                .update(Store.KEY_AVAILABILITY, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.collection(Store.KEY_COLLECTION_USER).document(Store.CURRENT_USER_ID)
                .update(Store.KEY_AVAILABILITY, 0);
    }

    private void init() {
        manager = new LinearLayoutManager(this);
        services = new ArrayList();
        handler = new Handler();
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetStyle);
        recyclerView = findViewById(R.id.user_list_recycler);
        searchBox = findViewById(R.id.searchBox);
        filterBtn = findViewById(R.id.filter_button);
        searchBox = findViewById(R.id.searchBox);
        noResultView = findViewById(R.id.no_result_view);
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        Store.CURRENT_USER.fcmToken = token;
        database.collection(Store.KEY_COLLECTION_USER).document(Store.CURRENT_USER_ID)
                .update(Store.KEY_FCM_TOKEN, token);
    }

    @Override
    protected void onStart() {
        super.onStart();
        database.collection(Store.KEY_COLLECTION_ALL_SERVICES)
                .addSnapshotListener(this, (value, error) -> {
                    if (error != null) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value == null) {
                        return;
                    }

                    List<Service> serviceList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Service service = documentSnapshot.toObject(Service.class);
                        if (service != null && !service.servedByUser.userID.equals(Store.CURRENT_USER_ID))
                            serviceList.add(service);
                        lastResult = documentSnapshot;
                    }
                    if (services.isEmpty()) services.addAll(serviceList);
                    handler.post(() -> {
                        buildRecycler(serviceList);
                    });
                });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    private void setListener() {

        searchBox.setOnTouchListener(new View.OnTouchListener() {

            final int LEFT = 0;
            final int TOP = 1;
            final int RIGHT = 2;
            final int BOTTOM = 3;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (searchBox.getRight() - searchBox.getCompoundDrawables()[RIGHT].getBounds().width())) {
                        Toast.makeText(getApplicationContext(), searchBox.getText().toString(), Toast.LENGTH_SHORT).show();
                        filterList(searchBox.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentVisible = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrolledOut = manager.findFirstVisibleItemPosition();

                if (isScrolling && totalItems == currentVisible + scrolledOut) {
                    isScrolling = false;

                    //fetch more data
                    database.collection(Store.KEY_COLLECTION_ALL_SERVICES)
                            .startAfter(lastResult)
                            .limit(5)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots.size() == 0) return;
                                List<Service> serviceList = new ArrayList<>();
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    Service service = documentSnapshot.toObject(Service.class);
                                    if (service != null && !service.servedByUser.userID.equals(Store.CURRENT_USER_ID))
                                        serviceList.add(service);
                                }

                                lastResult = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                handler.post(() -> {
                                    buildRecycler(serviceList);
                                });
                            });
                }
            }
        });
    }

    private void filterList(String filter) {
        if (filter == null) return;
        List<Service> filteredList = new ArrayList<>();
        for (Service s : services) {
            if (s.category.contains(filter)) {
                filteredList.add(s);
            }
        }

        buildRecycler(filteredList);

    }

    private void buildRecycler(List<Service> serviceList) {
        if(serviceList.size() == 0) {
            noResultView.setVisibility(View.VISIBLE);
        } else {
            noResultView.setVisibility(View.GONE);
        }
        adapter = new UserAdapter(serviceList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        Intent intent = new Intent(getApplicationContext(), ChooseServiceActivity.class);
        intent.putExtra(Store.KEY_INTENT_FRAGMENT, item.getItemId());
        startActivity(intent);
        return true;
    }
}