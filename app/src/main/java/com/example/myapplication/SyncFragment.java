package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncFragment extends Fragment {

    private TaskDataSource dataSource;

    public SyncFragment(TaskDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync, container, false);

        view.findViewById(R.id.syncButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAllDataToServer();
            }
        });

        return view;
    }

    private void sendAllDataToServer() {
        List<TaskModel> allTaskModels = dataSource.getAllTasks();

        ServerApiManager apiManager = new ServerApiManager();
        apiManager.createTask(allTaskModels, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("SyncFragment", "Данные успешно отправлены на сервер");
                } else {
                    Log.e("SyncFragment", "Ошибка при отправке данных на сервер: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("SyncFragment", "Ошибка при отправке данных на сервер: " + t.getMessage());
            }
        });
    }
}
