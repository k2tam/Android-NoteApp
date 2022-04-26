package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ResetCheckMail extends AppCompatActivity {

    Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_check_mail);

        btnHome = findViewById(R.id.checkMailBack);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

//        emailBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mailClient = new Intent(Intent.ACTION_VIEW);
//                mailClient.setPackage("com.google.android.gm");
//
//                if (mailClient.resolveActivity(getPackageManager())!=null)
//                    startActivity(mailClient);
//                else
//                    Toast.makeText(getApplicationContext(),"Gmail App is not installed",Toast.LENGTH_SHORT).show();
//
//
//            }
//        });


    }
}