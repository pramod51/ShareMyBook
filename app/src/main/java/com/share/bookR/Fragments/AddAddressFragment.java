package com.share.bookR.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.share.bookR.Constants;
import com.share.bookR.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class AddAddressFragment extends Fragment {
    private static final int PERMISSION_ID = 101;
    private TextInputLayout nameLayout, locationLayout, cityLayout, pinLayout, stateLayout;
    private TextInputEditText name, location, houseNumber, city, pinCode, landmark, state,floor,towerBlock;
    private RadioButton home, work, other;
    private final String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FusedLocationProviderClient mFusedLocationClient = null;
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            //latTextView.setText(mLastLocation.getLatitude()+"");
            //lonTextView.setText(mLastLocation.getLongitude()+"");
            Log.v("tag", "" + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());

        }
    };
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    setLocationDetails();

                    Log.v("tag","permimdkjkf f gkmg kg kn");
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_address, container, false);
        init(view);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences(Constants.SHARED_PREFS,Context.MODE_PRIVATE);
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                setLocationDetails();

            } else {
                Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }


        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    Constants constants = new Constants();
                    constants.ProgressDialogShow(getContext());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", name.getText().toString());
                    map.put("location", location.getText().toString());
                    map.put("houseNumber", houseNumber.getText().toString());
                    map.put("floor",floor.getText().toString());
                    map.put("towerBlock",towerBlock.getText().toString());
                    map.put("city", city.getText().toString());
                    map.put("pinCode", pinCode.getText().toString());
                    map.put("landmark", landmark.getText().toString());
                    map.put("state", state.getText().toString());
                    map.put("phone", sharedPreferences.getString(Constants.PHONE,""));
                    if (home.isChecked())
                        map.put("type", "Home");
                    else if (work.isChecked())
                        map.put("type", "Work");
                    else
                        map.put("type", "Other");
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("Addresses").push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            constants.HideProgressDialog();
                            Navigation.findNavController(view).navigate(R.id.action_addAddressFragment_to_addressesFragment);
                        }
                    });


                }
            }
        });
        return view;
    }

    private void setLocationDetails() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            getCompleteAddressString(location.getLatitude(), location.getLongitude());
                        }
                    }
                }
        );
    }

    private void init(View view) {
        nameLayout=view.findViewById(R.id.name_layout);
        locationLayout=view.findViewById(R.id.location_layout);
        cityLayout=view.findViewById(R.id.city_layout);
        pinLayout=view.findViewById(R.id.pin_layout);
        stateLayout=view.findViewById(R.id.state_layout);
        floor=view.findViewById(R.id.floor);
        towerBlock=view.findViewById(R.id.block);
        name=view.findViewById(R.id.name);
        location=view.findViewById(R.id.location);
        houseNumber=view.findViewById(R.id.house_number);
        city=view.findViewById(R.id.city);
        pinCode=view.findViewById(R.id.pin);
        landmark=view.findViewById(R.id.landmark);
        home=view.findViewById(R.id.home);
        work=view.findViewById(R.id.work);
        other=view.findViewById(R.id.other);
        state=view.findViewById(R.id.state);

    }
    private boolean isValid(){
        if (name.getText().toString().isEmpty()){
            nameLayout.setError("Enter name");
            return false;
        }
        else
            nameLayout.setError(null);
        if (pinCode.getText().toString().isEmpty()){
            pinLayout.setError("Enter Pin code");
        }
        else
            pinLayout.setError(null);
        if (state.getText().toString().isEmpty()){
            stateLayout.setError("Enter state name");
            return false;
        }
        else
            stateLayout.setError(null);

        if (location.getText().toString().isEmpty()){
            locationLayout.setError("Enter location");
            return false;
        }
        else
            locationLayout.setError(null);
        if (city.getText().toString().isEmpty()){
            cityLayout.setError("Enter city name");
            return false;
        }
        else
            cityLayout.setError(null);


        return true;
    }
    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==    	PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == 	PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
                Log.v("tag","permission granted");
                setLocationDetails();
            }
        }
        Log.v("tag","permimdkjkf f gkmg kg kn");
    }
    private void getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String cityName = addresses.get(0).getLocality();
        String stateName = addresses.get(0).getAdminArea();
        String countryName = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        if (cityName!=null)
        city.setText(cityName);
        if (stateName!=null)
        state.setText(stateName);
        pinCode.setText(postalCode);
        Log.v("tag","  "+"  "+postalCode);

        
        
        /*Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                HashMap<String, String> map=new HashMap<>();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    //map.put()
                }
                strAdd = strReturnedAddress.toString();

                //Log.v("tag", "Address--> "+strReturnedAddress.toString());
                Log.v("tag", "Address is--> "+addresses);
            } else {
                Log.v("tag", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("tag", "Canont get Address!");
        }*/

    }

    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }



}