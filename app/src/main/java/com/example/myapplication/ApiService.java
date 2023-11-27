package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;

public interface ApiService {
    @POST("/api/Note")
    Call<Void> createTask(@Body List<TaskModel> taskModels);
}

