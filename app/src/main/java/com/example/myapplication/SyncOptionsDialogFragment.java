package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class SyncOptionsDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите действие")
                .setItems(R.array.sync_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Обработка выбора пользователя
                        handleOptionSelection(which);
                    }
                });
        return builder.create();
    }

    private void handleOptionSelection(int option) {
        // В зависимости от выбора пользователя выполните соответствующее действие
        if (option == 0) {
            // Выбрана синхронизация с сервером
            syncWithServer();
        } else if (option == 1) {
            // Выбрана отправка на сервер
            sendToServer();
        }
    }

    private void syncWithServer() {
        // Реализация синхронизации с сервером
        // ...
    }

    private void sendToServer() {
        // Реализация отправки на сервер
        // ...
    }
}
