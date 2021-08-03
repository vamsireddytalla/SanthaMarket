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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.talla.santhamarket.R;
import com.talla.santhamarket.models.CartModel;
import com.talla.santhamarket.models.FinalPayTransferModel;
import com.talla.santhamarket.models.ProductModel;

import java.util.List;

public class BackGroundService extends Service {
    private Context context;
    public static final String TAG = "DELETE_CART_ITEMS";
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private FinalPayTransferModel finalModel = null;

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

        if (intent != null && intent.getExtras() != null) {
            finalModel = (FinalPayTransferModel) intent.getSerializableExtra("DELETE_CART_ITEMS");
            Log.d(TAG, "Intent Received In BACKGROUND SERVICE" + finalModel.toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<CartModel> cartModelList = finalModel.getCartModelList();
                    List<ProductModel> productModelList = finalModel.getProductModelsList();
                    for (int i = 0; i < cartModelList.size(); i++) {
                        DocumentReference docref = firebaseFirestore.collection(context.getResources().getString(R.string.CART_ITEMS)).document(cartModelList.get(i).getCart_doc_id());
                        int finalI = i;
                        if (cartModelList.size()==productModelList.size())
                        {
                            updateProductStatus(cartModelList.get(i).getCart_product_id(), productModelList.get(i).getTemp_qty());
                        }
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


    private void updateProductStatus(String prodDocId,int quantity) {
        DocumentReference docRef = firebaseFirestore.collection(context.getResources().getString(R.string.PRODUCTS)).document(prodDocId);

        docRef.update("totalStock", FieldValue.increment(-quantity));
        docRef.update("selled_items", FieldValue.increment(quantity));
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
