package com.main.chatFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.main.chatAdapters.ProfileServiceAdapter;
import com.main.chatListeners.ObjectPassListener;
import com.main.chatModel.Service;
import com.main.chatListeners.IntentListener;
import com.main.chatUtils.ConfirmationDialog;
import com.main.chatUtils.Store;
import com.main.chatUtils.Upload;
import com.main.chatapplication.R;
import com.main.chatapplication.ServiceInputActivity;
import com.main.chatapplication.ViewServiceActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyServicesFragment extends Fragment implements IntentListener, ObjectPassListener {

    private RecyclerView recyclerView;
    private ProfileServiceAdapter adapter;
    private Button addServiceButton;
    private BottomSheetDialog bottomSheetDialog;
    Handler handler;
    List<Service> services;
    private Service chosenService;
    private boolean forUpdate;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_service_fragment, container, false);

        init(view);
        setListeners();
        new Thread(this::loadServices).start();
        return view;
    }

    private void init(View view) {
        forUpdate = false;
        services = new ArrayList<>();
        handler = new Handler();
        bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetStyle);
        addServiceButton = view.findViewById(R.id.add_service_btn);
        recyclerView = view.findViewById(R.id.my_service_recycler);
    }

    private void setListeners() {
        addServiceButton.setOnClickListener(v -> {
            forUpdate = false;
            profileServiceInputLauncher.launch(new Intent(requireActivity(), ServiceInputActivity.class));
        });
    }

    ActivityResultLauncher<Intent> profileServiceInputLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    if (forUpdate) updateService(result.getData());
                    else extractResultAndUpload(result.getData());
                }
            });

    private void updateService(Intent intent) {
        chosenService = (Service) intent.getSerializableExtra("updated");
        adapter.notifyItemChanged(services.indexOf(chosenService));
        showToast("Updated");
    }

    private void extractResultAndUpload(Intent data) {
        Service service = (Service) data.getSerializableExtra("KEY"); //Try with parcelable
        services.add(service);

        CollectionReference serviceCollection = FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_ALL_SERVICES);
        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(Store.CURRENT_USER_ID).collection(Store.KEY_COLLECTION_USER_SERVICES)
                .add(service)
                .addOnSuccessListener(documentReference -> {
                    showToast("Added " + documentReference.getId());
                    service.servedByUser = Store.CURRENT_USER;
                    service.serviceID = documentReference.getId();
                    documentReference.update(Store.KEY_SERVICE_BY_USER, Store.CURRENT_USER);
                    documentReference.update(Store.KEY_SERVICE_ID, documentReference.getId());
                    serviceCollection.document(service.serviceID).set(service);
                });

        adapter.notifyItemInserted(services.size() - 1);
    }


    private void loadServices() {
        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(Store.CURRENT_USER_ID).collection(Store.KEY_COLLECTION_USER_SERVICES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Service> serviceList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Service service = documentSnapshot.toObject(Service.class);
                        serviceList.add(service);
                    }
                    services.addAll(serviceList);
                    handler.post(this::buildRecycler);
                });
    }

    private void buildRecycler() {
        adapter = new ProfileServiceAdapter(services, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void startActivity(Service service) {
        chosenService = service;
        showBottomSheet();
    }

    private void showBottomSheet() {
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.bottom_sheet_dialog_updates, requireActivity().findViewById(R.id.root_bottom_updates));
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        initBottomSheetViews(view);
    }

    private void initBottomSheetViews(View view) {
        TextView op1 = view.findViewById(R.id.menu_bottom_view);
        TextView op2 = view.findViewById(R.id.menu_bottom_edit);
        TextView op3 = view.findViewById(R.id.menu_bottom_delete);

        op1.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
            Intent intent = new Intent(requireActivity(), ViewServiceActivity.class);
            intent.putExtra(Store.KEY_EXTRA_INTENT_SERVICE, chosenService);
            startActivity(intent);
        });

        op2.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
            forUpdate = true;
            Intent intent = new Intent(requireActivity(), ServiceInputActivity.class);
            intent.putExtra("update", true);
            intent.putExtra("to update service", chosenService);
            startActivity(intent);
        });

        op3.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
            ConfirmationDialog dialog = new ConfirmationDialog(this);
            dialog.show(requireActivity().getSupportFragmentManager(), "Confirmation");

        });
    }

    private void deleteFromDatabase() {
        //Delete from user record
        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(Store.CURRENT_USER_ID).collection(Store.KEY_COLLECTION_USER_SERVICES)
                .document(chosenService.serviceID)
                .delete()
                .addOnSuccessListener(unused -> showToast("Deleted"));

        //Delete from all services collection
        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_ALL_SERVICES).document(chosenService.serviceID).delete();

    }

    private void showToast(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void passObject(Object... data) {
        int result = (int) data[0];
        if (result == 1) {
            int pos = services.indexOf(chosenService);
            services.remove(pos);
            adapter.notifyItemRemoved(pos);
            deleteFromDatabase();
        }
    }
}
