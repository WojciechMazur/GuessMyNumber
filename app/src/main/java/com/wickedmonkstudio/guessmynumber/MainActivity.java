package com.wickedmonkstudio.guessmynumber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends Activity {

    private Button checkButton;
    private EditText inputText;
    private int seekedNumber;
    private int clickCounter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkButton=(Button)findViewById(R.id.checkButton);
        inputText=(EditText)findViewById(R.id.inputNumberEditText);
        seekedNumber=new Random().nextInt(100);
       // showToast(String.valueOf(seekedNumber));

        checkButton.setOnClickListener((v)->{
            clickCounter++;
            if(Integer.parseInt(inputText.getText().toString())<seekedNumber)
                showToast("Too low!");
            else if(Integer.parseInt(inputText.getText().toString())>seekedNumber)
                showToast("Too high!");
            else {
                showToast("Correct!!!");
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("score", String.valueOf(clickCounter));
                startActivity(intent);
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }
}
