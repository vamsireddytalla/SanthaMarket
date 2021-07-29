package com.talla.santhamarket.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.MultiCartActivity;
import com.talla.santhamarket.adapters.LocalCartAdapter;
import com.talla.santhamarket.adapters.LocalProductsAdapter;
import com.talla.santhamarket.adapters.SummaryAdapter;
import com.talla.santhamarket.databinding.FragmentLocalItemsBinding;
import com.talla.santhamarket.interfaces.ChartsClickListner;
import com.talla.santhamarket.interfaces.PaymentListner;
import com.talla.santhamarket.interfaces.QuantityClickListner;
import com.talla.santhamarket.models.CartModel;
import com.talla.santhamarket.models.FavouriteModel;
import com.talla.santhamarket.models.FinalPayTransferModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.utills.CheckInternet;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.ArrayList;
import java.util.List;

public class LocalItemsFragment extends Fragment implements QuantityClickListner, ChartsClickListner {
    private FragmentLocalItemsBinding binding;
    private String UID;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private LocalCartAdapter localCartAdapter;
    private List<CartModel> cartModelsList = new ArrayList<>();
    private List<ProductModel> productModelList = new ArrayList<>();
    private int totalMrpPrice = 0;
    private float totalDiscount;
    private int finalPrice, deliveryCharges;
    private MultiCartActivity homeActivity;
    private FinalPayTransferModel finalPayTransferModel = new FinalPayTransferModel();
    private PaymentListner paymentListner;
    private static String TAG = "LocalItemsFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocalItemsBinding.inflate(inflater, container, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getCurrentUser().getUid();
        documentReference = firestore.collection(homeActivity.getResources().getString(R.string.FAVOURITES)).document(UID);
        localCartAdapter = new LocalCartAdapter(homeActivity, productModelList, this, this);
        binding.message.setSelected(true);

        getCartItems();

