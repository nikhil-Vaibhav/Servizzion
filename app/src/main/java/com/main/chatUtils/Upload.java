package com.main.chatUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.main.chatModel.ProfileItem;

import java.util.ArrayList;

public class Upload {


    public static final String PROFILE_IMAGE = "profileImage";
    public static final String SERVICE_IMAGE = "serviceImage";
    public static final StorageReference storageReference = FirebaseStorage.getInstance().getReference(Store.KEY_PROFILE_IMAGE_UPLOADS);
    public static final StorageReference serviceStorageReference = FirebaseStorage.getInstance().getReference(Store.KEY_SERVICE_IMAGE_UPLOADS);
    CollectionReference userCollection = FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER);

    private final Context context;
    private Uri downloadURI;

    public Upload(Context context) {
        this.context = context;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void uploadProfileImage(Uri uri) {
        if (uri == null) {
            Toast.makeText(context, "Select a file", Toast.LENGTH_SHORT).show();
            return;
        }
        String filePath = Store.CURRENT_USER_ID + PROFILE_IMAGE + "." + getFileExtension(uri);
        StorageReference fileRef = storageReference.child(filePath);

        if (Store.CURRENT_USER.profilePicturePath.length() != 0) {
            storageReference.child(Store.CURRENT_USER.profilePicturePath).delete();
        }

        fileRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            if(taskSnapshot.getMetadata() != null) {
                if(taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(uri1 -> {
                        Toast.makeText(context, "Upload Successful ", Toast.LENGTH_SHORT).show();
                        Store.CURRENT_USER.profilePicturePath = uri1.toString();
                        FirebaseFirestore.getInstance().collection(Store.KEY_COLLECTION_USER).document(Store.CURRENT_USER_ID)
                                .update(Store.KEY_USER_PROFILE_PICTURE_PATH , uri1.toString());
                    });
                }
            }
        });
    }


}
