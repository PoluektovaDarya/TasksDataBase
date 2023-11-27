package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class TaskDataSource {

    private static SQLiteDatabase database;
    private DBHelper dbHelper;

    public TaskDataSource(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context.getApplicationContext());
        }
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addTask(TaskModel taskModel) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TITLE, taskModel.getTitle());
        values.put(DBHelper.COLUMN_DATE, taskModel.getDateTime());
        values.put(DBHelper.COLUMN_DESCRIPTION, taskModel.getDescription() != null ? taskModel.getDescription() : "");

        long insertId = database.insert(DBHelper.TABLE_TASKS, null, values);
        taskModel.setId((int) insertId);
    }

    public List<TaskModel> getAllTasks() {
        List<TaskModel> taskModels = new ArrayList<>();
        try {
            open();

            Cursor cursor = database.query(DBHelper.TABLE_TASKS,
                    null, null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TaskModel taskModel = cursorToTask(cursor);
                taskModels.add(taskModel);
                cursor.moveToNext();
            }
            cursor.close();
        } finally {
            close();
        }
        return taskModels;
    }

    public void deleteTask(long taskId) {
        database.delete(DBHelper.TABLE_TASKS,
                DBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(taskId)});
    }

    public TaskModel getTaskById(int taskId) {
        Cursor cursor = database.query(
                DBHelper.TABLE_TASKS,
                DBHelper.allColumns,
                DBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(taskId)},
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            TaskModel taskModel = cursorToTask(cursor);
            cursor.close();
            return taskModel;
        } else {
            return null;
        }
    }

    public void updateTask(TaskModel currentTaskModel) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TITLE, currentTaskModel.getTitle());
        values.put(DBHelper.COLUMN_DATE, currentTaskModel.getDateTime());
        values.put(DBHelper.COLUMN_DESCRIPTION, currentTaskModel.getDescription());

        String selection = DBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(currentTaskModel.getId())};

        database.update(DBHelper.TABLE_TASKS, values, selection, selectionArgs);
    }

    @SuppressLint("Range")
    private TaskModel cursorToTask(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
        int titleIndex = cursor.getColumnIndex(DBHelper.COLUMN_TITLE);
        int dateIndex = cursor.getColumnIndex(DBHelper.COLUMN_DATE);
        int descriptionIndex = cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION);

        int id = cursor.getInt(idIndex);
        String title = cursor.getString(titleIndex);
        long dateTime = cursor.getLong(dateIndex);
        String description = cursor.getString(descriptionIndex);

        return new TaskModel(id, title, description, dateTime);
    }
}