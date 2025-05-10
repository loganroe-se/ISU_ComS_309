package com.example.experiment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button nextBtn;

    TextView numberTxt;

    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find screen elements
        nextBtn = findViewById(R.id.toCounterBtn);
        numberTxt = findViewById(R.id.number);

        //Read the extra count value from an intent if there is one
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            counter = extras.getInt("count");
            numberTxt.setText(String.valueOf(counter));
        }

        //Set click listener on the button
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Sending over the counter value via intent
                Intent intent = new Intent(MainActivity.this, CounterActivity.class);
                intent.putExtra("count", counter);
                startActivity(intent);
            }
        });


    }


}