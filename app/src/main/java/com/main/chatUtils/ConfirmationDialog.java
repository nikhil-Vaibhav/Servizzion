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
import androidx.fragment.app.DialogFragment;

import com.main.chatListeners.ObjectPassListener;
import com.main.chatapplication.R;

public class ConfirmationDialog extends DialogFragment {

    ObjectPassListener listener;

    public ConfirmationDialog(ObjectPassListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.confirm_dialog_layout , null);

        TextView confirmTv = view.findViewById(R.id.confirm_text);
        confirmTv.setText(R.string.delete_confirmation_text);

        builder.setView(view)
                .setTitle("Delete this Service")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    listener.passObject(1);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    listener.passObject(0);
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }
}
