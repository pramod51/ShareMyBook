package com.share.bookR.YourOrderAdopter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.share.bookR.Constants;
import com.share.bookR.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    ArrayList<OrderModel> orderModels;
    Context context;

    public OrdersAdapter(ArrayList<OrderModel> orderModels, Context context) {
        this.orderModels = orderModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.your_order_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel currentOrder=orderModels.get(position);
        Glide.with(context).load(currentOrder.getUrl()).into(holder.bookImage);
        holder.bookTitle.setText(currentOrder.getTitle());
        holder.shippingTo.setText("Ship To "+currentOrder.getName());
        //holder.price.setText("Price ₹"+currentOrder.getPrice());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString(Constants.URL,currentOrder.getUrl());
                Log.v("tag","loading.........");
                Navigation.findNavController(view).navigate(R.id.action_yourOrderFragment_to_orderDetailsFragment,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (orderModels==null)
            return 0;
        return orderModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookTitle,status,price,shippingTo;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage=itemView.findViewById(R.id.book_image);
            bookTitle=itemView.findViewById(R.id.book_title);
            status=itemView.findViewById(R.id.status);
            //price=itemView.findViewById(R.id.price);
            shippingTo=itemView.findViewById(R.id.ship);
            cardView=itemView.findViewById(R.id.card);


        }
    }
}
