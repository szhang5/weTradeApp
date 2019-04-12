package com.shiyunzhang.wetrade.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.UserInfo;
import com.shiyunzhang.wetrade.EditProfileActivity;
import com.shiyunzhang.wetrade.MainActivity;
import com.shiyunzhang.wetrade.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private String TAG = "ProfileFragment";
    private FirebaseAuth firebaseAuth;
    TextView userName, college, graduationDate, email, address;
    CircleImageView profileImg;
    Button editProfileButton, logout;
    private String uid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("User");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        getUserData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });
        logout.setOnClickListener(v -> logOut());
    }

    private void init(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        profileImg = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name_display);
        college = view.findViewById(R.id.user_college_display);
        graduationDate = view.findViewById(R.id.user_graduation_date);
        email = view.findViewById(R.id.user_email_display);
        address = view.findViewById(R.id.user_address_display);
        editProfileButton = view.findViewById(R.id.edit_profile);
        logout = view.findViewById(R.id.log_out_button);
    }

    public void getUserData() {
        userRef.whereEqualTo("id", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        UserInfo userInfo = queryDocumentSnapshot.toObject(UserInfo.class);
                        if (userInfo.getFirstName() != null && userInfo.getLastName() != null)
                            userName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
                        if (userInfo.getCollege() != null) college.setText(userInfo.getCollege());
                        if (userInfo.getExpectedGraduationDate() != null)
                            graduationDate.setText(userInfo.getExpectedGraduationDate());
                        if(userInfo.getEmail() != null) email.setText(userInfo.getEmail());
                        if(userInfo.getAddress() != null)
                            address.setText(userInfo.getCity() + ", " + userInfo.getState());
                        if (userInfo.getImageUrl() != null)
                            Glide.with(this).load(userInfo.getImageUrl()).into(profileImg);

                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }

    public void logOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(getContext(), MainActivity.class));
    }
}
