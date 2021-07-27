package com.talla.santhamarket.fcm;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.OtpActivity;
import com.talla.santhamarket.models.TokenModel;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

public class FirebaseTokenGneration extends FirebaseMessagingService
{
    FirebaseFirestore firebaseFirestore;
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        firebaseFirestore= FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        if(firebaseUser!=null){
            updateToken(getApplicationContext(),refreshToken);
        }
    }

    public void updateToken(Context context,String refreshToken){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        TokenModel tokenModel=new TokenModel();
        tokenModel.setUserToken(refreshToken);
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        tokenModel.setDeviceId(android_id);
        tokenModel.setTokenUpdated_date(StaticUtills.getTimeStamp());
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseFirestore.collection(context.getResources().getString(R.string.FCM_TOKENS)).document(firebaseUser.getUid()).set(tokenModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("FirebaseTokenGneration ","Success");
            }
        });
    }

}
