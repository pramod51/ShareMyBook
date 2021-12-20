package com.share.bookR.BookAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.share.bookR.Constants;
import com.share.bookR.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    ArrayList<BookModel> bookModels;
    Context context;

    public BookAdapter(ArrayList<BookModel> bookModels, Context context) {
        this.bookModels = bookModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookModel currentBook=bookModels.get(position);
        holder.title.setText(currentBook.getTitle());
        holder.ratingBar.setRating(Float.parseFloat(("0"+currentBook.getRating()).replaceAll("null","")));
        holder.description.setText("by "+currentBook.getAuthorName());
        Log.v("tag",currentBook.getBookUrl());
        Glide.with(context).load(currentBook.getBookUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.shimmerFrameLayout.stopShimmer();
                holder.shimmerFrameLayout.hideShimmer();
                //holder.bookImage.setBackground(R.color.white);
                return false;
            }
        }).into(holder.bookImage);
        holder.bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString(Constants.ISBN,currentBook.getIsbn());

                if (!currentBook.getIsbn().isEmpty())
                Navigation.findNavController(view).navigate(R.id.action_bookStoreFragment_to_bookDetailsFragment,bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,description;
        ImageView bookImage;
        RatingBar ratingBar;
        ShimmerFrameLayout shimmerFrameLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.tv_title);
            description=itemView.findViewById(R.id.tv_description);
            bookImage=itemView.findViewById(R.id.iv_rv_image);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            shimmerFrameLayout=itemView.findViewById(R.id.shimmerFrameLayout);
        }
    }
}
