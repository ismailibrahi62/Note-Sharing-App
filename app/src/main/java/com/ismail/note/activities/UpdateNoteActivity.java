package com.ismail.note.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ismail.note.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateNoteActivity extends AppCompatActivity {
    private TextView timeDateTextViewUpdate;
    private EditText titleEditTextUpdate;
    private EditText noteEditTextUpdate;
    private Button cancelButtonUpdate;
    private Button updateButton;
    private ProgressBar progressBarInUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        // Initialize views
        timeDateTextViewUpdate = findViewById(R.id.timeDateTextViewUpdate);
        titleEditTextUpdate = findViewById(R.id.titleEditTextUpdate);
        noteEditTextUpdate = findViewById(R.id.noteEditTextUpdate);
        cancelButtonUpdate = findViewById(R.id.cancelButtonUpdate);
        updateButton = findViewById(R.id.updateButton);
        progressBarInUpdate = findViewById(R.id.progressBarInUpdate);

        displayCurrentTime();

        Intent intent = getIntent();
        if (intent != null) {
            String noteTitle = intent.getStringExtra("noteTitle");
            String noteContent = intent.getStringExtra("noteContent");

            // Set note title and content in EditText fields
            titleEditTextUpdate.setText(noteTitle);
            noteEditTextUpdate.setText(noteContent);
        }
        // Add click listener to update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get updated title and content from EditText fields
                String updatedTitle = titleEditTextUpdate.getText().toString().trim();
                String updatedContent = noteEditTextUpdate.getText().toString().trim();

                // Check if the user has made any changes
                if (updatedTitle.isEmpty() && updatedContent.isEmpty()) {
                    // Show a toast message indicating that no changes were made
                    Toast.makeText(UpdateNoteActivity.this, "You did not make any changes", Toast.LENGTH_SHORT).show();
                } else {
                    // Update the note in the database
                    updateNote();
                }
            }
        });
        cancelButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
    private void displayCurrentTime() {
        // Get current time
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/h:mm a", Locale.getDefault());
        String currentTime = sdf.format(Calendar.getInstance().getTime());

        // Set the current time to timeDateTextView
        timeDateTextViewUpdate.setText(currentTime);
    }
    private void updateNote() {
        // Get the updated title and content
        String updatedTitle = titleEditTextUpdate.getText().toString().trim();
        String updatedNote = noteEditTextUpdate.getText().toString().trim();

        // Check if the user has made any changes
        if (updatedTitle.isEmpty() && updatedNote.isEmpty()) {
            // Show a toast message indicating that no changes were made
            Toast.makeText(this, "You didn't make any changes.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the note ID from the intent
        String noteId = getIntent().getStringExtra("noteId");

        // Update the note in the database with the current time
        DatabaseReference noteRef = FirebaseDatabase.getInstance().getReference()
                .child("notes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(noteId);

        Map<String, Object> noteUpdates = new HashMap<>();
        noteUpdates.put("title", updatedTitle);
        noteUpdates.put("content", updatedNote);
        noteUpdates.put("timestamp", timeDateTextViewUpdate.getText().toString()); // Use the displayed time

        // Perform the update operation
        noteRef.updateChildren(noteUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Note updated successfully
                        Toast.makeText(UpdateNoteActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update the note
                        Toast.makeText(UpdateNoteActivity.this, "Failed to update note. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}