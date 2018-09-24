package com.onur.kryptow.timetravel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.onur.kryptow.timetravel.custom_font.MyEditText;
import com.onur.kryptow.timetravel.custom_font.MyTextView;

public class LogInActivity extends AppCompatActivity {

    protected MyEditText emailEditText,passwordEditText;
    private MyTextView logInButton,signUpTextView,needhelp;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mFirebaseAuth = FirebaseAuth.getInstance();

        signUpTextView =  findViewById(R.id.signUpText);
        emailEditText =  findViewById(R.id.emailField);
        passwordEditText =  findViewById(R.id.passwordField);
        logInButton = findViewById(R.id.loginButton);
        needhelp =  findViewById(R.id.needhelp);

        needhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Mail: onuraykut@outlook.com", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog2=new ProgressDialog(LogInActivity.this);
                dialog2.setMessage("Giris yapiliyor..");
                dialog2.setCancelable(false);
                dialog2.setInverseBackgroundForced(false);
                dialog2.show();


                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                email = email.trim();
                password = password.trim();

                if (email.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    mFirebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    dialog2.dismiss();
                                    if (task.isSuccessful()) {

                                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                                        builder.setMessage(task.getException().getMessage())
                                                .setTitle(R.string.login_error_title)
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });

                }
            }
        });


    }
}
