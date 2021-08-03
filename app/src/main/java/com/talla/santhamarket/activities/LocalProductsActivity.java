package com.talla.santhamarket.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.LocalProductsAdapter;
import com.talla.santhamarket.databinding.ActivityLocalProductsBinding;
import com.talla.santhamarket.databinding.CustomProgressDialogBinding;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class LocalProductsActivity extends AppCompatActivity {
    private ActivityLocalProductsBinding binding;
    private LocalProductsAdapter localProductsAdapter;
    private Dialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration localProdListner;
    private CategoryModel categoryModel;
    private List<ProductModel> productModelList = new ArrayList<>();
    private static final String TAG = "LocalProductsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocalProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        dialogIninit();
        firebaseFirestore=FirebaseFirestore.getInstance();
        Intent intent=getIntent();
        if (intent!=null)
        {
            categoryModel= (CategoryModel) intent.getExtras().getSerializable(getString(R.string.intent_category_shopId));
            if (categoryModel!=null)
            {
                getLocalProducts();
            }else {
                showSnackBar("No Products Available");
            }
        }

        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

    public void dialogIninit() {
        progressDialog = new Dialog(this);
        CustomProgressDialogBinding customProgressDialogBinding = CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        binding.searchView.setMenuItem(item);
        binding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                if (localProductsAdapter != null) {
                    localProductsAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        return true;
    }


    private void getLocalProducts() {
        progressDialog.show();
        if (!productModelList.isEmpty()) {
            productModelList.clear();
        }
        localProdListner = firebaseFirestore.collection(getString(R.string.PRODUCTS)).whereEqualTo("category_id", categoryModel.getId()).whereEqualTo("sellerId", categoryModel.getShopId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    progressDialog.dismiss();
                    showDialog("Error Occured !", error.getMessage());
                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                ProductModel productModel = dc.getDocument().toObject(ProductModel.class);
                                productModelList.add(productModel);
                                Log.d(TAG, "Product data added to list: " + productModel.toString());
                                break;
                            case MODIFIED:
                                ProductModel productModelModified = dc.getDocument().toObject(ProductModel.class);
                                Log.d(TAG, "Product data modified to list: " + productModelModified.toString());
                                for (int i = 0; i < productModelList.size(); i++) {
                                    if (productModelList.get(i).getProduct_id().equalsIgnoreCase(productModelModified.getProduct_id())) {
                                        productModelList.remove(i);
                                        productModelList.add(i, productModelModified);
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                ProductModel productModelRemoved = dc.getDocument().toObject(ProductModel.class);
                                Log.d(TAG, "Product data removed to list: " + productModelRemoved.toString());
                                for (int i = 0; i < productModelList.size(); i++) {
                                    if (productModelList.get(i).getProduct_id().equalsIgnoreCase(productModelRemoved.getProduct_id())) {
                                        productModelList.remove(i);
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                }
                localProductsAdapter = new LocalProductsAdapter(productModelList, LocalProductsActivity.this);
                binding.localRCV.setAdapter(localProductsAdapter);
                if (productModelList.isEmpty()) {
                    binding.noProductsAvail.setVisibility(View.VISIBLE);
                } else {
                    binding.localRCV.setVisibility(View.VISIBLE);
                    binding.noProductsAvail.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        localProdListner.remove();
    }
}