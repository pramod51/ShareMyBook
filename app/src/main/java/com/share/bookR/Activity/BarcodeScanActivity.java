package com.share.bookR.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.share.bookR.Constants;
import com.share.bookR.GoogleBookApi.IsbnDetails;
import com.share.bookR.GoogleBookApi.Item;
import com.share.bookR.GoogleBookApi.JsonPlaceHolder;
import com.share.bookR.R;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BarcodeScanActivity extends AppCompatActivity {
    int MY_PERMISSIONS_REQUEST_CAMERA=0;

    String mtitle,msubTitle;
    String mdescription;
    String mauthor,mpublication;
    String uId=FirebaseAuth.getInstance().getCurrentUser().getUid();
    CodeScanner mCodeScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            Log.v("tag","else1");
            startScan();
        }
        findViewById(R.id.click_here).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog sheetDialog=new BottomSheetDialog(BarcodeScanActivity.this);
                View sheetView= LayoutInflater.from(BarcodeScanActivity.this).inflate(R.layout.bottom_sheet, null , false);
                sheetDialog.setContentView(sheetView);
                sheetDialog.show();
                TextInputEditText isbn=sheetView.findViewById(R.id.isbn);
                sheetView.findViewById(R.id.add_book).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isbn.getText().toString().length()!=10&&isbn.getText().length()!=13){
                            Toast.makeText(BarcodeScanActivity.this,"Enter ISBN 10 or 13 length",Toast.LENGTH_LONG).show();
                        }
                        else {
                            getResults(isbn.getText().toString());
                        }

                    }
                });
            }
        });


    }

    private void startScan() {
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        Log.v("tag",result.getText());
                        if (result.getText().length()!=10&&result.getText().length()!=13){
                            Log.v("tag","lenght =="+result.getText().length());
                            Toast.makeText(BarcodeScanActivity.this,"We not getting correct ISBN No from barcode\nTry again or press Click here at bottom",Toast.LENGTH_LONG).show();
                        }
                        else
                        getResults(result.getText());
                    }
                });
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted. Continue the action or workflow
                // in your app.
                startScan();
                Log.v("tag", "if");

            } else {
                // Explain to the user that the feature is unavailable because
                // the features requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to
                // system settings in an effort to convince the user to change
                // their decision.
                Log.v("tag", "cam req");
                Toast.makeText(BarcodeScanActivity.this, "Camera permission required to scan barcode", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
            return;
        }
// Other 'case' lines to check for other
        // permissions this app might request.
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*@Override
    protected void onPostResume() {
        super.onPostResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }*/


    public void getResults(String isbnNumber){

        Constants constants=new Constants();
        constants.ProgressDialogShow(this);

        FirebaseDatabase.getInstance().getReference().child(Constants.LIBRARY).child("Books")
                .child(isbnNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://www.googleapis.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);
                Call<IsbnDetails> detailsCall=jsonPlaceHolderApi.getDetails("isbn:"+isbnNumber,"AIzaSyCoGZjr05HCw6_6VMnjzgj3ev1M24XN-tM");

                int bookInstance=0;
                Log.v("tag",isbnNumber);
                if (snapshot.child("BookDetails").exists()){
                    //bookInstance = snapshot.child("BookDetails").child(Constants.BOOK_INSTANCE).getValue(Integer.class);
                    if(snapshot.child("BookOwning").child(uId).exists()&&snapshot.child("BookOwning").child(uId).child(isbnNumber).child("userId").getValue(String.class).equals(uId)){
                        Toast.makeText(BarcodeScanActivity.this,"This book already available in your library",Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
                detailsCall.enqueue(new Callback<IsbnDetails>() {
                    @Override
                    public void onResponse(Call<IsbnDetails> call, Response<IsbnDetails> response) {
                        if (!response.isSuccessful()){
                            Toast.makeText(BarcodeScanActivity.this,"Something went wrong, Please Scan again!",Toast.LENGTH_LONG).show();
                            return;
                        }
                        IsbnDetails isbnDetails=response.body();
                        if (isbnDetails.getTotalItems()==0){
                            Toast.makeText(BarcodeScanActivity.this,"Book details not available!",Toast.LENGTH_LONG).show();
                            constants.HideProgressDialog();
                            onBackPressed();
                            return;
                        }
//                        Log.v("tag","id=="+isbnDetails.getItems().get(0).getId());
//                        Log.v("tag","id=="+isbnDetails.get);

                        HashMap<String,Object> map=new HashMap<>();
                        HashMap<String,Object> owningMap=new HashMap<>();

                        map.put("title",isbnDetails.getItems().get(0).getVolumeInfo().getTitle());
                        if (isbnDetails.getItems().get(0).getVolumeInfo().getDescription()!=null)
                        map.put("description",isbnDetails.getItems().get(0).getVolumeInfo().getDescription());
                        else
                        map.put("description","Not Available");
                        if (isbnDetails.getItems().get(0).getVolumeInfo().getImageLinks()!=null)
                        map.put("frontCoverPhoto",isbnDetails.getItems().get(0).getVolumeInfo().getImageLinks().getThumbnail());
                        else
                        map.put("frontCoverPhoto","https://covers.openlibrary.org/b/isbn/"+isbnNumber+"-L.jpg");

                        //map.put("backCoverPhoto","");
                        if (isbnDetails.getItems().get(0).getVolumeInfo().getAuthors()!=null) {
                            for (int i=0;i<isbnDetails.getItems().get(0).getVolumeInfo().getAuthors().size();i++) {
                                mauthor+=isbnDetails.getItems().get(0).getVolumeInfo().getAuthors().get(i);
                                if (i!=isbnDetails.getItems().get(0).getVolumeInfo().getAuthors().size()-1)
                                    mauthor+=", ";
                            }
                            map.put("author",isbnDetails.getItems().get(0).getVolumeInfo().getAuthors().get(0));
                        }
                        else
                            map.put("author","NA");
                        map.put("isbn",isbnNumber);
                        map.put("genre","");
                        map.put("publication",isbnDetails.getItems().get(0).getVolumeInfo().getPublisher());
                        map.put("language",isbnDetails.getItems().get(0).getVolumeInfo().getLanguage());
                        if (isbnDetails.getItems().get(0).getVolumeInfo().getCategories()!=null)
                        map.put("category",isbnDetails.getItems().get(0).getVolumeInfo().getCategories().get(0));
                        else
                        map.put("category","NA");
                        if (isbnDetails.getItems().get(0).getVolumeInfo().getPublishedDate()!=null)
                            map.put("publishedDate",isbnDetails.getItems().get(0).getVolumeInfo().getPublishedDate());
                        else
                            map.put("publishedDate","2021");
                        owningMap.put("userId",uId);
                        map.put("bookId",isbnDetails.getItems().get(0).getId());
                        owningMap.put("isbn",isbnNumber);
                        owningMap.put("startDate",constants.getTodayDate());
                        owningMap.put("bookInstanceId",isbnNumber+uId.replace("-",""));
                        map.put("startDate",constants.getTodayDate());
                        //map.put(Constants.BOOK_INSTANCE, finalBookInstance +1);


                        FirebaseDatabase.getInstance().getReference().child(Constants.LIBRARY)
                                .child("Books").child(isbnNumber).child("BookDetails").setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference().child(Constants.LIBRARY).child("Books").child(isbnNumber).child("BookOwning")
                                        .child(uId).child(isbnNumber).setValue(owningMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(BarcodeScanActivity.this,"Congrats! Your book added into Book Store",Toast.LENGTH_LONG).show();
                                        constants.HideProgressDialog();
                                        onBackPressed();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(BarcodeScanActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                        constants.HideProgressDialog();
                                        onBackPressed();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(BarcodeScanActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<IsbnDetails> call, Throwable t) {
                        Log.v("tag",t.getMessage());
                        Toast.makeText(BarcodeScanActivity.this,"Something went wrong, Try Again",Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}