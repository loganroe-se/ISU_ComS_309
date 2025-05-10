package com.example.experiment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CounterActivity extends AppCompatActivity {

    Button increaseBtn, backBtn, decreaseBtn, randomBtn, resetBtn;

    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        //Finding the screen elements
        increaseBtn = findViewById(R.id.increaseBtn);
        backBtn = findViewById(R.id.backBtn);
        decreaseBtn = findViewById(R.id.decreaseBtn);
        randomBtn = findViewById(R.id.randomBtn);
        resetBtn = findViewById(R.id.resetBtn);

        //Read the extra count value from an intent if there is one
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            counter = extras.getInt("count");
        }

        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++counter;
            }
        });

        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Capping the counter at a minimum of 0
                counter = Math.max(0, counter - 1);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sending the counter value to the other activity via intent
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                intent.putExtra("count", counter);
                startActivity(intent);
            }
        });

        randomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter += Math.floor(Math.random() * (100) + 1);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 0;
            }
        });

    }
}