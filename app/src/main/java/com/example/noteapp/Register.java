package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText registerName, registerEmail, registerPassword, registerConfirmPassword;
    Button btnRegister, btnLogin;
    FirebaseAuth fireAuth;
    ConstraintLayout mLayoutRegister;
    String userID, userName, userEmail;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUi();
        initListener();
    }

    private void initUi(){
        mLayoutRegister = findViewById(R.id.layoutRegister);
        registerName = findViewById(R.id.registerName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirmPassword = findViewById(R.id.registerPassword2);
        btnRegister = findViewById(R.id.registerBtnRegister);
        btnLogin = findViewById(R.id.btnRegisterLogin);

        fireAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    private void initListener() {
        //        Switch to Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                  Extract the data from the form
                userName = registerName.getText().toString().trim();
                userEmail = registerEmail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();
                String confPass = registerConfirmPassword.getText().toString().trim();

                if(userName.isEmpty()){
                    setErr(registerName,"Name is required");
                    return;
                }

                if(userEmail.isEmpty()){
                    setErr(registerEmail,"Email is required");
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    setErr(registerEmail, "Email is invalid");
                    return;
                }

                if(password.isEmpty()){
                    setErr(registerPassword, "Password is required");
                    return;
                }

                if(confPass.isEmpty()){
                    setErr(registerPassword, "Confirm password is required");
                    return;
                }

                if(!password.equals(confPass)){
                    setErr(registerConfirmPassword, "Password is invalid");
                    return;
                }

//              Data is validated
                fireAuth.createUserWithEmailAndPassword(userEmail,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        userID = fireAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("name",userName);
                        user.put("email",userEmail);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("put","OK");
                            }
                        });

//                        Send user to next page
                        startActivity(new Intent(getApplicationContext(),VerifyEmail.class));
                        finishAffinity();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });

    }

    private void setErr(EditText edt, String warn){
        edt.setError(warn);
    }


}