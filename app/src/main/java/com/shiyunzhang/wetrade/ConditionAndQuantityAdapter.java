package com.shiyunzhang.wetrade;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.shiyunzhang.wetrade.DataClass.ConditionAndQuantity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.DataClass.ItemForSale;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConditionAndQuantityAdapter extends RecyclerView.Adapter {
    final static String COLLECTION = "ItemForSale";
    final static String TAG = "ConditionAndQuantity";
    Context context;
    ArrayList<ConditionAndQuantity> conditionAndQuantities;
    ArrayList<ConditionAndQuantity> conditionAndQuantitiesInSaleItem = new ArrayList<>();
    Inventory inventory;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference itemForSaleCollect = db.collection(COLLECTION);
    private ImageView imageView;
    private TextView nameView, descView, conditionView;
    private EditText priceView;
    private String itemID, imageUrl, category, name, description, uid, productId;
    private ItemForSale itemForSale;
    private int selectedQuantity;
    private double price;

    public ConditionAndQuantityAdapter(Context context, ArrayList<ConditionAndQuantity> conditionAndQuantities) {
        this.context = context;
        this.conditionAndQuantities = conditionAndQuantities;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        this.itemID = inventory.getItemID();
        this.imageUrl = inventory.getImageUrl();
        this.category = inventory.getCategory();
        this.name = inventory.getName();
        this.description = inventory.getDescription();
        this.uid = inventory.getUserID();
        this.productId = inventory.getProductID();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.condition_and_quantities_item, null);
        return new ConditionQuantityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ConditionAndQuantity item = getItem(i);
        String condition = item.getCondition();
        ConditionQuantityViewHolder holder = (ConditionQuantityViewHolder) viewHolder;
        holder.itemView.setTag(i);
        holder.condition.setText(condition);
        holder.quantity.setText(item.getQuantity() + "");
        holder.sellButton.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View mView = inflater.inflate(R.layout.sell_window_popup, null);
            int totalQuantity = item.getQuantity();

            initDialogView(mView, condition);
            setUpSpinner(mView, totalQuantity);

            mBuilder.setTitle("Please Enter Selling Item Info:");
            mBuilder.setPositiveButton("Confirm", (dialog, which) -> {
                saveItemForSaleInfo(dialog, condition);
            });
            mBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
        });
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on add button", Toast.LENGTH_SHORT).show();
            }
        });
        holder.subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on remove button", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initDialogView(View mView, String condition) {
        imageView = mView.findViewById(R.id.sell_item_image);
        Glide.with(context).load(inventory.getImageUrl()).into(imageView);
        nameView = mView.findViewById(R.id.sell_item_name);
        nameView.setText(inventory.getName());
        descView = mView.findViewById(R.id.sell_item_desc);
        descView.setText(inventory.getDescription());
        conditionView = mView.findViewById(R.id.sell_item_condition);
        conditionView.setText("Condition: " + condition);
        priceView = mView.findViewById(R.id.sell_item_price);
    }

    public void setUpSpinner(View mView, int quantity) {
        AppCompatSpinner quantitySpinner = mView.findViewById(R.id.sell_item_quantity);

        Integer[] quantities = new Integer[quantity];
        for (int i1 = 0; i1 < quantity; i1++) {
            quantities[i1] = i1 + 1;
        }
        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(context, R.layout.layout_spinner_item, quantities);
        quantitySpinner.setAdapter(quantityAdapter);
        quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedQuantity = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                quantitySpinner.setSelection(quantity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conditionAndQuantities.size();
    }

    public ConditionAndQuantity getItem(int position) {
        return conditionAndQuantities.get(position);
    }

    public class ConditionQuantityViewHolder extends RecyclerView.ViewHolder {

        TextView condition;
        TextView quantity;
        Button sellButton;
        ImageView add;
        ImageView subtract;

        public ConditionQuantityViewHolder(@NonNull View itemView) {
            super(itemView);
            this.condition = itemView.findViewById(R.id.condition);
            this.quantity = itemView.findViewById(R.id.quantity);
            this.sellButton = itemView.findViewById(R.id.sell_inventory);
            this.add = itemView.findViewById(R.id.plus);
            this.subtract = itemView.findViewById(R.id.remove);
        }
    }

    private void saveItemForSaleHelper(DialogInterface dialog, boolean addToSearch) {
        long timestamp = System.currentTimeMillis();
        ItemForSale itemForSale = new ItemForSale(itemID, imageUrl, category, name, description, conditionAndQuantitiesInSaleItem, uid, productId, timestamp);
        itemForSaleCollect.document(itemID).set(itemForSale, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if(addToSearch) {
                        Client client = new Client("KBCNT58640", "7bf45a6696368d5ebc1ff04e57e4e1e0");
                        Index index = client.getIndex(COLLECTION);
                        Gson gson = new Gson();
                        String json = gson.toJson(itemForSale);
                        try {
                            index.addObjectAsync(new JSONObject(json), null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(context, "Item Information Saved", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    ((DetailInventory)context).finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.toString());
                });
    }

    private void saveItemForSaleInfo(DialogInterface dialog, String condition) {
        if (!priceView.getText().toString().trim().isEmpty()) {
            price = Double.parseDouble(priceView.getText().toString().trim());
        } else {
            Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT).show();
        }

        itemForSaleCollect.whereEqualTo("itemID", inventory.getItemID())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        itemForSale = queryDocumentSnapshot.toObject(ItemForSale.class);
                    }
                    if (itemForSale != null) {
                        ArrayList<ConditionAndQuantity> existConditionAndQuantities = itemForSale.getConditionAndQuantities();
                        boolean exist = false;
                        Log.d(TAG, "saveItemForSaleInfo: " + existConditionAndQuantities);
                        if (existConditionAndQuantities != null) {
                            conditionAndQuantitiesInSaleItem = existConditionAndQuantities;
                        }
                        for (int i = 0; i < conditionAndQuantitiesInSaleItem.size(); i++) {
                            Log.d(TAG, "saveItemForSaleInfo: " + condition);
                            if (condition.equals(conditionAndQuantitiesInSaleItem.get(i).getCondition())) {
                                Log.d(TAG, "saveItemForSaleInfo: "+ conditionAndQuantitiesInSaleItem.toString());
                                exist = true;
                                conditionAndQuantitiesInSaleItem.get(i).setQuantity(selectedQuantity);
                                conditionAndQuantitiesInSaleItem.get(i).setPrice(price);
                                // todo: remove when quantity is 0
                            }
                        }
                        if (!exist)
                            conditionAndQuantitiesInSaleItem.add(new ConditionAndQuantity(condition, selectedQuantity, price));
                        saveItemForSaleHelper(dialog, false);
                    } else {
                        conditionAndQuantitiesInSaleItem.add(new ConditionAndQuantity(condition, selectedQuantity, price));
                        saveItemForSaleHelper(dialog, true);
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
    }
}
