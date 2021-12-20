package com.share.bookR.Authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.share.bookR.Activity.ProfileActivity;
import com.share.bookR.Constants;
import com.share.bookR.MainActivity;
import com.share.bookR.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;

public class MobileAthuntication extends AppCompatActivity {
    private static final int RC_SIGN_IN = 101;
    CountryCodePicker ccp;
    private EditText phoneNo;
    private Button continue_no;
    final static String PHONE="phone";
    final static String CUNTERYCODE="cc";
    final static String REF_CODE="RF";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    Constants constants=new Constants();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_athuntication);
        initViews();

        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MobileAthuntication.this,MainActivity.class));
                finish();
            }
        });
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                //Toast.makeText(MobileAthuntication.this, "Updated " + selectedCountry.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        continue_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNo.getText().toString().isEmpty()){
                    phoneNo.setError("Please enter mobile number");
                }
                else {
                    if (checkInternetConnection(v))
                        return;
                    Intent intent=new Intent(MobileAthuntication.this, MobileOtpAuth.class);
                    intent.putExtra(PHONE,phoneNo.getText().toString());
                    intent.putExtra(CUNTERYCODE,ccp.getSelectedCountryCodeWithPlus());
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.google_auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleAuthentication();
            }
        });

    }
    private void initViews(){

        mAuth = FirebaseAuth.getInstance();
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        phoneNo=findViewById(R.id.mobile_no);
        continue_no=findViewById(R.id.continue_button);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        finishAffinity();
    }
    private Boolean checkInternetConnection(View view) {
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null){
            Snackbar snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.dark_orange));
            snackbar.setTextColor(ContextCompat.getColor(this, R.color.black));
            snackbar.show();
            return true;
        }
        return false;
    }
    private void googleAuthentication(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("tag", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("tag", "Google sign in failed", e);
            }
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);



        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(MobileAthuntication.this,"OTP Verified", Toast.LENGTH_LONG).show();
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                //do create new user
                                HashMap<String, String> map=new HashMap<>();
                                SharedPreferences sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString(Constants.PHONE,task.getResult().getUser().getPhoneNumber());
                                editor.apply();
                                map.put("phone",task.getResult().getUser().getPhoneNumber());
                                map.put("startDate",constants.getTodayDate());
                                map.put("email",task.getResult().getUser().getEmail());
                                map.put("name",task.getResult().getUser().getDisplayName());
                                map.put("userName",task.getResult().getUser().getDisplayName().toLowerCase().replaceAll(" ",""));



                                FirebaseFirestore.getInstance().collection("Users").
                                        document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent inte = new Intent(MobileAthuntication.this, ProfileActivity.class);
                                        inte.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(inte);
                                    }
                                });

                            } else {
                                //user is exists, just do login
                                //constants.HideProgressDialog();
                                Intent inte = new Intent(MobileAthuntication.this, MainActivity.class);
                                inte.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(inte);
                            }


                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w("tag", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MobileAthuntication.this,"LogIn Failed\n"+task.getException(), Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }

                    }
                });
    }

    private void twitterAuthentication(){
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                    // The OAuth secret can be retrieved by calling:
                                    // authResult.getCredential().getSecret().
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                }
                            });
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
        }


        mAuth.startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // User is signed in.
                                // IdP data available in
                                // authResult.getAdditionalUserInfo().getProfile().
                                // The OAuth access token can also be retrieved:
                                // authResult.getCredential().getAccessToken().
                                // The OAuth secret can be retrieved by calling:
                                // authResult.getCredential().getSecret().
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure.
                            }
                });
    }




    private void linkAccount(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("tag", "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                        } else {
                            Log.w("tag", "linkWithCredential:failure", task.getException());
                            Toast.makeText(MobileAthuntication.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });



    }
}

/*
*   maine mobile se login kiya
*   email se krna hai
*
*
*
*
*
*
*
* */