package com.talla.santhamarket.serverCalls;


import com.google.gson.JsonObject;
import com.squareup.okhttp.ResponseBody;
import com.talla.santhamarket.models.FcmResponse;
import com.talla.santhamarket.models.NotificationModel;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface
{

    @GET("tagsList.txt")
    Call<JsonObject> getTagsList();


    @Headers({"Content-Type:application/json", "Authorization:key=AAAAVRqH3N4:APA91bH-t6i6Hu6GEbZGdopNNND48OhJEhmKewI4bMywNfuJLaMbfRU1aJ9rihyf2gnmG0iqXmYl9veJ_skwlo6QxnCQi_KbLfwWpvZ_ZgmZnXMKqx3YGJn0-BDcWsl20apJDW6KQCg7"})
    @POST("fcm/send")
    Call<FcmResponse> sendNotifcation(@Body NotificationModel body);

}
