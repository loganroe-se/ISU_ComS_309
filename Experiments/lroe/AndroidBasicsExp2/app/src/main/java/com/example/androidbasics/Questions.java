package com.example.androidbasics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Questions extends AppCompatActivity {

    String name, favFood;
    int favNumber, numPets, age;

    EditText nameInput, ageInput, favNumberInput, numPetsInput, favFoodInput;

    Button submitButton, backButton;

    String allInputs = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        nameInput = (EditText) findViewById(R.id.nameInput);
        ageInput = (EditText) findViewById(R.id.ageInput);
        favNumberInput = (EditText) findViewById(R.id.favNumberInput);
        numPetsInput = (EditText) findViewById(R.id.numPetsInput);
        favFoodInput = (EditText) findViewById(R.id.favFoodInput);

        submitButton = (Button) findViewById(R.id.submitButton);
        backButton = (Button) findViewById(R.id.backButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameInput.getText().toString();
                favFood = favFoodInput.getText().toString();
                favNumber = Integer.valueOf(favNumberInput.getText().toString());
                numPets = Integer.valueOf(numPetsInput.getText().toString());
                age = Integer.valueOf(ageInput.getText().toString());

                allInputs = String.join(", ", name, favFood, String.valueOf(favNumber), String.valueOf(numPets), String.valueOf(age));

                Toast.makeText(getApplicationContext(), allInputs, Toast.LENGTH_LONG).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Questions.this, MainActivity.class);
                i.putExtra("allInputs", allInputs);
                startActivity(i);
            }
        });
    }
}