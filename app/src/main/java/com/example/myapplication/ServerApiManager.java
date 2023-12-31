package com.example.myapplication;

import java.util.List;

import retrofit2.Callback;

public class ServerApiManager {
    private final ApiService apiService;

    public ServerApiManager() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    public void createTask(String theme, String noteText, Callback<Void> callback) {
        TaskData taskData = new TaskData(theme, noteText);
        apiService.createTask(taskData).enqueue(callback);
    }
    public void getTasks(Callback<List<TaskModel>> callback) {
        apiService.getTasks().enqueue(callback);
    }
}
