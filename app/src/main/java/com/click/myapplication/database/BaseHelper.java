package com.click.myapplication.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "solution.db";

    public BaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ Schema.Chat.NAME+"("+
                "_id integer primary key autoincrement, "+
                Schema.Chat.cols.QUESTION+", "+
                Schema.Chat.cols.HAPPYANS+", "+
                Schema.Chat.cols.SADANS+", "+
                Schema.Chat.cols.ANGRYANS+
                ")");

        db.execSQL("create table "+ Schema.ChatLearn.NAME+"("+
                "_id integer primary key autoincrement, "+
                Schema.ChatLearn.cols.QUESTION+", "+
                Schema.ChatLearn.cols.HAPPYANS+", "+
                Schema.ChatLearn.cols.SADANS+", "+
                Schema.ChatLearn.cols.ANGRYANS+
                ")");

        db.execSQL("create table "+ Schema.QA.NAME+"("+
                "_id integer primary key autoincrement, "+
                Schema.QA.cols.QUESTION+", "+
                Schema.QA.cols.SOLUTION+
                ")");

        db.execSQL("create table "+ Schema.UnansweredQuestions.NAME+"("+
                "_id integer primary key autoincrement, "+
                Schema.QA.cols.QUESTION+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
