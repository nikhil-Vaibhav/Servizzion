package com.main.chatapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.main.chatAdapters.ImageAdapter;
import com.main.chatModel.Service;
import com.main.chatUtils.Store;
import com.main.chatUtils.Upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServiceInputActivity extends AppCompatActivity implements ImageAdapter.NewImageListener {

    private AppCompatSpinner categoryInput;
    private TextInputEditText titleInput, shortDescInput, longDescInput;
    private EditText chargesInput;
    private Button submitButton;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageAdapter adapter;
    private List<Uri> images;
    private List<String> imageStrings, imageFilePaths;
    private boolean forUpdate;
    public static final StorageReference serviceStorageReference = FirebaseStorage.getInstance().getReference(Store.KEY_SERVICE_IMAGE_UPLOADS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_input);
        init();
        setListeners();

        forUpdate = getIntent().getBooleanExtra("update", false);
        if (forUpdate) loadData();
    }

    private void loadData() {
        Service service = (Service) getIntent().getSerializableExtra("to update service");
        titleInput.setText(service.title);
        shortDescInput.setText(service.short_description);
        longDescInput.setText(service.long_description);
        chargesInput.setText(service.charges);
    }

    private void init() {
        images = new ArrayList<>();
        imageStrings = new ArrayList<>();
        imageFilePaths = new ArrayList<>();
        images.add(null);

        categoryInput = findViewById(R.id.input_category);
        progressBar = findViewById(R.id.serviceImageProgressBar);
        titleInput = findViewById(R.id.input_title);
        shortDescInput = findViewById(R.id.input_short_desc);
        longDescInput = findViewById(R.id.input_long_desc);
        submitButton = findViewById(R.id.saveDataButton);
        chargesInput = findViewById(R.id.input_charges);
        recyclerView = findViewById(R.id.input_image_recycler);
        setUpSpinner();

        adapter = new ImageAdapter(this, this, images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> routineType = ArrayAdapter.createFromResource(this, R.array.categoryList, android.R.layout.simple_list_item_1);
        routineType.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        categoryInput.setAdapter(routineType);
    }

    private void setListeners() {
        submitButton.setOnClickListener(v -> {
            saveData();
        });
    }

    private void saveData() {
        if (!validInput()) return;
        Service service = new Service();
        service.title = Objects.requireNonNull(titleInput.getText()).toString();
        service.short_description = Objects.requireNonNull(shortDescInput.getText()).toString();
        service.long_description = Objects.requireNonNull(longDescInput.getText()).toString();
        service.category = categoryInput.getSelectedItem().toString();
        service.serviceImages = imageStrings;
        service.charges = chargesInput.getText().toString();

        titleInput.setText("");
        shortDescInput.setText("");
        longDescInput.setText("");
        chargesInput.setText("");
        addToLocalStorage(service);
        finish();
    }

    private boolean validInput() {

        if (Objects.requireNonNull(titleInput.getText()).toString().trim().isEmpty()) {
            toast("Title should not be empty");
            return false;
        }

        if (Objects.requireNonNull(shortDescInput.getText()).toString().length() < 50) {
            toast("Highlights should have at least 50 characters");
            return false;
        }

        if (Objects.requireNonNull(longDescInput.getText()).toString().length() < 100) {
            toast("Long description should have at least 100 characters");
            return false;
        }

        if(imageStrings.size() < 2) {
            toast("Add at least 1 image.");
            return false;
        }

        return true;
    }

    private void addToLocalStorage(Service service) {
        if (forUpdate) {
            Intent dataIntent = new Intent();
            dataIntent.putExtra("updated", service);
            setResult(RESULT_OK, dataIntent);
            return;
        }

        Intent dataIntent = new Intent();
        dataIntent.putExtra("KEY", service);
        setResult(RESULT_OK, dataIntent);
    }

    ActivityResultLauncher<Intent> imageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    loading(true);
                    images.add(result.getData().getData());
                    uploadImageToStorage(result.getData().getData());
                    adapter.notifyItemInserted(images.size() - 1);
                }
            });

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImageToStorage(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = Store.CURRENT_USER_ID + System.currentTimeMillis() + "." + getFileExtension(uri);
        imageFilePaths.add(filePath);

        StorageReference fileRef = serviceStorageReference.child(Store.CURRENT_USER_FOLDER_REFERENCE).child(filePath);
        fileRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            if (taskSnapshot.getMetadata() != null) {
                if (taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(uri1 -> {
                        imageStrings.add(uri1.toString());
                        loading(false);
                        toast("Uploaded");
                    });
                }
            }
        });
    }

    @Override
    public void onNewImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        imageLauncher.launch(intent);
    }

    @Override
    public void onDeleteImage(int position) {
        loading(true);
        String filePath = imageFilePaths.get(position);

        images.remove(position);
        imageStrings.remove(position);
        imageFilePaths.remove(position);

        serviceStorageReference.child(Store.CURRENT_USER_FOLDER_REFERENCE).child(filePath).delete().addOnSuccessListener(v -> {
            toast("Deleted");
            loading(false);
        });
        adapter.notifyItemRemoved(position);
    }

    private void loading(boolean isLoading) {
        if(isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}