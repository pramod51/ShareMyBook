package com.share.bookR.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.share.bookR.Activity.RazorpayActivity;
import com.share.bookR.Constants;
import com.share.bookR.MainActivity;
import com.share.bookR.R;
import com.share.bookR.RazorpaySubscription.CancelSubscription;
import com.share.bookR.RazorpaySubscription.CreateSubscription;
import com.share.bookR.RazorpaySubscription.CreateSubscriptionPost;
import com.share.bookR.RazorpaySubscription.JsonPlaceHolderRazorpay;
import com.share.bookR.RazorpaySubscription.SubscriptionDetails;

import org.json.JSONObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.subjects.Subject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.share.bookR.Constants.BEGINNER;
import static com.share.bookR.Constants.BOOK_WARM;
import static com.share.bookR.Constants.READER;

public class ManageSubscriptionFragment extends Fragment  {
    private final String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private JsonPlaceHolderRazorpay jsonPlaceHolderApi;
    private ImageView beginnerActive,readerActive,bookWarmActive;
    private CardView beginner,reader,bookWarm;
    private SharedPreferences sharedPreferences;
    private Editor editor;
    private final Constants constants=new Constants();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_manage_subscription, container, false);
        initViews(view);

        if (!getArguments().getString(Constants.KEY).isEmpty()){
            ((MainActivity)getActivity()).getSupportActionBar().setTitle("Your Title");
            view.findViewById(R.id.text).setVisibility(View.VISIBLE);

        }

        view.findViewById(R.id.cancel_subscription).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean(BEGINNER,false)||sharedPreferences.getBoolean(READER,false)||sharedPreferences.getBoolean(BOOK_WARM,false)){
                    Log.v("tag",sharedPreferences.getBoolean(BEGINNER,false)+"jdfrgkh  ff ,mf ");
                }
                else{
                    Toast.makeText(getContext(),"No Subscription Active",Toast.LENGTH_LONG).show();
                    Log.v("tag",sharedPreferences.getBoolean(BEGINNER,false)+"jdfrgkh  ff ,mf ");
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cancelSubscription(view);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });

                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Cancel Subscription");
                alert.show();
            }
        });

        view.findViewById(R.id.beginner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean(BEGINNER,false))
                    return;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                beginnerSubscription();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });

                AlertDialog alert = builder.create();
                //Setting the title manually
                if (sharedPreferences.getBoolean(READER,false)||sharedPreferences.getBoolean(BOOK_WARM,false))
                    alert.setTitle("Downgrade to "+BEGINNER);
                else
                alert.setTitle("Beginner Subscription");
                alert.show();
            }
        });

        view.findViewById(R.id.reader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean(READER,false))
                    return;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                readerSubscription();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });

                AlertDialog alert = builder.create();
                //Setting the title manually
                if (sharedPreferences.getBoolean(BEGINNER,false))
                    alert.setTitle("Upgrade to "+READER);
                else if (sharedPreferences.getBoolean(BOOK_WARM,false))
                    alert.setTitle("Downgrade to "+READER);
                else
                alert.setTitle("Reader Subscription");
                alert.show();
            }
        });

        view.findViewById(R.id.book_worm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean(BOOK_WARM,false))
                    return;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                bookWarmSubscription();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });

                AlertDialog alert = builder.create();
                //Setting the title manually
                if (sharedPreferences.getBoolean(BEGINNER,false)||sharedPreferences.getBoolean(READER,false))
                    alert.setTitle("Upgrade to "+BOOK_WARM);
                else
                alert.setTitle("Book Warm Subscription");
                alert.show();
            }
        });







        return view;
    }

    private void checkActiveSubscription() {
        if (sharedPreferences.getBoolean(BEGINNER,false)) {
            beginnerActive.setVisibility(View.VISIBLE);
            beginner.setCardBackgroundColor(Color.parseColor("#F57C00"));
        }
        if (sharedPreferences.getBoolean(Constants.READER,false)) {
            readerActive.setVisibility(View.VISIBLE);
            reader.setCardBackgroundColor(Color.parseColor("#F57C00"));
        }
        if (sharedPreferences.getBoolean(Constants.BOOK_WARM,false)) {
            bookWarmActive.setVisibility(View.VISIBLE);
            bookWarm.setCardBackgroundColor(Color.parseColor("#F57C00"));
        }
        /*else {
            FirebaseDatabase.getInstance().getReference().child("Users/"+uId+"/Subscriptions").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }*/
    }

    private void initViews(View view) {
        beginnerActive=view.findViewById(R.id.beginner_active);
        readerActive=view.findViewById(R.id.reader_active);
        bookWarmActive=view.findViewById(R.id.book_worm_active);
        beginner=view.findViewById(R.id.beginner);
        reader=view.findViewById(R.id.reader);
        bookWarm=view.findViewById(R.id.book_worm);

        sharedPreferences=getActivity().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

    }





    private void beginnerSubscription(){
        constants.ProgressDialogShow(getContext());

        Log.v("tag","enter");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.razorpay.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        /*String authorization="rzp_test_7g5gGpIh6R5hu1:8tPvO1oS41HASb4WhOGfQAk7";
        String authHeader="Basic "+ Base64.encodeToString(authorization.getBytes(),Base64.NO_WRAP);
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderRazorpay.class);
        */

        Call<CreateSubscription>  subscriptionCall=jsonPlaceHolderApi.createSubscription("plan_HSNlh0DaUtzwUL",6);
        subscriptionCall.enqueue(new Callback<CreateSubscription>() {
            @Override
            public void onResponse(Call<CreateSubscription> call, Response<CreateSubscription> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getContext(),"Something went wrong, Try Again",Toast.LENGTH_LONG).show();
                    constants.HideProgressDialog();
                    return;
                }
                Log.v("tag","entered");
                CreateSubscription subscription=response.body();
                Map<String, Object> map=new HashMap<>();
                map.put("subscriptionId",subscription.getId());
                map.put("planeId",subscription.getPlanId());
                map.put("userId",uId);
                map.put("methodId","");
                map.put("plan",BEGINNER);
                map.put("status","created");
                map.put("startDate",constants.getTodayDate());
                FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Subscriptions")
                        .child(constants.getIncreasingNode()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        constants.HideProgressDialog();

                        goForPayment(subscription.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onFailure(Call<CreateSubscription> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void goForPayment(String id) {
        Intent intent=new Intent(getActivity(), RazorpayActivity.class);
        intent.putExtra("subscriptionId",id);
        intent.putExtra(Constants.KEY,getArguments().getString(Constants.KEY));
        startActivity(intent);
        if (!getArguments().getString(Constants.KEY).isEmpty())
            Navigation.findNavController(getView()).popBackStack();
        /*Bundle bundle=new Bundle();
        bundle.putString(Constants.KEY,getArguments().getString(Constants.KEY));
        Navigation.findNavController(getView()).navigate(R.id.action_manageSubscriptionFragment_to_razorpayActivity,bundle);*/


    }

    private void readerSubscription(){
        constants.ProgressDialogShow(getContext());
        Log.v("tag","enter");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.razorpay.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderRazorpay.class);


        Call<CreateSubscription>  subscriptionCall=jsonPlaceHolderApi.createSubscription("plan_HSNmDp2yMqT2Jw",6);
        subscriptionCall.enqueue(new Callback<CreateSubscription>() {
            @Override
            public void onResponse(Call<CreateSubscription> call, Response<CreateSubscription> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getContext(),"Something went wrong, Try Again",Toast.LENGTH_LONG).show();
                    constants.HideProgressDialog();
                    return;
                }
                CreateSubscription subscription=response.body();
                Log.v("tag","entered"+subscription.getId());
                Map<String, Object> map=new HashMap<>();
                map.put("subscriptionId",subscription.getId());
                map.put("planeId",subscription.getPlanId());
                map.put("userId",uId);
                map.put("methodId","");
                map.put("plan",READER);
                map.put("status","created");
                map.put("startDate",constants.getTodayDate());
                FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Subscriptions")
                        .child(constants.getIncreasingNode()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        constants.HideProgressDialog();

                        goForPayment(subscription.getId());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onFailure(Call<CreateSubscription> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
    private void bookWarmSubscription(){
        constants.ProgressDialogShow(getContext());
        Log.v("tag","enter");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.razorpay.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderRazorpay.class);

        Call<CreateSubscription>  subscriptionCall=jsonPlaceHolderApi.createSubscription("plan_HSNnQp1lTvwJNt",6);
        subscriptionCall.enqueue(new Callback<CreateSubscription>() {
            @Override
            public void onResponse(Call<CreateSubscription> call, Response<CreateSubscription> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getContext(),"Something went wrong, Try Again",Toast.LENGTH_LONG).show();
                    constants.HideProgressDialog();
                    return;
                }
                Log.v("tag","entered");
                CreateSubscription subscription=response.body();
                Map<String, Object> map=new HashMap<>();
                map.put("subscriptionId",subscription.getId());
                map.put("planeId",subscription.getPlanId());
                map.put("userId",uId);
                map.put("methodId","");
                map.put("plan",BOOK_WARM);
                map.put("status","created");
                map.put("startDate",constants.getTodayDate());
                FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Subscriptions")
                        .child(constants.getIncreasingNode()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        constants.HideProgressDialog();

                        goForPayment(subscription.getId());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onFailure(Call<CreateSubscription> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }



    private void getSubscriptionsDetails(){
        constants.ProgressDialogShow(getContext());
        Log.v("tag","enter");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.razorpay.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderRazorpay.class);

        FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Subscriptions").limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.v("tag","local enter");
                        String key="",plane="",status="";
                        if (snapshot.exists()) {
                            Call<SubscriptionDetails>  detailsCall=null;
                            for (DataSnapshot ds:snapshot.getChildren()){
                                detailsCall=jsonPlaceHolderApi.getSubscriptionDetails(ds.child("subscriptionId").getValue(String.class));
                                key= ds.getKey();
                                status=ds.child("status").getValue(String.class);
                                plane=ds.child("plan").getValue(String.class);
                            }
                            if (detailsCall!=null) {
                                String finalKey = key;
                                String finalPlane = plane;
                                String finalStatus = status;
                                detailsCall.enqueue(new Callback<SubscriptionDetails>() {
                                    @Override
                                    public void onResponse(Call<SubscriptionDetails> call, Response<SubscriptionDetails> response) {
                                        if (!response.isSuccessful()){
                                            constants.HideProgressDialog();
                                            Toast.makeText(getContext(),"Something went wrong, Try Again",Toast.LENGTH_LONG).show();
                                            constants.HideProgressDialog();
                                            return;
                                        }

                                        SubscriptionDetails subscriptionDetails=response.body();
                                        Log.v("tag","entered"+subscriptionDetails.getStatus());

                                        if (subscriptionDetails.getStatus().equals("active")){
                                            if (finalPlane.equals(BEGINNER)){
                                             editor.putBoolean(BEGINNER,true);
                                             editor.putBoolean(READER,false);
                                             editor.putBoolean(BOOK_WARM,false);
                                             editor.apply();
                                            }
                                            else if (finalPlane.equals(READER)){
                                                editor.putBoolean(BEGINNER,false);
                                                editor.putBoolean(READER,true);
                                                editor.putBoolean(BOOK_WARM,false);
                                                editor.apply();
                                            }
                                            else if (finalPlane.equals(BOOK_WARM)){
                                                editor.putBoolean(BEGINNER,false);
                                                editor.putBoolean(READER,false);
                                                editor.putBoolean(BOOK_WARM,true);
                                                editor.apply();
                                            }
                                        }
                                        else {
                                            editor.putBoolean(BEGINNER,false);
                                            editor.putBoolean(READER,false);
                                            editor.putBoolean(BOOK_WARM,false);
                                            editor.apply();
                                        }

                                        checkActiveSubscription();

                                        if (!finalStatus.equals(subscriptionDetails.getStatus())) {
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Subscriptions").
                                                    child(finalKey+"").child("status").setValue(subscriptionDetails.getStatus()).
                                                    addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            if (finalPlane.equals(BEGINNER)&&subscriptionDetails.getStatus().equals("active")){
                                                                editor.putBoolean(BEGINNER,true);
                                                                editor.apply();
                                                                beginnerActive.setVisibility(View.VISIBLE);
                                                            }
                                                            constants.HideProgressDialog();
                                                        }
                                                    });
                                        } else
                                            constants.HideProgressDialog();

                                    }

                                    @Override
                                    public void onFailure(Call<SubscriptionDetails> call, Throwable t) {
                                        Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }else
                            constants.HideProgressDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onResume() {
        getSubscriptionsDetails();
        super.onResume();
    }

    private void cancelSubscription(View view){
        constants.ProgressDialogShow(getContext());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.razorpay.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderRazorpay.class);
        Log.v("tag","enter");

        FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Subscriptions").limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.v("tag","local enter");
                        String key="",plane="",status="";
                        if (snapshot.exists()) {
                            Call<SubscriptionDetails>  detailsCall=null;
                            for (DataSnapshot ds:snapshot.getChildren()){
                                detailsCall=jsonPlaceHolderApi.getSubscriptionDetails(ds.child("subscriptionId").getValue(String.class));
                                key= ds.getKey();
                                plane=ds.child("plan").getValue(String.class);
                                status=ds.child("status").getValue(String.class)+"";
                            }
                            Log.v("tag",plane);
                            if (detailsCall!=null) {
                                String finalKey = key;
                                detailsCall.enqueue(new Callback<SubscriptionDetails>() {
                                    @Override
                                    public void onResponse(Call<SubscriptionDetails> call, Response<SubscriptionDetails> response) {
                                        if (!response.isSuccessful()){
                                            constants.HideProgressDialog();
                                            Toast.makeText(getContext(),"Something went wrong, Try Again",Toast.LENGTH_LONG).show();
                                            constants.HideProgressDialog();
                                            return;
                                        }
                                        SubscriptionDetails subscriptionDetails=response.body();
                                        Log.v("tag","entered"+subscriptionDetails.getStatus());
                                        if (!subscriptionDetails.getStatus().equals("active")) {
                                            Log.v("tag",subscriptionDetails.getStatus());
                                            Toast.makeText(getContext(),"No Subscription Active",Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        Call<CancelSubscription> cancelSubscriptionCall=jsonPlaceHolderApi.
                                                cancelSubscription(subscriptionDetails.getId());
                                        cancelSubscriptionCall.enqueue(new Callback<CancelSubscription>() {
                                            @Override
                                            public void onResponse(Call<CancelSubscription> call, Response<CancelSubscription> response) {
                                                if (!response.isSuccessful()){
                                                    constants.HideProgressDialog();
                                                    Toast.makeText(getContext(),"Something went wrong, Try Again1",Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                CancelSubscription cancelSubscription=response.body();

                                                HashMap<String, Object> map=new HashMap<>();
                                                map.put("status",cancelSubscription.getStatus());
                                                map.put("endDate",constants.getTodayDate());
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Subscriptions").
                                                        child(finalKey).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        constants.HideProgressDialog();
                                                        editor.putBoolean(BEGINNER,false);
                                                        editor.putBoolean(READER,false);
                                                        editor.putBoolean(BOOK_WARM,false);
                                                        editor.apply();
                                                        checkActiveSubscription();

                                                        Navigation.findNavController(view).navigate(R.id.action_manageSubscriptionFragment_to_cancelledSubscriptionFragment2);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                        constants.HideProgressDialog();
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onFailure(Call<CancelSubscription> call, Throwable t) {
                                                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                                                constants.HideProgressDialog();
                                            }
                                        });


                                    }

                                    @Override
                                    public void onFailure(Call<SubscriptionDetails> call, Throwable t) {
                                        Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                                        constants.HideProgressDialog();
                                    }
                                });
                            }

                        }else {
                            Toast.makeText(getContext(),"No Subscription Active",Toast.LENGTH_LONG).show();
                            constants.HideProgressDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        constants.HideProgressDialog();
                    }
                });
    }

}
