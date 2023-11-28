package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskModel {
    private int id;
    private long dateTime;
    @SerializedName("theme")
    private String title;
    @SerializedName("noteText")
    private String description;
    private String dateTimeString;

    public TaskModel(int id, String title, String description, Long dateTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
    }
    public TaskModel(String title, String description, Long dateTime) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
    }
    public TaskModel(){}

    public String getTitle() {
        return title;
    }

    public String setTitle(String title) {
        this.title = title;
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String setDescription(String description) {
        this.description = description;
        return description;
    }
    public long getDateTime() {
        return dateTime;
    }
    public Date getDueDate() {
        return new Date(dateTime);
    }
    public void setDate(Date date) {
        this.dateTime = date.getTime();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDateTimeString() {
        return dateTimeString;
    }

    public void setDateTimeString(String dateTimeString) {
        this.dateTimeString = dateTimeString;
    }

    // Метод для форматирования даты в строку
    public String formatDateTimeToString() {
        if (dateTime != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00", Locale.getDefault());
            return sdf.format(new Date(dateTime));
        } else {
            return "";
        }
    }


}
