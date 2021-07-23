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
import com.google.firebase.firestore.CollectionReference;
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
import com.talla.santhamarket.interfaces.ChartsClickListner;
import com.talla.santhamarket.interfaces.QuantityClickListner;
import com.talla.santhamarket.models.CartModel;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.FavouriteModel;
import com.talla.santhamarket.models.OrderModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.UserAddress;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderSummaryActivity extends AppCompatActivity implements QuantityClickListner, ChartsClickListner {
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
    private List<FavouriteModel> favouriteModelList = new ArrayList<>();
    private List<OrderModel> orderModelList = new ArrayList<>();
    private int totalMrpPrice = 0;
    private float totalDiscount;
    private int finalPrice;
    private FavouriteModel favouriteModel;
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
        getCartItems();
        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                        progressDialog.dismiss();
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
        firestore.collection("CART_ITEMS").whereEqualTo("user_id", UID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                CartModel cartModel = dc.getDocument().toObject(CartModel.class);
                                cartModelsList.add(cartModel);
                                getProdBasedOnProdId(cartModel.getCart_product_id(), cartModel);
                                Log.d(TAG, "Cart data added to list: " + cartModel.toString());
                                break;
                            case MODIFIED:
                                CartModel cartModelModified = dc.getDocument().toObject(CartModel.class);
                                Log.d(TAG, "Cart data modified to list: " + cartModelModified.toString());
                                for (int i = 0; i < cartModelsList.size(); i++) {
                                    if (cartModelsList.get(i).getCart_doc_id().equalsIgnoreCase(cartModelModified.getCart_doc_id())) {
                                        cartModelsList.remove(i);
                                        cartModelsList.add(i, cartModelModified);
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                CartModel cartModelRemoved = dc.getDocument().toObject(CartModel.class);
                                Log.d(TAG, "Cart data removed to list: " + cartModelRemoved.toString());
                                for (int i = 0; i < cartModelsList.size(); i++) {
                                    if (cartModelsList.get(i).getCart_doc_id().equalsIgnoreCase(cartModelRemoved.getCart_doc_id())) {
                                        cartModelsList.remove(i);
                                        break;
                                    }
                                }
                                for (int i = 0; i < productModelList.size(); i++) {
                                    if (productModelList.get(i).getProduct_id().equalsIgnoreCase(cartModelRemoved.getCart_product_id())) {
                                        productModelList.remove(i);
                                        summaryAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                if (cartModelsList.size() == 0) {
                                    progressDialog.dismiss();
                                    finish();
                                }
                                break;
                        }
                    }
                    getFavData();
                }
            }
        });

    }

    private void getProdBasedOnProdId(final String productId, final CartModel cartModel) {
        firestore.collection("PRODUCTS").whereEqualTo("product_id", productId).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                    productModel.setTemp_qty(Long.parseLong("1"));
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
                                        productModel1.setTemp_qty(Long.parseLong("1"));
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
                    summaryAdapter = new SummaryAdapter(OrderSummaryActivity.this, productModelList, OrderSummaryActivity.this, OrderSummaryActivity.this);
                    binding.summaryItems.setAdapter(summaryAdapter);
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void quantityClickListener(int pos, ProductModel productModel) {
        productModelList.set(pos, productModel);
        totalMrpPrice = 0;
        totalDiscount = 0;
        for (int i = 0; i < productModelList.size(); i++) {
            ProductModel productModel1 = productModelList.get(i);
            double mrp_price = productModel1.getMrp_price();
            double selling_price = productModel1.getProduct_price();

            Long a = productModelList.get(i).getTemp_qty();
            double sellingPrice = (selling_price * a);
            double mrpPrice = (mrp_price * productModelList.get(i).getTemp_qty());
            totalMrpPrice = (int) (totalMrpPrice + Math.round(mrpPrice));
            Log.d(TAG, sellingPrice + "\n" + mrpPrice);
            totalDiscount = (float) (totalDiscount + (mrpPrice - sellingPrice));

        }
        String deliveryCharges = binding.deliveryCharges.getText().toString();
        if (!deliveryCharges.equalsIgnoreCase("FREE")) {
            finalPrice = Integer.parseInt(deliveryCharges) + ((int) totalMrpPrice - (int) totalDiscount);
        } else {
            finalPrice = (int) totalMrpPrice - (int) totalDiscount;
        }
        binding.allItemsPriceText.setText(getString(R.string.rs_symbol)+totalMrpPrice);
        binding.discountPriceText.setText("-" + totalDiscount+getString(R.string.rs_symbol));
        binding.subTotalPriceText.setText(getString(R.string.rs_symbol)+finalPrice);
        binding.totalPrice.setText("Total " + finalPrice + " " + getString(R.string.Rs));
    }

    @Override
    public void onSelectionCLick(String selectionVal, String key) {
        if (key.equalsIgnoreCase(getString(R.string.remove_item))) {
            for (int i = 0; i < cartModelsList.size(); i++) {
                if (cartModelsList.get(i).getCart_product_id().equals(selectionVal)) {
                    firestore.collection("CART_ITEMS").document(cartModelsList.get(i).getCart_doc_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(OrderSummaryActivity.this, "Item Removed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } else {
            //fav item add or remove
            if (key.equalsIgnoreCase(getString(R.string.add_to_fav))) {
                documentReference = firestore.collection("FAVOURITES").document();
                FavouriteModel insertFavModel = new FavouriteModel();
                insertFavModel.setProduct_id(selectionVal);
                insertFavModel.setUserId(UID);
                insertFavModel.setTimestamp(StaticUtills.getTimeStamp());
                String id = documentReference.getId();
                insertFavModel.setFavId(id);
                documentReference.set(insertFavModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderSummaryActivity.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderSummaryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                firestore.collection("FAVOURITES").document(selectionVal).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderSummaryActivity.this, "Removed as Favourites", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderSummaryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void isFavOrNot(final String productId) {
        if (auth.getCurrentUser() != null) {
            Query query = firestore.collection("FAVOURITES");
            query = query.whereEqualTo("product_id", productId);
            query = query.whereEqualTo("userId", UID);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                        Toast.makeText(OrderSummaryActivity.this, "error", Toast.LENGTH_SHORT).show();
                    } else {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    favouriteModel = dc.getDocument().toObject(FavouriteModel.class);
                                    Log.d(TAG, "Fav MODEl Added :" + favouriteModel.toString());
                                    if (favouriteModel.getProduct_id().equals(productId)) {
                                        for (int i = 0; i < productModelList.size(); i++) {
                                            if (productModelList.get(i).getProduct_id().equalsIgnoreCase(favouriteModel.getProduct_id())) {
                                                productModelList.get(i).setTemp_favourite(true);
                                                summaryAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    break;
                                case MODIFIED:
                                    favouriteModel = dc.getDocument().toObject(FavouriteModel.class);
                                    Log.d(TAG, "Fav MODEl Modified :" + favouriteModel.toString());
                                    break;
                                case REMOVED:
                                    favouriteModel = dc.getDocument().toObject(FavouriteModel.class);
                                    Log.d(TAG, "Fav MODEl Removed :" + favouriteModel.toString());
                                    break;
                            }

                        }
                    }
                }
            });
        }
    }

    private void getFavData() {
        firestore.collection("FAVOURITES").whereEqualTo("userId", UID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                    Toast.makeText(OrderSummaryActivity.this, "error", Toast.LENGTH_SHORT).show();
                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                FavouriteModel favModel = dc.getDocument().toObject(FavouriteModel.class);
                                for (int i = 0; i < productModelList.size(); i++) {
                                    if (productModelList.get(i).getProduct_id().equals(favModel.getProduct_id())) {
                                        productModelList.get(i).setTemp_favourite(true);
                                        productModelList.get(i).setTemp_favouriteId(favModel.getFavId());
                                        summaryAdapter.notifyDataSetChanged();
                                    }
                                }
                                break;
                            case MODIFIED:
                                FavouriteModel favModelModified = dc.getDocument().toObject(FavouriteModel.class);
                                for (int i = 0; i < productModelList.size(); i++) {
                                    if (productModelList.get(i).getProduct_id().equals(favModelModified.getProduct_id())) {
                                        productModelList.get(i).setTemp_favourite(false);
                                        productModelList.get(i).setTemp_favouriteId(favModelModified.getFavId());
                                        summaryAdapter.notifyDataSetChanged();
                                    }
                                }
                                break;
                            case REMOVED:
                                FavouriteModel favModelRemoved = dc.getDocument().toObject(FavouriteModel.class);
                                Log.d(TAG, "Get Fav Data Removed Case");
                                for (int i = 0; i < productModelList.size(); i++) {
                                    if (productModelList.get(i).getProduct_id().equals(favModelRemoved.getProduct_id())) {
                                        productModelList.get(i).setTemp_favourite(false);
                                        productModelList.get(i).setTemp_favouriteId(favModelRemoved.getFavId());
                                        summaryAdapter.notifyDataSetChanged();
                                    }
                                }
                                break;
                        }
                    }

                }
            }
        });
    }

    public void placeOrder(View view) {
        String defaultAddress = binding.addressTxt.getText().toString();
        if (defaultAddress.equalsIgnoreCase(getString(R.string.no_address_found))) {
            showSnackBar("Add Address First");
        } else {
            for (int i = 0; i < productModelList.size(); i++) {
                ProductModel proModel=productModelList.get(i);
                DocumentReference ref=firestore.collection(getResources().getString(R.string.ORDERS)).document();
                String order_doc_id=ref.getId();
                String spliter[] = UUID.randomUUID().toString().split("-");
                OrderModel orderModel = new OrderModel();
                orderModel.setOrder_id(order_doc_id);
                orderModel.setTransaction_id(System.currentTimeMillis() + spliter[0]);
                orderModel.setOrdered_date(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
                orderModel.setDelivered_date(CheckUtill.getDateAfterDays(7));
                orderModel.setPayment_method("Online");
                orderModel.setProduct_id(proModel.getProduct_id());
                orderModel.setProduct_name(proModel.getProduct_name());
                orderModel.setSeller_name(proModel.getSeller_name());
                orderModel.setProduct_price(proModel.getProduct_price());
                orderModel.setMrp_price(proModel.getMrp_price());
                orderModel.setProduct_image(proModel.getSubProductModelList().get(0).getProduct_images().get(0).getProduct_image());
                orderModel.setSelectedColor(proModel.getSelectedColor());
                orderModel.setSelectedSize(proModel.getSelectedSize());
                orderModel.setTemp_qty(proModel.getTemp_qty());
                orderModel.setWebUrl("");
                orderModel.setUserId(UID);
                ref.set(orderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showSnackBar("Order Done");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSnackBar(e.getMessage());
                    }
                });
            }

            Intent intent = new Intent(this, OrderSucessActivity.class);
            startActivity(intent);
        }
    }
}