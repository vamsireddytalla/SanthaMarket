package com.talla.santhamarket.serverCalls;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface
{

    @GET("tagsList.txt")
    Call<JsonObject> getTagsList();

//
//    @Headers({"Content-Type:application/json", "Authorization:key=xxxxxxxxxxxxxxxxxxx"})
//    @POST("fcm/send")
//    Call<MyResponse> sendNotifcation(@Body NotificationSender body);

}
