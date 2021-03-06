package com.example.foodhub.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.foodhub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity  {

    private EditText forgotPasswordEmail;
    private Button resetPassword, resetCansel;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPasswordEmail = findViewById(R.id.forgotPasswordEmail);
        resetCansel = findViewById(R.id.resetPasswordCansel);
        resetPassword = findViewById(R.id.resetPassword);

        progressBar = findViewById(R.id.forgotPasswordProgressBar);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();

        // Вернуться в прошлую активность
        resetCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    // Изменение пароля
    private void resetPassword() {
        String email = forgotPasswordEmail.getText().toString().trim();

        if (email.isEmpty()) {
            forgotPasswordEmail.setError("Напишите тут свою почту");
            forgotPasswordEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            forgotPasswordEmail.setError("Укажите корректную почту");
            forgotPasswordEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Проверьте свою почту, чтобы поменять пароль", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this, "Что-то пошло не так, повторите попытку", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}