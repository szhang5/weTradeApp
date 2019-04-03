package com.shiyunzhang.wetrade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;
    private EditText editFirstName, editLastName, editEmail, editPhone, editAddress1, editAddress2, editCity, editState, editZipcode;
    private Switch editGender;
    private TextView male, female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        init();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        editEmail.setText(firebaseUser.getEmail());

        editGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    male.setTextColor(Color.parseColor("#000000"));
                    female.setTextColor(Color.parseColor("#FF5D7D"));
                } else {
                    female.setTextColor(Color.parseColor("#000000"));
                    male.setTextColor(Color.parseColor("#36B88B"));
                }
            }
        });

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        findViewById(R.id.log_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    public void init(){
        male = findViewById(R.id.male_selector);
        female = findViewById(R.id.female_selector);
        editFirstName = findViewById(R.id.user_firstname_input);
        editLastName = findViewById(R.id.user_lastname_input);
        editEmail = findViewById(R.id.user_email_input);
        editPhone = findViewById(R.id.user_phone_input);
        editAddress1 = findViewById(R.id.user_address1_input);
        editAddress2 = findViewById(R.id.user_address2_input);
        editCity = findViewById(R.id.user_city_input);
        editState = findViewById(R.id.user_state_input);
        editZipcode = findViewById(R.id.user_zipcode_input);
        editGender = findViewById(R.id.sexSwitch);
    }

    public void  saveUserInformation(){
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address1 = editAddress1.getText().toString().trim();
        String address2 = editAddress2.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String state = editState.getText().toString().trim();
        long zipCode = Long.parseLong(editZipcode.getText().toString().trim());
        String gender = editGender.isChecked()? "Female" : "Male";

        UserInfo userInfo = new UserInfo(firstName, lastName, email, phone, address1, address2, city, state, zipCode,gender);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(userInfo);

        Toast.makeText(ProfileActivity.this, "Profile Information Saved", Toast.LENGTH_LONG).show();
    }


    public void logOut(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
    };
}
