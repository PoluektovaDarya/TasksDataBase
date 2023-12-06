package com.example.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/Note")
    Call<Void> createTask(@Body TaskData taskData);

    @GET("/api/Note")
    Call<List<TaskModel>> getTasks();
}
