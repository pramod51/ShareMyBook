package com.share.bookR.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.share.bookR.Constants;
import com.share.bookR.R;

public class NotificationFragment extends Fragment {

    private Switch pushNotifications,enableAll, orderAndParches;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notification, container, false);
        init(view);
        SharedPreferences sharedPreferences=getContext().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        Editor editor=sharedPreferences.edit();
        pushNotifications.setChecked(sharedPreferences.getBoolean(Constants.PUSH_NOTIFICATION,true));
        enableAll.setChecked(sharedPreferences.getBoolean(Constants.ENABLE_ALL,true));
        orderAndParches.setChecked(sharedPreferences.getBoolean(Constants.ORDER_AND_PURCHASE,true));
        pushNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(Constants.PUSH_NOTIFICATION,b);
                editor.apply();
            }
        });
        enableAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(Constants.ENABLE_ALL,b);
                editor.apply();
            }
        });
        orderAndParches.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(Constants.ORDER_AND_PURCHASE,b);
                editor.apply();
            }
        });
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });


        return view;
    }

    private void init(View view) {
        pushNotifications=view.findViewById(R.id.push_notification);
        enableAll=view.findViewById(R.id.enable_all);
        orderAndParches=view.findViewById(R.id.order_and_purches);


    }
}