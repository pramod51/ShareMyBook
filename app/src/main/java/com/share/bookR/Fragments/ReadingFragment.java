package com.share.bookR.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.share.bookR.Authentication.MobileAthuntication;
import com.share.bookR.R;

public class ReadingFragment extends Fragment {

    FirebaseUser uId= FirebaseAuth.getInstance().getCurrentUser();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_reading, container, false);


        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MobileAthuntication.class));
                getActivity().finish();
            }
        });
        if (uId==null) {
            view.findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
            return view;
        }










        return view;
    }
}