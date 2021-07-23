package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.HomeCategoryAdapter;
import com.talla.santhamarket.adapters.ProductAdapter;
import com.talla.santhamarket.adapters.SearchedProductsAdapter;
import com.talla.santhamarket.databinding.ActivitySearchProductBinding;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.ServerModel;
import com.talla.santhamarket.models.TagsModel;
import com.talla.santhamarket.serverCalls.ApiClient;
import com.talla.santhamarket.serverCalls.ApiInterface;
import com.talla.santhamarket.utills.CheckInternet;
import com.talla.santhamarket.databinding.CheckInternetBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchProductActivity extends AppCompatActivity {
    private ActivitySearchProductBinding binding;
    private CheckInternetBinding checkInternetBinding;
    private FirebaseFirestore firebaseFirestore;
    private Dialog progressDialog;
    private ApiInterface apiInterface;
    private ServerModel serverModel;
    private SearchedProductsAdapter searchedProductsAdapter;
    List<String> tagsList = new ArrayList<>();
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private List<ProductModel> productModelList = new ArrayList<>();
    private static final String TAG = "SearchProductActivity";


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
        firebaseFirestore = FirebaseFirestore.getInstance();
        dialogIninit();
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

        binding.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                binding.searchView.setSuggestionIcon(SearchProductActivity.this.getResources().getDrawable(R.drawable.icoo));
                String[] arrayOfTags = tagsList.toArray(new String[tagsList.size()]);
                binding.searchView.setSuggestions(arrayOfTags);
                binding.searchView.showSuggestions();
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        binding.searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSearchedData(tagsList.get(position));
            }
        });

        final MaterialSearchView searchView;
        searchView = (MaterialSearchView) this.findViewById(R.id.search_view);
        //inflalte with your searchview ID final
        EditText editText = (EditText) searchView.findViewById(com.miguelcatalan.materialsearchview.R.id.searchTextView);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        binding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                String cap = query.substring(0, 1).toUpperCase() + query.substring(1);
                getSearchedData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        // Close Material Search view when back (up) icon is pressed
        findViewById(com.miguelcatalan.materialsearchview.R.id.action_up_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Your code here, in my case I finish the activity
                finish();
            }
        });


        return true;
    }

    private void checkInternetAndMakeServerCall() {
        if (CheckInternet.checkInternet(this)) {
            binding.includeLayoutViewProducts.setVisibility(View.GONE);
            binding.layoutOne.setVisibility(View.VISIBLE);
            getDeviceInfo();
        } else {
            binding.layoutOne.setVisibility(View.GONE);
            binding.includeLayoutViewProducts.setVisibility(View.VISIBLE);
        }
    }

    public void getDeviceInfo() {
        progressDialog.show();
        DocumentReference ref = firebaseFirestore.collection(getString(R.string.DEVICE_INFO)).document("54321");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        serverModel = document.toObject(ServerModel.class);
                        Log.d(TAG, "DocumentSnapshot data: " + serverModel.toString());
                        if (serverModel != null) {

                            apiInterface = ApiClient.getClient(serverModel.getTags_url()).create(ApiInterface.class);
                            apiInterface.getTagsList().enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if (response.isSuccessful()) {
                                        if (response.body() != null) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                                Log.d(TAG, jsonObject.toString());
                                                int length = jsonObject.length();
                                                for (int i = 0; i < length; i++) {
                                                    tagsList.add(jsonObject.getString("Tag" + i));
                                                }
                                                Log.d(TAG, "TAGS LIST :" + tagsList.toString());
                                                progressDialog.dismiss();
                                                String[] arrayOfTags = tagsList.toArray(new String[tagsList.size()]);
                                                binding.searchView.setSuggestions(arrayOfTags);
                                                binding.searchView.showSuggestions();
                                            } catch (Exception e) {
                                                progressDialog.dismiss();
                                                e.printStackTrace();
                                                showSnackBar(e.getMessage());
                                            }
                                        }

                                    } else {
                                        progressDialog.dismiss();
                                        showSnackBar(response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    progressDialog.dismiss();
                                    showSnackBar(t.getMessage());
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                        }

                    } else {
                        progressDialog.dismiss();
                        Log.d(TAG, "No such document");
                    }
                } else {
                    progressDialog.dismiss();
                    showSnackBar(task.getException() + "");
                }
            }
        });
    }

    public void dialogIninit() {
        progressDialog = new Dialog(this);
        com.talla.santhamarket.databinding.CustomProgressDialogBinding customProgressDialogBinding = com.talla.santhamarket.databinding.CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

    private void getSearchedData(final String query) {
        progressDialog.show();
        if (!productModelList.isEmpty()) {
            productModelList.clear();
        }
        firebaseFirestore.collection(getString(R.string.PRODUCTS)).whereArrayContains("tagsList", query).whereEqualTo("itemTypeModel.local", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                        ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                        productModelList.add(productModel);
                    }
                } else {
                    showSnackBar(task.getException().getMessage());
                }
                searchedProductsAdapter = new SearchedProductsAdapter(productModelList, SearchProductActivity.this);
                binding.seacheItemsRCV.setAdapter(searchedProductsAdapter);
                if (productModelList.isEmpty()) {
                    binding.noProdFound.setVisibility(View.VISIBLE);
                } else {
                    binding.noProdFound.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
            }
        });

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
                                if (categoryModelList.get(i).getId().equals(categoryModel1.getId())) {
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
                                if (categoryModelList.get(i).getId().equals(categoryModel2.getId())) {
                                    categoryModelList.remove(i);
                                    break;
                                }
                            }
                            break;
                    }
                }
                progressDialog.dismiss();

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
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        binding.getRoot().getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
        });
        alertDialogBuilder.show();
    }


}