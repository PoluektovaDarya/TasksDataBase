package com.example.myapplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class ServerApiManager {

    private final ApiService apiService;

    public ServerApiManager() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    public void createTask(List<TaskModel> task, Callback<Void> callback) {
        Call<Void> call = apiService.createTask(task);
        call.enqueue(callback);
    }

}
