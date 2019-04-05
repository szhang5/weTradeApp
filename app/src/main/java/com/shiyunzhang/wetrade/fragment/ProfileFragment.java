package com.shiyunzhang.wetrade.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class ProfileFragment extends Fragment {
    private String TAG = "ProfileFragment";
    private FirebaseAuth firebaseAuth;
    TextView userName, location;
    private String uid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("User");

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userName = view.findViewById(R.id.user_name_display);
        location = view.findViewById(R.id.user_city_display);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        getUserData();
//        SharedPreferences preference = getActivity().getSharedPreferences("PREFERENCE",
//               getActivity().MODE_PRIVATE);
//        String name = preference.getString("FIRSTNAME", "") + " " + preference.getString("LASTNAME", "");
//        String city = "Location: " + preference.getString("CITY", "");
//        userName.setText(name);
//        location.setText(city);

        view.findViewById(R.id.edit_profile).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });
        view.findViewById(R.id.log_out_button).setOnClickListener(v -> logOut());

        return view;
    }

    public void getUserData() {
        userRef.whereEqualTo("id", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        UserInfo userInfo = queryDocumentSnapshot.toObject(UserInfo.class);
                        userName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
                        location.setText("Location: " + userInfo.getCity());
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }

    public void logOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(getContext(), MainActivity .class));
    }
}
