package com.share.bookR.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.share.bookR.Constants;
import com.share.bookR.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.circularreveal.cardview.CircularRevealCardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 10;
    private CircleImageView profileImage;
    private CircularRevealCardView editProfileImage;
    private TextInputEditText name,phone,email,dateOfBirth,userName,description;
    private TextInputLayout userNameLayout,dobLayout;
    private String mUrl=null;
    private final String uId=FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final StorageReference profileReference = FirebaseStorage.getInstance().getReference()
            .child("userImage").child(uId).child("profile.jpeg");
    final Constants constants=new Constants();
    private int mYear, mMonth, mDay;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE );
        editor = sharedPreferences.edit();
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(ProfileActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(ProfileActivity.this, "You have already granted this permission!",Toast.LENGTH_SHORT).show();
                    launchCamera();
                }/*
                else if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    requestCameraPermission();
                }*/
                else {
                    requestStorageAndCameraPermission();
                }
            }
        });
        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, BookListActivity.class));
                finish();
            }
        });
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalenderDialog();
            }
        });
        constants.ProgressDialogShow(this);
        showUserDetails();

    }


    private void showUserDetails(){

        FirebaseFirestore.getInstance().collection("Users").document(uId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                constants.HideProgressDialog();
                //if (documentSnapshot.getString("")>1)
                    //findViewById(R.id.skip).setVisibility(View.GONE);
                Glide.with(getApplicationContext()).load(documentSnapshot.getString("url")).placeholder(R.drawable.user).into(profileImage);
                name.setText(documentSnapshot.getString("name"));
                userName.setText(documentSnapshot.getString("userName"));
                phone.setText(documentSnapshot.getString("phone"));
                email.setText(documentSnapshot.getString("email"));
                dateOfBirth.setText(documentSnapshot.getString("dateOfBirth"));
                mUrl=documentSnapshot.getString("url");
                findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, Object> map=new HashMap<>();
                        map.put("name",name.getText().toString());
                        map.put("photo",mUrl);
                        map.put("phone",phone.getText().toString());
                        map.put("email",email.getText().toString());
                        map.put("dateOfBirth",dateOfBirth.getText().toString());
                        map.put("userName",userName.getText().toString());
                        map.put("description",description.getText().toString());
                        editor.putString(Constants.NAME,name.getText().toString());
                        editor.putString(Constants.PHONE,phone.getText().toString());
                        editor.putString(Constants.USER_NAME,userName.getText().toString());
                        editor.putString(Constants.PROFILE,documentSnapshot.getString("photo"));
                        editor.apply();

                        if (userName.getText().toString().isEmpty())
                            map.put("userName",name.getText().toString().replaceAll(" ","").toLowerCase());
                        //FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("User").updateChildren(map);
                        FirebaseFirestore.getInstance().collection("Users").document(uId).update(map);
                        startActivity(new Intent(ProfileActivity.this, BookListActivity.class));
                        finish();
                    }
                });
            }
        });

    }


    private void openCalenderDialog() {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateOfBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void init(){
        profileImage=findViewById(R.id.profile_image);
        editProfileImage=findViewById(R.id.upload);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.mobile_no);
        email=findViewById(R.id.email);
        dateOfBirth=findViewById(R.id.date_of_birth);
        dobLayout=findViewById(R.id.date_of_birth_layout);
        dobLayout.setVisibility(View.GONE);
        dateOfBirth.setVisibility(View.GONE);
        userName=findViewById(R.id.user_name);
        userNameLayout=findViewById(R.id.user_name_layout);
        userNameLayout.setVisibility(View.GONE);
        userName.setVisibility(View.GONE);
        description=findViewById(R.id.tv_description);
    }
    private void launchCamera() {
        Intent intent =
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setAspectRatio(35,50)
                        .getIntent(getApplicationContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    private void requestStorageAndCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale
                (this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Please Grant Permissions to upload photo")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ProfileActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
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
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
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
                    profileImage.setImageBitmap(photo);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ASK_MULTIPLE_PERMISSION_REQUEST_CODE) {
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
                                    ActivityCompat.requestPermissions(ProfileActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
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
                                    ActivityCompat.requestPermissions(ProfileActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
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

    private void uploadProfile(Bitmap bitmap) {

//        progressBar.setVisibility(View.VISIBLE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profileReference.putBytes(data);
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
                        mUrl=uri.toString();
                        Glide.with(getApplicationContext()).load(mUrl).placeholder(R.drawable.user).into(profileImage);
                        Log.v("tag", "onSuccess: "+uri.toString());
//                        progressBar.setVisibility(View.GONE);
                        constants.HideProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to upload! Try Again :)", Toast.LENGTH_LONG).show();
                        Log.v("tag",e.toString());
                        constants.HideProgressDialog();
                    }
                });


            }
        });
    }
}