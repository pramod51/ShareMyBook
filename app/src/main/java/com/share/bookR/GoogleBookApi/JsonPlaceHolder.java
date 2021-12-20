package com.share.bookR.GoogleBookApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolder {
    @GET("books/v1/volumes")
    Call<IsbnDetails> getDetails(@Query("q") String isbnNumber,
                                 @Query("key") String authKey);

    @GET("books/v1/volumes?q=isbn:9780671741907&key=AIzaSyCoGZjr05HCw6_6VMnjzgj3ev1M24XN-tM")
    Call<IsbnDetails> getAll();



}
