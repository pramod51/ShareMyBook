package com.share.bookR.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.share.bookR.R;
import com.share.bookR.YourOrderAdopter.OrderModel;
import com.share.bookR.YourOrderAdopter.OrdersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class YourOrderFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private TextView textView;
    private ArrayList<OrderModel> orderModels;
    private final String uId= FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_your_order, container, false);
        init(view);
        textView.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderModels=new ArrayList<>();
                for (DataSnapshot ds:snapshot.getChildren()){
                    orderModels.add(new OrderModel(ds.child("title").getValue(String.class),ds.child("frontCoverPhoto").getValue(String.class),
                            ds.child("Address").child("name").getValue(String.class),""));
                }
                if (!snapshot.exists())
                    textView.setVisibility(View.VISIBLE);
                adapter=new OrdersAdapter(orderModels,getContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void init(View view) {
        textView=view.findViewById(R.id.text);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }
}