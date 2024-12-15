package com.ismail.note.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ismail.note.R;
import com.ismail.note.activities.UpdateNoteActivity;
import com.ismail.note.model_classes.UserNotes;

import java.util.List;

public class UserNotesAdapter extends RecyclerView.Adapter<UserNotesAdapter.NoteViewHolder> {
    private Context context;
    private List<UserNotes> notesList;

    public UserNotesAdapter(Context context, List<UserNotes> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        UserNotes note = notesList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());
        holder.dateTimeTextView.setText(note.getTimestamp());

        holder.shareImageView.setOnClickListener(v -> {
            // Handle share action
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "Title: " + note.getTitle() + "\n\n" + note.getContent();
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, note.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement onClick action here, e.g., open UpdateNoteActivity
                Intent intent = new Intent(context, UpdateNoteActivity.class);
                // Pass necessary data to the UpdateNoteActivity if needed
                intent.putExtra("noteId", note.getNoteId()); // Pass the ID of the note
                intent.putExtra("noteTitle", note.getTitle()); // Pass the title of the note
                intent.putExtra("noteContent", note.getContent()); // Pass the content of the note
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView titleTextView;
        TextView contentTextView;
        TextView dateTimeTextView;
        ImageView shareImageView;
        ConstraintLayout itemLayout;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.readTitle);
            contentTextView = itemView.findViewById(R.id.readContent);
            dateTimeTextView = itemView.findViewById(R.id.readDataAndTime);
            shareImageView = itemView.findViewById(R.id.shareIcon);
            itemLayout = itemView.findViewById(R.id.itemLayout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Open UpdateNoteActivity
            UserNotes note = notesList.get(getAdapterPosition());
            Intent intent = new Intent(context, UpdateNoteActivity.class);
            intent.putExtra("noteId", note.getNoteId()); // Pass the ID of the note
            intent.putExtra("noteTitle", note.getTitle()); // Pass the title of the note
            intent.putExtra("noteContent", note.getContent()); // Pass the content of the note
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            // Show a delete confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Delete Note");
            builder.setMessage("Are you sure you want to delete this note?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        removeNoteAt(position);
                    }
                }
            });
            builder.setNegativeButton("No", null);
            builder.show();
            return true; // Consume the long-click event
        }
    }


    public void removeNoteAt(int position) {
        if (position >= 0 && position < notesList.size()) {
            UserNotes note = notesList.remove(position);
            notifyItemRemoved(position);

            String noteId = note.getNoteId();
            if (noteId != null) {
                // Also remove the note from Firebase
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference noteRef = FirebaseDatabase.getInstance().getReference()
                        .child("notes")
                        .child(userId)
                        .child(noteId);
                noteRef.removeValue();
            } else {
                Toast.makeText(context, "Failed to delete note. Note ID is missing.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle invalid position
        }
    }
}
