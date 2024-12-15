package com.ismail.note.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ismail.note.R;
import com.ismail.note.model_classes.User;

public class SignUpActivity extends AppCompatActivity {
    private EditText personNameEditText;
    private EditText emailSignUpEditText;
    private EditText passwordSignUpEditText;
    private Button signUpButton;
    private TextView signInTxt;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Use the actual URL of your uploaded default profile image
    private static final String DEFAULT_PROFILE_URL = "https://firebasestorage.googleapis.com/v0/b/note-b5a7c.appspot.com/o/defualt_profile.png?alt=media&token=dfbc0c7d-f23f-4d67-8d5c-7750a306ec04";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        personNameEditText = findViewById(R.id.ed_person_name);
        emailSignUpEditText = findViewById(R.id.ed_email_signUp);
        passwordSignUpEditText = findViewById(R.id.ed_pwd_singUp);
        signUpButton = findViewById(R.id.buttonSignUp);
        signInTxt = findViewById(R.id.tvSingIn);
        progressBar = findViewById(R.id.progressBar);


        // Initialize Firebase Auth and database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when signUpTxt is clicked
                // For example, open SignUpActivity and clear the activity stack
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish(); // Close the current activity
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = personNameEditText.getText().toString();
                String userEmail = emailSignUpEditText.getText().toString();
                String userPassword = passwordSignUpEditText.getText().toString();


                if (userName.isEmpty()){
                    personNameEditText.setError("Your name is required");
                    personNameEditText.requestFocus();
                }
                else if (userName.contains(" ")) {
                    personNameEditText.setError("Name cannot contain whitespace");
                    personNameEditText.requestFocus();
                } else if (userName.length() > 7) {
                    personNameEditText.setError("Name cannot be more than 10 characters");
                    personNameEditText.requestFocus();
                }
                else if(userEmail.isEmpty()){
                    emailSignUpEditText.setError("Your email is required");
                    emailSignUpEditText.requestFocus();
                }
                else if (userEmail.contains(" ")) {
                    emailSignUpEditText.setError("Email cannot contain whitespace");
                    emailSignUpEditText.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    emailSignUpEditText.setError("Please enter a valid email address");
                    emailSignUpEditText.requestFocus();
                }
                else if(userPassword.isEmpty()){
                    passwordSignUpEditText.setError("Your password is required");
                    passwordSignUpEditText.requestFocus();
                }
                else if (userPassword.length() > 10) {
                    passwordSignUpEditText.setError("Password cannot be more than 10 characters");
                    passwordSignUpEditText.requestFocus();
                }
                else if (userPassword.contains(" ")) {
                    passwordSignUpEditText.setError("Password cannot contain whitespace");
                    passwordSignUpEditText.requestFocus();
                }
                else if (userPassword.length() < 6) {
                    passwordSignUpEditText.setError("Password cannot be less than 6 characters");
                    passwordSignUpEditText.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    signUpWithEmailAndPassword(userName,userEmail,userPassword);
                }
            }
        });


    }

    /*private void signUpWithEmailAndPassword(String userName, String userEmail, String userPassword) {
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Hide progress bar
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "User creation failed. " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/

    private void signUpWithEmailAndPassword(String userName, String userEmail, String userPassword) {
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Get the newly created user
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Create a User object to store in the database
                                User newUser = new User(userName, userEmail, DEFAULT_PROFILE_URL);

                                // Save user details to the database
                                mDatabase.child("users").child(user.getUid()).setValue(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, "You 're registered successfully!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignUpActivity.this, NotesActivity.class);
                                                    intent.putExtra("userName",userName);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                                                    startActivity(intent);
                                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                    finish(); // Close the current activity
                                                } else {
                                                    Toast.makeText(SignUpActivity.this, "Failed to save user data. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "User creation failed. " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(SignUpActivity.this, SignInActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}