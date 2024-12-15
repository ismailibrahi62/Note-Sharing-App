package com.ismail.note.activities;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismail.note.R;

public class SignInActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private TextView forgetPasswordTextView;
    private TextView signUpTxt;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Find views by their IDs
        emailEditText = findViewById(R.id.ed_email);
        passwordEditText = findViewById(R.id.ed_pwd);
        signInButton = findViewById(R.id.buttonSignIn);
        forgetPasswordTextView = findViewById(R.id.forget_password);
        signUpTxt = findViewById(R.id.signUptxt);
        progressBar = findViewById(R.id.progressBarInSignIn);

        forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgetPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        //intialize firebase
        mAuth = FirebaseAuth.getInstance();

        // Set OnClickListener
        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when signUpTxt is clicked
                // For example, open SignUpActivity and clear the activity stack
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish(); // Close the current activity
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailEditText.getText().toString();
                String userPassword = passwordEditText.getText().toString();



                if(userEmail.isEmpty()){
                    emailEditText.setError("Your email is required");
                    emailEditText.requestFocus();
                }
                else if (userEmail.contains(" ")) {
                    emailEditText.setError("Email cannot contain whitespace");
                    emailEditText.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    emailEditText.setError("Please enter a valid email address");
                    emailEditText.requestFocus();
                }
                else if(userPassword.isEmpty()){
                    passwordEditText.setError("Your password is required");
                    passwordEditText.requestFocus();
                }
                else if (userPassword.length() > 10) {
                    passwordEditText.setError("Password cannot be more than 10 characters");
                    passwordEditText.requestFocus();
                }
                else if (userPassword.contains(" ")) {
                    passwordEditText.setError("Password cannot contain whitespace");
                    passwordEditText.requestFocus();
                }
                else if (userPassword.length() < 6) {
                    passwordEditText.setError("Password cannot be less than 6 characters");
                    passwordEditText.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    signInWithEmailPassword(userEmail,userPassword);
                }
            }
        });




    }

    private void signInWithEmailPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(SignInActivity.this, NotesActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, navigate to main activity
            Intent mainIntent = new Intent(SignInActivity.this, NotesActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}