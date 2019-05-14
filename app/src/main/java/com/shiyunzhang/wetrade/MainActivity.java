package com.shiyunzhang.wetrade;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shiyunzhang.wetrade.Authentication.RegisterActivity;
import com.shiyunzhang.wetrade.DataClass.UserFCMToken;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private EditText emailInput;
    private EditText passwordInput;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        }

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);

        addButtonListener();
    }

    public void userLogIn(){
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

        progressDialog.setMessage("User signing in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    registerFCMToken();
                    finish();
                    startActivity(new Intent(this, HomeActivity.class));
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            });


    }

    public void addButtonListener(){
        findViewById(R.id.sign_up).setOnClickListener(v -> {
            finish();
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.sign_in_button).setOnClickListener(v -> userLogIn());
    }

    public void registerFCMToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    UserFCMToken userFCMToken = new UserFCMToken(uid, token);
                    DocumentReference userTokenRef = db.collection("UserToken").document(uid);
                    userTokenRef.set(userFCMToken, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "added token successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show());

                });
    }
}
