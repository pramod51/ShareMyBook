package com.share.bookR.Fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.share.bookR.Constants;
import com.share.bookR.R;
import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.circularreveal.cardview.CircularRevealCardView;
import com.google.android.material.textfield.TextInputEditText;
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

public class EditProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private CircularRevealCardView editProfileImage;
    private TextInputEditText name,phone,email,dateOfBirth,userName;
    private String mUrl=null;
    private final String uId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final StorageReference profileReference = FirebaseStorage.getInstance().getReference()
            .child("userImage").child(uId).child("profile.jpeg");
    final Constants constants=new Constants();
    private int mYear, mMonth, mDay;
    private int ASK_MULTIPLE_PERMISSION_REQUEST_CODE=10;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public EditProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
        View view= inflater.inflate(R.layout.activity_profile, container, false);
        init(view);
        sharedPreferences=getContext().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE );
        editor = sharedPreferences.edit();
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                }/*
                else if (ContextCompat.checkSelfPermission(getActivity()
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    requestCameraPermission();
                }*/
                else {
                    requestStorageAndCameraPermission();
                }
            }
        });
        view.findViewById(R.id.skip).setVisibility(View.INVISIBLE);
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalenderDialog();
            }
        });
        constants.ProgressDialogShow(getContext());



        FirebaseFirestore.getInstance().collection("Users").document(uId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                constants.HideProgressDialog();
                //if (snapshot.getChildrenCount()>1)
                    //view.findViewById(R.id.skip).setVisibility(View.GONE);
                Glide.with(getContext()).load(documentSnapshot.getString("photo")).placeholder(R.drawable.user).into(profileImage);
                name.setText(documentSnapshot.getString("name"));
                userName.setText(documentSnapshot.getString("userName"));
                phone.setText(documentSnapshot.getString("phone"));
                email.setText(documentSnapshot.getString("email"));
                dateOfBirth.setText(documentSnapshot.getString("dateOfBirth"));
                mUrl=documentSnapshot.getString("photo");

                if (!phone.getText().toString().contains("+")){
                    phone.setEnabled(true);
                }

                view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, Object> map=new HashMap<>();
                        map.put("name",name.getText().toString());
                        map.put("photo",mUrl);
                        if (!phone.isEnabled())
                            map.put("phone",phone.getText().toString());
                        else
                            map.put("phone",phone.getText().toString().replace("+",""));
                        map.put("email",email.getText().toString());
                        map.put("dateOfBirth",dateOfBirth.getText().toString());
                        map.put("userName",userName.getText().toString());
                        //FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("User").updateChildren(map);
                        editor.putString(Constants.NAME,name.getText().toString());
                        editor.putString(Constants.PHONE,phone.getText().toString());
                        editor.putString(Constants.USER_NAME,userName.getText().toString());
                        editor.putString(Constants.PROFILE,documentSnapshot.getString("photo"));
                        editor.apply();
                        FirebaseFirestore.getInstance().collection("Users").document(uId).update(map);
                        Navigation.findNavController(view).navigateUp();
                    }
                });
            }
        });

        return view;
    }
    private void openCalenderDialog() {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateOfBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void init(View view){
        profileImage=view.findViewById(R.id.profile_image);
        editProfileImage=view.findViewById(R.id.upload);
        name=view.findViewById(R.id.name);
        phone=view.findViewById(R.id.mobile_no);
        email=view.findViewById(R.id.email);
        dateOfBirth=view.findViewById(R.id.date_of_birth);
        userName=view.findViewById(R.id.user_name);
    }
    private void launchCamera() {
        Intent intent =
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setAspectRatio(35,50)
                        .getIntent(getContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
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
                        inputStream = getContext().getContentResolver().openInputStream(imageUri);
                        Bitmap photo = BitmapFactory.decodeStream(inputStream);
                        int nh = (int) (photo.getHeight() * (512.0 / photo.getWidth()));
                        Bitmap scaled = Bitmap.createScaledBitmap(photo, 512, nh, true);
                        uploadProfile(scaled);
                        profileImage.setImageBitmap(photo);
                        profileImage.setImageURI(imageUri);

                        constants.ProgressDialogShow(getContext());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(getContext(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "" + requestCode, Toast.LENGTH_SHORT).show();
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
                        Glide.with(getContext()).load(mUrl).placeholder(R.drawable.user).into(profileImage);
                        Log.v("tag", "onSuccess: "+uri.toString());
//                        progressBar.setVisibility(View.GONE);
                        constants.HideProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to upload! Try Again :)", Toast.LENGTH_LONG).show();
                        Log.v("tag",e.toString());
                        constants.HideProgressDialog();
                    }
                });


            }
        });
    }

    private void requestStorageAndCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale
                (getActivity(), Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission needed")
                    .setMessage("Please Grant Permissions to upload photo")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
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
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
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
                    new AlertDialog.Builder(getContext())
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed to access camera")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(),
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
                } else{
                    new AlertDialog.Builder(getContext())
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed to access the photo from storage")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(),
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
}