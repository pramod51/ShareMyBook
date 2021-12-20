package com.share.bookR.AddressAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.share.bookR.Constants;
import com.share.bookR.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    ArrayList<AddressModel> addressModels;
    Context context;
    Constants constants=new Constants();
    private final String uId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    public AddressAdapter(ArrayList<AddressModel> addressModels, Context context) {
        this.addressModels = addressModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressModel currentAddress=addressModels.get(position);
        holder.name.setText(currentAddress.getName());
        holder.details.setText(currentAddress.getDetails());
        holder.saveAs.setText(currentAddress.getSaveAs());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE );
                Log.v("tag",sharedPreferences.getString(Constants.ISBN,"kf"));
                if (!sharedPreferences.getString(Constants.ISBN,"").isEmpty()){
                    constants.ProgressDialogShow(context);
                    Log.v("tag",sharedPreferences.getString(Constants.ISBN,"kf"));
                    FirebaseDatabase.getInstance().getReference().child(Constants.LIBRARY).child("Books")
                            .child(sharedPreferences.getString(Constants.ISBN,"")).child("BookDetails").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HashMap<String, Object> map=new HashMap();
                            map.put("description",snapshot.child("description").getValue(String.class));
                            map.put("frontCoverPhoto",snapshot.child("frontCoverPhoto").getValue(String.class));
                            map.put("backCoverPhoto",snapshot.child("backCoverPhoto").getValue(String.class));
                            map.put("isbn",snapshot.child("isbn").getValue(String.class));
                            map.put("genre",snapshot.child("genre").getValue(String.class));
                            map.put("publication",snapshot.child("publication").getValue(String.class));
                            map.put("language",snapshot.child("language").getValue(String.class));
                            map.put("category",snapshot.child("category").getValue(String.class));
                            map.put("title",snapshot.child("title").getValue(String.class));
                            //map.put("price",snapshot.child("price").getValue(String.class));
                            map.put("Address",currentAddress);
                            map.put("sourceHoldingId",sharedPreferences.getString(Constants.USER_ID,""));
                            map.put("bookInstanceId",sharedPreferences.getString(Constants.BOOK_ID,""));
                            map.put("orderId",constants.getRandomUniqueId().replaceAll("-",""));
                            map.put("dateOfOrder",constants.getTodayDate());
                            map.put("stage","");

                            map.put("author",snapshot.child("author").getValue(String.class));


                            FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Orders").push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    constants.HideProgressDialog();
                                    Navigation.findNavController(view).navigate(R.id.action_addressesFragment_to_trackOrderFragment);
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (addressModels==null)
            return 0;
        return addressModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,details,saveAs;
        LinearLayout cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            details=itemView.findViewById(R.id.details);
            cardView=itemView.findViewById(R.id.card);
            saveAs=itemView.findViewById(R.id.saveAs);

        }
    }
}
