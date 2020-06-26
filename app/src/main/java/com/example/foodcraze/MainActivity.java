package com.example.foodcraze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button btnSignUp,btnSignIn;
    TextView txtSlogan,txtSlogan1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        txtSlogan = (TextView)findViewById(R.id.txtSlogan);
        txtSlogan1 = (TextView)findViewById(R.id.txtSlogan1);
        Typeface face =Typeface.createFromAsset(getAssets(),"fonts/LHANDW.TTF");
        Typeface face1 =Typeface.createFromAsset(getAssets(),"fonts/ALGER.TTF");
        txtSlogan.setTypeface(face);
        txtSlogan1.setTypeface(face);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(MainActivity.this,SignIn.class);
                startActivity(signin);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(MainActivity.this,SignUp.class);
                startActivity(signup);
            }
        });
    }
}
