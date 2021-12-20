package com.share.bookR.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.share.bookR.Activity.BarcodeScanActivity;
import com.share.bookR.Authentication.MobileAthuntication;
import com.share.bookR.BookAdapter.BookAdapter;
import com.share.bookR.BookAdapter.BookModel;
import com.share.bookR.Constants;
import com.share.bookR.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class LibraryFragment extends Fragment {


    public LibraryFragment() {
        // Required empty public constructor
    }

    int MY_PERMISSIONS_REQUEST_CAMERA=0;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<BookModel> bookModels;
    private Constants constants=new Constants();
    private final FirebaseUser uId= FirebaseAuth.getInstance().getCurrentUser();



    //private final StorageReference profileReference = FirebaseStorage.getInstance().getReference().child("Images").child(uId.getUid()).child(UUID.randomUUID().toString()+".jpeg");
    public String isbnNumber;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_library, container, false);
       fab= view.findViewById(R.id.fab);
       recyclerView=view.findViewById(R.id.recyclerView);
       recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MobileAthuntication.class));
                getActivity().finish();
            }
        });
        if (uId==null) {
            view.findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            return view;
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
                else {
                    Log.v("tag","else1");
                    startActivity(new Intent(getContext(), BarcodeScanActivity.class));

                }

            }
        });


        return view;
    }

    private void loadLibraryDAta(){

        constants.ProgressDialogShow(getContext());

        FirebaseDatabase.getInstance().getReference().child(Constants.LIBRARY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                bookModels=new ArrayList<>();
                bookModels.clear();
                FirebaseDatabase.getInstance().getReference().child("Users/"+uId.getUid()+"/Orders").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {



                        for (DataSnapshot ds:snapshot1.getChildren())
                            bookModels.add(new BookModel("",ds.child("frontCoverPhoto").getValue(String.class),"","","","",""));

                        for (DataSnapshot ds2:snapshot.child("Books").getChildren()){
                            DataSnapshot ds1=ds2.child("BookDetails");
                            if (ds2.child("BookOwning").child(uId.getUid()).exists())
                                bookModels.add(new BookModel("",ds1.child("frontCoverPhoto").getValue(String.class),"","","","",""));
                        }

                        adapter=new BookAdapter(bookModels,getContext());
                        recyclerView.setAdapter(adapter);
                        constants.HideProgressDialog();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                constants.HideProgressDialog();
                Log.v("tag",error.toString());
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

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
                            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, Constants.ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
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
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, Constants.ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
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
                    new AlertDialog.Builder(getContext())
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed to access camera")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(),
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
                    new AlertDialog.Builder(getContext())
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed to access the photo from storage")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(),
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
                    inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap photo = BitmapFactory.decodeStream(inputStream);
                    int nh = (int) (photo.getHeight() * (512.0 / photo.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(photo, 512, nh, true);
                    uploadProfile(scaled);

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
            uploadTask = profileReference.putBytes(data);

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
//                        mRef.child(uId.getUid().child("Profile").child("profileUrl").setValue(uri.toString());
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
                        FirebaseDatabase.getInstance().getReference().child("Users").child(uId.getUid().child("Books").push().child("BookDetails").setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                constants.HideProgressDialog();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

                        Log.v("tag", "onSuccess: "+uri.toString());
//                        progressBar.setVisibility(View.GONE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted. Continue the action or workflow
                // in your app.
                Log.v("tag", "if");
                startActivity(new Intent(getContext(), BarcodeScanActivity.class));
            } else {
                // Explain to the user that the feature is unavailable because
                // the features requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to
                // system settings in an effort to convince the user to change
                // their decision.
                Log.v("tag", "cam req");
                Toast.makeText(getContext(), "Camera permission required to scan barcode", Toast.LENGTH_LONG).show();

            }
            return;
        }
// Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    public void onResume() {
        if (uId!=null)
        loadLibraryDAta();
        Log.v("tag","resume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.v("tag","stop");
        super.onStop();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.v("tag","attach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.v("tag","deattach");
        super.onDetach();
    }

    @Override
    public void onPause() {
        Log.v("tag","pause");
        super.onPause();
    }

    @Override
    public void onStart() {
        Log.v("tag","start");
        super.onStart();
    }
}