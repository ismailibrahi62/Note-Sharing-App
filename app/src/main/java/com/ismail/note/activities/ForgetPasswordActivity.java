package com.ismail.note.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.ismail.note.R;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText edEmailForget;
    private Button buttonSend;
    private ProgressBar progressBarForgetPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize the UI components
        edEmailForget = findViewById(R.id.ed_email_forget);
        buttonSend = findViewById(R.id.buttonSend);
        progressBarForgetPassword = findViewById(R.id.progressBarForgetPassword);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Optionally, you can set listeners or any other initialization code here
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String email = edEmailForget.getText().toString();

                if(email.isEmpty()){
                    edEmailForget.setError("Your email is required");
                    edEmailForget.requestFocus();
                }
                else if (email.contains(" ")) {
                    edEmailForget.setError("Email cannot contain whitespace");
                    edEmailForget.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edEmailForget.setError("Please enter a valid email address");
                    edEmailForget.requestFocus();
                }
                else{
                    progressBarForgetPassword.setVisibility(View.VISIBLE);
                    sendResetEmail(email);
                }
            }
        });
    }

    private void sendResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            progressBarForgetPassword.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(ForgetPasswordActivity.this, "password reset sent succesfully check your email!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgetPasswordActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                    edEmailForget.setError("No account found with this email.");
                    edEmailForget.requestFocus();
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ForgetPasswordActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}