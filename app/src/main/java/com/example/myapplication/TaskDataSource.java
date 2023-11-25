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

    public void addTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TITLE, task.getTitle());
        values.put(DBHelper.COLUMN_DATE, task.getDateTime());
        values.put(DBHelper.COLUMN_DESCRIPTION, task.getDescription() != null ? task.getDescription() : "");

        long insertId = database.insert(DBHelper.TABLE_TASKS, null, values);
        task.setId((int) insertId);
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        Cursor cursor = database.query(DBHelper.TABLE_TASKS,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }

        cursor.close();
        return tasks;
    }
    public void deleteTask(long taskId) {
        database.delete(DBHelper.TABLE_TASKS,
                DBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(taskId)});
    }
    public Task getTaskById(int taskId) {
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
            Task task = cursorToTask(cursor);
            cursor.close();
            return task;
        } else {
            return null;
        }
    }

    public void updateTask(Task currentTask) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TITLE, currentTask.getTitle());
        values.put(DBHelper.COLUMN_DATE, currentTask.getDateTime());
        values.put(DBHelper.COLUMN_DESCRIPTION, currentTask.getDescription());

        String selection = DBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(currentTask.getId())};

        database.update(DBHelper.TABLE_TASKS, values, selection, selectionArgs);
    }

    @SuppressLint("Range")
    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();
        task.setId((int) cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
        task.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TITLE)));

        long dateTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_DATE));
        task.setDateTime(dateTime);

        task.setDescription(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION)));

        return task;
    }

}
