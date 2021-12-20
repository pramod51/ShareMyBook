package com.share.bookR.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.share.bookR.R;


public class SendFeedbackFragment extends Fragment {


    TextInputEditText feedback;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_send_feedback, container, false);
        feedback=view.findViewById(R.id.feedback);
        view.findViewById(R.id.submit_feedback);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!feedback.getText().toString().isEmpty()) {
                    sendFeedback();
                }
            }
        });


        return view;
    }
    private void sendFeedback(){
        String to = "bookrpw@gmail.com";
        String subject= "BookR Feedback";
        String body=feedback.getText().toString();
        String mailTo = "mailto:" + to +
                "?&subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(body);
        Intent emailIntent = new Intent(Intent.ACTION_VIEW);
        emailIntent.setData(Uri.parse(mailTo));
        startActivity(emailIntent);
        feedback.setText("");
    }

}