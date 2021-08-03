package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.CategoriesAdapter;
import com.talla.santhamarket.databinding.ActivityCategoriesBinding;
import com.talla.santhamarket.databinding.CustomProgressDialogBinding;
import com.talla.santhamarket.models.CartModel;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.RegisterModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {
    private ActivityCategoriesBinding binding;
    private Dialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private RegisterModel registerModel;
    private CategoriesAdapter categoriesAdapter;
    private List<String> catList = new ArrayList<>();
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private Map<String, Object> categoriesList;
    private static final String TAG = "CategoriesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialogIninit();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            registerModel = (RegisterModel) intent.getExtras().getSerializable(getString(R.string.intent_reg_model_key));
            categoriesList=registerModel.getCategoriesList();
            getCategoriesData();
        }
        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void getCategoriesData() {
        if (categoriesList.size() > 0) {
            binding.noCategoriesFound.setVisibility(View.GONE);
            for (Map.Entry<String, Object> set : categoriesList.entrySet()) {
                Log.d(TAG, "getCategoriesData: "+set.getValue());
                firebaseFirestore.collection(getString(R.string.CATEGORIES)).document(set.getValue().toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            progressDialog.dismiss();
                            binding.noCategoriesFound.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onEvent: " + error.getMessage());
                        } else {
                            if (value != null && value.exists()) {
                                Log.d(TAG, "Current data: " + value.getData());
                                CategoryModel categoryModel = value.toObject(CategoryModel.class);
                                categoryModel.setShopId(registerModel.getShop_id());
                                categoryModelList.add(categoryModel);
                            }
                        }
                        //execute here
                        Log.d(TAG, "getCategoriesData: "+categoryModelList.size());
                        if (categoryModelList.size() > 0) {
                            progressDialog.dismiss();
                            binding.noCategoriesFound.setVisibility(View.GONE);
                            binding.categoriesRCV.setLayoutManager(new GridLayoutManager(CategoriesActivity.this, 2));
                            categoriesAdapter = new CategoriesAdapter(categoryModelList, CategoriesActivity.this);
                            binding.categoriesRCV.setAdapter(categoriesAdapter);
                        } else {
                            progressDialog.dismiss();
                            binding.noCategoriesFound.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

        } else {
            progressDialog.dismiss();
            binding.noCategoriesFound.setVisibility(View.VISIBLE);
        }
    }

    public void dialogIninit() {
        progressDialog = new Dialog(this);
        CustomProgressDialogBinding customProgressDialogBinding = CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void showDialog(String title, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        binding.getRoot().getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
        });
        alertDialogBuilder.show();
    }

}