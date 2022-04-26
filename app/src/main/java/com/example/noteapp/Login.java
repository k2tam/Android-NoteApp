package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextView loginForgot;
    EditText loginEmail, loginPassword;
    Button loginSign, loginRegister;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();

        initUi();
        initListener();
    }

    private void initListener() {
        //Switch to create account
        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        loginSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Extract Data
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if(email.isEmpty()){
                    loginEmail.setError("Email is missing");
                    return;
                }

                if(password.isEmpty()){
                    loginPassword.setError("Password is missing");
                    return;
                }

//                Data is valid
//                Login user
                fAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if(!fAuth.getCurrentUser().isEmailVerified()){
                            startActivity(new Intent(getApplicationContext(), VerifyEmail.class));
                            finish();
                        }else{
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        loginForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetPassword.class));
            }
        });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(FirebaseAuth.getInstance().getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//        }
//    }

    private void initUi(){
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginSign = findViewById(R.id.loginSignIn);
        loginRegister = findViewById(R.id.loginRegister);
        loginForgot = findViewById(R.id.loginForgot);

    }
}