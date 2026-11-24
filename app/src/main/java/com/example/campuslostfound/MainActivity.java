package com.example.campuslostfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn, signupBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);

        // 1. Login Button
        loginBtn.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if (!validateInputs(userEmail, userPassword)) return;

            loginBtn.setEnabled(false);
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        loginBtn.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // 2. Create New Account Button
        signupBtn.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if (!validateInputs(userEmail, userPassword)) return;

            signupBtn.setEnabled(false);
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnSuccessListener(authResult -> {
                        String uid = mAuth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("userId", uid);
                        user.put("email", userEmail);

                        db.collection("Users").document(uid).set(user)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(MainActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    finish();
                                });
                    })
                    .addOnFailureListener(e -> {
                        signupBtn.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Signup Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private boolean validateInputs(String userEmail, String userPassword) {
        if (userEmail.isEmpty()) {
            email.setError("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter valid email");
            return false;
        } else if (userPassword.isEmpty()) {
            password.setError("Enter password");
            return false;
        } else if (userPassword.length() < 6) {
            password.setError("Minimum 6 characters required");
            return false;
        }
        return true;
    }
}