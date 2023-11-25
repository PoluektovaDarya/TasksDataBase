package com.example.myapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task {
    private int id;
    private String title;
    private long dateTime;
    private Date date;
    private String description;

    public Task() {}

    public Task(String title, String dateStr, String description, long dateTime) {
        this.title = title;
        this.dateTime = dateTime;
        this.description = description;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            if (dateStr != null && !dateStr.isEmpty()) {
                Date parsedDate = dateFormat.parse(dateStr);
                this.dateTime = parsedDate != null ? parsedDate.getTime() : 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDateString() {
        if (dateTime != 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.format(new Date(dateTime));
        } else {
            return null;
        }
    }

    public Date getDueDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }
    public long getDateTime() {
        return date != null ? date.getTime() : 0;
    }
    public void setDateTime(long dateTime) {
        this.date = new Date(dateTime);
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return title;
    }
}
