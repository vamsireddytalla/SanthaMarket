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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.CartTabsAdapter;
import com.talla.santhamarket.databinding.ActivityMultiCartBinding;
import com.talla.santhamarket.fragments.GlobalItemsFragment;
import com.talla.santhamarket.fragments.LocalItemsFragment;
import com.talla.santhamarket.interfaces.PaymentListner;
import com.talla.santhamarket.models.BackGroundModel;
import com.talla.santhamarket.models.CartModel;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.DeliveryModel;
import com.talla.santhamarket.models.FinalPayTransferModel;
import com.talla.santhamarket.models.OrderModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.SCModel;
import com.talla.santhamarket.models.ServerModel;
import com.talla.santhamarket.models.SpecificationModel;
import com.talla.santhamarket.models.UserAddress;
import com.talla.santhamarket.models.UserModel;
import com.talla.santhamarket.services.BackGroundService;
import com.talla.santhamarket.utills.CheckInternet;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
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
    private UserModel userModel;
    private SCModel curlObj;
    private FinalPayTransferModel finalPay;
    private ListenerRegistration serverListner, profileListner, addressDataListner, addressCountListner;
    private String transactionType = "COD", orderID, transactionID, orderDocId, receiptId;
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


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tabSelection = bundle.getInt(getString(R.string.cart_typ_key));
        }


        if (CheckInternet.checkInternet(this) && !CheckInternet.checkVPN(this)) {
            getAddressCount();
            getDeviceInfo();
            getProfileData();

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
            showDialog("Invalid Connection", "Check Internet Connection/Disconnect VPN");
        }


        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void openAddressBook(View view) {
        Intent intent = new Intent(this, AddressBookActivity.class);
        startActivity(intent);
    }

    private void getAddressData() {
        progressDialog.show();
        Query addreRef = firestore.collection(getString(R.string.ADDRESS_BOOK)).whereEqualTo("userId", UID).whereEqualTo("defaultAddress", true);
        addressDataListner = addreRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    progressDialog.dismiss();
                    showDialog("Adddress Error", "Unable to fetch Address Details" + error.getLocalizedMessage());
                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                userAddress = dc.getDocument().toObject(UserAddress.class);
                                userAddress.setDocID(documentReference.getId());
                                binding.changeAddress.setText(R.string.add_or_change);
                                binding.addressTxt.setText(userAddress.getUser_name() + "\n" + userAddress.getUser_country() + " , " + userAddress.getUser_state() + "\n" + userAddress.getUser_city() + " , " + userAddress.getUser_pincode() + "\n" + userAddress.getUser_streetAddress());
                                progressDialog.dismiss();
                                break;
                        }
                    }

                }
            }
        });

    }

    private void getAddressCount() {
        Query query = firestore.collection(getString(R.string.ADDRESS_BOOK)).whereEqualTo("userId", UID);
        addressCountListner = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error :" + error.getMessage());
                    showDialog("Address List Error", error.getMessage());
                } else {
                    totalAddress = value.getDocuments().size();
                    if (totalAddress > 0) {
                        getAddressData();
                    } else {
                        binding.changeAddress.setText(R.string.add_new_address);
                    }
                }
            }
        });
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

    private void getProfileData() {
        DocumentReference profileDocRef = firestore.collection(this.getResources().getString(R.string.USERS)).document(UID);

        profileListner = profileDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    showDialog("Info Error", error.getMessage());
                } else {
                    if (value != null && value.exists()) {
                        userModel = value.toObject(UserModel.class);
                    } else {
                        showSnackBar("Error Occured in Server Info");
                    }
                }
            }
        });


    }

    @Override
    public void paymentClickListen(FinalPayTransferModel finalPayTransferModel) {
        if (finalPayTransferModel != null) {
            boolean isLocal = finalPayTransferModel.getProductModelsList().get(0).getItemTypeModel().isLocal();
            if (isLocal) {
                localFinalStep(finalPayTransferModel);
            } else {
                finalPay = finalPayTransferModel;
                if (userAddress != null && userAddress.getUser_name() != null && !userAddress.getUser_name().isEmpty()) {
                    if (finalPay.getTotalPayment() != 0 && finalPay.getTotalPayment() > 20) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MultiCartActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.show();
                                    }
                                });
                                if (orderID != null && !orderID.isEmpty() && orderDocId != null && !orderDocId.isEmpty()) {
                                    updateOrderIdData(orderDocId);
                                } else {
                                    orderID = makeCurl(finalPay.getTotalPayment());
                                    addOrderIdData();
                                }
                            }
                        }).start();
                    } else {
                        showDialog("Total Amount Error", "Minimum Total amount must be above 20 rupees");
                    }
                } else {
                    showSnackBar("Please Add Address to Continue !");
                }

            }
        } else {
            showDialog("Error Occured", getString(R.string.final_no_data_found));
        }
    }

    private void addOrderIdData() {
        DocumentReference orderIDsref = firestore.collection(getString(R.string.PAYMENT_STATUS)).document();
        orderDocId = orderIDsref.getId();
        curlObj.setAttempts(curlObj.getAttempts() + 1);
        curlObj.setAmount(finalPay.getTotalPayment());
        curlObj.setAmountDue(finalPay.getTotalPayment());
        curlObj.setUser_name(userAddress.getUser_name());
        String userPhone = "";
        if (userAddress.getUser_phone() != null || !userAddress.getUser_phone().isEmpty()) {
            userPhone = userAddress.getUser_phone();
        } else {
            userPhone = userAddress.getUser_alter_phone();
        }
        curlObj.setPhoneNumber(userPhone);
        curlObj.setTimestamp(StaticUtills.getTimeStamp());
        orderIDsref.set(curlObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (orderID != null && !orderID.isEmpty()) {
                    startPayment(orderID);
                } else {
                    MultiCartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.show();
                            showDialog("Server Order-ID Adding Error", "Contact santhamarketonline@gmail.com");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                showDialog("Error Occuered retry", e.getLocalizedMessage());
            }
        });
    }

    private void updateOrderIdData(String docId) {
        DocumentReference orderIDsref = firestore.collection(getString(R.string.PAYMENT_STATUS)).document(docId);
        curlObj.setAttempts(curlObj.getAttempts() + 1);
        curlObj.setAmount(finalPay.getTotalPayment());
        curlObj.setAmountDue(finalPay.getTotalPayment());
        curlObj.setUser_name(userAddress.getUser_name());
        String userPhone = "";
        if (userAddress.getUser_phone() != null || !userAddress.getUser_phone().isEmpty()) {
            userPhone = userAddress.getUser_phone();
        } else {
            userPhone = userAddress.getUser_alter_phone();
        }
        curlObj.setPhoneNumber(userPhone);
        curlObj.setTimestamp(StaticUtills.getTimeStamp());
        orderIDsref.set(curlObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (orderID != null && !orderID.isEmpty()) {
                    startPayment(orderID);
                } else {
                    MultiCartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.show();
                            showDialog("Server Order-ID Update Error", "Contact santhamarketonline@gmail.com");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                showDialog("Error Occuered retry", e.getLocalizedMessage());
            }
        });
    }

    public void startPayment(String orderID) {

        Checkout checkout = new Checkout();
        checkout.setKeyID(serverModel.getPayment_key());
        try {
            JSONObject options = new JSONObject();
            options.put("name", userAddress.getUser_name());
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", orderID);
            options.put("currency", "INR");
            int payAmount = Math.round(Float.parseFloat(String.valueOf(finalPay.getTotalPayment())) * 100);
            options.put("amount", payAmount);

            if (userModel != null && userModel.getUser_email() != null && !userModel.getUser_email().isEmpty()) {
                options.put("prefill.email", userModel.getUser_email());
            }

            String userPhone = "";
            if (userAddress.getUser_phone() != null || !userAddress.getUser_phone().isEmpty()) {
                userPhone = userAddress.getUser_phone();
            } else {
                userPhone = userAddress.getUser_alter_phone();
            }
            options.put("prefill.contact", userPhone);
            checkout.open(this, options);
        } catch (Exception e) {
            MultiCartActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
            showDialog("Error Occured", e.getMessage());
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
                orderModel.setPayment_method(getString(R.string.COD));
                orderModel.setDelvery_address(userAddress);
                orderModel.setTransaction_id(transactionID);
                orderModel.setDelivered_date(CheckUtill.getSystemTime(this));
                orderModel.setDelivered(false);
                orderModel.setWebUrl("");
                orderModel.setLocal(true);
                receiptId = UUID.randomUUID().toString();
                String[] ordersIds=receiptId.split("-");
                orderModel.setOrder_id(ordersIds[ordersIds.length-1]);
                int deliveryCharges = (int) (((productModel.getProduct_price() * productModel.getTemp_qty()) * 10) / 100);
                orderModel.setDeliveryCharges(deliveryCharges);
                orderModel.setPaidOrNot(false);
                List<DeliveryModel> deliveryModelList = new ArrayList<>();
                DeliveryModel deliveryModel = new DeliveryModel();
                deliveryModel.setDeliveryDate(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
                deliveryModel.setDeliveryTitle(getString(R.string.ORDERED));
                deliveryModel.setDeliveryMessage("Order has been placed sucessfully");
                deliveryModelList.add(deliveryModel);
                orderModel.setDeliveryModelList(deliveryModelList);
                //after for loop this
                final DocumentReference docRef = firestore.collection(getString(R.string.ORDERS)).document();
                orderModel.setOrder_doc_id(docRef.getId());

                docRef.set(orderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        localDocList.add(docRef.getId());
                        if (localDocList.size() == productModelList.size()) {
                            progressDialog.dismiss();
//                            Intent backIntent = new Intent(MultiCartActivity.this, BackGroundService.class);
//                            backIntent.putExtra("DELETE_CART_ITEMS", (Serializable) finalPay);
//                            startService(backIntent);
                            updateProductDataDeleteCartItems(finalModel);
                            Intent intent = new Intent(MultiCartActivity.this, OrderSucessActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            showDialog("Error Occured", "Order Cancelled");
                        }
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

        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        transactionID = s;
        addPaymentStatus();
    }

    private void addPaymentStatus() {
        progressDialog.show();
        curlObj.setAmountPaid(finalPay.getTotalPayment());
        curlObj.setAmountDue(0);
        curlObj.setTransactionId(transactionID);
        firestore.collection(getString(R.string.PAYMENT_STATUS)).document(orderDocId).set(curlObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onlineFinalStep(finalPay);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //dont change title message in dialog because it has dependency
                showDialog("Unknown Error Occured", "Please Click Ok to retry Again");
            }
        });
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
                orderModel.setPayment_method(getString(R.string.online));
                orderModel.setDelvery_address(userAddress);
                //Transaction id Pending
                orderModel.setTransaction_id(transactionID);
                orderModel.setOrder_id(orderID);
                orderModel.setPayment_status_doc(orderDocId);
                orderModel.setDelivered_date(CheckUtill.getDateAfterDays(7));
                orderModel.setDelivered(false);
                orderModel.setWebUrl("");
                orderModel.setLocal(false);
                orderModel.setDeliveryCharges((int) productModel.getDelivery_charges());
                orderModel.setPaidOrNot(true);
                List<DeliveryModel> deliveryModelList = new ArrayList<>();
                DeliveryModel deliveryModel = new DeliveryModel();
                deliveryModel.setDeliveryDate(CheckUtill.getDateFormat(System.currentTimeMillis(), this));
                deliveryModel.setDeliveryTitle(getString(R.string.ORDERED));
                deliveryModel.setDeliveryMessage("Order has been placed sucessfully");
                deliveryModelList.add(deliveryModel);
                orderModel.setDeliveryModelList(deliveryModelList);
                //after for loop this
                final DocumentReference docRef = firestore.collection(getString(R.string.ORDERS)).document();
                orderModel.setOrder_doc_id(docRef.getId());
                docRef.set(orderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        localDocList.add(docRef.getId());
                        if (localDocList.size() == productModelList.size()) {
                            progressDialog.dismiss();
//                            Intent backIntent = new Intent(MultiCartActivity.this, BackGroundService.class);
//                            backIntent.putExtra("DELETE_CART_ITEMS", (Serializable) finalPay);
//                            startService(backIntent);
                            updateProductDataDeleteCartItems(finalModel);
                            Intent intent = new Intent(MultiCartActivity.this, OrderSucessActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            showDialog("Error Occured", "Order Cancelled");
                        }
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
        }
    }


    @Override
    public void onPaymentError(int i, String s) {
        progressDialog.dismiss();
        showDialog("Payment Error Occured", s);
    }

    public static String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("PAYMENT", output.toString());
        return output.toString();
    }

    private void updateProductDataDeleteCartItems(FinalPayTransferModel finalPayTransferModel)
    {
        progressDialog.show();
        Log.d(TAG, "updateProductDataDeleteCartItems: "+finalPayTransferModel.toString());
        List<CartModel> cartModelList = finalPayTransferModel.getCartModelList();
        List<ProductModel> productModelList = finalPayTransferModel.getProductModelsList();
        for (int i = 0; i < cartModelList.size(); i++) {
            DocumentReference docref = firestore.collection(getString(R.string.CART_ITEMS)).document(cartModelList.get(i).getCart_doc_id());
            int finalI = i;
            if (cartModelList.size()==productModelList.size())
            {
                updateProductStatus(cartModelList.get(i).getCart_product_id(), productModelList.get(i).getTemp_qty());
            }
            docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Deleted Cart Item " + finalI);
                }
            });
        }
        progressDialog.dismiss();
    }

    private void updateProductStatus(String prodDocId,int quantity) {
        DocumentReference docRef = firestore.collection(getString(R.string.PRODUCTS)).document(prodDocId);

        docRef.update("totalStock", FieldValue.increment(-quantity));
        docRef.update("selled_items", FieldValue.increment(quantity));
    }

    private String makeCurl(int amount) {
        String serverKey = serverModel.getPayment_key();
        String serverSecretKey = serverModel.getPayment_secret_key();
        int payAmount = Math.round(Float.parseFloat(String.valueOf(amount)) * 100);
        receiptId = UUID.randomUUID().toString();
        String culString = executeCommand("curl -v -u " + serverKey + ":" + serverSecretKey + " -X POST https://api.razorpay.com/v1/orders -H \"content-type: application/json\" -d '{\"amount\":" + payAmount + ",\"currency\":\"INR\",\"receipt\": \"1\"}'");
        Gson gson = new Gson();
        gson.toJson(culString);
        curlObj = new Gson().fromJson(culString, SCModel.class);
        String orderID = curlObj.getId();
        return orderID;
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
                } else if (title.equalsIgnoreCase("Unknown Error Occured")) {
                    dialogInterface.dismiss();
                    addPaymentStatus();
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverListner.remove();
        profileListner.remove();
        addressDataListner.remove();
        addressCountListner.remove();
    }




}