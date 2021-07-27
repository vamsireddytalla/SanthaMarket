package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.CartTabsAdapter;
import com.talla.santhamarket.databinding.ActivityMultiCartBinding;
import com.talla.santhamarket.fragments.GlobalItemsFragment;
import com.talla.santhamarket.fragments.LocalItemsFragment;
import com.talla.santhamarket.interfaces.PaymentListner;
import com.talla.santhamarket.models.DeliveryModel;
import com.talla.santhamarket.models.FinalPayTransferModel;
import com.talla.santhamarket.models.OrderModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.ServerModel;
import com.talla.santhamarket.models.SpecificationModel;
import com.talla.santhamarket.models.UserAddress;
import com.talla.santhamarket.utills.CheckInternet;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MultiCartActivity extends AppCompatActivity implements PaymentListner, PaymentResultListener {
    private ActivityMultiCartBinding binding;
    private CartTabsAdapter cartTabsAdapter;
    private List<Fragment> fragmentList;
    private Dialog progressDialog;
    private String UID;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private int totalAddress = 0;
    private int selectedTabPosition = 0, tabSelection = 0;
    private ServerModel serverModel;
    private UserAddress userAddress;
    private FinalPayTransferModel finalPay;
    private ListenerRegistration serverListner;
    private String transactionID = "COD", orderID;
    private List<String> localDocList = new ArrayList<>();
    private static String TAG = "MultiCartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentList = new ArrayList<>();
        dialogIninit();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getCurrentUser().getUid();
        documentReference = firestore.collection(this.getResources().getString(R.string.FAVOURITES)).document(UID);
        Checkout.preload(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tabSelection = bundle.getInt(getString(R.string.cart_typ_key));
        }


        if (CheckInternet.checkInternet(this) && !CheckInternet.checkVPN(this)) {
            getAddressCount();

            fragmentList.add(new GlobalItemsFragment());
            binding.dynamicTabLayout.addTab(binding.dynamicTabLayout.newTab().setText("Global Products"));
            fragmentList.add(new LocalItemsFragment());
            binding.dynamicTabLayout.addTab(binding.dynamicTabLayout.newTab().setText("Local Products"));

            cartTabsAdapter = new CartTabsAdapter(getSupportFragmentManager(), fragmentList);
            binding.cartItemsViewPager.setAdapter(cartTabsAdapter);
            binding.cartItemsViewPager.setOffscreenPageLimit(1);
            binding.cartItemsViewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.dynamicTabLayout));

            binding.dynamicTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    binding.cartItemsViewPager.setCurrentItem(tab.getPosition());
                    selectedTabPosition = tab.getPosition();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            binding.cartItemsViewPager.setCurrentItem(tabSelection);
        } else {
            showDialog("Check Internet Connection", "Invalid Network Connection");
        }


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
        getDeviceInfo();
    }

    public void proceedNow(View view) {
        Toast.makeText(this, "Proceed Now Clicked" + selectedTabPosition, Toast.LENGTH_SHORT).show();
    }

    public void openAddressBook(View view) {
        Intent intent = new Intent(this, AddressBookActivity.class);
        startActivity(intent);
    }

    private void getAddressData() {
        progressDialog.show();
        firestore.collection(getString(R.string.ADDRESS_BOOK)).whereEqualTo("userId", UID).whereEqualTo("defaultAddress", true).get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Get Address data sucessfull");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "Server response : " + document.getId() + " => " + document.getData());
                        userAddress = document.toObject(UserAddress.class);
                        userAddress.setDocID(document.getId());
                        binding.changeAddress.setText(R.string.add_or_change);
                        binding.addressTxt.setText(userAddress.getUser_name() + "\n" + userAddress.getUser_country()+" , "+ userAddress.getUser_state() + "\n" + userAddress.getUser_city() + " , " + userAddress.getUser_pincode() + "\n" + userAddress.getUser_streetAddress());
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
        Query query = firestore.collection(getString(R.string.ADDRESS_BOOK)).whereEqualTo("userId", UID);
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

    public void dialogIninit() {
        progressDialog = new Dialog(this);
        com.talla.santhamarket.databinding.CustomProgressDialogBinding customProgressDialogBinding = com.talla.santhamarket.databinding.CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
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

    public void startPayment(int totalAmount) {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_8nwGNuKgx0Zk5H");
//        checkout.setImage(R.drawable.btn_bg);
        try {
            JSONObject options = new JSONObject();
            options.put("name", userAddress.getUser_name());
            options.put("description", "cgchcjh");
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            orderID = UUID.randomUUID().toString();
            options.put("order_id", "hvjhvjhvkhjvjv");
            options.put("currency", "INR");
            options.put("amount", "30000");
            options.put("prefill.email", "vamsip140@gmail.com");
            String userPhone = "";
            if (userAddress.getUser_phone() != null || !userAddress.getUser_phone().isEmpty()) {
                userPhone = userAddress.getUser_phone();
            } else {
                userPhone = userAddress.getUser_alter_phone();
            }
            options.put("prefill.contact", userPhone);
            checkout.open(this, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    public void getDeviceInfo() {

        DocumentReference ref = firestore.collection(getString(R.string.DEVICE_INFO)).document("54321");
        serverListner = ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    showDialog("Error Occured !", error.getMessage());
                } else {
                    if (value != null && value.exists()) {
                        serverModel = value.toObject(ServerModel.class);
                    } else {
                        showSnackBar("Error Occured in Server Info");
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverListner.remove();
    }

    @Override
    public void paymentClickListen(FinalPayTransferModel finalPayTransferModel) {
        if (finalPayTransferModel != null) {
            boolean isLocal = finalPayTransferModel.getProductModelsList().get(0).getItemTypeModel().isLocal();
            if (isLocal) {
                localFinalStep(finalPayTransferModel);
            } else {
                finalPay = finalPayTransferModel;
                startPayment(finalPay.getTotalPayment());
            }
        } else {
            showSnackBar(getString(R.string.final_no_data_found));
        }
    }

    private void onlineFinalStep(FinalPayTransferModel finalModel) {
        localDocList.clear();
        if (userAddress == null) {
            showSnackBar("Add Address First");
        } else {
            progressDialog.show();
            final List<ProductModel> productModelList = finalModel.getProductModelsList();
            for (int i = 0; i < productModelList.size(); i++) {
                ProductModel productModel = productModelList.get(i);
                OrderModel orderModel = new OrderModel();
                orderModel.setProduct_id(productModel.getProduct_id());
                orderModel.setProduct_name(productModel.getProduct_name());
                orderModel.setProduct_image(productModel.getSubProductModelList().get(0).getProduct_images().get(0).getProduct_image());
                orderModel.setSelectedColor(productModel.getSelectedColor());
                orderModel.setSelectedSize(productModel.getSelectedSize());
                orderModel.setSelected_quantity(productModel.getTemp_qty());
                orderModel.setProduct_price(productModel.getProduct_price());
                orderModel.setMrp_price(productModel.getMrp_price());
                orderModel.setTotalProductPrice(finalModel.getTotalPayment());
                orderModel.setUserId(UID);
                orderModel.setSeller_name(productModel.getSeller_name());
                orderModel.setSeller_id(productModel.getSellerId());
                orderModel.setOrdered_date(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
                orderModel.setPayment_method("Online");
                orderModel.setDelvery_address(userAddress);
                orderModel.setTransaction_id(transactionID);
                orderModel.setDelivered_date(CheckUtill.getSystemTime(this));
                orderModel.setDelivered(false);
                orderModel.setWebUrl("");
                orderModel.setDeliveryCharges((int) productModel.getDelivery_charges());
                orderModel.setPaidOrNot(true);
                List<DeliveryModel> deliveryModelList = new ArrayList<>();
                DeliveryModel deliveryModel = new DeliveryModel();
                deliveryModel.setDeliveryDate(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
                deliveryModel.setDeliveryTitle("Order Placed");
                deliveryModel.setDeliveryMessage("Order has been placed sucessfully");
                deliveryModelList.add(deliveryModel);
                orderModel.setDeliveryModelList(deliveryModelList);
                //after for loop this
                final DocumentReference docRef = firestore.collection(getString(R.string.ORDERS)).document();
                orderModel.setOrder_id(docRef.getId());
                localDocList.add(docRef.getId());
                docRef.set(orderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (localDocList.size() > 0) {
                            localDocList.remove(0);
                        }
                    }
                });

            }

            if (localDocList.size() == productModelList.size()) {
                progressDialog.dismiss();
                Toast.makeText(MultiCartActivity.this, "Sucess", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                Toast.makeText(MultiCartActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void localFinalStep(FinalPayTransferModel finalModel) {
        localDocList.clear();
        if (userAddress == null) {
            showSnackBar("Add Address First");
        } else {
            progressDialog.show();
            final List<ProductModel> productModelList = finalModel.getProductModelsList();
            for (int i = 0; i < productModelList.size(); i++) {
                ProductModel productModel = productModelList.get(i);
                OrderModel orderModel = new OrderModel();
                orderModel.setProduct_id(productModel.getProduct_id());
                orderModel.setProduct_name(productModel.getProduct_name());
                orderModel.setProduct_image(productModel.getSubProductModelList().get(0).getProduct_images().get(0).getProduct_image());
                orderModel.setSelectedColor(productModel.getSelectedColor());
                orderModel.setSelectedSize(productModel.getSelectedSize());
                orderModel.setSelected_quantity(productModel.getTemp_qty());
                orderModel.setProduct_price(productModel.getProduct_price());
                orderModel.setMrp_price(productModel.getMrp_price());
                orderModel.setTotalProductPrice(finalModel.getTotalPayment());
                orderModel.setUserId(UID);
                orderModel.setSeller_name(productModel.getSeller_name());
                orderModel.setSeller_id(productModel.getSellerId());
                orderModel.setOrdered_date(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
                orderModel.setPayment_method("COD");
                orderModel.setDelvery_address(userAddress);
                orderModel.setTransaction_id(transactionID);
                orderModel.setDelivered_date(CheckUtill.getSystemTime(this));
                orderModel.setDelivered(false);
                orderModel.setWebUrl("");
                int deliveryCharges = (int) (((productModel.getProduct_price() * productModel.getTemp_qty()) * 10) / 100);
                orderModel.setDeliveryCharges(deliveryCharges);
                orderModel.setPaidOrNot(false);
                List<DeliveryModel> deliveryModelList = new ArrayList<>();
                DeliveryModel deliveryModel = new DeliveryModel();
                deliveryModel.setDeliveryDate(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
                deliveryModel.setDeliveryTitle("Order Placed");
                deliveryModel.setDeliveryMessage("Order has been placed sucessfully");
                deliveryModelList.add(deliveryModel);
                orderModel.setDeliveryModelList(deliveryModelList);
                //after for loop this
                final DocumentReference docRef = firestore.collection(getString(R.string.ORDERS)).document();
                orderModel.setOrder_id(docRef.getId());
                localDocList.add(docRef.getId());
                docRef.set(orderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (localDocList.size() > 0) {
                            localDocList.remove(0);
                        }
                    }
                });

            }

            if (localDocList.size() == productModelList.size()) {
                progressDialog.dismiss();
                Toast.makeText(MultiCartActivity.this, "Sucess", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                Toast.makeText(MultiCartActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
            }


        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        onlineFinalStep(finalPay);
    }

    @Override
    public void onPaymentError(int i, String s) {
        showDialog("Payment Error Occured", s);
    }
}