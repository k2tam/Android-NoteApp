package com.example.noteapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.noteapp.R.drawable.ic_unlock;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InputNote extends AppCompatActivity {
    private EditText mTitle, mContent;
    private ImageButton mAddNote, mPinNote, mInputLockNote;
    private String userID;
    private FirebaseFirestore fStore;
    private static int mPriority = 0;
    private static boolean mLock;
    private String mPassword;

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
                    finish();
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
            mInputLockNote.setBackgroundResource(R.drawable.ic_password);
            if(mTitle.getText().toString().isEmpty() && mContent.getText().toString().isEmpty()){
                Toast.makeText(this,"Cannot set password for empty note",Toast.LENGTH_SHORT).show();
                return;
            }
            DialogSetNotePass();
        }else if(mLock == true){
            unlockNoteDialog();
        }
    }

    private void unlockNoteDialog() {
        AlertDialog.Builder unlockNoteDialog = new AlertDialog.Builder(this);
        final AlertDialog show = unlockNoteDialog.show();
        unlockNoteDialog.setCancelable(true);
        unlockNoteDialog.setIcon(ic_unlock);
        unlockNoteDialog.setTitle("Unlock note");
        unlockNoteDialog.setMessage("Are you sure want to unlock this note ?");

        unlockNoteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mLock = false;
                mPassword = "";
                Toast.makeText(getApplicationContext(), "Unlocked note", Toast.LENGTH_SHORT).show();
                mInputLockNote.setBackgroundResource(R.drawable.ic_password);
                show.dismiss();

            }
        });

        unlockNoteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                show.dismiss();
            }
        });

        unlockNoteDialog.show();
    }

    private void DialogSetNotePass() {
        Dialog dialogSetNotePass = new Dialog(this);
        dialogSetNotePass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSetNotePass.setContentView(R.layout.dialog_note_set_pass);
        dialogSetNotePass.setCanceledOnTouchOutside(true);
        dialogSetNotePass.show();

        Window window = dialogSetNotePass.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        EditText edtNotePass = dialogSetNotePass.findViewById(R.id.edtUdtNotePass);
        EditText edtNotePassConf = dialogSetNotePass.findViewById(R.id.edtUdtNoteConf);
        Button btnCancel = dialogSetNotePass.findViewById(R.id.edtUdtNotePassNegative);
        Button btnOK = dialogSetNotePass.findViewById(R.id.edtNotePassPositive);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notePass = edtNotePass.getText().toString().trim();
                String notePassConf = edtNotePassConf.getText().toString().trim();

                if(notePass.isEmpty()){
                    edtNotePass.setError("Please enter note password");
                    return;
                }

                if(notePassConf.isEmpty()){
                    edtNotePassConf.setError("Please enter note password verification");
                    return;
                }

                if(!notePassConf.equals(notePass)){
                    edtNotePassConf.setError("Note password verification and password are not match");
                    return;
                }

                mLock = true;
                mPassword = notePass;

                dialogSetNotePass.dismiss();
                mInputLockNote.setBackgroundResource(R.drawable.ic_unlock);
                Toast.makeText(getApplicationContext(),"Successfully set password for note",Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetNotePass.dismiss();
            }
        });
    }

    private void pushNote(String noteID, int priority, String title, String content, String password,boolean lock) {
        Note note = new Note(noteID, priority, title,content, password,lock);

        DocumentReference documentReference = fStore.collection("users").document(userID).collection("notes").document(noteID);
        Map<String, Object> noteAdd = note.toMap();
        documentReference.set(noteAdd);
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
        mLock = false;
        mPassword = "";

    }

    private String createNoteID(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return (dtf.format(now)).toString().replace('/','-').replaceAll("\\s","").trim();
    }
}