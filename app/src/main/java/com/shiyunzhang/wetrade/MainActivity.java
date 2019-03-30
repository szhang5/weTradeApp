package com.shiyunzhang.wetrade;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;

public class MainActivity extends AppCompatActivity {

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
    private EditText emailInput;
    private EditText passwordInput;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }

        emailInput = findViewById(R.id.email_input_register);
        passwordInput = findViewById(R.id.password_input_register);

        addButtonListener();

    }


    public void registerUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void addButtonListener() {
        findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}
