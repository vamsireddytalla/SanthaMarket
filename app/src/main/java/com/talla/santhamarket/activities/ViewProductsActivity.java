package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private String categoryId;
    private FirebaseFirestore firebaseFirestore;
    private ProductAdapter productAdapter;
    private List<ProductModel> productModelList = new ArrayList<>();
    private Dialog progressDialog;
    private int sortItemPos = -1;
    private Dialog dialog;
    private int priceRangeStart = 10, priceRangeEnd = 5000;
    private static final String TAG = "VIEW_PRODUCT_ACTIVITY";

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
            categoryId = (String) bundle.get(getString(R.string.intent_cat_id));
            Log.d(TAG, "CategoryId from Intent :" + categoryId);
        } else {
            finish();
        }
        dialogIninit();
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
            binding.noProductsAvail.setVisibility(View.GONE);
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
        firebaseFirestore.collection(getString(R.string.PRODUCTS)).whereEqualTo("category_id", categoryId)
                .whereEqualTo("itemTypeModel.local", false).orderBy("product_date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    showDialog(getString(R.string.error_occured), error.getMessage());
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
                    binding.productRCV.setHasFixedSize(true);
                    binding.productRCV.setLayoutManager(new GridLayoutManager(ViewProductsActivity.this, 2));
                    productAdapter = new ProductAdapter(ViewProductsActivity.this, productModelList);
                    binding.productRCV.setAdapter(productAdapter);
                    progressDialog.dismiss();
                    if (productModelList.size() == 0) {
                        Log.d(TAG, "No Related Products Available");
                        showSnackBar("No Products Available !");
                        binding.noProductsAvail.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void priceRangeProducts() {
        List<ProductModel> filteredList = new ArrayList<>();
        for (int j = 0; j < productModelList.size(); j++) {
            if (productModelList.get(j).getProduct_price() >= priceRangeStart && productModelList.get(j).getProduct_price() <= priceRangeEnd) {
                filteredList.add(productModelList.get(j));
            }
        }
        productAdapter.setProductModelList(filteredList);
    }

    public void sortItems(View view) {
        String[] itemList = new String[]{"Price -- Low - high", "Price -- High - Low"};
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
                            int a = (int) obj1.getProduct_price();
                            int b = (int) obj2.getProduct_price();
                            return Integer.valueOf(a).compareTo(b);// To compare integer values
                        }
                        // ## Descending order
                        else {
                            // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                            Log.d(TAG, "Descending to Ascending");
                            int a = (int) obj2.getProduct_price();
                            int b = (int) obj1.getProduct_price();
                            return Integer.valueOf(a).compareTo(b); // To compare integer values
                        }
                    }
                });
                dialogInterface.dismiss();

                productAdapter.setProductModelList(productModelList);
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

    public void dialogIninit() {
        progressDialog = new Dialog(this);
        com.talla.santhamarket.databinding.CustomProgressDialogBinding customProgressDialogBinding = com.talla.santhamarket.databinding.CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        if (binding.searchView.isSearchOpen()) {
            binding.searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void showDialog(final String title, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (title.equalsIgnoreCase(getString(R.string.error_occured))) {
                    dialogInterface.dismiss();
                    finish();
                } else {
                    dialogInterface.dismiss();
                }

            }
        });
        alertDialogBuilder.show();
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


}