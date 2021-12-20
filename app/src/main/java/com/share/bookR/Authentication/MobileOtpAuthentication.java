package com.share.bookR.Authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.share.bookR.Activity.ProfileActivity;
import com.share.bookR.Constants;
import com.share.bookR.MainActivity;
import com.share.bookR.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class MobileOtpAuthentication extends AppCompatActivity {
    private TextView next,resend;
    final static String PHONE="phone";
    final static String CUNTERYCODE="cc";
    final static String REF_CODE="RF";
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private OtpTextView otpTextView;
    private Button continueButton;

    String otp="";
    String id;
    String phoneN0=null,referralCode=null;
    Constants constants=new Constants();


    Date d = new Date();
    CharSequence date  = DateFormat.format("dd/MM/yyyy", d.getTime());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_otp_authentication);
        progressBar = findViewById(R.id.progressBar1);
        continueButton=findViewById(R.id.next);
        progressBar.setVisibility(View.GONE);
        resend=findViewById(R.id.resend);
        mAuth = FirebaseAuth.getInstance();
        otpTextView=findViewById(R.id.otp_view);

        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otpId) {
                otp="";
                otp+=otpId;
                Log.v("tag",otp);
            }
        });

        mAuth=FirebaseAuth.getInstance();
        referralCode=getIntent().getStringExtra(REF_CODE);
        phoneN0=getIntent().getStringExtra(CUNTERYCODE)+getIntent().getStringExtra(PHONE);
        SendVerificationCode(phoneN0);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendVerificationCode(phoneN0);
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("tag",otp);
                if (otp.isEmpty()){
                    Toast.makeText(MobileOtpAuthentication.this,"Please Enter 6 Digit OTP",Toast.LENGTH_LONG).show();
                }
                else if (otp.replace(" ","").length()!=6){
                    Toast.makeText(MobileOtpAuthentication.this,"Please Enter 6 Digit OTP",Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp);
                        signInWithPhoneAuthCredential(credential);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Enter correct otp", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });





    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        constants.ProgressDialogShow(this);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MobileOtpAuthentication.this,"OTP Verified", Toast.LENGTH_LONG).show();
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                //do create new user
                                HashMap<String, String> map=new HashMap<>();
                                SharedPreferences sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
                                Editor editor=sharedPreferences.edit();
                                editor.putString(Constants.PHONE,phoneN0);
                                editor.apply();
                                map.put("phone",phoneN0);
                                map.put("startDate",date.toString());


                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("User").setValue(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                constants.HideProgressDialog();
                                                Intent inte = new Intent(MobileOtpAuthentication.this, ProfileActivity.class);
                                                inte.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(inte);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.v("tag",e.getMessage());
                                    }
                                });


                            } else {
                                //user is exists, just do login
                                constants.HideProgressDialog();
                                Intent inte = new Intent(MobileOtpAuthentication.this, MainActivity.class);
                                inte.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(inte);
                            }


                            progressBar.setVisibility(View.GONE);
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            constants.HideProgressDialog();
                            Toast.makeText(MobileOtpAuthentication.this,"Verification Failed",Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("tag",e.toString());
                constants.HideProgressDialog();
            }
        });



    }


    private void SendVerificationCode(String phoneN0) {
        new CountDownTimer(60000,1000){

            @Override
            public void onTick(long l) {
                resend.setText(""+l/1000);
                resend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resend.setText("Resend");
                resend.setEnabled(true);
            }
        }.start();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneN0,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        MobileOtpAuthentication.this.id=id;
                        //super.onCodeSent(s, forceResendingToken);
                        Log.v("tag","code send ho gya hai bhai");
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        //signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MobileOtpAuthentication.this," Verification Failed \n"+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });        // OnVerificationStateChangedCallbacks
    }

}
