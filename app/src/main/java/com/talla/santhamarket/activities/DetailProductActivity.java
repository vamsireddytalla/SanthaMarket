package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.ColorChartAdapter;
import com.talla.santhamarket.adapters.ProductAdapter;
import com.talla.santhamarket.adapters.ProductDetailsViewPagerAdapter;
import com.talla.santhamarket.adapters.ProductImagesAdapter;
import com.talla.santhamarket.adapters.ProductSizeAdapter;
import com.talla.santhamarket.adapters.QuantityAdapter;
import com.talla.santhamarket.adapters.RatingsAdapter;
import com.talla.santhamarket.databinding.ActivityDetailProductBinding;
import com.talla.santhamarket.databinding.ProductDescriptionLayoutBinding;
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

public class DetailProductActivity extends AppCompatActivity {
    private ActivityDetailProductBinding binding;
    private ViewPager2 viewPagerPrductImages;
    private TabLayout imagesTabLayout;
    private ViewPager2 viewPagerDescription;
    private TabLayout descriptionTabLayout;
    private ProductImagesAdapter poductImagesAdapter;
    private List<ProductImageModel> productsImagesList = new ArrayList<>();
    private boolean isFavItem = false;
    private ProgressDialog progressDialog;
    private String productId;
    private ProductModel productModel;
    private int selectedQty = 1;
    private String UID;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private FavouriteModel favModel;
    private boolean isFirstTime = true;
    private static final String TAG = "DetailProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productId = bundle.getString("product_id");
            Log.d(TAG, "CategoryId from Intent :" + productId);
        } else {
            finish();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching Data");
        progressDialog.setMessage("Please Wait . . .");
        progressDialog.setCancelable(false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getCurrentUser().getUid();
        documentReference = firestore.collection("FAVOURITES").document(UID);
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

        binding.quantitySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    selectedQty = (int) adapterView.getSelectedItem();
                    Log.d(TAG, "Selected Quantity in Spinner");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.dateDelivery.setText(CheckUtill.getDateAfterDays(7));

        List<RatingModel> ratingModelList = new ArrayList<>();
        for (int i = 20; i < 25; i++) {
            RatingModel ratingModel = new RatingModel();
            ratingModel.setRating_title("Title " + i);
            ratingModel.setRating_desc("Desc " + i);
            List<ProductImageModel> productImageModelList = new ArrayList<>();
            ProductImageModel productImageModel = new ProductImageModel();
            productImageModel.setProduct_image("https://getk2.org/media/k2/items/cache/42433d914d50be2debde725181e28d45_XL.jpg?t=20160407_065923");
            productImageModelList.add(productImageModel);
            ratingModel.setProductImageModelList(productImageModelList);
            ratingModelList.add(ratingModel);
        }
        RatingsAdapter ratingsAdapter = new RatingsAdapter(this, ratingModelList);
        binding.ratingRCV.setAdapter(ratingsAdapter);

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
                        documentReference = firestore.collection("FAVOURITES").document();
                        FavouriteModel insertFavModel = new FavouriteModel();
                        insertFavModel.setProduct_id(productId);
                        insertFavModel.setUserId(UID);
                        insertFavModel.setTimestamp(StaticUtills.getTimeStamp());
                        String id = documentReference.getId();
                        insertFavModel.setFavId(id);
                        Log.d(TAG, "Reference Id Times Check----------------- " + id);

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
        firestore.collection("PRODUCTS").whereEqualTo("product_id", productId).addSnapshotListener(this,new EventListener<QuerySnapshot>() {
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
        productsImagesList.addAll(productModel.getProduct_images());
        poductImagesAdapter.setProductImageModelList(productsImagesList);
        List<Map.Entry<String, Object>> sizeChartList = new ArrayList(productModel.getProduct_sizes().entrySet());
        if (!sizeChartList.isEmpty()) {
            binding.tempView.setVisibility(View.VISIBLE);
            binding.sizeRoot.setVisibility(View.VISIBLE);
            ProductSizeAdapter productSizeAdapter = new ProductSizeAdapter(this, sizeChartList);
            binding.sizeRCV.setAdapter(productSizeAdapter);
        }
        List<Map.Entry<String, Object>> colorChartList = new ArrayList(productModel.getProduct_colors().entrySet());
        if (!colorChartList.isEmpty()) {
            binding.tempView.setVisibility(View.VISIBLE);
            binding.colorRoot.setVisibility(View.VISIBLE);
            ColorChartAdapter colorChartAdapter = new ColorChartAdapter(this, colorChartList);
            binding.colorChartRCV.setAdapter(colorChartAdapter);
        }

        binding.productName.setText(productModel.getProduct_name());
        binding.productPrice.setText(CheckUtill.FormatCost(Math.round(productModel.getProduct_price())) + " Rs");
        binding.mrpPrice.setText(CheckUtill.FormatCost(Math.round(productModel.getMrp_price())) + " Rs");
        binding.mrpPrice.setPaintFlags(binding.mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        Long mrp_price = productModel.getMrp_price();
        Long selling_price = productModel.getProduct_price();
        float res = StaticUtills.discountPercentage(selling_price, mrp_price);
        binding.discount.setText(String.valueOf(res).substring(0, 2) + "%OFF");
        quatitySpin(Math.round(productModel.getMax_quantity()));
    }

    private void quatitySpin(int quantity) {
        List<String> quantityList = new ArrayList<>();
        for (int i = 1; i <= quantity; i++) {
            quantityList.add(i + "");
        }
        QuantityAdapter quantityAdapter = new QuantityAdapter(quantityList, this);
        binding.quantitySpin.setAdapter(quantityAdapter);
    }


}