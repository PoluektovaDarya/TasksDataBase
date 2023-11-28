package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
                sendToServer();
            }
        });
        return view;
    }
    private void sendToServer() {
        ServerApiManager apiManager = new ServerApiManager();
        TaskDataSource dataSource = new TaskDataSource(requireContext().getApplicationContext());

        // Получаем все задачи из базы данных
        List<TaskModel> taskList = dataSource.getAllTasks();

        // Отправляем каждую задачу на сервер
        for (TaskModel taskModel : taskList) {
            // Получаем значения title и dateTimeString из каждой задачи
            String theme = taskModel.getTitle(); // заголовок
            String date = taskModel.formatDateTimeToString(); // дата
            //String noteText = taskModel.getDescription(); отправка описания

            // Отправляем данные на сервер
            apiManager.createTask(theme, date, new Callback<Void>() {
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
    private List<TaskModel> getAllTasksFromDB() {
        return dataSource.getAllTasks();
    }
}
