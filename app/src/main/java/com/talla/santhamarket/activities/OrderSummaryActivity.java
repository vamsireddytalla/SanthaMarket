package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.HomeCategoryAdapter;
import com.talla.santhamarket.adapters.ProductAdapter;
import com.talla.santhamarket.adapters.SummaryAdapter;
import com.talla.santhamarket.databinding.ActivityOrderSummaryBinding;
import com.talla.santhamarket.interfaces.QuantityClickListner;
import com.talla.santhamarket.models.CartModel;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.UserAddress;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.ArrayList;
import java.util.List;

public class OrderSummaryActivity extends AppCompatActivity implements QuantityClickListner {
    private ActivityOrderSummaryBinding binding;
    private View view;
    private ProgressDialog progressDialog;
    private String UID;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private int totalAddress = 0;
    private SummaryAdapter summaryAdapter;
    private List<CartModel> cartModelsList = new ArrayList<>();
    private List<ProductModel> productModelList = new ArrayList<>();
    private int totalMrpPrice=0;
    private float totalDiscount;
    private int finalPrice;
    private static String TAG = "ActivityOrderSummaryBinding";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSummaryBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.processing_request));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getCurrentUser().getUid();
        documentReference = firestore.collection("FAVOURITES").document(UID);
        getAddressCount();

    }

    private void getAddressData() {
        progressDialog.show();
        firestore.collection("Address Book").whereEqualTo("userId", UID).whereEqualTo("defaultAddress", true).get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Get Address data sucessfull");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "Server response : " + document.getId() + " => " + document.getData());
                        UserAddress userAddress = document.toObject(UserAddress.class);
                        userAddress.setDocID(document.getId());
                        binding.changeAddress.setText(R.string.add_or_change);
                        binding.addressTxt.setText(userAddress.getUser_name() + "\n" + userAddress.getUser_country() + "\n" + userAddress.getUser_state() + "\n" + userAddress.getUser_city() + "\n" + userAddress.getUser_pincode() + "\n" + userAddress.getUser_streetAddress());
                        getCartItems();
                    }
                } else {
                    progressDialog.dismiss();
                    Log.d(TAG, "error occured" + task.getException());
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                showSnackBar(e.getMessage());
            }
        });
    }

    private void getAddressCount() {
        Query query = firestore.collection("Address Book").whereEqualTo("userId", UID);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error :" + error.getMessage());
                } else {
                    totalAddress = value.getDocuments().size();
                    if (totalAddress > 0) {
                        getAddressData();
                    } else {
                        binding.changeAddress.setText(R.string.add_new_address);
                    }
                    Log.d(TAG, "Total Address List : " + totalAddress);
                }
            }
        });
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void openAddressBook(View view) {
        Intent intent = new Intent(this, AddressBookActivity.class);
        startActivity(intent);
    }

    private void getCartItems() {
        Query query = firestore.collection("CART_ITEMS").whereEqualTo("user_id", UID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CartModel cartModel = document.toObject(CartModel.class);
                        getProdBasedOnProdId(cartModel.getCart_product_id(), cartModel);
                    }
                    Log.d(TAG, "getCartItems---> " + cartModelsList.toString());
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void getProdBasedOnProdId(final String productId, final CartModel cartModel) {
        progressDialog.show();
        firestore.collection("PRODUCTS").whereEqualTo("product_id", productId).addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                ProductModel productModel = dc.getDocument().toObject(ProductModel.class);
                                if (cartModel != null) {
                                    productModel.setSelectedSize(cartModel.getSize_chart());
                                    productModel.setSelectedColor(cartModel.getSelected_color());
                                }
                                productModelList.add(productModel);
                                Log.d(TAG, "Product data added to list: " + productModel.toString());
                                break;
                            case MODIFIED:
                                ProductModel productModel1 = dc.getDocument().toObject(ProductModel.class);
                                Log.d(TAG, "Product data modified to list: " + productModel1.toString());
                                for (int i = 0; i < productModelList.size(); i++) {
                                    if (productModelList.get(i).getProduct_id().equalsIgnoreCase(productModel1.getProduct_id())) {
                                        productModel1.setSelectedColor(productModelList.get(i).getSelectedColor());
                                        productModel1.setSelectedSize(productModelList.get(i).getSelectedSize());
                                        productModelList.remove(i);
                                        productModelList.add(i, productModel1);
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                final ProductModel productModel2 = dc.getDocument().toObject(ProductModel.class);
                                Log.d(TAG, "Product data removed to list: " + productModel2.toString());
                                for (int i = 0; i < productModelList.size(); i++) {
                                    if (productModelList.get(i).getProduct_id().equalsIgnoreCase(productModel2.getProduct_id())) {
                                        productModelList.remove(i);
                                        firestore.collection("CART_ITEMS").document(productModel2.getProduct_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Cart Item Deleted From Server Admin :" + productModel2.getProduct_id());
                                            }
                                        });
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                    binding.summaryItems.setHasFixedSize(true);
                    summaryAdapter = new SummaryAdapter(OrderSummaryActivity.this, productModelList,OrderSummaryActivity.this);
                    binding.summaryItems.setAdapter(summaryAdapter);
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void quantityClickListener(int pos, int type) {
       productModelList.get(pos).setMax_quantity((long) type);
       Log.d(TAG,pos+"\n"+type);
       totalMrpPrice=0;
       totalDiscount=0;
       for (int i=0;i<productModelList.size();i++)
       {
           ProductModel productModel=productModelList.get(i);
           Long mrp_price = productModel.getMrp_price();
           Long selling_price = productModel.getProduct_price();
           if (pos==i)
           {
               totalMrpPrice=totalMrpPrice+(Math.round(mrp_price)*type);
               float sellingPrice=(selling_price*type);
               float mrpPrice=(mrp_price*type);
               Log.d(TAG,sellingPrice+"\n"+mrpPrice);
               totalDiscount=totalDiscount+sellingPrice;
           }else {
               totalMrpPrice=totalMrpPrice+Math.round(mrp_price);
               float res = StaticUtills.discountPercentage(selling_price, mrp_price);
               totalDiscount=totalDiscount+res;
           }

       }
       String deliveryCharges=binding.deliveryCharges.getText().toString();
       if (!deliveryCharges.equalsIgnoreCase("FREE"))
       {
          finalPrice =Integer.parseInt(deliveryCharges)+((int)totalMrpPrice-(int)totalDiscount);
       }else {
           finalPrice =(int)totalMrpPrice-(int)totalDiscount;
       }
       binding.itemsTotalPrice.setText(totalMrpPrice+getString(R.string.rs_symbol));
       binding.discountText.setText("-"+totalDiscount+getString(R.string.rs_symbol));
       binding.finalPrice.setText(finalPrice+getString(R.string.rs_symbol));
       binding.totalPrice.setText("PAY "+finalPrice+" "+getString(R.string.Rs));
    }
}