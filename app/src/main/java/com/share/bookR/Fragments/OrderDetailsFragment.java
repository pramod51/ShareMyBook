package com.share.bookR.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.share.bookR.Constants;
import com.share.bookR.R;
import com.bumptech.glide.Glide;


public class OrderDetailsFragment extends Fragment {

    private ImageView bookImage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_order_details, container, false);
        initViews(view);
        Glide.with(getContext()).load(getArguments().getString(Constants.URL)).into(bookImage);
        view.findViewById(R.id.okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
        return view;
    }

    private void initViews(View view) {
        bookImage=view.findViewById(R.id.book_image);
    }
}