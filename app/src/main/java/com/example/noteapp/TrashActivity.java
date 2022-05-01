package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


public class TrashActivity extends AppCompatActivity {
    NoteAdapter noteAdapter;
    RecyclerView trashRecyclerView;
    CollectionReference collectionReference;
    FirebaseFirestore fStore;
    private GridLayoutManager gridLayoutManager;
    private FirestoreRecyclerOptions<Note> notes_del;
    private TextView txtBtnEmptyTrash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        initUI();
        initListener();
//        setLayoutManager();
//        setUpTrashRecyclerView();


//        clearExpiredNote();
    }

    private boolean checkDeleteCondition(QueryDocumentSnapshot document) throws ParseException {
        Boolean isDelete = false;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String deletedF = document.get("deleted_forever").toString();
        Calendar c_del = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        c_del.setTime(sdf.parse(deletedF));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String current_date = dtf.format(now);
        current.setTime(sdf.parse(current_date));

        if(current.compareTo(c_del) <= 0){
            isDelete = true;
        }

        return isDelete;
    }

    private void clearExpiredNote() {
        collectionReference.whereEqualTo("deleted","true").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
//                        try {
//                            if(checkDeleteCondition(document)){
//                                collectionReference.document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Log.d("delete","delete success");
//                                    }
//                                });
//
//                            };
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }
        });
    }


    private void setLayoutManager() {
        if(gridLayoutManager == null){
            gridLayoutManager = new GridLayoutManager(TrashActivity.this,2);
            trashRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
    }

    private void setUpTrashRecyclerView() {
        Query query = collectionReference.whereEqualTo("deleted",true).orderBy("priority",Query.Direction.DESCENDING);

        notes_del = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();



        noteAdapter = new NoteAdapter(notes_del);
        trashRecyclerView.setAdapter(noteAdapter);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        noteAdapter.startListening();
//        noteAdapter.notifyDataSetChanged();
//    }



//    @Override
//    public void onStop() {
//        super.onStop();
//        noteAdapter.stopListening();
//    }

    private void initUI() {
        txtBtnEmptyTrash = findViewById(R.id.txtBtnEmptyTrash);
        trashRecyclerView = findViewById(R.id.trashRecyclerView);
        fStore = FirebaseFirestore.getInstance();
        collectionReference = fStore.collection("users").document(FirebaseAuth.getInstance().getUid()).collection("notes");
    }

    private void initListener() {
        txtBtnEmptyTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}