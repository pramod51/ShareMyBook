package com.share.bookR.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.share.bookR.Authentication.MobileAthuntication;
import com.share.bookR.Constants;
import com.share.bookR.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.share.bookR.RazorpaySubscription.JsonPlaceHolderRazorpay;
import com.share.bookR.RazorpaySubscription.SubscriptionDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.share.bookR.Constants.BEGINNER;
import static com.share.bookR.Constants.READER;


public class BookDetailsFragment extends Fragment {


    private TextView bookTitle,authorName,rating,price,publisherDescription,genre,released,language;
    private Button getBook;
    private ImageView bookImage;
    private RatingBar ratingBar;
    private RecyclerView recyclerView;
    Constants constants=new Constants();
    private final FirebaseUser uId= FirebaseAuth.getInstance().getCurrentUser();
    private String isSubscriptionActive="Not";
    private Boolean getClick=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_book_details, container, false);
        init(view);


        if (uId!=null){
            constants.ProgressDialogShow(getContext());
            getSubscriptionsDetails();
        }
        Log.v("tag","isbn No=="+getArguments().getString(Constants.ISBN));
        FirebaseDatabase.getInstance().getReference().child(Constants.LIBRARY).child("Books").child(getArguments().getString(Constants.ISBN))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snapshot=dataSnapshot.child("BookDetails");
                constants.HideProgressDialog();
                if (snapshot.exists()){
                    Glide.with(getContext()).load(snapshot.child("frontCoverPhoto").getValue(String.class)).into(bookImage);
                    bookTitle.setText(snapshot.child("title").getValue(String.class));
                    authorName.setText(snapshot.child("author").getValue(String.class));
                    publisherDescription.setText(snapshot.child("description").getValue(String.class));
                    genre.setText(snapshot.child("category").getValue(String.class).replace("[","").replace("]",""));
                    if (snapshot.child("publishedDate").exists())
                    released.setText(snapshot.child("publishedDate").getValue(String.class));
                    //language.setText(snapshot.child("language").getValue(String.class));

                    /*rating.setText(snapshot.child("rating").getValue(String.class));
                    price.setText("Price ₹"+snapshot.child("price").getValue(String.class));
                    if (snapshot.child("rating").getValue(String.class)!=null)
                    ratingBar.setRating(Float.parseFloat("0"+snapshot.child("rating").getValue(String.class).replaceAll("null","")));
                    else
                        ratingBar.setRating(0);*/
                }
                getBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getContext(), MobileAthuntication.class));
                                getActivity().finish();
                            }
                        });
                        if (uId==null) {
                            view.findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
                            return;
                        }
                        else if (isSubscriptionActive.equals("No")){
                            Bundle bundle=new Bundle();
                            bundle.putString(Constants.KEY,"key");
                            Navigation.findNavController(view1).navigate(R.id.action_bookDetailsFragment_to_manageSubscriptionFragment,bundle);
                            return;
                        }
                        else if (isSubscriptionActive.equals("Not")){
                            //getSubscriptionsDetails();
                            constants.ProgressDialogShow(getContext());
                            getClick=true;
                            return;
                        }





                        SharedPreferences sharedPreferences=getActivity().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
                        Editor editor=sharedPreferences.edit();
                        //editor.putString(Constants.USER_ID,getArguments().getString(Constants.USER_ID));
                        editor.putString(Constants.ISBN,getArguments().getString(Constants.ISBN));
                        editor.putString(Constants.TITLE,snapshot.child("title").getValue(String.class));
                        //editor.putString(Constants.PRICE,snapshot.child("price").getValue(String.class));
                        editor.putString(Constants.URL,snapshot.child("frontCoverPhoto").getValue(String.class));
                        editor.apply();
                        Log.v("tag","loading.");
                        Navigation.findNavController(view1).navigate(R.id.action_bookDetailsFragment_to_addressesFragment);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void init(View view) {
        bookTitle=view.findViewById(R.id.book_title);
        authorName=view.findViewById(R.id.author_name);
        rating=view.findViewById(R.id.rating);
        price=view.findViewById(R.id.price);
        publisherDescription=view.findViewById(R.id.publisher_description);
        getBook=view.findViewById(R.id.get_book);
        bookImage=view.findViewById(R.id.book_image);
        ratingBar=view.findViewById(R.id.ratingBar);
        genre=view.findViewById(R.id.genre);
        released=view.findViewById(R.id.released);
        language=view.findViewById(R.id.language);

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }


    private void getSubscriptionsDetails(){
        if (getClick)
            constants.ProgressDialogShow(getContext());
        Log.v("tag","enter");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.razorpay.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderRazorpay jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderRazorpay.class);


        FirebaseDatabase.getInstance().getReference().child("Users").child(uId.getUid()).child("Subscriptions").limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.v("tag","local enter");
                        String key="",plane="";
                        if (snapshot.exists()) {
                            Call<SubscriptionDetails>  detailsCall=null;
                            for (DataSnapshot ds:snapshot.getChildren()){
                                detailsCall=jsonPlaceHolderApi.getSubscriptionDetails(ds.child("subscriptionId").getValue(String.class));
                                key= ds.getKey();
                                plane=ds.child("plan").getValue(String.class);
                            }
                            if (detailsCall!=null) {
                                detailsCall.enqueue(new Callback<SubscriptionDetails>() {
                                    @Override
                                    public void onResponse(Call<SubscriptionDetails> call, Response<SubscriptionDetails> response) {
                                        if (!response.isSuccessful()){
                                            isSubscriptionActive="Not get";
                                            constants.HideProgressDialog();
                                            //Toast.makeText(getContext(),"Something went wrong, Try Again",Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        if (getClick)
                                            Toast.makeText(getContext(),"Try Again ",Toast.LENGTH_SHORT).show();
                                        constants.HideProgressDialog();
                                        SubscriptionDetails subscriptionDetails=response.body();
                                        Log.v("tag","entered"+subscriptionDetails.getStatus());

                                        if (subscriptionDetails.getStatus().equals("active")){
                                            isSubscriptionActive="Yes";
                                        }
                                        else {
                                            isSubscriptionActive="No";
                                        }


                                    }

                                    @Override
                                    public void onFailure(Call<SubscriptionDetails> call, Throwable t) {
                                        Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                                        constants.HideProgressDialog();
                                    }
                                });
                            }

                        }
                        else {
                            isSubscriptionActive="No";
                            Log.v("tag","No subscription");
                            //constants.HideProgressDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }



}