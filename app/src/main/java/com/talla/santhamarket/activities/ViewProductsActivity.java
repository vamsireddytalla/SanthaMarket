package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.ProductAdapter;
import com.talla.santhamarket.databinding.ActivityViewProductsBinding;
import com.talla.santhamarket.databinding.CheckInternetBinding;
import com.talla.santhamarket.databinding.DashCatLayoutBinding;
import com.talla.santhamarket.databinding.RangeSeekLayoutBinding;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.utills.CheckInternet;
import com.talla.santhamarket.utills.CheckUtill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewProductsActivity extends AppCompatActivity {
    private ActivityViewProductsBinding binding;
    private CheckInternetBinding checkInternetBinding;
    private Long categoryId;
    private FirebaseFirestore firebaseFirestore;
    private ProductAdapter productAdapter;
    private List<ProductModel> productModelList = new ArrayList<>();
    private static final String TAG = "VIEW_PRODUCT_ACTIVITY";
    private ProgressDialog progressDialog;
    private int sortItemPos = -1;
    private Dialog dialog;
    private int priceRangeStart = 10, priceRangeEnd = 50000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkInternetBinding = CheckInternetBinding.bind(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            categoryId = bundle.getLong("categoryId");
            Log.d(TAG, "CategoryId from Intent :" + categoryId);
        } else {
            finish();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching Data");
        progressDialog.setMessage("Please Wait . . .");
        progressDialog.setCancelable(false);
        //firebase instance
        firebaseFirestore = FirebaseFirestore.getInstance();


        binding.filterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog("Hello");
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkInternetAndMakeServerCall();
        checkInternetBinding.tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternetAndMakeServerCall();
            }
        });


    }

    private void checkInternetAndMakeServerCall() {
        if (CheckInternet.checkInternet(this)) {
            binding.includeLayoutViewProducts.setVisibility(View.GONE);
            binding.layoutOne.setVisibility(View.VISIBLE);
            getProductsBasedOnCategory();
        } else {
            binding.layoutOne.setVisibility(View.GONE);
            binding.includeLayoutViewProducts.setVisibility(View.VISIBLE);
        }
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
                if (productAdapter != null) {
                    productAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        return true;
    }

    private void getProductsBasedOnCategory() {
        progressDialog.show();
        if (productModelList != null) {
            productModelList.clear();
        }
        firebaseFirestore.collection("PRODUCTS").whereEqualTo("category_id", categoryId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ProductModel productModel = document.toObject(ProductModel.class);
                        Log.d(TAG, "getProductsBasedOnCategory---> " + productModel.toString());
                        productModelList.add(productModel);
                    }
                    binding.productRCV.setHasFixedSize(true);
                    binding.productRCV.setLayoutManager(new GridLayoutManager(ViewProductsActivity.this, 2));
                    productAdapter = new ProductAdapter(ViewProductsActivity.this, productModelList);
                    binding.productRCV.setAdapter(productAdapter);
                    progressDialog.dismiss();
                    if (productModelList.size() == 0) {
                        Log.d(TAG, "No Related Products Available");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void priceRangeProducts() {
        progressDialog.show();
        if (productModelList != null) {
            productModelList.clear();
        }
        firebaseFirestore.collection("PRODUCTS").whereEqualTo("category_id", categoryId)
                .whereGreaterThanOrEqualTo("product_price", priceRangeStart).whereLessThanOrEqualTo("product_price", priceRangeEnd).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ProductModel productModel = document.toObject(ProductModel.class);
                        Log.d(TAG, "priceRangeProducts---> " + productModel.toString());
                        productModelList.add(productModel);
                    }
                    binding.productRCV.setHasFixedSize(true);
                    binding.productRCV.setLayoutManager(new GridLayoutManager(ViewProductsActivity.this, 2));
                    productAdapter = new ProductAdapter(ViewProductsActivity.this, productModelList);
                    binding.productRCV.setAdapter(productAdapter);
                    progressDialog.dismiss();
                    if (productModelList.size() == 0) {
                        Log.d(TAG, "No Related Products Available");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void GetProductDataChangeListner() {
        productAdapter = new ProductAdapter(ViewProductsActivity.this, productModelList);
        firebaseFirestore.collection("PRODUCTS").whereEqualTo("category_id", categoryId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        ProductModel productModel = dc.getDocument().toObject(ProductModel.class);
                        Log.d(TAG, "PRoduct MODEl :" + productModel.toString());
                        productAdapter.setProductModel(productModel);
                    }
                    binding.productRCV.setHasFixedSize(true);
                    binding.productRCV.setLayoutManager(new GridLayoutManager(ViewProductsActivity.this, 2));
                    binding.productRCV.setAdapter(productAdapter);
                }
            }
        });
    }

    public void sortItems(View view) {
        String[] itemList = new String[]{"Price---Low to high", "Price---High to Low"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_DayNight_Dialog_MinWidth);
        builder.setTitle("SORT BY");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                Collections.sort(productModelList, new Comparator<ProductModel>() {
                    public int compare(ProductModel obj1, ProductModel obj2) {
                        // ## Ascending order
                        Log.d(TAG, "Checked Position = " + sortItemPos);
                        if (sortItemPos == 0) {
                            // return obj1.firstName.compareToIgnoreCase(obj2.firstName); // To compare string values
                            Log.d(TAG, "Ascending_toDesc");
                            return Integer.valueOf(String.valueOf(obj1.getProduct_price())).compareTo(Integer.valueOf(String.valueOf(obj2.getProduct_price()))); // To compare integer values
                        }
                        // ## Descending order
                        else {
                            // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                            Log.d(TAG, "Descending to Ascending");
                            return Integer.valueOf(String.valueOf(obj2.getProduct_price())).compareTo(Integer.valueOf(String.valueOf(obj1.getProduct_price()))); // To compare integer values
                        }

                    }
                });
                dialogInterface.dismiss();
                productAdapter.notifyDataSetChanged();
                Log.d(TAG, "AFTE FILTER" + productModelList.toString());
            }
        }).setSingleChoiceItems(itemList, sortItemPos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "Sort Item Position :" + i);
                sortItemPos = i;
            }
        });
        builder.show();
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void showFilterDialog(String shownMesssage) {
        final RangeSeekLayoutBinding rangeSeekLayoutBinding;
        dialog = new Dialog(this, R.style.Theme_MaterialComponents_DayNight_Dialog_MinWidth);
        dialog.setCancelable(true);
        rangeSeekLayoutBinding = RangeSeekLayoutBinding.inflate(getLayoutInflater());
        dialog.setContentView(rangeSeekLayoutBinding.getRoot());
        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(width, height);
        dialog.show();
        rangeSeekLayoutBinding.priceSelected.setText(getResources().getString(R.string.Rs) + CheckUtill.FormatCost(priceRangeStart) + "  -  " + getResources().getString(R.string.Rs) + CheckUtill.FormatCost(priceRangeEnd));

        rangeSeekLayoutBinding.rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> floatList = slider.getValues();
                Log.d(TAG, "OnChangeListner Ranage SLider" + floatList.toString());
                priceRangeStart = Math.round(floatList.get(0));
                priceRangeEnd = Math.round(floatList.get(1));
                rangeSeekLayoutBinding.priceSelected.setText(getResources().getString(R.string.Rs) + CheckUtill.FormatCost(priceRangeStart) + "  -  " + getResources().getString(R.string.Rs) + CheckUtill.FormatCost(priceRangeEnd));
            }
        });

        rangeSeekLayoutBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        rangeSeekLayoutBinding.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                priceRangeProducts();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (binding.searchView.isSearchOpen()) {
            binding.searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}