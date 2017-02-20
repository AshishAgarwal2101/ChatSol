package com.click.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    int radio = 0;
    Button b2, b3;
    Button c1, c2, c3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c1 = (Button) findViewById(R.id.happyButton);
        c2 = (Button) findViewById(R.id.sadButton);
        c3 = (Button) findViewById(R.id.angryButton);
        b2 = (Button) findViewById(R.id.buttonSolution);
        b3 = (Button) findViewById(R.id.buttonAnswer);

        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio= 1;
                Intent intent = new Intent(MainActivity.this, Chatty.class);
                intent.putExtra("radio", radio);
                startActivity(intent);
            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio= 2;
                Intent intent = new Intent(MainActivity.this, Chatty.class);
                intent.putExtra("radio", radio);
                startActivity(intent);
            }
        });
        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio= 3;
                Intent intent = new Intent(MainActivity.this, Chatty.class);
                intent.putExtra("radio", radio);
                startActivity(intent);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SolutionBot.class);
                startActivity(intent);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, giveSolutionQA.class);
                startActivity(intent);
            }
        });
        TextView title = (TextView) findViewById(R.id.title);
        TextView t1 = (TextView) findViewById(R.id.tv1);
        TextView t2 = (TextView) findViewById(R.id.tv2);
        TextView t3 = (TextView) findViewById(R.id.tv3);
        TextView t4 = (TextView) findViewById(R.id.tv4);
        title.setTypeface(EasyFonts.captureIt(this));
        t1.setTypeface(EasyFonts.freedom(this));
        t2.setTypeface(EasyFonts.freedom(this));
        t3.setTypeface(EasyFonts.freedom(this));
        t4.setTypeface(EasyFonts.freedom(this));
        final Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.Bounce)
                                .duration(2000)
                                .playOn(findViewById(R.id.tv1));
                        YoYo.with(Techniques.RubberBand)
                                .duration(2000)
                                .playOn(findViewById(R.id.tv2));
                        YoYo.with(Techniques.Shake)
                                .duration(2000)
                                .playOn(findViewById(R.id.tv3));
                        YoYo.with(Techniques.Swing)
                                .duration(2000)
                                .playOn(findViewById(R.id.tv4));
                        YoYo.with(Techniques.Bounce)
                                .duration(2000)
                                .playOn(findViewById(R.id.happyButton));
                        YoYo.with(Techniques.RubberBand)
                                .duration(2000)
                                .playOn(findViewById(R.id.sadButton));
                        YoYo.with(Techniques.Shake)
                                .duration(2000)
                                .playOn(findViewById(R.id.angryButton));
                        YoYo.with(Techniques.Swing)
                                .duration(2000)
                                .playOn(findViewById(R.id.about));
                    }
                });/*
                if(radio!=0){
                    myTimer.cancel();
                }*/
            }
        }, 1000, 2000);
    }
}
