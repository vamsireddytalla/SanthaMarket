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

        getFavData();
    }

    private void getFavData() {

        firestore.collection("FAVOURITES").whereEqualTo("userId", UID).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                    Toast.makeText(FavouriteActivity.this, "error", Toast.LENGTH_SHORT).show();
                } else {
                    favouriteModelList.clear();
                    productModelList.clear();
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                FavouriteModel favModel = dc.getDocument().toObject(FavouriteModel.class);
                                Log.d(TAG, "Fav MODEl Added :" + favModel.toString());
                                favouriteModelList.add(favModel);
                                break;
                            case MODIFIED:
                                FavouriteModel favModelModified = dc.getDocument().toObject(FavouriteModel.class);
                                Log.d(TAG, "Fav MODEl Modified :" + favModelModified.toString());
                                break;
                            case REMOVED:
                                FavouriteModel favModelRemoved = dc.getDocument().toObject(FavouriteModel.class);
                                Log.d(TAG, "Fav MODEl Removed :" + favModelRemoved.toString());
                                for (int i = 0; i < favouriteModelList.size(); i++) {
                                    if (favModelRemoved.getFavId().equalsIgnoreCase(favouriteModelList.get(i).getFavId())) {
                                        favouriteModelList.remove(i);
                                        if (favModelRemoved.getFavId().equalsIgnoreCase(productModelList.get(i).getExtra_filed())) {
                                            productModelList.remove(i);
                                            favouriteAdapter.setFavouriteModelList(productModelList);
                                        }
                                    }
                                }
                                break;
                        }
                    }
                    getProdBasedOnProdId();
                }
            }
        });
    }

    private void getProdBasedOnProdId() {

        alertDialog.show();
        for (int i = 0; i < favouriteModelList.size(); i++) {
            Log.d(TAG, "Fav Item Id in getProdBasedOnProdId()--->" + favouriteModelList.get(i));

            final int finalI = i;
            firestore.collection("PRODUCTS").document(favouriteModelList.get(i).getProduct_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            ProductModel productModel = document.toObject(ProductModel.class);
                            if (productModel != null) {
                                productModel.setExtra_filed(favouriteModelList.get(finalI).getFavId());
                                productModelList.add(productModel);
                                favouriteAdapter.setFavouriteModelList(productModelList);
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        if (productModelList.size() == 0) {
            binding.noItemFound.setVisibility(View.VISIBLE);
        } else {
            binding.noItemFound.setVisibility(View.GONE);
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
            firestore.collection("FAVOURITES").document(productModelList.get(pos).getExtra_filed()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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