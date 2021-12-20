package com.share.bookR;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.format.DateFormat;

import java.util.Date;
import java.util.UUID;

public class Constants {
    public static final String SHARED_PREFS="shared_prefs";
    public static final String USER_ID="outer_key";
    public static final String BOOK_ID="inner_key";
    public static final String TITLE="title";
    public static final String URL="url";
    public static final String PRICE="price";
    public static final String PUSH_NOTIFICATION="push_notification";
    public static final String ENABLE_ALL="enable_all";
    public static final String ORDER_AND_PURCHASE="order_and_purchase";
    public static final String FIRST_OPEN="first_open";
    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 10;
    public static final String PHONE="phone";
    public static final String ISBN = "isbn";
    public static final String LIBRARY ="Library" ;
    public static final String BOOK_INSTANCE ="bookInstance" ;
    public static final String BEGINNER ="Beginner" ;
    public static final String READER ="Reader" ;
    public static final String BOOK_WARM ="Book Warm" ;
    public static final String SUBSCRIPTION_ID ="subscription_id" ;
    public static final String NEW_USER ="new_user" ;
    public static final String NAME ="name" ;
    public static final String PROFILE ="profile" ;

    public static final String USER_NAME ="user_name" ;



    public static final String ACTIVE_SUBSCRIPTION ="active_subscription" ;
    public static final String KEY ="key" ;








    ProgressDialog progressDialog;
    public void ProgressDialogShow(Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void setProgressDialogMessage(String message){
        progressDialog.setMessage(message);
    }
    public void HideProgressDialog(){
        if (progressDialog!=null)
        progressDialog.dismiss();
    }
    public String getTodayDate(){

        Date d = new Date();
        return DateFormat.format("dd/MM/yyyy", d.getTime()).toString();
    }
    public String getIncreasingNode(){

        Date d = new Date();
        return DateFormat.format("yyyyMMdd", d.getTime()).toString();
    }

    public String getRandomUniqueId(){
        return UUID.randomUUID().toString();
    }
}
