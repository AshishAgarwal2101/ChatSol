package com.click.myapplication.database;


import android.database.Cursor;
import android.database.CursorWrapper;

public class Wrapper extends CursorWrapper {
    public Wrapper(Cursor cursor){
        super(cursor);
    }

    public String getChatQuestion(){
        return getString(getColumnIndex(Schema.Chat.cols.QUESTION));
    }
    public String getChatHappyAns(){
        return getString(getColumnIndex(Schema.Chat.cols.HAPPYANS));
    }
    public String getChatSadAns(){
        return getString(getColumnIndex(Schema.Chat.cols.SADANS));
    }
    public String getChatAngryAns(){
        return getString(getColumnIndex(Schema.Chat.cols.ANGRYANS));
    }


    //ChatLearn
    public String getChatLearnQuestion(){
        return getString(getColumnIndex(Schema.ChatLearn.cols.QUESTION));
    }
    public String getChatLearnHappyAns(){
        return getString(getColumnIndex(Schema.ChatLearn.cols.HAPPYANS));
    }
    public String getChatLearnSadAns(){
        return getString(getColumnIndex(Schema.ChatLearn.cols.SADANS));
    }
    public String getChatLearnAngryAns(){
        return getString(getColumnIndex(Schema.ChatLearn.cols.ANGRYANS));
    }


    //QA table
    public String getQAQueastion(){
        return getString(getColumnIndex(Schema.QA.cols.QUESTION));
    }
    public String getQASol(){
        return getString(getColumnIndex(Schema.QA.cols.SOLUTION));
    }


    //Solution
    public String getUnansweredQuestion(){
        return getString(getColumnIndex(Schema.UnansweredQuestions.cols.QUESTION));
    }

}
