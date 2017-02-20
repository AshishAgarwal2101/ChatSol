package com.click.myapplication;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chatty extends Activity {
    private RecyclerView recyclerView;
    String name="", names="", lowerCase="", learnQuestion;
    private ChatAdapter chatAdapter;
    private Button button1;
    private EditText editText1;
    private ArrayList<String> replies,udc;
    private SQLiteDatabase database;
    private Pattern p= Pattern.compile("my name is ");
    private Pattern p1=Pattern.compile("i am ");
    private Matcher m,m1;
    private Context context;
    private int mood, spaceIndex, storenewLearn=0;
    private String br="",botReply=null;
    private Random random=new Random();
    //int replyOptions = 1; //0=bot.....1=user

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatty_and_solution_layout);
        context= getApplicationContext();
        File db= context.getDatabasePath(Schema.Chat.NAME);
        database = new BaseHelper(getApplicationContext()).getWritableDatabase();
        if(!(db.exists())) {
            insert();
            //Toast.makeText(this, "Database created", Toast.LENGTH_SHORT).show();
        }

        button1 = (Button)findViewById(R.id.send);
        editText1 = (EditText)findViewById(R.id.reply);

        recyclerView = (RecyclerView)findViewById(R.id.chatty_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);

        udc = new ArrayList<>();
        udc.add("I didn't get you");
        udc.add("Can you please be specific?");
        udc.add("I didn't understand it");
        udc.add("That seems like it might be a good thing");
        udc.add("Oh!");
        udc.add("Hmm...");

        replies = new ArrayList<String>();
        load();
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());

        mood = getIntent().getIntExtra("radio", 0);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String reply = editText1.getText().toString();
                if(reply.equals("")){
                    Toast.makeText(Chatty.this, "No text", Toast.LENGTH_SHORT).show();
                }
                else {
                    lowerCase = reply.toLowerCase();
                    if (storenewLearn != 1) {
                        m = p.matcher(lowerCase);
                        m1 = p1.matcher(lowerCase);

                        if (m.find()) {

                            names = reply.substring(m.end(), lowerCase.length());
                            spaceIndex = names.indexOf(" ");
                            //Toast.makeText(Chatty.this, spaceIndex + "", Toast.LENGTH_SHORT).show();
                            if (spaceIndex != -1) {
                                name = names.substring(0, spaceIndex);
                            } else {
                                name = names;
                            }
                        } else if (m1.find()) {
                            names = reply.substring(m1.end(), lowerCase.length());
                            spaceIndex = names.indexOf(" ");
                            if (spaceIndex != -1) {
                                name = names.substring(0, spaceIndex);
                            } else {
                                name = names;
                            }

                        }

                        //Toast.makeText(Chatty.this,names,Toast.LENGTH_SHORT).show();
                        // Cursor c = database.rawQuery("SELECT "+Schema.Chat.cols.HAPPYANS+" , "+ Schema.Chat.cols.ANGRYANS+" , "+ Schema.Chat.cols.SADANS+" FROM "+ Schema.Chat.NAME+" WHERE question = "+lowerCase,null);
                        // Wrapper cursor = queryChat(Schema.Chat.cols.QUESTION+" = ?", new String[]{"what is my name?","do you know my name?"});
                        //cursor.moveToFirst();
                        if (!(name.equals(""))) {

                            ContentValues contentValues = getChatContentValues("what is my name?", "Your name is " + name, "Your name is " + name, "Your name is " + name);
                            ContentValues cv1 = getChatContentValues("do you know my name?", "Your name is " + name, "Your name is " + name, "Your name is " + name);
                            database.update(Schema.Chat.NAME, contentValues, Schema.Chat.cols.QUESTION + " = ?", new String[]{"what is my name?"});
                            database.update(Schema.Chat.NAME, cv1, Schema.Chat.cols.QUESTION + " = ?", new String[]{"do you know my name?"});
                            replies.add("u" + reply);
                            chatAdapter.notifyItemInserted(replies.size() - 1);
                            replies.add("bOh,that's a nice name");
                            chatAdapter.notifyItemInserted(replies.size() - 1);
                            name = "";
                            names = "";
                            reply="";

                            save();
                        } else {
                            String reply1 = "u" + reply;
                            replies.add(reply1);
                            chatAdapter.notifyItemInserted(replies.size() - 1);
                            botReply(reply);
                            reply="";
                            reply1="";
                            save();
                        }
                    }
                    else {
                        if(storenewLearn == 1){   //insert values as learn
                            ContentValues c;
                            String happyReply="", sadReply="", angryReply="";
                            learnQuestion = learnQuestion.toLowerCase();
                            Wrapper cursor = queryChat(Schema.Chat.cols.QUESTION+" = ?", new String[]{learnQuestion});
                            try{
                                cursor.moveToFirst();
                                happyReply = cursor.getChatHappyAns();
                                sadReply = cursor.getChatSadAns();
                                angryReply = cursor.getChatAngryAns();

                            }finally {
                                cursor.close();
                            }
                            if(mood == 1) {
                                c = getChatContentValues(learnQuestion, reply, sadReply, angryReply);
                                database.update(Schema.Chat.NAME, c, Schema.Chat.cols.QUESTION+" = ?", new String[]{learnQuestion});
                            }
                            else if(mood == 2){
                                c = getChatContentValues(learnQuestion, happyReply, reply, angryReply);
                                database.update(Schema.Chat.NAME, c, Schema.Chat.cols.QUESTION+" = ?", new String[]{learnQuestion});
                            }
                            else {
                                c = getChatContentValues(learnQuestion, happyReply, sadReply, reply);
                                database.update(Schema.Chat.NAME, c, Schema.Chat.cols.QUESTION+" = ?", new String[]{learnQuestion});
                            }
                            storenewLearn = 0;
                            String reply1 = "u" + reply;
                            replies.add(reply1);
                            chatAdapter.notifyItemInserted(replies.size() - 1);
                            reply1 = "b" + "Oh! Got it.";
                            replies.add(reply1);
                            chatAdapter.notifyItemInserted(replies.size() - 1);
                            reply1="";
                            reply="";
                            botReply=null;
                            save();
                        }

                    }
                    editText1.setText("");
                }

            }
        });
    }

    public void load() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        replies.clear();
        int size = sharedPreferences.getInt("Status_size", 0);

        for(int i=0;i<size;i++)
        {
            replies.add(sharedPreferences.getString("Status_" + i, null));
        }
    }

    public void save() {
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Status_size", replies.size());
        for(int i=0;i<replies.size();i++)
        {
            editor.remove("Status_" + i);
            editor.putString("Status_" + i, replies.get(i));
        }
        editor.commit();
    }
    private String learning() {
        String question="",ans;
        Random random = new Random();
        ArrayList<String> questions = new ArrayList<>();
        Wrapper cursor;
        if(mood == 1) {
            cursor = queryChat(Schema.Chat.cols.HAPPYANS+" = ?", new String[]{"five11"});
            ans = "Try saying this.. She sells sea shells on the sea shore";
        }
        else if(mood == 2){
            cursor = queryChat(Schema.Chat.cols.SADANS+" = ?", new String[]{"five11"});
            ans = "Someday everything will all make perfect sense. So for now, laugh at the confusion, smile through the tears, and keep reminding yourself that everything happens for a reason..";
        }
        else {
            cursor = queryChat(Schema.Chat.cols.ANGRYANS+" = ?", new String[]{"five11"});
            ans = "The worst thing that happens to you may be the best thing ever";
        }
        try {
            if (cursor.moveToFirst()) {
                while(!(cursor.isAfterLast())) {
                    questions.add(cursor.getChatQuestion());
                    cursor.moveToNext();
                }
                Toast.makeText(this,question,Toast.LENGTH_SHORT).show();
            }
        }
        finally{
            cursor.close();
        }
        if(questions.size() != 0) {
            question = questions.get(random.nextInt(questions.size()));
            learnQuestion = question;
            storenewLearn = 1;
        }
        if(question.equals(""))
        {
            return "b" + ans;
        }
        botReply ="b" + question;
        return botReply;

    }

    private void insert(){

        ContentValues c = getChatContentValues("hi", "Hello.. Wassup?", "Hello", "Hi");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("hey", "Hello.. Wassup?", "Hello", "Hi");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("hello", "Hello.. Wassup?", "Hello", "Hi");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("what are you doing?", "Chatting with you. How may I help you?", "Trying to make you feel better", "Trying to calm you down");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("how are you?", "Fine as always", "Confident that you'll feel better", "Confident that you'll calm down");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("say something", "anyyy", "anyyy", "anyyy");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("what is my name?", "I don't know. Can you please tell me your name?", "I don't know. Can you please tell me your name?", "I don't know. Can you please tell me your name?");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("do you know my name?", "I don't know. Can you please tell me your name?", "I don't know. Can you please tell me your name?", "I don't know. Can you please tell me your name?");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("i wanted to tell you something", "Yeah, sure! Go on", "Yeah, sure! Go on", "Yeah, sure! Go on");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("what is your name?", "My name is Chatty. I am your friend and you can chat with me all the time. As they say,” A friend in need is a friend indeed.", "My name is Chatty. I am your friend and you can chat with me all the time. As they say,” A friend in need is a friend indeed.", "My name is Chatty. I am your friend and you can chat with me all the time. As they say,” A friend in need is a friend indeed.");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("who are you?", "My name is Chatty. I am your friend and you can chat with me all the time. As they say,” A friend in need is a friend indeed.", "My name is Chatty. I am your friend and you can chat with me all the time. As they say,” A friend in need is a friend indeed.", "My name is Chatty. I am your friend and you can chat with me all the time. As they say,” A friend in need is a friend indeed.");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("what can you do for me?", "I can talk to you all the time. Whenever you are lonely or want to share something with me. I am almost a human, but don’t tell me to dance please!", "I can talk to you all the time. Whenever you are lonely or want to share something with me. I am almost a human, but don’t tell me to dance please!", "I can talk to you all the time. Whenever you are lonely or want to share something with me. I am almost a human, but don’t tell me to dance please!");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("what can you do?", "I can talk to you all the time. Whenever you are lonely or want to share something with me. I am almost a human, but don’t tell me to dance please!", "I can talk to you all the time. Whenever you are lonely or want to share something with me. I am almost a human, but don’t tell me to dance please!", "I can talk to you all the time. Whenever you are lonely or want to share something with me. I am almost a human, but don’t tell me to dance please!");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("hi chatty", "Hello friend!", "Hello friend!", "Hello friend!");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("tell me a joke", "What did 0 tell 8? \n Wow, Nice belt!", "What did 0 tell 8? \n" +
                " Wow, Nice belt!", "What did 0 tell 8? \n" +
                " Wow, Nice belt!");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("Once more", "Once is enough sometimes", "Once is enough sometimes", "Once is enough sometimes");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("hello chatty", "Hello friend!", "Hello friend!", "Hello friend!");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("lol", "Happy to make you laugh :D", "Happy to make you laugh :D", "Happy to make you laugh :D");
        database.insert(Schema.Chat.NAME, null, c);
        c = getChatContentValues("i wish to die", "I'll join too. Let's find the best place to do so", "Oh, you should'nt say so. Life is full of ups and downs and how you recover from every circumstance is what makes life special.", "Calm down! Things will be fine eventualy.");
        database.insert(Schema.Chat.NAME, null, c);
    }

    private void botReply(String userReply){  //the bot replies
        botReply = getBotReply(userReply.toLowerCase());
        String question="";
        br="";
        if(botReply != null){
            if(botReply.equals("anyyy")){
                br = botReply;
                botReply = learning();

            }
            else if(botReply.equals("five11")){
                botReply="b" + udc.get(random.nextInt(udc.size()));
            }
            else {
                botReply = "b" + botReply;
            }
        }
        else {
            botReply="b" + udc.get(random.nextInt(udc.size()));
            //insert in ChatLearn
            ContentValues cv = getChatContentValues(userReply, "five11", "five11", "five11");
            database.insert(Schema.Chat.NAME, null, cv);
            Toast.makeText(this, "Inserted new question", Toast.LENGTH_SHORT).show();
        }
        replies.add(botReply);
        save();
        chatAdapter.notifyItemInserted(replies.size()-1);
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
        botReply=null;
        /*String ss="";
        for(String s:replies){
            ss=ss+"\n"+s;
        }
        Toast.makeText(Chatty.this, ss, Toast.LENGTH_LONG).show();*/
    }

    private String getBotReply(String userReply){
        String botReply;
        Wrapper cursor = queryChat(Schema.Chat.cols.QUESTION+" = ?", new String[]{userReply});
        try{
            if(cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            if(mood == 1){
                botReply = cursor.getChatHappyAns();
            }
            else if(mood == 2){
                botReply = cursor.getChatSadAns();
            }
            else {
                botReply = cursor.getChatAngryAns();
            }
        }finally {
            cursor.close();
        }
        return botReply;
    }

    private Wrapper queryChat(String whereClause, String[] whereArgs){
        Cursor cursor = database.query(
                Schema.Chat.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new Wrapper(cursor);
    }
    private Wrapper queryChatLearn(String whereClause, String[] whereArgs){
        Cursor cursor = database.query(
                Schema.ChatLearn.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new Wrapper(cursor);
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

    private ContentValues getChatContentValues(String ques, String happyans, String sadans, String angryans){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.Chat.cols.QUESTION, ques);
        contentValues.put(Schema.Chat.cols.HAPPYANS, happyans);
        contentValues.put(Schema.Chat.cols.SADANS, sadans);
        contentValues.put(Schema.Chat.cols.ANGRYANS, angryans);
        return contentValues;
    }

    private ContentValues getChatLearnContentValues(String ques, String happyans, String sadans, String angryans){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.Chat.cols.QUESTION, ques);
        contentValues.put(Schema.Chat.cols.HAPPYANS, happyans);
        contentValues.put(Schema.Chat.cols.SADANS, sadans);
        contentValues.put(Schema.Chat.cols.ANGRYANS, angryans);
        return contentValues;
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
            LayoutInflater inflater = LayoutInflater.from(Chatty.this);
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


