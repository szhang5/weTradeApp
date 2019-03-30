package com.shiyunzhang.wetrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.user_email);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        mTextView.setText("Welcome, " + firebaseUser.getEmail());

        findViewById(R.id.log_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    public void logOut(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
    };
}
