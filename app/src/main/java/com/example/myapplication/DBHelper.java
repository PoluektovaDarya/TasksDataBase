package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    static final String TABLE_TASKS = "tasks";
    private static final String TABLE_DESCRIPTION = "descriptions";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_DESCRIPTION = "description";

    private static final String DATABASE_CREATE_TASKS = "create table "
            + TABLE_TASKS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null, " + COLUMN_DATE
            + " text not null, " + COLUMN_DESCRIPTION
            + " text);";
    private static final String DATABASE_CREATE_DESCRIPTION = "create table "
            + TABLE_DESCRIPTION + "(" + COLUMN_ID
            + " integer primary key, " + COLUMN_DESCRIPTION
            + " text not null);";

    public static final String[] allColumns = {COLUMN_ID, COLUMN_TITLE, COLUMN_DATE, COLUMN_DESCRIPTION};

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TASKS);
        database.execSQL(DATABASE_CREATE_DESCRIPTION);
    }
   /* @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESCRIPTION);
        onCreate(db);
    } */
   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // Создаем временную таблицу tasks с новой схемой
       db.execSQL("CREATE TABLE " + TABLE_TASKS + "_temp AS SELECT * FROM " + TABLE_TASKS);
       // Создаем временную таблицу descriptions с новой схемой
       db.execSQL("CREATE TABLE " + TABLE_DESCRIPTION + "_temp AS SELECT * FROM " + TABLE_DESCRIPTION);
       // Удаляем старые таблицы
       db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
       db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESCRIPTION);
       // Создаем новые таблицы с обновленной схемой
       onCreate(db);
       // Копируем данные из временных таблиц в новые таблицы
       db.execSQL("INSERT INTO " + TABLE_TASKS + " SELECT * FROM " + TABLE_TASKS + "_temp");
       db.execSQL("INSERT INTO " + TABLE_DESCRIPTION + " SELECT * FROM " + TABLE_DESCRIPTION + "_temp");
       // Удаляем временные таблицы
       db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS + "_temp");
       db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESCRIPTION + "_temp");
   }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Создаем временную таблицу tasks с новой схемой
        db.execSQL("CREATE TABLE " + TABLE_TASKS + "_temp AS SELECT * FROM " + TABLE_TASKS);
        // Создаем временную таблицу descriptions с новой схемой
        db.execSQL("CREATE TABLE " + TABLE_DESCRIPTION + "_temp AS SELECT * FROM " + TABLE_DESCRIPTION);
        // Удаляем старые таблицы
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESCRIPTION);
        // Создаем новые таблицы с обновленной схемой
        onCreate(db);
        // Копируем данные из временных таблиц в новые таблицы
        db.execSQL("INSERT INTO " + TABLE_TASKS + " SELECT * FROM " + TABLE_TASKS + "_temp");
        db.execSQL("INSERT INTO " + TABLE_DESCRIPTION + " SELECT * FROM " + TABLE_DESCRIPTION + "_temp");
        // Удаляем временные таблицы
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS + "_temp");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESCRIPTION + "_temp");
    }
}
