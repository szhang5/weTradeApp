package com.shiyunzhang.wetrade.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shiyunzhang.wetrade.DataClass.UserFCMToken;
import com.shiyunzhang.wetrade.Activity.HomeActivity;
import com.shiyunzhang.wetrade.Activity.MainActivity;
import com.shiyunzhang.wetrade.R;

public class RegisterActivity extends AppCompatActivity {
    private final static String TAG = "RegisterActivity";
    private EditText emailInput;
    private EditText passwordInput;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userTokenRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        progressDialog = new ProgressDialog(this);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
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
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user'SearchFragment information
                        Toast.makeText(RegisterActivity.this, "Register User With Email: success!!!.", Toast.LENGTH_SHORT).show();
                        registerFCMToken();
                        finish();
                        startActivity(new Intent(this, HomeActivity.class));
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(RegisterActivity.this, "Register failed, please check your email again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void addButtonListener() {
        findViewById(R.id.sign_up_button).setOnClickListener(v -> registerUser());

        findViewById(R.id.sign_in).setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, MainActivity.class)));
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
                    userTokenRef = db.collection("UserToken").document(uid);
                    userTokenRef.set(userFCMToken, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Toast.makeText(RegisterActivity.this, "added token successfully!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show());

                });
    }

}
