package com.shiyunzhang.wetrade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.UserInfo;


public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private FirebaseAuth firebaseAuth;
    private String uid;

    private EditText editFirstName, editLastName, editEmail, editPhone, editAddress, editCity, editState, editZipcode;
    private Switch editGender;
    private TextView male, female;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
        }
        init();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        editEmail.setText(firebaseUser.getEmail());
        uid = firebaseUser.getUid();
        setTitle("Edit Profile");
        setUpButtonListener();

    }

    @Override
    protected void onStart() {
        super.onStart();
        userRef.whereEqualTo("id", uid)
                .addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        UserInfo userInfo = documentSnapshot.toObject(UserInfo.class);
                        handleUserData(userInfo);
                    }
                });
    }

    public void init() {
        male = findViewById(R.id.male_selector);
        female = findViewById(R.id.female_selector);
        editFirstName = findViewById(R.id.user_firstname_input);
        editLastName = findViewById(R.id.user_lastname_input);
        editEmail = findViewById(R.id.user_email_input);
        editPhone = findViewById(R.id.user_phone_input);
        editAddress = findViewById(R.id.user_address1_input);
        editCity = findViewById(R.id.user_city_input);
        editState = findViewById(R.id.user_state_input);
        editZipcode = findViewById(R.id.user_zipcode_input);
        editGender = findViewById(R.id.sexSwitch);
    }

    public void setUpButtonListener() {
        editGender.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                male.setTextColor(Color.parseColor("#000000"));
                female.setTextColor(Color.parseColor("#36B88B"));
            } else {
                female.setTextColor(Color.parseColor("#000000"));
                male.setTextColor(Color.parseColor("#36B88B"));
            }
        });
        findViewById(R.id.log_out_button).setOnClickListener(v -> logOut());
        findViewById(R.id.save_button).setOnClickListener(v -> saveUserInformation());

    }

    public void saveUserInformation() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String state = editState.getText().toString().trim();
        long zipCode = Long.parseLong(editZipcode.getText().toString().trim());
        String gender = editGender.isChecked() ? "Female" : "Male";


        UserInfo userInfo = new UserInfo(firstName, lastName, email, phone, address, city, state, zipCode, gender, uid);

        userRef.add(userInfo)
                .addOnSuccessListener(aVoid -> Toast.makeText(EditProfileActivity.this, "Profile Information Saved", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Error!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.toString());
                });
    }

    public void handleUserData(UserInfo userInfo) {
        editFirstName.setText(userInfo.getFirstName());
        editLastName.setText(userInfo.getLastName());
        editEmail.setText(userInfo.getEmail());
        editPhone.setText(userInfo.getPhone());
        editAddress.setText(userInfo.getAddress());
        editCity.setText(userInfo.getCity());
        editState.setText(userInfo.getState());
        editZipcode.setText(Long.toString(userInfo.getZipCode()));
        if (userInfo.getGender().equals("Male")) {
            editGender.setChecked(false);
        } else {
            editGender.setChecked(true);
        }
    }

    public void logOut() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

}

