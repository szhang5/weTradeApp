package com.shiyunzhang.wetrade;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddItemActivity extends AppCompatActivity {
    private static final String TAG = "AddItemActivity";
    private static final int PICK_IMAGE_REQUEST = 2;

    private AppCompatSpinner itemCondition, itemCategory;
    private EditText itemName, itemDescription, itemPrice, itemQuantity;
    private Button saveButton, chooseImgButton;
    private ImageView itemImg;
    private String category, name, description, condition, imageUrl;
    private Uri imageUri;
    private double price;
    private int quantity;
    private long currentTime;
    private FirebaseAuth firebaseAuth;
    private String uid, itemID;
    private ProgressBar mProgressBar;
    private UserInfo userInfo;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("InventoryImage");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private CollectionReference inventoryRef = db.collection("Inventory");
    private DocumentReference inventoryRef = db.collection("Inventory").document();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(AddItemActivity.this, LoginActivity.class));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        setTitle("Add an item");
        setUpActionBar();
        init();
        getUserInfoFromPreference();
        setUpConditionSpinner();
        setUpCategorySpinner();
        setUpButtonListener();

    }

    private void getUserInfoFromPreference(){
        SharedPreferences preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String user = preferences.getString("USER", "");
        Gson gson = new Gson();
        userInfo = gson.fromJson(user, UserInfo.class);
    }

    private void init() {
        itemID = inventoryRef.getId();
        itemCategory = findViewById(R.id.item_category);
        itemName = findViewById(R.id.item_name);
        itemDescription = findViewById(R.id.item_description);
        itemPrice = findViewById(R.id.item_price);
        itemQuantity = findViewById(R.id.item_quantity);
        itemCondition = findViewById(R.id.item_condition);
        saveButton = findViewById(R.id.save_button);
        chooseImgButton = findViewById(R.id.upload_img_button);
        itemImg = findViewById(R.id.item_image);
        mProgressBar = findViewById(R.id.inventory_progressbar);
    }

    private boolean isValidInput() {
        if(itemName.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item name", Toast.LENGTH_LONG).show();
            return false;
        }
        if(itemDescription.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item description", Toast.LENGTH_LONG).show();
            return false;
        }
        if(itemPrice.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item price", Toast.LENGTH_LONG).show();
            return false;
        }
        if(itemQuantity.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item quantities", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void setUpButtonListener(){
        saveButton.setOnClickListener(v->{
            uploadFile();
        });
        chooseImgButton.setOnClickListener(v->{
            openFileChooser();
        });
    }


    public void clearAllInfo(){
        itemCategory.setSelection(0);
        itemName.setText("");
        itemDescription.setText("");
        itemPrice.setText("");
        itemQuantity.setText("");
        itemCondition.setSelection(0);
        itemImg.setImageURI(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            mProgressBar.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                itemImg.setImageBitmap(bitmap);
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

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    private void setUpConditionSpinner(){
        String[] conditions = getResources().getStringArray(R.array.itemCondition);
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, conditions);
        itemCondition.setAdapter(conditionAdapter);
        itemCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                condition = conditions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                condition = conditions[0];
            }
        });
    }

    private void setUpCategorySpinner(){
        String[] categories = getResources().getStringArray(R.array.itemCategory);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, categories);
        itemCategory.setAdapter(categoryAdapter);
        itemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = categories[0];
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveInventoryInfo(){
        if(isValidInput()){
            name = itemName.getText().toString();
            description = itemDescription.getText().toString();
            price = Double.parseDouble(itemPrice.getText().toString());
            quantity = Integer.parseInt(itemQuantity.getText().toString());
        }

        currentTime = System.currentTimeMillis();

        Inventory inventoryInfo = new Inventory(itemID, imageUrl, category, name, description, price, quantity, condition, currentTime, uid);

        inventoryRef.set(inventoryInfo)
            .addOnSuccessListener(aVoid -> {
                Client client = new Client("KBCNT58640", "7bf45a6696368d5ebc1ff04e57e4e1e0");
                Index index = client.getIndex("Inventory");
                Gson gson = new Gson();
                String json = gson.toJson(inventoryInfo);
                try {
                    index.addObjectAsync(new JSONObject(json), null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(AddItemActivity.this, "Item Information Saved", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(AddItemActivity.this, "Error!", Toast.LENGTH_LONG).show();
                Log.d(TAG, e.toString());
            });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = mStorageRef.child(itemID + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrl = uri.toString();
                            Handler handler = new Handler();
                            handler.postDelayed(() -> mProgressBar.setProgress(0), 5000);
                            saveInventoryInfo();
                            mProgressBar.setVisibility(View.GONE);
                            finish();
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show())
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mProgressBar.setProgress((int) progress);
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }
}
