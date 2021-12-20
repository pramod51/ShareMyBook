package com.share.bookR.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.share.bookR.Constants;
import com.share.bookR.Authentication.MobileAthuntication;
import com.share.bookR.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.circularreveal.cardview.CircularRevealCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountsFragment extends Fragment {

    private TextView name,phoneNumber,userName;
    private CircleImageView profileImage;
    private CircularRevealCardView revealCardView;
    private final FirebaseUser uId= FirebaseAuth.getInstance().getCurrentUser();
    private SharedPreferences sharedPreferences;
    private Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_accounts, container, false);
        sharedPreferences=getContext().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE );
        editor = sharedPreferences.edit();
        init(view);
        name.setText(sharedPreferences.getString(Constants.NAME,""));
        phoneNumber.setText(sharedPreferences.getString(Constants.PHONE,""));
        userName.setText(sharedPreferences.getString(Constants.USER_NAME,""));
        Glide.with(getContext()).load(sharedPreferences.getString(Constants.PROFILE,"")).placeholder(R.drawable.user).into(profileImage);




        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),MobileAthuntication.class));
                getActivity().finish();
            }
        });
        if (uId==null) {
            view.findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
            return view;
        }

        view.findViewById(R.id.your_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_accountsFragment_to_yourOrderFragment);
            }
        });
        view.findViewById(R.id.address_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_accountsFragment_to_addressesFragment);
            }
        });
        view.findViewById(R.id.your_rating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_accountsFragment_to_yourRatingFragment2);
            }
        });

        view.findViewById(R.id.manage_subscription).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString(Constants.KEY,"");
                Navigation.findNavController(view).navigate(R.id.action_accountsFragment_to_manageSubscriptionFragment,bundle);
            }
        });

        view.findViewById(R.id.notification_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_accountsFragment_to_notificationFragment);
            }
        });
        view.findViewById(R.id.delete_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteAccount();
            }
        });
        view.findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_accountsFragment_to_aboutFragment);
            }
        });
        view.findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        view.findViewById(R.id.invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClicked();
            }
        });


        Constants constants=new Constants();
        if (uId!=null) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(uId.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child("User").exists()){
                        Glide.with(getContext()).load(snapshot.child("photo").getValue(String.class)).into(profileImage);
                        name.setText(snapshot.child("name").getValue(String.class));
                        userName.setText(snapshot.child("userName").getValue(String.class));
                        phoneNumber.setText((""+snapshot.child("phone").getValue(String.class)).replace("+91 ",""));

                        FirebaseFirestore.getInstance().collection("Users").document(uId.getUid())
                                .set(snapshot.child("User").getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.v("tag","transferd");
                                FirebaseDatabase.getInstance().getReference().child("Users").child(uId.getUid()).child("User").removeValue();
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (name.getText().toString().isEmpty()&&phoneNumber.getText().toString().isEmpty()&&userName.getText().toString().isEmpty()) {
                constants.ProgressDialogShow(getContext());
                FirebaseFirestore.getInstance().collection("Users").document(uId.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.v("tag","mila");
                        name.setText(documentSnapshot.getString("name"));
                        phoneNumber.setText(documentSnapshot.getString("phone"));
                        userName.setText(documentSnapshot.getString("userName"));
                        editor.putString(Constants.NAME,name.getText().toString());
                        editor.putString(Constants.PHONE,phoneNumber.getText().toString());
                        editor.putString(Constants.USER_NAME,userName.getText().toString());
                        editor.putString(Constants.PROFILE,documentSnapshot.getString("photo"));
                        editor.apply();
                        constants.HideProgressDialog();
                    }
                });
            }

        }

        revealCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_accountsFragment_to_editProfileFragment);
            }
        });
        return view;
    }
    private void onShareClicked() {

        String link = "https://play.google.com/store/apps/details?id="+getActivity().getPackageName();

        Uri uri = Uri.parse(link);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, link.toString());
        intent.putExtra(Intent.EXTRA_TITLE, "BookR");

        startActivity(Intent.createChooser(intent, "Share Link"));
    }
    private void init(View view) {
        name=view.findViewById(R.id.name);
        phoneNumber=view.findViewById(R.id.phone);
        profileImage=view.findViewById(R.id.profile_image);
        revealCardView=view.findViewById(R.id.edit);
        userName=view.findViewById(R.id.user_name);
    }
    private void logOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //builder.setMessage("") .setTitle("");

        //Setting message manually and performing action on button click
        builder.setMessage("are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Toast.makeText(getContext(),"Logged out",
                                Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), MobileAthuntication.class));
                        getActivity().finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        //Toast.makeText(getContext(),"you choose no action for alertbox", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Log Out");
        alert.show();
    }
    private void DeleteAccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //builder.setMessage("") .setTitle("");

        //Setting message manually and performing action on button click
        builder.setMessage("are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(),"Account Deleted",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(), MobileAthuntication.class));
                                getActivity().finish();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        //Toast.makeText(getContext(),"you choose no action for alertbox", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Delete Account");
        alert.show();
    }

}