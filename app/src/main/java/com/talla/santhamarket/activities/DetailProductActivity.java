package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firestore.v1.StructuredQuery;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.ColorChartAdapter;
import com.talla.santhamarket.adapters.ProductAdapter;
import com.talla.santhamarket.adapters.ProductDetailsViewPagerAdapter;
import com.talla.santhamarket.adapters.ProductImagesAdapter;
import com.talla.santhamarket.adapters.ProductSizeAdapter;
import com.talla.santhamarket.adapters.QuantityAdapter;
import com.talla.santhamarket.adapters.RatingsAdapter;
import com.talla.santhamarket.databinding.ActivityDetailProductBinding;
import com.talla.santhamarket.databinding.AddCartItemBinding;
import com.talla.santhamarket.databinding.CheckInternetBinding;
import com.talla.santhamarket.databinding.CustomCartBinding;
import com.talla.santhamarket.databinding.OrderSummaryItemBinding;
import com.talla.santhamarket.databinding.ProductDescriptionLayoutBinding;
import com.talla.santhamarket.interfaces.ChartsClickListner;
import com.talla.santhamarket.models.CartModel;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.FavouriteModel;
import com.talla.santhamarket.models.ProductImageModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.RatingModel;
import com.talla.santhamarket.models.SpecificationModel;
import com.talla.santhamarket.models.SubProductModel;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;
import static com.google.firebase.firestore.DocumentChange.Type.MODIFIED;
import static com.google.firebase.firestore.DocumentChange.Type.REMOVED;

public class DetailProductActivity extends AppCompatActivity implements ChartsClickListner {
    private ActivityDetailProductBinding binding;
    private ProductImagesAdapter poductImagesAdapter;
    private ProductSizeAdapter productSizeAdapter;
    private List<ProductImageModel> productsImagesList = new ArrayList<>();
    private List<SubProductModel> subProductModelList;
    private String productId, key;
    private ProductModel productModel;
    private String UID;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private Dialog progressDialog;
    private FavouriteModel favModel;
    private SpecificationModel specificationModel;
    List<String> productColors = new ArrayList<>();
    List<String> productSizes = new ArrayList<>();
    private View view;
    private int totalCart_items = 0,selectedQty=1;
    private ListenerRegistration cartitemCountListner, productListner, favListner;
    private static final String TAG = "DetailProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        view = binding.getRoot();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dialogIninit();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getCurrentUser().getUid();
        documentReference = firestore.collection(getString(R.string.FAVOURITES)).document(UID);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productId = bundle.getString(getString(R.string.intent_product_id));
            Log.d(TAG, "CategoryId from Intent :" + productId);
        } else {
            finish();
        }

        //firebase instance
        poductImagesAdapter = new ProductImagesAdapter(this, productsImagesList);
        binding.viewPagerPrductImages.setAdapter(poductImagesAdapter);
        new TabLayoutMediator(binding.tabIndicator, binding.viewPagerPrductImages, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        }).attach();

        binding.productDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailProductActivity.this, PoductDescriptionActivity.class);
                intent.putExtra(getString(R.string.intent_poductDetails), productModel);
                startActivity(intent);
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.dateDelivery.setText(CheckUtill.getDateAfterDays(7));

