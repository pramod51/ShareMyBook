package com.share.bookR.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseUser;
import com.share.bookR.BookAdapter.BookAdapter;
import com.share.bookR.BookAdapter.BookModel;
import com.share.bookR.Constants;
import com.share.bookR.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BookStoreFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<BookModel> bookModels;
    private final FirebaseUser uId= FirebaseAuth.getInstance().getCurrentUser();
    private LinearLayout layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_book_store, container, false);

        init(view);
        bookModels=new ArrayList<>();
        for (int i=0;i<8;i++)
        bookModels.add(new BookModel("","","","","","",""));


        adapter=new BookAdapter(bookModels,getContext());
        recyclerView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference().child(Constants.LIBRARY)
                .child("Books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //bookModels=new ArrayList<>();
                bookModels.clear();
                //for (DataSnapshot ds:snapshot.getChildren())

                for (DataSnapshot ds:snapshot.getChildren()){
                    DataSnapshot ds1=ds.child("BookDetails");
                    bookModels.add(new BookModel(ds1.child("title").getValue(String.class),ds1.child("frontCoverPhoto").getValue(String.class),ds1.child("description").getValue(String.class),
                            ds1.child("price").getValue(String.class),ds1.child("rating").getValue(String.class),ds1.child("author").getValue(String.class),ds1.child("isbn").getValue(String.class)));
                }
                //adapter.notifyDataSetChanged();
                adapter=new BookAdapter(bookModels,getContext());
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return view;
    }

    private void init(View view) {
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

    }

}