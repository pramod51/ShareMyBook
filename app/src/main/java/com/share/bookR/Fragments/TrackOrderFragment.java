package com.share.bookR.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.share.bookR.Constants;
import com.share.bookR.R;
import com.bumptech.glide.Glide;


public class TrackOrderFragment extends Fragment {
    ImageView bookImage;
    TextView bookTitle,bookPrice;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_track_order, container, false);
        bookImage=view.findViewById(R.id.product_image);
        bookTitle=view.findViewById(R.id.product_title);
        bookPrice=view.findViewById(R.id.product_price);
        SharedPreferences sharedPreferences=getContext().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);

        Glide.with(getActivity()).load(sharedPreferences.getString(Constants.URL,"")).into(bookImage);
        bookTitle.setText(sharedPreferences.getString(Constants.TITLE,""));
        bookPrice.setText("₹"+sharedPreferences.getString(Constants.PRICE,"100"));


        return view;
    }
}