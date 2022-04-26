package com.example.noteapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;


import android.media.MediaPlayer;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class InputNote extends AppCompatActivity {
    private EditText mTitle, mContent;
    private ImageButton mAddNote, mPinNote, mInputLockNote;
    private String userID;
    private FirebaseFirestore fStore;
    private static int mPriority = 0;
    private static boolean mLock = false;
    private String mPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_note);

        initUI();
        initListener();

    }

    private void initListener() {
        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString().trim();
                String content = mContent.getText().toString().trim();

                if(title.isEmpty() && content.isEmpty()){
                    finish();
                }else{
                    String noteID = createNoteID();
                    pushNote(noteID, mPriority, title,content, mPassword,mLock);
                    mPriority = 0;
                }
            }
        });

        mPinNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notePin();
            }
        });
        
        mInputLockNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockNote();
            }
        });
    }

    private void lockNote() {
        if(mLock == false){
            mInputLockNote.setBackgroundResource(R.drawable.ic_lock);
            mLock = true;
        }else{
            mInputLockNote.setBackgroundResource(R.drawable.ic_unlock);
            mLock = false;
        }
    }

    private void pushNote(String noteID, int priority, String title, String content, String password,boolean lock) {
        Note note = new Note(noteID, priority, title,content, password,lock);

        DocumentReference documentReference = fStore.collection("users").document(userID).collection("notes").document(noteID);
        Map<String, Object> noteAdd = note.toMap();
        documentReference.set(noteAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                finish();
            }
        });
    }

    private void notePin() {
        if(mPriority == 0){
            mPinNote.setBackgroundResource(R.drawable.ic_pined);
            mPriority = 1;
        }else if(mPriority == 1){
            mPinNote.setBackgroundResource(R.drawable.ic_pin);
            mPriority = 0;
        }

    }

    private void initUI() {
        mTitle = findViewById(R.id.edtTitle);
        mContent = findViewById(R.id.edtContent);
        mAddNote = findViewById(R.id.addNote_save);
        mPinNote = findViewById(R.id.addNote_pin);
        mInputLockNote = findViewById(R.id.addNote_lock);
        fStore = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private String createNoteID(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return (dtf.format(now)).toString().replace('/','-').replaceAll("\\s","").trim();
    }
}