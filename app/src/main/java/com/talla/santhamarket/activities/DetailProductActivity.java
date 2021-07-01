package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
import com.talla.santhamarket.databinding.OrderSummaryItemBinding;
import com.talla.santhamarket.databinding.ProductDescriptionLayoutBinding;
import com.talla.santhamarket.interfaces.ChartsClickListner;
import com.talla.santhamarket.models.CartModel;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.FavouriteModel;
import com.talla.santhamarket.models.ProductImageModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.RatingModel;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;
import static com.google.firebase.firestore.DocumentChange.Type.MODIFIED;
import static com.google.firebase.firestore.DocumentChange.Type.REMOVED;

public class DetailProductActivity extends AppCompatActivity implements ChartsClickListner
{
    private ActivityDetailProductBinding binding;
    private AddCartItemBinding addCartItemBinding;
    private ViewPager2 viewPagerPrductImages;
    private TabLayout imagesTabLayout;
    private ViewPager2 viewPagerDescription;
    private TabLayout descriptionTabLayout;
    private ProductImagesAdapter poductImagesAdapter;
    private List<ProductImageModel> productsImagesList = new ArrayList<>();
    private boolean isFavItem = false;
    private ProgressDialog progressDialog;
    private String productId, key;
    private ProductModel productModel;
    private int selectedQty = 1;
    private String UID;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private FavouriteModel favModel;
    private boolean isFirstTime = true;
    private View view;
    int totalCart_items = 0;
    private static final String TAG = "DetailProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        view = binding.getRoot();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.processing_request));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getCurrentUser().getUid();
        documentReference = firestore.collection("FAVOURITES").document(UID);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productId = bundle.getString(getString(R.string.pro_id));
            getCartItemsCount();
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
                intent.putExtra("descObj", productModel);
                startActivity(intent);
            }
        });
        getProdBasedOnProdId();


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

        isFavOrNot();
        binding.toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.toggleBtn.isChecked()) {
                    if (auth.getCurrentUser() != null) {
                        documentReference = firestore.collection("FAVOURITES").document();
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
                        firestore.collection("FAVOURITES").document(favModel.getFavId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DetailProductActivity.this, "Removed as Favourites", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.toggleBtn.setChecked(true);
                                Toast.makeText(DetailProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                key=getString(R.string.add_to_cart);
                checkItemIsExists(key);
            }
        });


    }

    private void addItemToCart() {
        progressDialog.show();
        DocumentReference ref = firestore.collection("CART_ITEMS").document();
        CartModel cartModel = new CartModel();
        cartModel.setCart_doc_id(ref.getId());
        cartModel.setCart_product_id(productModel.getProduct_id());
        cartModel.setUser_id(UID);
        cartModel.setTimestamp(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
        cartModel.setSize_chart(productModel.getSelectedSize());
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
        DocumentReference ref = firestore.collection("CART_ITEMS").document(docId);
        CartModel cartModel = new CartModel();
        cartModel.setCart_doc_id(ref.getId());
        cartModel.setCart_product_id(productModel.getProduct_id());
        cartModel.setUser_id(UID);
        cartModel.setTimestamp(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
        cartModel.setSize_chart(productModel.getSelectedSize());
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
        CollectionReference docref = firestore.collection("CART_ITEMS");
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
    Query query = firestore.collection("CART_ITEMS").whereEqualTo("user_id", UID);
    query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if (error != null) {
                Log.e(TAG, "Error :" + error.getMessage());
            } else {
                totalCart_items = value.getDocuments().size();
                binding.cartInclude.cartCount.setText(totalCart_items + "");
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
            Query query = firestore.collection("FAVOURITES");
            query = query.whereEqualTo("product_id", productId);
            query = query.whereEqualTo("userId", UID);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        firestore.collection("PRODUCTS").whereEqualTo("product_id", productId).addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        productModel = dc.getDocument().toObject(ProductModel.class);
                        Log.d(TAG, "Product Model :" + productModel.toString());
                        setDataToUi(productModel);
                    }
                }
                progressDialog.dismiss();
            }
        });
    }

    private void setDataToUi(ProductModel productModel) {
        productsImagesList.clear();
        productsImagesList.addAll(productModel.getProduct_images());
        poductImagesAdapter.setProductImageModelList(productsImagesList);
        List<Map.Entry<String, Object>> sizeChartList = new ArrayList(productModel.getProduct_sizes().entrySet());
        if (!sizeChartList.isEmpty()) {
            binding.tempView.setVisibility(View.VISIBLE);
            binding.sizeRoot.setVisibility(View.VISIBLE);
            ProductSizeAdapter productSizeAdapter = new ProductSizeAdapter(this, sizeChartList, this);
            binding.sizeRCV.setAdapter(productSizeAdapter);
        }
        List<Map.Entry<String, Object>> colorChartList = new ArrayList(productModel.getProduct_colors().entrySet());
        if (!colorChartList.isEmpty()) {
            binding.tempView.setVisibility(View.VISIBLE);
            binding.colorRoot.setVisibility(View.VISIBLE);
            ColorChartAdapter colorChartAdapter = new ColorChartAdapter(this, colorChartList, this);
            binding.colorChartRCV.setAdapter(colorChartAdapter);
        }

        binding.productName.setText(productModel.getProduct_name());
        binding.productPrice.setText(CheckUtill.FormatCost(Math.round(productModel.getProduct_price())) + getString(R.string.Rs));
        binding.mrpPrice.setText(CheckUtill.FormatCost(Math.round(productModel.getMrp_price())) + getString(R.string.Rs));
        binding.mrpPrice.setPaintFlags(binding.mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        Long mrp_price = productModel.getMrp_price();
        Long selling_price = productModel.getProduct_price();
        float res = StaticUtills.discountPercentage(selling_price, mrp_price);
        binding.discount.setText(String.valueOf(res).substring(0, 2) + "%OFF");
        if (productModel.isOut_of_stock()) {
            binding.continueBtn.setBackgroundColor(getResources().getColor(R.color.orange));
            binding.continueText.setText("Out of Stock");
        } else {
            binding.continueBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            binding.continueText.setText("Continue");
        }
        key = binding.continueText.getText().toString();
    }

    @Override
    public void onSelectionCLick(String selectionVal, String key) {
        if (key.equals(getString(R.string.Selected_Color))) {
            productModel.setSelectedColor(selectionVal);
//            showSnackBar("Selected Color " + selectionVal);
        } else {
            productModel.setSelectedSize(selectionVal);
//            showSnackBar("Selected Size " + selectionVal);
        }
    }

    public void nextStep(View view) {
        key = binding.continueText.getText().toString();
        checkItemIsExists(key);
    }

    private void openIntent() {
        Intent intent = new Intent(this, OrderSummaryActivity.class);
        startActivity(intent);
    }

}