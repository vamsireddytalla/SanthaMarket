package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.HomeCategoryAdapter;
import com.talla.santhamarket.databinding.ActivitySearchProductBinding;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.utills.CheckInternet;
import com.talla.santhamarket.databinding.CheckInternetBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchProductActivity extends AppCompatActivity {
    private ActivitySearchProductBinding binding;
    private CheckInternetBinding checkInternetBinding;
    private Long categoryId;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "SearchProductActivity";
    private ProgressDialog progressDialog;
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private List<ProductModel> productModelList = new ArrayList<>();
    private HomeCategoryAdapter homeCategoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkInternetBinding = com.talla.santhamarket.databinding.CheckInternetBinding.bind(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching Data");
        progressDialog.setMessage("Please Wait . . .");
        progressDialog.setCancelable(false);
        //firebase instance
        firebaseFirestore = FirebaseFirestore.getInstance();

        checkInternetAndMakeServerCall();
        checkInternetBinding.tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternetAndMakeServerCall();
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        binding.searchView.setMenuItem(item);
        binding.searchView.showSearch(true);

        binding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 4) {
                    getSearchedData(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        return true;
    }

    private void checkInternetAndMakeServerCall() {
        if (CheckInternet.checkInternet(this)) {
            binding.includeLayoutViewProducts.setVisibility(View.GONE);
            binding.layoutOne.setVisibility(View.VISIBLE);
            getCategoriesData();
        } else {
            binding.layoutOne.setVisibility(View.GONE);
            binding.includeLayoutViewProducts.setVisibility(View.VISIBLE);
        }
    }

    private void getCategoriesData() {
        progressDialog.show();
        if (categoryModelList != null) {
            categoryModelList.clear();
        }
        firebaseFirestore.collection("CATEGORIES").orderBy("index").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            CategoryModel categoryModel = dc.getDocument().toObject(CategoryModel.class);
                            categoryModelList.add(categoryModel);
                            Log.d(TAG, "Category data added to list: " + categoryModel.toString());
                            break;
                        case MODIFIED:
                            CategoryModel categoryModel1 = dc.getDocument().toObject(CategoryModel.class);
                            Log.d(TAG, "Category data modified to list: " + categoryModel1.toString());
                            for (int i = 0; i < categoryModelList.size(); i++) {
                                if (categoryModelList.get(i).getIndex() == categoryModel1.getIndex()) {
                                    categoryModelList.remove(i);
                                    categoryModelList.add(i, categoryModel1);
                                    break;
                                }
                            }
                            break;
                        case REMOVED:
                            CategoryModel categoryModel2 = dc.getDocument().toObject(CategoryModel.class);
                            Log.d(TAG, "Category data removed to list: " + categoryModel2.toString());
                            for (int i = 0; i < categoryModelList.size(); i++) {
                                if (categoryModelList.get(i).getIndex() == categoryModel2.getIndex()) {
                                    categoryModelList.remove(i);
                                    break;
                                }
                            }
                            break;
                    }
                }
                binding.categoryRCV.setHasFixedSize(true);
                binding.categoryRCV.setLayoutManager(new GridLayoutManager(SearchProductActivity.this, 4));
                homeCategoryAdapter = new HomeCategoryAdapter(SearchProductActivity.this, categoryModelList);
                binding.categoryRCV.setAdapter(homeCategoryAdapter);
                progressDialog.dismiss();

            }
        });
    }

    private void getSearchedData(final String tagName) {
        progressDialog.show();
        Log.d(TAG, "Searched TAG Name " + tagName);
        firebaseFirestore.collection("PRODUCTS").whereArrayContains("tags", tagName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        ProductModel productModel = document.toObject(ProductModel.class);
                        Log.d(TAG, "Searched Products ---> " + productModel.toString());
                        productModelList.add(productModel);
                    }
                    progressDialog.dismiss();
                    if (productModelList.size()>0)
                    {
                        Intent intent = new Intent(SearchProductActivity.this, ViewProductsActivity.class);
                        intent.putExtra("categoryId", productModelList.get(0).getCategory_id());
                        startActivity(intent);
                        finish();
                    }else {
                        showDialog("No Items Found with "+tagName, "Click OK to Search Again");
                    }

                } else {
                    Toast.makeText(SearchProductActivity.this, "Error Occured  " + task.getException(), Toast.LENGTH_SHORT).show();
                }
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
                binding.searchView.showSearch(true);
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        binding.getRoot().getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
        });
        alertDialogBuilder.show();
    }

}