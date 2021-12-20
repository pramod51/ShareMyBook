package com.share.bookR.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.share.bookR.Constants;
import com.share.bookR.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import org.json.JSONObject;

import static com.share.bookR.Constants.KEY;


public class RazorpayActivity extends Activity implements PaymentResultListener {

    private String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Checkout.preload(getApplicationContext());
        startPayment(getIntent().getStringExtra("subscriptionId"));

    }
    public void startPayment(String subscriptionId) {

        FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Checkout checkout = new Checkout();
                checkout.setKeyID("rzp_test_7g5gGpIh6R5hu1");
                /**
                 * Instantiate Checkout
                 */
                /**
                 * Set your logo here
                 */
                checkout.setImage(R.drawable.book);
                /**
                 * Reference to current activity
                 */

                /**
                 * Pass your payment options to the Razorpay Checkout as a JSONObject
                 */

                try {
                    JSONObject options = new JSONObject();

                    options.put("name", snapshot.child("name").getValue(String.class));
                    options.put("description", snapshot.child("description").getValue(String.class));
                    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                    //options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
                    options.put("theme.color", "#3399cc");
                    options.put("currency", "INR");
                    //options.put("amount", amount);//pass amount in currency subunits
                    options.put("prefill.email", snapshot.child("email").getValue(String.class));
                    options.put("prefill.contact",snapshot.child("phone").getValue(String.class).replace("+91",""));
                    options.put("subscription_id",subscriptionId);
                    JSONObject retryObj = new JSONObject();
                    retryObj.put("enabled", true);
                    retryObj.put("max_count", 4);
                    options.put("retry", retryObj);
                    checkout.open(RazorpayActivity.this, options);

                } catch(Exception e) {
                    Log.e("tag", "Error in starting Razorpay Checkout", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this,"Payment Successful",Toast.LENGTH_LONG).show();
        onBackPressed();

        //Navigation.findNavController()
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this,"Payment Failed",Toast.LENGTH_LONG).show();
        Log.v("tag",s);
        onBackPressed();
    }
    public interface GoBack{
        public void goCall();
    }
}