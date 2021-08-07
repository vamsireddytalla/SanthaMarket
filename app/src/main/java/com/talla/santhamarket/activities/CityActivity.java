package com.talla.santhamarket.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.CityAdapter;
import com.talla.santhamarket.databinding.ActivityCityBinding;
import com.talla.santhamarket.databinding.CustomProgressDialogBinding;
import com.talla.santhamarket.models.CityModel;

import java.util.ArrayList;
import java.util.List;

public class CityActivity extends AppCompatActivity {
    private ActivityCityBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private Dialog progressDialog;
    private ListenerRegistration citiesListner;
    private CityAdapter cityAdapter;
    private List<CityModel> cityModelList = new ArrayList<>();
    private static final String TAG = "CityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        dialogIninit();
        cityAdapter = new CityAdapter(cityModelList, this);
        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getCitiesData();

    }

    private void getCitiesData() {
        if (cityModelList != null) {
            cityModelList.clear();
        }
        progressDialog.show();
        citiesListner = firebaseFirestore.collection(getString(R.string.CITIES)).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            CityModel cityModel = dc.getDocument().toObject(CityModel.class);
                            cityModelList.add(cityModel);
                            Log.d(TAG, "City data added to list: " + cityModel.toString());
                            break;
                        case MODIFIED:
                            CityModel cityModelModified = dc.getDocument().toObject(CityModel.class);
                            Log.d(TAG, "City data modified to list: " + cityModelModified.toString());
                            for (int i = 0; i < cityModelList.size(); i++) {
                                if (cityModelList.get(i).getCityId().equals(cityModelModified.getCityId())) {
                                    cityModelList.remove(i);
                                    cityModelList.add(i, cityModelModified);
                                    break;
                                }
                            }
                            break;
                        case REMOVED:
                            CityModel cityModelRemoved = dc.getDocument().toObject(CityModel.class);
                            Log.d(TAG, "City data removed to list: " + cityModelRemoved.toString());
                            for (int i = 0; i < cityModelList.size(); i++) {
                                if (cityModelList.get(i).getCityId().equals(cityModelRemoved.getCityId())) {
                                    cityModelList.remove(i);
                                    break;
                                }
                            }
                            break;
                    }
                }
                cityAdapter = new CityAdapter(cityModelList, CityActivity.this);
                binding.citiesRCV.setAdapter(cityAdapter);
                if (cityModelList.isEmpty()) {
                    binding.noProductsAvail.setVisibility(View.VISIBLE);
                } else {
                    binding.noProductsAvail.setVisibility(View.GONE);
                    binding.citiesRCV.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();

            }
        });
    }

    public void dialogIninit() {
        progressDialog = new Dialog(this);
        CustomProgressDialogBinding customProgressDialogBinding = CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

}
