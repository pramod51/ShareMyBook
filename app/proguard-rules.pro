-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

-keep class com.share.bookR.GoogleBookApi.** {*; }
-keep class com.share.bookR.RazorpaySubscription.** {*; }




-keep class com.share.bookR**.data_models.** {*; }

-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}

-optimizations !method/inlining/*

-keepclasseswithmembers class * {
  public void onPayment*(...);
}