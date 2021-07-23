package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.FavouriteAdapter;
import com.talla.santhamarket.databinding.ActivityFavouriteBinding;
import com.talla.santhamarket.databinding.CustomProgressDialogBinding;
import com.talla.santhamarket.interfaces.ToggleItemListner;
import com.talla.santhamarket.models.FavouriteModel;
import com.talla.santhamarket.models.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity implements ToggleItemListner {
    private ActivityFavouriteBinding binding;
    private String UID;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private CustomProgressDialogBinding customProgressDialogBinding;
    private androidx.appcompat.app.AlertDialog alertDialog;
    private FavouriteAdapter favouriteAdapter;
    List<ProductModel> productModelList = new ArrayList<>();
    List<FavouriteModel> favouriteModelList = new ArrayList<>();
    private boolean isFirstTime = true;
    private static final String TAG = "FavouriteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialogIninit();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getCurrentUser().getUid();
        documentReference = firestore.collection("FAVOURITES").document(UID);
        favouriteAdapter = new FavouriteAdapter(this, productModelList, this);
        binding.favRCV.setAdapter(favouriteAdapter);

        //get ALl Fav Data Based on UID
        getFavData();
    }

    private void getFavData() {
        favouriteModelList.clear();
        productModelList.clear();
        firestore.collection("FAVOURITES").whereEqualTo("userId", UID).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                    Toast.makeText(FavouriteActivity.this, "error", Toast.LENGTH_SHORT).show();
                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                FavouriteModel favModel = dc.getDocument().toObject(FavouriteModel.class);
                                Log.d(TAG, "Get Fav Data API Method In Side Loop Item Added");
                                favouriteModelList.add(favModel);
                                break;
                            case MODIFIED:
                                FavouriteModel favModelModified = dc.getDocument().toObject(FavouriteModel.class);
                                Log.d(TAG, "Get Fav Data API Method In Side Loop Item Modified");
                                break;
                            case REMOVED:
                                FavouriteModel favModelRemoved = dc.getDocument().toObject(FavouriteModel.class);
                                Log.d(TAG, "Get Fav Data Removed Case");
                                for (int i = 0; i < favouriteModelList.size(); i++) {
                                    if (favModelRemoved.getFavId().equalsIgnoreCase(favouriteModelList.get(i).getFavId())) {
                                        favouriteModelList.remove(i);
                                       for(int j = 0; j < productModelList.size(); j++)
                                       {
                                           if (favModelRemoved.getFavId().equalsIgnoreCase(productModelList.get(j).getExtra_field())) {
                                               productModelList.remove(j);
                                               Log.d(TAG, "After Product Model Removed List Updated " + j + " Remaining Products List For Recycle Items " + productModelList.size());
                                               favouriteAdapter.setFavouriteModelList(productModelList);
                                               if (productModelList.isEmpty())
                                                   binding.noItemFound.setVisibility(View.VISIBLE);
                                           }
                                       }
                                    }
                                }
                                break;
                        }
                    }
                    Log.d(TAG, "Get Fav Data API Method Out Side Loop");
                    if (isFirstTime) {
                        isFirstTime = false;
                        getProdBasedOnProdId();
                    }
                }
            }
        });
    }

    private void getProdBasedOnProdId() {

        alertDialog.show();
        if (!favouriteModelList.isEmpty()) {
            for (int i = 0; i < favouriteModelList.size(); i++) {
                Log.d(TAG, " Fav Item Id in getProdBasedOnProdId()---> " + favouriteModelList.get(i).getFavId());

                final int finalI = i;
                firestore.collection(getString(R.string.PRODUCTS)).document(favouriteModelList.get(i).getProduct_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
//                                Log.d(TAG, " getProdBasedOnProdId()---> DocumentSnapshot data: " + document.getData());
                                ProductModel productModel = document.toObject(ProductModel.class);
                                if (productModel != null) {
                                    productModel.setExtra_field(favouriteModelList.get(finalI).getFavId());
                                    productModelList.add(productModel);
                                    favouriteAdapter.setFavouriteModelList(productModelList);
                                } else {
                                    favouriteAdapter.setFavouriteModelList(productModelList);
                                    Log.d(TAG, " getProdBasedOnProdId() no items Found ");
                                }
                            } else {
                                Log.d(TAG, "No such document found with above Product ID " + favouriteModelList.get(finalI).getFavId());
                            }

                        } else {
                            Log.d(TAG, " getProdBasedOnProdId() failed with ", task.getException());
                        }
                    }
                });
            }
            binding.noItemFound.setVisibility(View.GONE);
        } else {
            binding.noItemFound.setVisibility(View.VISIBLE);
        }

        alertDialog.dismiss();

    }

    public void dialogIninit() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_Light_Dialog);
        customProgressDialogBinding = CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        builder.setView(customProgressDialogBinding.getRoot());
        builder.setBackground(this.getResources().getDrawable(R.drawable.bg_round));
        builder.setCancelable(false);
        alertDialog = builder.create();
    }

    @Override
    public void toggleChangeListner(final int pos, int type) {
        if (type == 0) {
            alertDialog.show();
            firestore.collection("FAVOURITES").document(productModelList.get(pos).getExtra_field()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Sucess");
                        alertDialog.dismiss();
                    }
                }
            });
        }
    }
}