        binding.placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckInternet.checkVPN(homeActivity) && CheckInternet.checkInternet(homeActivity)) {

                    if (finalPrice <= 0 || finalPrice < 20) {
                        showDialog("Minimum Requirement", "Total Cart Value Atleast 20rs\nAdd some more items to Cart to proceed");
                    } else {
                        finalPayTransferModel.setProductModelsList(productModelList);
                        finalPayTransferModel.setTotalPayment(finalPrice);
                        finalPayTransferModel.setCartModelList(cartModelsList);
                        paymentListner.paymentClickListen(finalPayTransferModel);
                    }
                } else {
                    showSnackBar("Check Internet Connection/Disconnect VPN");
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        homeActivity = (MultiCartActivity) activity;
        try {
            paymentListner = (PaymentListner) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    private void getCartItems() {
        firestore.collection(homeActivity.getResources().getString(R.string.CART_ITEMS))
                .whereEqualTo("user_id", UID)
                .whereEqualTo("itemTypeModel.local", true).orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                        localCartAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                if (cartModelsList.size() < 0) {
                                    binding.globalRoot.setVisibility(View.GONE);
                                    binding.globalProgress.setVisibility(View.GONE);
                                    binding.paymentRoot.setVisibility(View.GONE);
                                    binding.noItemsFound.setVisibility(View.VISIBLE);

                                }
                                break;
                        }
                    }
                    if (cartModelsList.size() > 0) {
                        getFavData();
                    } else {
                        binding.globalRoot.setVisibility(View.GONE);
                        binding.globalProgress.setVisibility(View.GONE);
                        binding.paymentRoot.setVisibility(View.GONE);
                        binding.noItemsFound.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    private void getProdBasedOnProdId(final String productId, final CartModel cartModel) {
        firestore.collection(homeActivity.getResources().getString(R.string.PRODUCTS)).whereEqualTo("product_id", productId).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                    productModel.setTemp_qty(1);
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
                                        productModel1.setTemp_qty(1);
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
                    binding.itemsRCV.setHasFixedSize(true);
                    localCartAdapter.setProductModelList(productModelList);
                    binding.itemsRCV.setAdapter(localCartAdapter);
                }
                //execute after code here
                if (productModelList.size() > 0) {
                    binding.globalProgress.setVisibility(View.GONE);
                    binding.noItemsFound.setVisibility(View.GONE);
                    binding.paymentRoot.setVisibility(View.VISIBLE);
                    binding.globalRoot.setVisibility(View.VISIBLE);
                } else {
                    binding.globalRoot.setVisibility(View.GONE);
                    binding.globalProgress.setVisibility(View.GONE);
                    binding.paymentRoot.setVisibility(View.GONE);
                    binding.noItemsFound.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getFavData() {
        firestore.collection(homeActivity.getResources().getString(R.string.FAVOURITES)).whereEqualTo("userId", UID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                    Toast.makeText(homeActivity, "error", Toast.LENGTH_SHORT).show();
                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                FavouriteModel favModel = dc.getDocument().toObject(FavouriteModel.class);
                                for (int i = 0; i < productModelList.size(); i++) {
                                    if (productModelList.get(i).getProduct_id().equals(favModel.getProduct_id())) {
                                        productModelList.get(i).setTemp_favourite(true);
                                        productModelList.get(i).setTemp_favouriteId(favModel.getFavId());
                                        localCartAdapter.notifyDataSetChanged();
                                    }
                                }
                                break;
                            case MODIFIED:
                                FavouriteModel favModelModified = dc.getDocument().toObject(FavouriteModel.class);
                                for (int i = 0; i < productModelList.size(); i++) {
                                    if (productModelList.get(i).getProduct_id().equals(favModelModified.getProduct_id())) {
                                        productModelList.get(i).setTemp_favourite(false);
                                        productModelList.get(i).setTemp_favouriteId(favModelModified.getFavId());
                                        localCartAdapter.notifyDataSetChanged();
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
                                        localCartAdapter.notifyDataSetChanged();
                                    }
                                }
                                break;
                        }
                    }

                }
                //execute here

            }
        });
    }

    @Override
    public void onSelectionCLick(String selectionVal, String key) {
        if (key.equalsIgnoreCase(getString(R.string.remove_item))) {
            for (int i = 0; i < cartModelsList.size(); i++) {
                if (cartModelsList.get(i).getCart_product_id().equals(selectionVal)) {
                    firestore.collection(homeActivity.getResources().getString(R.string.CART_ITEMS)).document(cartModelsList.get(i).getCart_doc_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(homeActivity, "Item Removed", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(homeActivity, "Added to Favourites", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(homeActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                firestore.collection("FAVOURITES").document(selectionVal).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(homeActivity, "Removed as Favourites", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(homeActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void quantityClickListener(int pos, ProductModel productModel) {
        productModelList.set(pos, productModel);
        totalMrpPrice = 0;
        totalDiscount = 0;
        for (int i = 0; i < productModelList.size(); i++) {
            ProductModel productModel1 = productModelList.get(i);
            if (!productModel1.isOut_of_stock() && !(productModel1.getTotalStock().equals(productModel1.getSelled_items()))) {
                double mrp_price = productModel1.getMrp_price();
                double selling_price = productModel1.getProduct_price();

                int a = productModelList.get(i).getTemp_qty();
                double sellingPrice = (selling_price * a);
                double mrpPrice = (mrp_price * productModelList.get(i).getTemp_qty());
                totalMrpPrice = (int) (totalMrpPrice + Math.round(mrpPrice));
                Log.d(TAG, sellingPrice + "\n" + mrpPrice);
                totalDiscount = (float) (totalDiscount + (mrpPrice - sellingPrice));
            }
        }


        finalPrice = ((int) totalMrpPrice - (int) totalDiscount);
        deliveryCharges = (finalPrice * 10) / 100;
        finalPrice = finalPrice + deliveryCharges;

        binding.allItemsPriceText.setText(homeActivity.getResources().getString(R.string.rs_symbol) + CheckUtill.FormatCost(totalMrpPrice));
        binding.discountPriceText.setText("-" + CheckUtill.FormatCost((int) totalDiscount) + homeActivity.getResources().getString(R.string.rs_symbol));
        binding.subTotalPriceText.setText(homeActivity.getResources().getString(R.string.rs_symbol) + CheckUtill.FormatCost(finalPrice));
        binding.totalPrice.setText("Total " + CheckUtill.FormatCost(finalPrice) + " " + homeActivity.getResources().getString(R.string.Rs));
        binding.deliveryCharges.setText((homeActivity.getResources().getString(R.string.rs_symbol) + deliveryCharges));
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(homeActivity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void showDialog(final String title, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(homeActivity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (title.equalsIgnoreCase(getString(R.string.error_occured))) {
                    dialogInterface.dismiss();
                } else {
                    dialogInterface.dismiss();
                }

            }
        });
        alertDialogBuilder.show();
    }

}