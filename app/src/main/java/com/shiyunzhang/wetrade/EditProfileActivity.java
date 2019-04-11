package com.shiyunzhang.wetrade;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth firebaseAuth;
    private String uid;
    private Uri imageUri;
    private String profileImageUrl;

    private EditText editFirstName, editLastName, editEmail, editCollege, editGraduationDate, editAddress, editCity, editState, editZipcode;
    private Switch editGender;
    private TextView male, female;
    private CircleImageView profileImage;
    FloatingActionButton floatingActionButton;

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("UserImages");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("User");

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
        profileImage = findViewById(R.id.profile_image);
        male = findViewById(R.id.male_selector);
        female = findViewById(R.id.female_selector);
        editFirstName = findViewById(R.id.user_firstname_input);
        editLastName = findViewById(R.id.user_lastname_input);
        editEmail = findViewById(R.id.user_email_input);
        editCollege = findViewById(R.id.user_college_input);
        editGraduationDate = findViewById(R.id.user_expected_graduation_date_input);
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
        findViewById(R.id.save_button).setOnClickListener(v -> saveUserInformation());
        findViewById(R.id.upload_profile).setOnClickListener(v -> {
            Toast.makeText(this, "clicked save profile image!!!", Toast.LENGTH_LONG).show();
            uploadFile();
        });
        floatingActionButton = findViewById(R.id.choose_image);
        floatingActionButton.setOnClickListener(v -> openFileChooser());
    }

    public void saveUserInformation() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String college = editCollege.getText().toString().trim();
        String graduationDate = editGraduationDate.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String state = editState.getText().toString().trim();
        long zipCode = Long.parseLong(editZipcode.getText().toString().trim());
        String gender = editGender.isChecked() ? "Female" : "Male";

        UserInfo userInfo = new UserInfo(firstName, lastName, email, college, graduationDate, address, city, state, zipCode, gender, uid, profileImageUrl);

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
        editCollege.setText(userInfo.getCollege());
        editGraduationDate.setText(userInfo.getExpectedGraduationDate());
        editAddress.setText(userInfo.getAddress());
        editCity.setText(userInfo.getCity());
        editState.setText(userInfo.getState());
        editZipcode.setText(Long.toString(userInfo.getZipCode()));
        if (userInfo.getGender().equals("Male")) {
            editGender.setChecked(false);
        } else {
            editGender.setChecked(true);
        }
        if(userInfo.getImageUrl().trim().length() != 0){
            Log.d("aaaaaaaaaa", "user image url: " + userInfo.getImageUrl());
            profileImage.setImageURI(Uri.parse(userInfo.getImageUrl()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if(imageUri != null) {
            Log.d("aaaaaaaaaa", "imageUri: " + imageUri.toString());
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(EditProfileActivity.this, "Saved Successfully!", Toast.LENGTH_LONG).show();
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                profileImageUrl = uri.toString();
                                Log.d("aaaaaaaaaa" , "DownloadURL: " + uri.toString());
                            }
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }

}

