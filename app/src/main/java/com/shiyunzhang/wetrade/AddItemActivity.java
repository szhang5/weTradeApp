package com.shiyunzhang.wetrade;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class AddItemActivity extends AppCompatActivity {
    AppCompatSpinner itemCondition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        setTitle("Add an item");
        setUpActionBar();
        setUpConditionSpinner();
    }


    private void setUpActionBar(){
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.action_bar_style);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));
    }

    private void setUpConditionSpinner(){
        itemCondition = findViewById(R.id.item_condition);
        String[] conditions = getResources().getStringArray(R.array.itemCondition);
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_item, conditions);
        itemCondition.setAdapter(conditionAdapter);
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
