package com.example.noteapp;

import static com.example.noteapp.R.drawable.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noteapp.fragment.NoteFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;
import com.google.firestore.v1.WriteResult;

import java.util.Map;

public class UpdateNoteActivity extends AppCompatActivity   {
    ImageButton btnUpdateNoteSave, btnUpdatePin, btnUpdateNoteAction;
    TextView txtUdtTitle, txtUdtContent;
    private String userID;
    private FirebaseFirestore fStore;
    private DocumentReference noteDocReference;
    private int mPriority;
    private boolean mLock = false;
    private String mPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        initUI();
        initListener();
    }

    private void initUI() {
        btnUpdateNoteSave = findViewById(R.id.updateNoteUpdate);
        btnUpdatePin = findViewById(R.id.updateNotePin);
        btnUpdateNoteAction = findViewById(R.id.updateNoteAction);
        txtUdtTitle = findViewById(R.id.updateEdtTitle);
        txtUdtContent = findViewById(R.id.updateEdtContent);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        noteDocReference = fStore.collection("users").document(userID).collection("notes").document(getNoteClickedID());

    }

    private void initListener() {
        initPriority();
        getNoteClickedID();
        getNoteData();

        btnUpdateNoteAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionMenu();
            }
        });

        btnUpdateNoteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String udtNoteTitle = txtUdtTitle.getText().toString().trim();
                String udtNoteContent = txtUdtContent.getText().toString().trim();

                saveUpdateNote(udtNoteTitle,udtNoteContent);
            }
        });

        btnUpdatePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePin();
            }
        });
    }

    private void initPriority() {
        noteDocReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Note note = documentSnapshot.toObject(Note.class);
                int Priority = note.getPriority();
                Log.d("test",""+Priority);
                if(Priority == 0){
                    mPriority = 0;
                    btnUpdatePin.setBackgroundResource(ic_pin);
                }else if(Priority == 1){
                    mPriority = 1;
                    btnUpdatePin.setBackgroundResource(ic_pined);

                }
            }
        });
    }

    private void updatePin() {
        if(mPriority == 0){
            btnUpdatePin.setBackgroundResource(ic_pined);
            mPriority = 1;
        }else if(mPriority == 1){
            btnUpdatePin.setBackgroundResource(ic_pin);
            mPriority = 0;
        }
    }

    private void saveUpdateNote(String udtNoteTitle, String udtNoteContent) {
        Note note = new Note(userID, mPriority, udtNoteTitle, udtNoteContent, mPassword,mLock);
        Map<String, Object> noteUdt = note.toMap();
        noteDocReference.set(noteUdt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });

    }

    private void getNoteData() {
        noteDocReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Note note = documentSnapshot.toObject(Note.class);

                txtUdtTitle.setText(note.getTitle());
                txtUdtContent.setText(note.getContent());
            }
        });
    }

    private String getNoteClickedID() {
        return getIntent().getStringExtra("noteClickedID");
    }

    private void showActionMenu(){
        PopupMenu actionNoteMenu = new PopupMenu(getApplicationContext(), btnUpdateNoteAction);
        actionNoteMenu.getMenuInflater().inflate(R.menu.note_actions, actionNoteMenu.getMenu());
//        actionNoteMenu.setForceShowIcon(true);
        actionNoteMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.act_note_delete:
                        noteDocReference.delete();
                        onBackPressed();
                        break;
                }
                return false;
            }
        });
        actionNoteMenu.show();
    }
}