package com.talla.santhamarket.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.talla.santhamarket.R;
import com.talla.santhamarket.models.CartModel;
import com.talla.santhamarket.models.ProductModel;

import java.util.List;

public class BackGroundService extends Service {
    private Context context;
    public static final String TAG = "DELETE_CART_ITEMS";
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "OnStartCommant Called");
        List<CartModel> cartModelList = null;
        if (intent != null && intent.getExtras() != null) {
            cartModelList = (List<CartModel>) intent.getSerializableExtra("DELETE_CART_ITEMS");
            Log.d(TAG, "Intent Received " + cartModelList.toString());
            final List<CartModel> cartModelList1 = cartModelList;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < cartModelList1.size(); i++) {
                        DocumentReference docref = firebaseFirestore.collection(context.getResources().getString(R.string.CART_ITEMS)).document(cartModelList1.get(i).getCart_doc_id());
                        int finalI = i;
                        docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Deleted Cart Item " + finalI);
                            }
                        });
                    }
                    Log.d(TAG, "Background Service  Stop Self Called");
                    stopSelf();
                }
            }).start();
        } else {
            Log.d(TAG, "Background Service  Intent is Empty");
            stopSelf();
        }

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "OnDestroy called");
    }
}
