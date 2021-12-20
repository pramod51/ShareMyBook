package com.share.bookR.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.share.bookR.AddBookAdapter.AddBookAdapter;
import com.share.bookR.AddBookAdapter.AddBookModel;
import com.share.bookR.BookAdapter.BookAdapter;
import com.share.bookR.BookAdapter.BookModel;
import com.share.bookR.Constants;
import com.share.bookR.Fragments.LibraryFragment;
import com.share.bookR.MainActivity;
import com.share.bookR.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BookListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AddBookAdapter addBookAdapter;
    private ArrayList<AddBookModel> addBookModels;
    private final String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Constants constants=new Constants();

    int MY_PERMISSIONS_REQUEST_CAMERA=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        addBookModels=new ArrayList<>();



        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookListActivity.this, MainActivity.class));
                finish();
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookListActivity.this, MainActivity.class));
                finish();
            }
        });

        constants.ProgressDialogShow(this);
        Log.v("tag","tuyag");
        FirebaseDatabase.getInstance().getReference().child(Constants.LIBRARY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addBookModels.clear();
                Log.v("tag","tuyag");
                for (DataSnapshot ds2:snapshot.child("Books").getChildren()){
                    DataSnapshot ds1=ds2.child("BookDetails");
                    if (ds2.child("BookOwning").child(uId).exists())
                        addBookModels.add(new AddBookModel(ds1.child("frontCoverPhoto").getValue(String.class)));
                }
                constants.HideProgressDialog();
                addBookModels.add(new AddBookModel(""));
                addBookAdapter=new AddBookAdapter(addBookModels,BookListActivity.this);
                recyclerView.setAdapter(addBookAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.v("tag",error.toString());
                constants.HideProgressDialog();
                Toast.makeText(BookListActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = result.getUriContent();
                Log.v("Tag", "uploaded tas1");
                InputStream inputStream;
                try {
                    inputStream = this.getContentResolver().openInputStream(imageUri);
                    Bitmap photo = BitmapFactory.decodeStream(inputStream);
                    int nh = (int) (photo.getHeight() * (512.0 / photo.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(photo, 512, nh, true);
                    uploadProfile(scaled);

                    constants.ProgressDialogShow(this);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "" + requestCode, Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadProfile(Bitmap bitmap) {

//        progressBar.setVisibility(View.VISIBLE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask;
        uploadTask = FirebaseStorage.getInstance().getReference()
                .child("Images").child(uId).child(UUID.randomUUID().toString()+".jpeg").putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.v("tag", exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...

                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        Picasso.get().load(uri.toString()).into(profileImageView);
//                        mRef.child(uId).child("Profile").child("profileUrl").setValue(uri.toString());
                        HashMap<String,String> map=new HashMap<>();
                        map.put("title","");
                        map.put("description","");
                        map.put("frontCoverPhoto",uri.toString());
                        map.put("backCoverPhoto","");
                        map.put("author","");
                        map.put("isbn","");
                        map.put("genre","");
                        map.put("publication","");
                        map.put("language","");
                        map.put("category","");
                        map.put("startDate",constants.getTodayDate());
                        FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Books").push().child("BookDetails").setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                constants.HideProgressDialog();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(BookListActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

                        Log.v("tag", "onSuccess: "+uri.toString());
//                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BookListActivity.this, "Failed to upload! Try Again :)", Toast.LENGTH_LONG).show();
                        Log.v("tag",e.toString());
                        constants.HideProgressDialog();
                    }
                });


            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.ASK_MULTIPLE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (cameraPermission && readExternalFile) {
                    // write your logic here
                    launchCamera();
                }
                else if (!cameraPermission) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed to access camera")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(BookListActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Constants.ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed to access the photo from storage")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(BookListActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Constants.ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
            }
        }
    }
    private void launchCamera() {
        Intent intent =
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setAspectRatio(35,50)
                        .getIntent(this);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted. Continue the action or workflow
                // in your app.
                startActivity(new Intent(this, BarcodeScanActivity.class));
                Log.v("tag", "if");

            } else {
                // Explain to the user that the feature is unavailable because
                // the features requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to
                // system settings in an effort to convince the user to change
                // their decision.
                Log.v("tag", "cam req");
                Toast.makeText(BookListActivity.this, "Camers permission required to scan barcode", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
            return;
        }
// Other 'case' lines to check for other
        // permissions this app might request.
    }
}