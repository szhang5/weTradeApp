package com.shiyunzhang.wetrade.Activity;

import android.content.ContentResolver;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.DataClass.Product;
import com.shiyunzhang.wetrade.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 1;
    private final String COLLECTION = "Product";
    private final String TAG = "AddProductActivity";
    private Uri imageUri;
    private FrameLayout mProgressBarFrame;
    private ImageView productImage;
    private String category, imageUrl, name, description;
    private EditText productName, productDescription;
    private AppCompatSpinner productCategory;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("ProductImage");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference productRef = db.collection(COLLECTION).document();
    private String productId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);
        setUpActionBar();
        init();
    }

    private void init() {
        mProgressBarFrame = findViewById(R.id.progressbar_frame);
        productImage = findViewById(R.id.product_image);
        productCategory = findViewById(R.id.product_category);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productId = productRef.getId();

        setUpCategorySpinner();
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

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
    }

    public void chooseProductImage(View view) {
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
                productImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpCategorySpinner() {
        String[] categories = getResources().getStringArray(R.array.itemCategory);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_item, categories);
        productCategory.setAdapter(categoryAdapter);
        productCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    public void saveProductInfo(View view) {
        if (imageUri != null) {
            mProgressBarFrame.setVisibility(View.VISIBLE);
            StorageReference fileReference = mStorageRef.child(productId + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        saveProductBasicInfo();

                        Intent intent = new Intent(AddProductActivity.this, AddItemActivity.class);
                        intent.putExtra("imageUrl", imageUrl);
                        intent.putExtra("productName", name);
                        intent.putExtra("productDescription", description);
                        intent.putExtra("productId", productId);
                        intent.putExtra("category", category);
                        startActivity(intent);
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    mProgressBarFrame.setVisibility(View.GONE);
                    Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void saveProductBasicInfo() {
        if (isValidInput()) {
            name = productName.getText().toString();
            description = productDescription.getText().toString();
        }

        Product productInfo = new Product(productId, name, description, category, imageUrl);

        productRef.set(productInfo)
                .addOnSuccessListener(aVoid -> {
                    Client client = new Client("KBCNT58640", "7bf45a6696368d5ebc1ff04e57e4e1e0");
                    Index index = client.getIndex(COLLECTION);
                    Gson gson = new Gson();
                    String json = gson.toJson(productInfo);
                    try {
                        index.addObjectAsync(new JSONObject(json), null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(AddProductActivity.this, "Product Information Saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Error!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.toString());
                });

    }

    private boolean isValidInput() {
        if(productName.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter product name", Toast.LENGTH_LONG).show();
            return false;
        }
        if(productDescription.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter product description", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
