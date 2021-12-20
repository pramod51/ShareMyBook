package com.share.bookR.RazorpaySubscription;

import android.util.Base64;

import com.share.bookR.GoogleBookApi.IsbnDetails;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderRazorpay {


    @Headers("Authorization:Basic cnpwX3Rlc3RfN2c1Z0dwSWg2UjVodTE6OHRQdk8xb1M0MUhBU2I0V2hPR2ZRQWs3")
    @POST("v1/subscriptions")
    Call<CreateSubscription> createSubscription(@Query("plan_id") String planId,@Query("total_count") int totalCount);


    @Headers("Authorization:Basic cnpwX3Rlc3RfN2c1Z0dwSWg2UjVodTE6OHRQdk8xb1M0MUhBU2I0V2hPR2ZRQWs3")
    @GET("v1/subscriptions/{sub_id}")
    Call<SubscriptionDetails> getSubscriptionDetails(@Path("sub_id") String subscriptionId);

    @Headers("Authorization:Basic cnpwX3Rlc3RfN2c1Z0dwSWg2UjVodTE6OHRQdk8xb1M0MUhBU2I0V2hPR2ZRQWs3")
    @POST("v1/subscriptions/{sub_id}/cancel")
    Call<CancelSubscription> cancelSubscription(@Path("sub_id") String subscriptionId);







}
