package com.shiyunzhang.wetrade.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shiyunzhang.wetrade.Authentication.LoginActivity;
import com.shiyunzhang.wetrade.DataClass.Inventory;
import com.shiyunzhang.wetrade.R;
import com.shiyunzhang.wetrade.RecentItemsAdapter;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    private String TAG = "HomeFragment";
    private ArrayList<Inventory> recentItemsList;
    private RecyclerView recyclerView;
    private RecentItemsAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recentItemsRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        recentItemsRef = db.collection("Inventory");
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recentItemsList.clear();
        adapter.notifyDataSetChanged();
        getRecentItems();

    }

    private void init(View view){
        recentItemsList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recent_items_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new RecentItemsAdapter(getContext(), recentItemsList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getRecentItems(){
        recentItemsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                    Inventory item = queryDocumentSnapshot.toObject(Inventory.class);
                    recentItemsList.add(item);
                    adapter.notifyDataSetChanged();
                }
            })
            .addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }
}
