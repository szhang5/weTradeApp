package com.shiyunzhang.wetrade;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerInfoActivity extends AppCompatActivity {

    private String uid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;
    private CircleImageView sellerImage;
    private TextView sellerName, sellerCollege;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_info);
        setUpActionBar();
        init();
        getSellerInfo();
    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void init(){
        Intent intent = getIntent();
        uid = intent.getStringExtra("UID");
        userRef = db.collection("User").document(uid);
        sellerImage = findViewById(R.id.seller_image);
        sellerName = findViewById(R.id.seller_name);
        sellerCollege = findViewById(R.id.seller_college);
    }

    private void getSellerInfo(){
        userRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                UserInfo userInfo = documentSnapshot.toObject(UserInfo.class);
                if(userInfo.getImageUrl() != null) {
                    Glide.with(SellerInfoActivity.this).load(userInfo.getImageUrl()).into(sellerImage);
                }
                if(userInfo.getFirstName() != null && userInfo.getLastName() != null) {
                    String name = userInfo.getFirstName() + " " + userInfo.getLastName();
                    setTitle(name);
                    sellerName.setText(name);
                }
                if(userInfo.getCollege() != null){
                    sellerCollege.setText(userInfo.getCollege());
                }
            })
            .addOnFailureListener(e -> Toast.makeText(SellerInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }
}
