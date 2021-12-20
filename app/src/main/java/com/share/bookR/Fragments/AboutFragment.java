package com.share.bookR.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.share.bookR.BuildConfig;
import com.share.bookR.R;


public class AboutFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_about, container, false);
        TextView version=view.findViewById(R.id.version);
        String versionName = BuildConfig.VERSION_NAME;
        version.setText(versionName);
        view.findViewById(R.id.terms_and_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_aboutFragment_to_termsOfServiceFragment);
            }
        });
        view.findViewById(R.id.privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_aboutFragment_to_privacyPolicyFragment);
            }
        });
        view.findViewById(R.id.sharing_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_aboutFragment_to_sharingPolicyFragment);
            }
        });
        view.findViewById(R.id.cancellation_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_aboutFragment_to_cancellationPoliceyFragment);
            }
        });
        view.findViewById(R.id.send_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_aboutFragment_to_sendFeedbackFragment);
            }
        });



        return view;
    }

    private void initView(View view) {

    }
}