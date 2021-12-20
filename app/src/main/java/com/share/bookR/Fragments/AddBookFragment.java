package com.share.bookR.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.share.bookR.Constants;
import com.share.bookR.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class AddBookFragment extends Fragment {


    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 10;

    public AddBookFragment() {
        // Required empty public constructor
    }

    private NavController navController;

    private final String uId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final StorageReference profileReference = FirebaseStorage.getInstance().getReference()
            .child("Images").child(uId).child(UUID.randomUUID().toString()+".jpeg");
    private final StorageReference profileReference1 = FirebaseStorage.getInstance().getReference()
            .child("Images").child(uId).child(UUID.randomUUID().toString()+".jpeg");

    private ImageView frontCover,backCover;
    private RelativeLayout uploadFrontCover,uploadBackCover;

    private FloatingActionButton fabSubmit;
    private EditText mTitle,mDescription,authorName,isbn,genre,publication,language,category;
    private String frontCoverUrl,backCoverUrl;

    private final Date d = new Date();
    private final CharSequence date  = DateFormat.format("dd/MM/yyyy", d.getTime());
    private final Constants constants=new Constants();
    private boolean isFrontCoverUploading=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        initViews(view);
        uploadFrontCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFrontCoverUploading=true;
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //launchCamera();
                }/*
                else if (ContextCompat.checkSelfPermission(getActivity()
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    requestCameraPermission();
                }*/
                else {
                    //requestStorageAndCameraPermission();
                }
            }
        });
        uploadBackCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFrontCoverUploading=false;

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //launchCamera();
                }/*
                else if (ContextCompat.checkSelfPermission(getActivity()
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    requestCameraPermission();
                }*/
                else {
                    //requestStorageAndCameraPermission();
                }
            }
        });


        fabSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title,description,price;
                title=mTitle.getText().toString();
                description=mDescription.getText().toString();
                //price=mPrice.getText().toString();
                if (!validate())
                    return;;

                HashMap<String,String> map=new HashMap<>();
                map.put("title",title);
                map.put("description",description);
                map.put("frontCoverPhoto",frontCoverUrl);
                map.put("backCoverPhoto",backCoverUrl);
                map.put("author",authorName.getText().toString());
                map.put("isbn",isbn.getText().toString());
                map.put("genre",genre.getText().toString());
                map.put("publication",publication.getText().toString());
                map.put("language",language.getText().toString());
                map.put("category",category.getText().toString());
                map.put("startDate",date.toString());
                FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Books").push().child("BookDetails").setValue(map);
                Navigation.findNavController(view).navigate(R.id.action_addBookFragment_to_libraryFragment);


            }
        });
        return view;
    }

    private boolean validate() {
        if (mTitle.getText().toString().isEmpty()){
            mTitle.setError("Enter Title");
            return false;
        }
        else
            mTitle.setError(null);
        if (authorName.getText().toString().isEmpty()){
            authorName.setError("Enter Author Name");
            return false;
        }
        else
            authorName.setError(null);
        if (isbn.getText().toString().isEmpty()){
            isbn.setError("Enter ISBN");
            return false;
        }
        else
            isbn.setError(null);
        if (genre.getText().toString().isEmpty()){
            genre.setError("Enter Genre");
            return false;
        }
        else
            genre.setError(null);
        if (publication.getText().toString().isEmpty()){
            publication.setError("Enter Publication");
            return false;
        }
        else
            publication.setError(null);
        if (language.getText().toString().isEmpty()){
            language.setError("Enter Language");
            return false;
        }
        else
            language.setError(null);
        if (category.getText().toString().isEmpty()){
            category.setError("Enter Category");
            return false;
        }
        else
            category.setError(null);
        if (mDescription.getText().toString().isEmpty()){
            mDescription.setError("Enter Description");
            return false;
        }
        else
            mDescription.setError(null);
        if (frontCoverUrl.isEmpty()&&backCoverUrl.isEmpty()){
            Toast.makeText(getContext(),"Upload Book Cover",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /*private void launchCamera() {
        Intent intent =
                CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                //.setAspectRatio(35,50)
                .getIntent(getContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
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
                if (!cameraPermission) {
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
                } else {
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
    }*/

    private void initViews(View view) {

        frontCover = view.findViewById(R.id.front_cover);
         backCover= view.findViewById(R.id.back_cover);
         uploadFrontCover= view.findViewById(R.id.upload_front_cover);
         uploadBackCover= view.findViewById(R.id.upload_back_cover);

        fabSubmit=view.findViewById(R.id.fab_submit);
        mTitle=view.findViewById(R.id.et_title);
        mDescription=view.findViewById(R.id.et_description);
        authorName=view.findViewById(R.id.et_author);
        isbn=view.findViewById(R.id.isbn);
        genre=view.findViewById(R.id.genre);
        publication=view.findViewById(R.id.publication);
        language=view.findViewById(R.id.language);
        category=view.findViewById(R.id.category);
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
                    inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap photo = BitmapFactory.decodeStream(inputStream);
                    int nh = (int) (photo.getHeight() * (512.0 / photo.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(photo, 512, nh, true);
                    uploadProfile(scaled);
                    if (isFrontCoverUploading)
                    frontCover.setImageBitmap(photo);
                    else
                        backCover.setImageBitmap(scaled);
                    constants.ProgressDialogShow(getContext());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "" + requestCode, Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadProfile(Bitmap bitmap) {

//        progressBar.setVisibility(View.VISIBLE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask;
        if (isFrontCoverUploading)
            uploadTask = profileReference.putBytes(data);
        else
            uploadTask = profileReference1.putBytes(data);

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
                        if (isFrontCoverUploading)
                        frontCoverUrl=uri.toString();
                        else
                            backCoverUrl=uri.toString();
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
*/
}


