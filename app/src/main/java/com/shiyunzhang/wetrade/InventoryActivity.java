package com.shiyunzhang.wetrade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyListData[] myListData = new MyListData[] {
                new MyListData("Concert tickets",android.R.drawable.ic_dialog_email,
                        60, "Tickets", "new", 2   ),
                new MyListData("Calculus Textbook",android.R.drawable.ic_dialog_email,
                        100, "Books", "used", 1   ),
                new MyListData("Organic Chemistry Notes",android.R.drawable.ic_dialog_email,
                        25, "Class Notes", "used", 1   )

        };

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.inventory_recycle_view);
        MyListAdapter adapter = new MyListAdapter(myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}