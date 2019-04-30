package com.shiyunzhang.wetrade;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.UserInfo;

import java.io.IOException;
import java.util.ArrayList;


public class DetailInventory extends AppCompatActivity {
    private String TAG = "DetailInventory";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference itemRef;
    private ImageView itemImage, itemImageEdit;
    private TextView itemName, itemDesc, itemCondition, itemPrice, itemCategory, itemQuantity;
    private EditText itemNameEdit, itemDescEdit, itemPriceEdit, itemQuantityEdit;
    private AppCompatSpinner itemConditionEdit;
    private AppCompatSpinner itemCategoryEdit;
    private LinearLayout itemDisplay, itemEdit, buttonGroup;
    private String name, desc, condition, category, imageUrl, itemID;
    private double price;
    private int quantity;
    private Button editButton;
    private ArrayAdapter<String> conditionAdapter;
    private ArrayAdapter<String> categoryAdapter;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private UserInfo userInfo;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("InventoryImage");
    private ArrayList<ConditionAndQuantity> conditionAndQuantities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_inventory);
        init();
        getUserInfoFromPreference();
        setUpButtonListener();
        setUpConditionSpinner();
        setUpCategorySpinner();
        setUpActionBar();
        getItemInfo();

    }

    private void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    public void init(){
        Intent intent = this.getIntent();
        String uid = intent.getStringExtra("UID");
        String documentId = intent.getStringExtra("ID");
        itemRef = db.collection("Inventory").document(documentId);
        itemImage = findViewById(R.id.item_detail_image);
        itemName = findViewById(R.id.item_detail_name);
        itemDesc = findViewById(R.id.item_detail_description);
        itemCondition = findViewById(R.id.item_detail_condition);
        itemPrice = findViewById(R.id.item_detail_price);
        itemCategory = findViewById(R.id.item_detail_category);
        itemQuantity = findViewById(R.id.item_detail_quantity);
        itemDisplay = findViewById(R.id.item_display_linear_layout);
        itemImageEdit = findViewById(R.id.item_detail_image_edit);
        itemEdit = findViewById(R.id.item_edit_linear_layout);
        itemNameEdit = findViewById(R.id.item_detail_name_edit);
        itemDescEdit = findViewById(R.id.item_detail_description_edit);
        itemConditionEdit = findViewById(R.id.item_detail_condition_edit);
        itemPriceEdit = findViewById(R.id.item_detail_price_edit);
        itemCategoryEdit = findViewById(R.id.item_detail_category_edit);
        itemQuantityEdit = findViewById(R.id.item_detail_quantity_edit);
        buttonGroup = findViewById(R.id.save_and_cancel_button);
        editButton = findViewById(R.id.edit_item);
    }

    private void getUserInfoFromPreference(){
        SharedPreferences preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String user = preferences.getString("USER", "");
        Gson gson = new Gson();
        userInfo = gson.fromJson(user, UserInfo.class);
    }

    public void setUpButtonListener(){
        editButton.setOnClickListener(v -> {
            itemDisplay.setVisibility(View.GONE);
            itemEdit.setVisibility(View.VISIBLE);
            buttonGroup.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        });

        findViewById(R.id.delete_item).setOnClickListener(v -> setUpAlertDialog());

        findViewById(R.id.save_button).setOnClickListener(v -> {
            uploadFile();
        });

        findViewById(R.id.cancel_button).setOnClickListener(v -> {
            itemDisplay.setVisibility(View.VISIBLE);
            itemEdit.setVisibility(View.GONE);
            buttonGroup.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.change_image).setOnClickListener(v -> openFileChooser());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                itemImageEdit.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
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
                            updateItem();
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(DetailInventory.this, e.getMessage(), Toast.LENGTH_LONG).show());

        } else {
            updateItem();
        }
    }

    private void setUpAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailInventory.this);
        builder.setMessage("Delete item - " + name);
        builder.setTitle("Please Confirm?");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            deleteItem();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteItem(){
        itemRef.delete()
            .addOnSuccessListener(aVoid -> finish());
    }

    private void setUpConditionSpinner(){
        String[] conditions = getResources().getStringArray(R.array.itemCondition);
        conditionAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, conditions);
        itemConditionEdit.setAdapter(conditionAdapter);
        itemConditionEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        categoryAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, categories);
        itemCategoryEdit.setAdapter(categoryAdapter);
        itemCategoryEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                condition = categories[0];
            }
        });
    }

    public void getItemInfo(){
        itemRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                Inventory item = documentSnapshot.toObject(Inventory.class);
                if(item.getImageUrl() != null){
                    imageUrl = item.getImageUrl();
                    Glide.with(DetailInventory.this).load(imageUrl).into(itemImage);
                    Glide.with(DetailInventory.this).load(imageUrl).into(itemImageEdit);
                }
                if(item.getName() != null){
                    setTitle(item.getName());
                    name = item.getName();
                    itemName.setText(name);
                    itemNameEdit.setText(name);
                }
                if(item.getDescription() != null){
                    desc = item.getDescription();
                    itemDesc.setText(desc);
                    itemDescEdit.setText(desc);
                }
//                if(item.getConditionAndQuantities() != null) {
//                    conditionAndQuantities = item.getConditionAndQuantities();
//                    itemCondition.setText("Condition: " + condition);
//                    itemConditionEdit.setSelection(conditionAdapter.getPosition(condition));
//                }
                if(item.getCategory() != null) {
                    category = item.getCategory();
                    itemCategory.setText("Category: " + category);    
                    itemCategoryEdit.setSelection(categoryAdapter.getPosition(category));
                }
//                if(item.getPrice() != 0) {
//                    price = item.getPrice();
//                    itemPrice.setText("$" + price);
//                    itemPriceEdit.setText("" + price);
//                }
//                if(item.getQuantity() != 0) {
//                    quantity = item.getQuantity();
//                    itemQuantity.setText("Quantities: " + quantity);
//                    itemQuantityEdit.setText("" + quantity);
//                }
                if(item.getItemID() != null) {
                    itemID = item.getItemID();
                }
            })
            .addOnFailureListener(e -> Toast.makeText(DetailInventory.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    public void updateItem(){
//        if(isValidInput()) {
//            name = itemNameEdit.getText().toString();
//            desc = itemDescEdit.getText().toString();
//            price = Double.parseDouble(itemPriceEdit.getText().toString());
//            quantity = Integer.parseInt(itemQuantityEdit.getText().toString());
//
//            long current = System.currentTimeMillis();
//            Inventory inventory = new Inventory(itemID, imageUrl, category, name, desc, price, quantity, condition, current, userInfo.getId());
//
//            itemRef.set(inventory, SetOptions.merge())
//                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(DetailInventory.this, "Update Item Info Successfully!", Toast.LENGTH_SHORT).show();
//                        getItemInfo();
//                        itemDisplay.setVisibility(View.VISIBLE);
//                        itemEdit.setVisibility(View.GONE);
//                        buttonGroup.setVisibility(View.GONE);
//                        editButton.setVisibility(View.VISIBLE);
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(DetailInventory.this, "Error!", Toast.LENGTH_LONG).show();
//                        Log.d(TAG, e.toString());
//                    });
//        }
    }

    public boolean isValidInput(){
        if(itemNameEdit.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item name", Toast.LENGTH_LONG).show();
            return false;
        }
        if(itemDescEdit.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item description", Toast.LENGTH_LONG).show();
            return false;
        }
        if(itemPriceEdit.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item price", Toast.LENGTH_LONG).show();
            return false;
        }
        if(itemQuantityEdit.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter item quantities", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
}
