package com.share.bookR.AddBookAdapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.share.bookR.Activity.BarcodeScanActivity;
import com.share.bookR.Constants;
import com.share.bookR.R;
import com.bumptech.glide.Glide;


import java.util.ArrayList;

public class AddBookAdapter extends RecyclerView.Adapter {
    private final ArrayList<AddBookModel> addBookModels;
    private final Context context;

    int MY_PERMISSIONS_REQUEST_CAMERA=0;
    public AddBookAdapter(ArrayList<AddBookModel> addBookModels, Context context) {
        this.addBookModels = addBookModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == 0) {
            view = layoutInflater.inflate(R.layout.add_book_item,parent, false);
            return new ViewHolderTwo(view);
        }

        view = layoutInflater.inflate(R.layout.add_book_item1, parent, false);
        return new ViewHolderOne(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (addBookModels.get(position).getPhotoUrl().isEmpty()){
            ViewHolderTwo  viewHolder=(ViewHolderTwo)holder;
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    else {
                        Log.v("tag","else1");
                        context.startActivity(new Intent(context, BarcodeScanActivity.class));

                    }


                }
            });
        }else {
            ViewHolderOne viewHolderOne=(ViewHolderOne)holder;
            Glide.with(context).load(addBookModels.get(position).getPhotoUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    viewHolderOne.shimmerFrameLayout.stopShimmer();
                    viewHolderOne.shimmerFrameLayout.hideShimmer();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    viewHolderOne.shimmerFrameLayout.stopShimmer();
                    viewHolderOne.shimmerFrameLayout.hideShimmer();

                    return false;
                }
            }).into(viewHolderOne.imageView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==addBookModels.size()-1)
            return 0;
        return 1;
    }
    @Override
    public int getItemCount() {
        return addBookModels.size();
    }
    class ViewHolderOne extends RecyclerView.ViewHolder{
        ImageView imageView;
        ShimmerFrameLayout shimmerFrameLayout;
        public ViewHolderOne(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
            shimmerFrameLayout=itemView.findViewById(R.id.shimmerFrameLayout);
        }
    }
    class ViewHolderTwo extends RecyclerView.ViewHolder{
        LinearLayout layout;
        public ViewHolderTwo(@NonNull View itemView) {
            super(itemView);
            layout=itemView.findViewById(R.id.layout);
        }
    }


    /*private void launchCamera() {
        Intent intent =
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setAspectRatio(35,50)
                        .getIntent(context);

        ((Activity) context).startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void requestStorageAndCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                Manifest.permission.READ_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale
                ((Activity) context, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(context)
                    .setTitle("Permission needed")
                    .setMessage("Please Grant Permissions to upload photo")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, Constants.ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
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
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, Constants.ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }*/
}
