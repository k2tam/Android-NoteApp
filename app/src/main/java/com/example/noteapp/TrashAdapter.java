package com.example.noteapp;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class TrashAdapter extends FirestoreRecyclerAdapter<Note, TrashAdapter.TrashHolder> {
    private OnItemClickListener listener;


    public TrashAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    class TrashHolder extends RecyclerView.ViewHolder {
            TextView title, content;
            CardView noteLayout;

            public TrashHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.note_title);
                content = itemView.findViewById(R.id.note_content);
                noteLayout = itemView.findViewById(R.id.noteLayout);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAbsoluteAdapterPosition();
                        if(position != RecyclerView.NO_POSITION && listener != null){
                            listener.onItemClick(getSnapshots().getSnapshot(position), position);
                        }
                    }
                });
            }
        }

        @Override
        protected void onBindViewHolder(@NonNull com.example.noteapp.TrashAdapter.TrashHolder holder, int position, @NonNull Note model) {
            holder.title.setText(model.getTitle());
            holder.content.setText(model.getContent());
        }

        @NonNull
        @Override
        public com.example.noteapp.TrashAdapter.TrashHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_grid, parent, false);

            return new TrashAdapter.TrashHolder(view);
        }

        public interface  OnItemClickListener{
            void onItemClick(DocumentSnapshot documentSnapshot, int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
}
