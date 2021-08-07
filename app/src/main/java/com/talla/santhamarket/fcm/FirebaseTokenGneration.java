package com.talla.santhamarket.fcm;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.HomeActivity;
import com.talla.santhamarket.activities.OtpActivity;
import com.talla.santhamarket.models.TokenModel;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.SharedEncryptUtills;
import com.talla.santhamarket.utills.StaticUtills;

public class FirebaseTokenGneration extends FirebaseMessagingService
{
    FirebaseFirestore firebaseFirestore;
    private SharedEncryptUtills sharedEncryptUtills;
    private Context mContext;

    public FirebaseTokenGneration() {
    }

    public FirebaseTokenGneration(Context context)
    {
        firebaseFirestore= FirebaseFirestore.getInstance();
        sharedEncryptUtills=SharedEncryptUtills.getInstance(context);
        this.mContext=context;
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String fcm_token= sharedEncryptUtills.getData(SharedEncryptUtills.FCM_TOKEN);
        FirebaseMessaging.getInstance().subscribeToTopic("SanthaMarket");
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (!fcm_token.equalsIgnoreCase(refreshedToken))
        {
            // Get new Instance ID token
            if(firebaseUser!=null){
                updateToken(refreshedToken);
            }
        }
    }

    public void updateToken(String refreshToken){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        TokenModel tokenModel=new TokenModel();
        tokenModel.setUserToken(refreshToken);
        String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        tokenModel.setDeviceId(android_id);
        tokenModel.setTokenUpdated_date(StaticUtills.getTimeStamp());

        firebaseFirestore.collection(mContext.getResources().getString(R.string.FCM_TOKENS)).document(firebaseUser.getUid()).set(tokenModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("FirebaseTokenGneration ","Success");
                sharedEncryptUtills.saveData(SharedEncryptUtills.FCM_TOKEN,refreshToken);
            }
        });
    }



}
