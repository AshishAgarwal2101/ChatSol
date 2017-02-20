package com.click.myapplication;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.click.myapplication.database.BaseHelper;
import com.click.myapplication.database.Schema;
import com.click.myapplication.database.Wrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class SolutionBot extends Activity {
    RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private Button button1;
    private EditText editText1;
    private ArrayList<String> replies,udc;
    private SQLiteDatabase database;
    private Context context;
    private int mood, spaceIndex, storenewLearn=0;
    private String br="",botReply=null;
    private Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatty_and_solution_layout);

        context= getApplicationContext();
        File db= context.getDatabasePath(Schema.QA.NAME);
        database = new BaseHelper(getApplicationContext()).getWritableDatabase();
        //if(!(db.exists())) {
            insert();
            //Toast.makeText(this, "Database created", Toast.LENGTH_SHORT).show();
      //  }
        udc= new ArrayList<>();

        button1 = (Button)findViewById(R.id.send);
        editText1 = (EditText)findViewById(R.id.reply);

        recyclerView = (RecyclerView)findViewById(R.id.chatty_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);
        replies = new ArrayList<String>();
        load();
        if(replies.size() == 0){
            replies.add("bHey! Ask your Queries Here!");
        }
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String reply = editText1.getText().toString();
                if(reply.equals("")){
                    Toast.makeText(SolutionBot.this, "No text", Toast.LENGTH_SHORT).show();
                }
                else if(reply.equalsIgnoreCase("Yes")||reply.equalsIgnoreCase("Yeah")||reply.equalsIgnoreCase("yo")||reply.equalsIgnoreCase("yup")){
                    replies.add("u"+reply);  //user display
                    chatAdapter.notifyItemInserted(replies.size()-1);
                    editText1.setText("");
                    botReply= "bYeah please. My pleasure.";
                    replies.add(botReply);
                    chatAdapter.notifyItemInserted(replies.size()-1);
                    botReply=null;
                }
                else if(reply.equalsIgnoreCase("no")||reply.equalsIgnoreCase("nope")||reply.equalsIgnoreCase("na")||reply.equalsIgnoreCase("nopes")){
                    replies.add("u"+reply);  //user display
                    chatAdapter.notifyItemInserted(replies.size()-1);
                    editText1.setText("");
                    botReply= "bHappy to help you! Bye";
                    replies.add(botReply);
                    chatAdapter.notifyItemInserted(replies.size()-1);
                    botReply=null;
                }
                else if(reply.equalsIgnoreCase("okay")||reply.equalsIgnoreCase("bye")||reply.equalsIgnoreCase("bye!")||reply.equalsIgnoreCase("good bye")||reply.equalsIgnoreCase("thanks")||reply.equalsIgnoreCase("thank you")||reply.equalsIgnoreCase("thank you!")){
                    replies.add("u"+reply);  //user display
                    chatAdapter.notifyItemInserted(replies.size()-1);
                    editText1.setText("");
                    botReply= "bHappy to help you! Bye";
                    replies.add(botReply);
                    chatAdapter.notifyItemInserted(replies.size()-1);
                    botReply=null;
                }

                else {
                    replies.add("u"+reply);  //user display
                    chatAdapter.notifyItemInserted(replies.size()-1);
                    editText1.setText("");

                    String reply1 = reply.toLowerCase();

                    botReply = getSolution(reply1);  //bot display
                    if(botReply != null) {
                        botReply = "b"+botReply;
                    }
                    else {
                        botReply = "b"+"I don't have an answer to your question currently. Please come back in a while until I fetch the answer";
                        ContentValues contentValues = getUnanswredQuestionsContentValues(reply1);
                        database.insert(Schema.UnansweredQuestions.NAME, null, contentValues);

                    }
                    replies.add(botReply);
                    chatAdapter.notifyItemInserted(replies.size()-1);
                    replies.add("bDo you have any further questions?");
                    chatAdapter.notifyItemInserted(replies.size()-1);
                    botReply=null;

                }
                save();
            }

        });
    }

    public void load() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        replies.clear();
        int size = sharedPreferences.getInt("Stat_size", 0);

        for(int i=0;i<size;i++)
        {
            replies.add(sharedPreferences.getString("Stat_" + i, null));
        }
    }

    public void save() {
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Stat_size", replies.size());
        for(int i=0;i<replies.size();i++)
        {
            editor.remove("Stat_" + i);
            editor.putString("Stat_" + i, replies.get(i));
        }
        editor.commit();
    }

    private String getSolution(String userReply){
        String botReply = null;
        Wrapper cursor = queryQA(Schema.QA.cols.QUESTION+" = ?", new String[]{userReply});
        try {
            if(cursor.moveToFirst()){
                botReply = cursor.getQASol();
            }
        }
        finally {
            cursor.close();
        }
        return botReply;
    }
    private void insert()
    {
        ContentValues contentValues= getQAContentValues("i lost my sim card. what to do?","You should lodge an F.I.R. in the nearest police station. Then contact your service provider to block your sim card");
        database.insert(Schema.QA.NAME, null, contentValues);

    }
    private ContentValues getQAContentValues(String ques, String solution){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.QA.cols.QUESTION, ques);
        contentValues.put(Schema.QA.cols.SOLUTION, solution);
        return contentValues;
    }

    private ContentValues getUnanswredQuestionsContentValues(String ques){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.UnansweredQuestions.cols.QUESTION, ques);
        return contentValues;
    }

    private Wrapper queryQA(String whereClause, String[] whereArgs){
        Cursor cursor = database.query(
                Schema.QA.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new Wrapper(cursor);
    }
    private Wrapper queryUnansweredQues(String whereClause, String[] whereArgs){
        Cursor cursor = database.query(
                Schema.UnansweredQuestions.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new Wrapper(cursor);
    }



    private class ChatHolder extends RecyclerView.ViewHolder{
        private TextView userReplyTextView;
        private TextView botReplyTextView;

        public ChatHolder(View itemView){
            super(itemView);
            userReplyTextView = (TextView) itemView.findViewById(R.id.user_reply_text_view);
            botReplyTextView = (TextView) itemView.findViewById(R.id.bot_reply_text_view);
        }
        public void bindTextBot(String reply){
            botReplyTextView.setText(reply);
            botReplyTextView.setBackground(getDrawable(R.drawable.out_message_bg));
        }
        public void bindTextUser(String reply){
            userReplyTextView.setText(reply);
            userReplyTextView.setBackground(getDrawable(R.drawable.in_message_bg));
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatHolder>{

        @Override
        public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(SolutionBot.this);
            View view = inflater.inflate(R.layout.reply, parent, false);
            return new ChatHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatHolder holder, int position) {
            String reply = replies.get(position);
            char turn = reply.charAt(0);
            reply = reply.substring(1,reply.length());
            if(turn == 'b'){
                holder.bindTextBot(reply);
            }
            else {
                holder.bindTextUser(reply);
            }
        }

        @Override
        public int getItemCount() {
            return replies.size();
        }
    }

}
