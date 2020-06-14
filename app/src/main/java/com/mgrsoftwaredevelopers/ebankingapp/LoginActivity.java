package com.mgrsoftwaredevelopers.ebankingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText userEmail, userPassword, resetPassword;
    LinearLayout loginView, resetPasswordViews;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();

    }

    private void initializeViews() {

        userEmail = findViewById(R.id.email_txt);
        userPassword = findViewById(R.id.password_txt);
        resetPassword = findViewById(R.id.reset_email_txt);

        loginView = findViewById(R.id.login_view);
        resetPasswordViews = findViewById(R.id.reset_password_view);

    }

//  todo step 1 begins @ logging
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (!(currentUser == null)) {

            sendUserToMainActivity();

        }

    }

    private void sendUserToMainActivity() {

        Intent mainAct = new Intent(this, MainActivity.class);
        mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainAct);
        finish();

    }
//  todo step 1 ends @ logging

//    todo step 3 begins
    public void login(View view) {

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)){

            Toast.makeText(this, "Please Enter Your Email Address.. ", Toast.LENGTH_LONG).show();
            userEmail.setError("Please type your Email");

        } else if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Please Enter Your Password.. ", Toast.LENGTH_LONG).show();
            userPassword.setError("Please type your Password");

        } else {

            final ProgressDialog loadingBar;
            loadingBar = new ProgressDialog(this);

            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while your account is login");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        sendUserToMainActivity();
                        loadingBar.dismiss();

                    } else {

                        String error_msg = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(LoginActivity.this, "error occurred: " + error_msg, Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Error !!!");
                        builder.setMessage(error_msg);
                        builder.show();

                    }

                }
            });

        }
    }
//    todo step 3 ends

//    todo step 2 begins
    public void createAccount(View view) {

        Intent intent = new Intent(this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
//    todo step 2 ends

//    todo step 5 begins
    public void resetPassword(View view) {

        switch (view.getId()) {

            case R.id.resetPasswordTxt:

                loginView.setVisibility(View.GONE);
                resetPasswordViews.setVisibility(View.VISIBLE);

                break;

            case R.id.reset_email_btn:

                resetUserPassword();

                break;


        }

    }

    private void resetUserPassword() {

        String email = resetPassword.getText().toString();

        if (TextUtils.isEmpty(email)){

            resetPassword.setError("email address needed");
            Toast.makeText(this, "Please enter the EmailAddress you registered with \n Thank you...", Toast.LENGTH_LONG).show();

        } else {

            final ProgressDialog loadingBar;
            loadingBar = new ProgressDialog(this);
            loadingBar.setTitle("Resetting Your Password");
            loadingBar.setMessage("Please Wait While the system is Resetting Your Password. \n\r Check Your Email to Create a New Password. ");
            loadingBar.show();

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        loginView.setVisibility(View.VISIBLE);
                        resetPasswordViews.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Your Password Has been reset. \n " +
                                "check your email address to create a new password", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();

                    } else {
                        loadingBar.dismiss();
                        String error_msg = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(LoginActivity.this, "Error: "+ error_msg, Toast.LENGTH_LONG).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Error !!!");
                        builder.setMessage(error_msg);
                        builder.show();

                    }

                }
            });

        }

    }

    public void closeResetPassword(View view) {

        loginView.setVisibility(View.VISIBLE);
        resetPasswordViews.setVisibility(View.GONE);

    }
//    todo step 4 ends

}
