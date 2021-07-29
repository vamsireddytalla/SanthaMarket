package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.LocalProductsAdapter;
import com.talla.santhamarket.adapters.ProductAdapter;
import com.talla.santhamarket.adapters.SearchedProductsAdapter;
import com.talla.santhamarket.databinding.ActivityLocalShopBinding;
import com.talla.santhamarket.databinding.CustomProgressDialogBinding;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.utills.SharedEncryptUtills;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalShopActivity extends AppCompatActivity {
    private ActivityLocalShopBinding binding;
    private SharedEncryptUtills sharedEncryptUtills;
    private LocalProductsAdapter localProductsAdapter;
    private FirebaseFirestore firebaseFirestore;
    private List<ProductModel> productModelList = new ArrayList<>();
    private Dialog progressDialog;
    private ListenerRegistration localProdListner;
    private LocationManager locationManager;
    private static final String TAG = "LocalShopActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocalShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        sharedEncryptUtills = SharedEncryptUtills.getInstance(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        dialogIninit();
        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermissions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        localProdListner.remove();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_Light_Dialog_MinWidth);
                builder.setTitle("Need Location Permissions");
                builder.setMessage("This App requires location permission");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.warning_icon);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(LocalShopActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
                builder.show();
            } else {
                if (!sharedEncryptUtills.getBooleanData(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    sharedEncryptUtills.saveBooleanData(Manifest.permission.ACCESS_FINE_LOCATION, true);
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                } else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_Light_Dialog_MinWidth);
                    builder.setTitle("Requires Permission");
                    builder.setMessage("You have Denied permissions if you want to use this Application " +
                            "Feature You have to allow permissions in Settings manually.");
                    builder.setCancelable(false);
                    builder.setIcon(R.drawable.warning_icon);
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent i = new Intent();
                            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:" + getPackageName()));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(i);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
                    builder.show();

                }
            }
        } else {
            // Permission has already been granted
            getLocalProducts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocalProducts();
                } else {
                    Toast.makeText(this, "Denied Storage Permission", Toast.LENGTH_SHORT).show();
                    requestPermissions();
                }
                return;
            }
        }
    }

    public void dialogIninit() {
        progressDialog = new Dialog(this);
        CustomProgressDialogBinding customProgressDialogBinding = CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

    private void getLocalProducts() {
        progressDialog.show();
        if (!productModelList.isEmpty()) {
            productModelList.clear();
        }
        localProdListner = firebaseFirestore.collection(getString(R.string.PRODUCTS)).whereEqualTo("itemTypeModel.local", true).whereEqualTo("itemTypeModel.pincode", "533343").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                localProductsAdapter = new LocalProductsAdapter(productModelList, LocalShopActivity.this);
                binding.localRCV.setAdapter(localProductsAdapter);
                if (productModelList.isEmpty()) {
                    binding.noProductsAvail.setVisibility(View.VISIBLE);
                } else {
                    binding.noProductsAvail.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
            }
        });

    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
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