package com.shiyunzhang.wetrade.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.UserInfo;
import com.shiyunzhang.wetrade.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth firebaseAuth;
    private Uri imageUri;
    private String profileImageUrl;
    private ProgressBar mProgressBar;

    private EditText editFirstName, editLastName, editEmail, editCollege, editGraduationDate, editAddress, editCity, editState, editZipcode;
    private String firstName, lastName, email, college, graduationDate, address, city, state, gender, uid;
    private long zipCode;
    private Switch editGender;
    private TextView male, female;
    private CircleImageView profileImage;
    FloatingActionButton floatingActionButton;

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("UserImages");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("User");

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
        setUpActionBar();
        setUpButtonListener();
    }


    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
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
                        if(userInfo.getFirstName()!= null) firstName = userInfo.getFirstName();
                        if(userInfo.getLastName()!= null) lastName = userInfo.getLastName();
                        if(userInfo.getEmail()!= null) email = userInfo.getEmail();
                        if(userInfo.getCollege()!= null) college = userInfo.getCollege();
                        if(userInfo.getExpectedGraduationDate()!= null) graduationDate = userInfo.getExpectedGraduationDate();
                        if(userInfo.getAddress()!= null) address = userInfo.getAddress();
                        if(userInfo.getCity()!= null) city = userInfo.getCity();
                        if(userInfo.getState()!= null) state = userInfo.getState();
                        if(userInfo.getZipCode()!= 0) zipCode = userInfo.getZipCode();
                        if(userInfo.getGender() != null) gender = userInfo.getGender();
                        if(userInfo.getImageUrl() != null) profileImageUrl = userInfo.getImageUrl();
                        handleUserData(userInfo);
                    }
                });
    }

    public void init() {
        mProgressBar = findViewById(R.id.profile_progressbar);
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

    private boolean isValidInput() {
        if (editFirstName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editLastName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your last name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editCollege.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your college", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editGraduationDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your expected graduation date", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editCity.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter the city", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editState.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter state", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editZipcode.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter zip code", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
        floatingActionButton = findViewById(R.id.choose_image);
        floatingActionButton.setOnClickListener(v -> openFileChooser());
    }

    public void saveUserInformation() {
        if (isValidInput()) {
            firstName = editFirstName.getText().toString().trim();
            lastName = editLastName.getText().toString().trim();
            email = editEmail.getText().toString().trim();
            college = editCollege.getText().toString().trim();
            graduationDate = editGraduationDate.getText().toString().trim();
            address = editAddress.getText().toString().trim();
            city = editCity.getText().toString().trim();
            state = editState.getText().toString().trim();
            zipCode = Long.parseLong(editZipcode.getText().toString().trim());
            gender = editGender.isChecked() ? "Female" : "Male";

            UserInfo userInfo = new UserInfo(firstName, lastName, email, college, graduationDate, address, city, state, zipCode, gender, uid, profileImageUrl);

            userRef.document(uid).set(userInfo, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Toast.makeText(EditProfileActivity.this, "Profile Information Saved", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.toString());
                    });
        }
    }


    public void saveUserProfile(){
        UserInfo userInfo = new UserInfo(firstName, lastName, email, college, graduationDate, address, city, state, zipCode, gender, uid, profileImageUrl);

        userRef.document(uid).set(userInfo, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(EditProfileActivity.this, "Profile Image Saved", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Error!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.toString());
                });
    }


    public void handleUserData(UserInfo userInfo) {
        if(firstName!= null) editFirstName.setText(firstName);
        if(lastName!= null) editLastName.setText(lastName);
        if(email!= null) editEmail.setText(email);
        if(college!= null) editCollege.setText(college);
        if(graduationDate!= null) editGraduationDate.setText(graduationDate);
        if(address!= null) editAddress.setText(address);
        if(city!= null) editCity.setText(city);
        if(state!= null) editState.setText(state);
        if(zipCode!= 0) editZipcode.setText(Long.toString(zipCode));
        if(gender != null && gender.equals("Male")) editGender.setChecked(false);
        else editGender.setChecked(true);
        if (profileImageUrl != null) Glide.with(this).load(userInfo.getImageUrl()).into(profileImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            mProgressBar.setVisibility(View.VISIBLE);
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                profileImage.setImageBitmap(bitmap);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
            uploadFile();
        }
    }

    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = mStorageRef.child(uid + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            profileImageUrl = uri.toString();
                            Handler handler = new Handler();
                            handler.postDelayed(() -> mProgressBar.setProgress(0), 5000);
                            saveUserProfile();
                            mProgressBar.setVisibility(View.GONE);
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show())
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mProgressBar.setProgress((int) progress);
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }

}

