package com.share.bookR.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.share.bookR.AddressAdapter.AddressAdapter;
import com.share.bookR.AddressAdapter.AddressModel;
import com.share.bookR.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AddressesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<AddressModel> addressModels;
    private TextView addNewAddress;
    private final String uId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_addresses, container, false);
        init(view);
        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_addressesFragment_to_addAddressFragment);
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Addresses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addressModels=new ArrayList<>();
                for (DataSnapshot ds:snapshot.getChildren())
                addressModels.add(new AddressModel(ds.child("name").getValue(String.class),ds.child("location").getValue()+", "+ds.child("houseNumber").getValue()+", "+
                        ds.child("city").getValue()+" "+ds.child("pinCode").getValue()+"\n\n"+ds.child("phone").getValue(),"",ds.child("type").getValue(String.class)));
                adapter=new AddressAdapter(addressModels,getContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    private void init(View view) {
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addNewAddress=view.findViewById(R.id.add_new_address);
    }
}