//        List<RatingModel> ratingModelList = new ArrayList<>();
//        for (int i = 20; i < 25; i++) {
//            RatingModel ratingModel = new RatingModel();
//            ratingModel.setRating_title("Title " + i);
//            ratingModel.setRating_desc("Desc " + i);
//            List<ProductImageModel> productImageModelList = new ArrayList<>();
//            ProductImageModel productImageModel = new ProductImageModel();
//            productImageModel.setProduct_image("https://getk2.org/media/k2/items/cache/42433d914d50be2debde725181e28d45_XL.jpg?t=20160407_065923");
//            productImageModelList.add(productImageModel);
//            ratingModel.setProductImageModelList(productImageModelList);
//            ratingModelList.add(ratingModel);
//        }
//        RatingsAdapter ratingsAdapter = new RatingsAdapter(this, ratingModelList);
//        binding.ratingRCV.setAdapter(ratingsAdapter);

        binding.toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.toggleBtn.isChecked()) {
                    if (auth.getCurrentUser() != null) {
                        documentReference = firestore.collection(getString(R.string.FAVOURITES)).document();
                        FavouriteModel insertFavModel = new FavouriteModel();
                        insertFavModel.setProduct_id(productId);
                        insertFavModel.setUserId(UID);
                        insertFavModel.setTimestamp(StaticUtills.getTimeStamp());
                        String id = documentReference.getId();
                        insertFavModel.setFavId(id);
                        Log.d(TAG, "Reference Id Times Check----------------- " + id);

                        Log.d(TAG, "Reference Id Times Check----------------- " + "Checked");
                        documentReference.set(insertFavModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DetailProductActivity.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        Intent intent = new Intent(DetailProductActivity.this, AuthenticationActivity.class);
                        startActivity(intent);
                    }
                } else {
                    if (auth.getCurrentUser() != null) {
                        Log.d(TAG, "Reference Id Times Check----------------- " + "UN-Checked");
                        firestore.collection(getString(R.string.FAVOURITES)).document(favModel.getFavId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DetailProductActivity.this, "Removed as Favourites", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.toggleBtn.setChecked(true);
                                showSnackBar(e.getMessage());
                            }
                        });

                    } else {
                        Intent intent = new Intent(DetailProductActivity.this, AuthenticationActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        binding.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                key = getString(R.string.add_to_cart);
                checkItemIsExists(key);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCartItemsCount();
        getProdBasedOnProdId();
        isFavOrNot();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cartitemCountListner.remove();
        productListner.remove();
        favListner.remove();
    }

    private void addItemToCart() {
        progressDialog.show();
        DocumentReference ref = firestore.collection(getString(R.string.CART_ITEMS)).document();
        CartModel cartModel = new CartModel();
        cartModel.setCart_doc_id(ref.getId());
        cartModel.setCart_product_id(productModel.getProduct_id());
        cartModel.setUser_id(UID);
        cartModel.setTimestamp(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
        cartModel.setSize_chart(productModel.getSelectedSize());
        cartModel.setItemTypeModel(productModel.getItemTypeModel());
        cartModel.setSelected_color(productModel.getSelectedColor());
        ref.set(cartModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    if (key.equalsIgnoreCase("Continue")) {
                        openIntent();
                    } else {
                        showDialog("Sucessfully added item to Cart !");
                    }

                } else {
                    progressDialog.dismiss();
                    showDialog(task.getException() + "");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                showDialog("Error Occured while Adding Product to Cart : " + e.getMessage());
            }
        });
    }

    private void updateItemToCart(String docId) {
        progressDialog.show();
        DocumentReference ref = firestore.collection(getString(R.string.CART_ITEMS)).document(docId);
        CartModel cartModel = new CartModel();
        cartModel.setCart_doc_id(ref.getId());
        cartModel.setCart_product_id(productModel.getProduct_id());
        cartModel.setUser_id(UID);
        cartModel.setTimestamp(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
        cartModel.setSize_chart(productModel.getSelectedSize());
        cartModel.setItemTypeModel(productModel.getItemTypeModel());
        cartModel.setSelected_color(productModel.getSelectedColor());
        ref.set(cartModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    openIntent();
                } else {
                    progressDialog.dismiss();
                    showDialog(task.getException() + "");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                showDialog("Error Occured while Adding Product to Cart : " + e.getMessage());
            }
        });
    }

    private void checkItemIsExists(final String key) {
        progressDialog.show();
        CollectionReference docref = firestore.collection(getString(R.string.CART_ITEMS));
        docref.whereEqualTo("cart_product_id", productModel.getProduct_id()).whereEqualTo("user_id", UID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    CartModel cartModel = null;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        cartModel = document.toObject(CartModel.class);
                    }
                    if (cartModel == null) {
                        Log.d(TAG, "No Doc Available");
                        progressDialog.dismiss();
                        if (totalCart_items < 10 || key.equalsIgnoreCase("Continue")) {
                            addItemToCart();
                        } else {
                            showDialog("Cart Items limit Exceeded...");
                        }

                    } else {
                        progressDialog.dismiss();
                        if (key.equalsIgnoreCase("Continue")) {
                            updateItemToCart(cartModel.getCart_doc_id());
                        } else {
                            showDialog("Already Added Item to Cart!");
                        }
                        Log.d(TAG, "Doc Available");
                    }
                } else {
                    progressDialog.dismiss();
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG, "No Data Found : " + e.getMessage());
            }
        });

    }

    private void getCartItemsCount() {
        Query query = firestore.collection(getString(R.string.CART_ITEMS)).whereEqualTo("user_id", UID);
        cartitemCountListner = query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error :" + error.getMessage());
                } else {
                    totalCart_items = value.getDocuments().size();
                    Log.d(TAG, "Cart Items " + totalCart_items);
                    binding.cartItemsCount.setText(totalCart_items + "");
                }
            }
        });
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void isFavOrNot() {
        if (auth.getCurrentUser() != null) {
            Query query = firestore.collection(getString(R.string.FAVOURITES));
            query = query.whereEqualTo("product_id", productId);
            query = query.whereEqualTo("userId", UID);
            favListner = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                        Toast.makeText(DetailProductActivity.this, "error", Toast.LENGTH_SHORT).show();
                    } else {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    favModel = dc.getDocument().toObject(FavouriteModel.class);
                                    Log.d(TAG, "Fav MODEl Added :" + favModel.toString());
                                    if (favModel.getProduct_id().equals(productId)) {
                                        binding.toggleBtn.setChecked(true);
                                    }
                                    break;
                                case MODIFIED:
                                    favModel = dc.getDocument().toObject(FavouriteModel.class);
                                    Log.d(TAG, "Fav MODEl Modified :" + favModel.toString());
                                    break;
                                case REMOVED:
                                    favModel = dc.getDocument().toObject(FavouriteModel.class);
                                    Log.d(TAG, "Fav MODEl Removed :" + favModel.toString());
                                    break;
                            }

                        }
                    }
                }
            });
        }
    }

    private void getProdBasedOnProdId() {
        progressDialog.show();
        final DocumentReference docRef = firestore.collection(getString(R.string.PRODUCTS)).document(productId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    showDialog(error.getMessage());
                } else {
                    if (value != null && value.exists()) {
                        productModel = value.toObject(ProductModel.class);
                        Log.d(TAG, "Product Model :" + productModel.toString());
                        productListner = docRef.collection(getString(R.string.SPECIFICATIONS)).document(productModel.getProduct_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    showDialog(error.getMessage());
                                } else {
                                    if (value != null && value.exists()) {
                                        specificationModel = value.toObject(SpecificationModel.class);
                                        productModel.setSpecificationModel(specificationModel);
                                    }
                                }
                            }
                        });
                        setDataToUi(productModel);
                    } else {
                        showDialog("Product Removed From Server");
                    }
                }
                progressDialog.dismiss();
            }
        });
    }

    private void setDataToUi(ProductModel productModel) {
        productsImagesList.clear();

        subProductModelList = productModel.getSubProductModelList();
        productColors.clear();
        for (int f = 0; f < subProductModelList.size(); f++) {
            SubProductModel subProductModel = subProductModelList.get(f);
            if (subProductModel.getProduct_color() != null) {
                productColors.add(subProductModel.getProduct_color());
            }
        }

        if (subProductModelList.get(0).getProduct_sizes() != null) {
            productSizes = subProductModelList.get(0).getProduct_sizes();
        }

        if (subProductModelList.get(0).getProduct_images() != null) {
            for (int g = 0; g < subProductModelList.get(0).getProduct_images().size(); g++) {
                productsImagesList.add(subProductModelList.get(0).getProduct_images().get(g));
            }
        }

        poductImagesAdapter.setProductImageModelList(productsImagesList);
        if (!productSizes.isEmpty()) {
            binding.tempView.setVisibility(View.VISIBLE);
            binding.sizeRoot.setVisibility(View.VISIBLE);
            productSizeAdapter = new ProductSizeAdapter(this, productSizes, this);
            binding.sizeRCV.setAdapter(productSizeAdapter);
        }

        if (!productColors.isEmpty()) {
            binding.tempView.setVisibility(View.VISIBLE);
            binding.colorRoot.setVisibility(View.VISIBLE);
            ColorChartAdapter colorChartAdapter = new ColorChartAdapter(this, productColors, this);
            binding.colorChartRCV.setAdapter(colorChartAdapter);
        }

        binding.productName.setText(productModel.getProduct_name());
        binding.productPrice.setText(CheckUtill.FormatCost((int) Math.round(productModel.getProduct_price())) + getString(R.string.Rs));
        binding.mrpPrice.setText(CheckUtill.FormatCost((int) Math.round(productModel.getMrp_price())) + getString(R.string.Rs));
        binding.mrpPrice.setPaintFlags(binding.mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        double mrp_price = productModel.getMrp_price();
        double selling_price = productModel.getProduct_price();
        binding.myRatingBar.setRating((float) productModel.getAvgRatings());
        binding.totalRatings.setText(productModel.getTotal_ratings() + "(Reviews)");
        int res = StaticUtills.discountPercentage(selling_price, mrp_price);
        binding.discount.setText(String.valueOf(res) + "%OFF");
        binding.productWeight.setText("Product Weight : " + productModel.getProduct_weight() + " Kg");
        if (productModel.isOut_of_stock() || (productModel.getTotalStock().equals(productModel.getSelled_items()))) {
            binding.continueBtn.setBackgroundColor(getResources().getColor(R.color.red));
            binding.continueText.setText(getString(R.string.out_of_stock));
        } else {
            binding.continueBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            binding.continueText.setText(R.string.Continue);
        }
        key = binding.continueText.getText().toString();
    }

    @Override
    public void onSelectionCLick(String selectionVal, String key) {
        if (key.equals(getString(R.string.Selected_Color))) {
            productModel.setSelectedColor(selectionVal);
            for (int f = 0; f < subProductModelList.size(); f++) {
                SubProductModel subProductModel = subProductModelList.get(f);
                if (subProductModel.getProduct_color().equals(selectionVal)) {

                    if (subProductModelList.get(f).getProduct_sizes() != null) {
                        productSizes = subProductModelList.get(f).getProduct_sizes();
                        productSizeAdapter.setSizeList(productSizes);
                    }

                    if (subProductModelList.get(f).getProduct_images() != null) {
                        productsImagesList.clear();
                        for (int g = 0; g < subProductModelList.get(f).getProduct_images().size(); g++) {
                            productsImagesList.add(subProductModelList.get(f).getProduct_images().get(g));
                        }
                        poductImagesAdapter.setProductImageModelList(productsImagesList);
                    }

                }
            }
//            showSnackBar("Selected Color " + selectionVal);
        } else {
            productModel.setSelectedSize(selectionVal);
//            showSnackBar("Selected Size " + selectionVal);
        }
    }

    public void nextStep(View view) {
        key = binding.continueText.getText().toString();
        if (!key.equalsIgnoreCase(getString(R.string.out_of_stock))) {
            checkItemIsExists(key);
        }

    }

    private void openIntent() {
        Intent intent = new Intent(this, MultiCartActivity.class);
        startActivity(intent);
    }

    public void dialogIninit() {
        // In Activity's onCreate() for instance
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        progressDialog = new Dialog(this);
        com.talla.santhamarket.databinding.CustomProgressDialogBinding customProgressDialogBinding = com.talla.santhamarket.databinding.CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

}