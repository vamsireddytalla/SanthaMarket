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
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.CartTabsAdapter;
import com.talla.santhamarket.databinding.ActivityMultiCartBinding;
import com.talla.santhamarket.fragments.GlobalItemsFragment;
import com.talla.santhamarket.fragments.LocalItemsFragment;
import com.talla.santhamarket.interfaces.PaymentListner;
import com.talla.santhamarket.models.FinalPayTransferModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.ServerModel;
import com.talla.santhamarket.models.SpecificationModel;
import com.talla.santhamarket.models.UserAddress;
import com.talla.santhamarket.utills.CheckInternet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MultiCartActivity extends AppCompatActivity implements PaymentListner {
    private ActivityMultiCartBinding binding;
    private CartTabsAdapter cartTabsAdapter;
    private List<Fragment> fragmentList;
    private Dialog progressDialog;
    private String UID;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private int totalAddress = 0;
    private int selectedTabPosition = 0;
    private ServerModel serverModel;
    private UserAddress userAddress;
    private ListenerRegistration serverListner;
    private FinalPayTransferModel finalPayTransferModel;
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

        } else {
            showDialog("Check Internet Connection", "Invalid Network Connection");
        }
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

    public void startPayment() {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_icAel9lvJ5HjQ3");
        checkout.setImage(R.drawable.btn_bg);
        try {
            JSONObject options = new JSONObject();
            options.put("name", userAddress.getUser_name());
            options.put("description", "Reference No. #123456");
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            String id = UUID.randomUUID().toString();
            options.put("order_id", id);
            options.put("currency", "INR");
            options.put("amount", "");
            options.put("prefill.email", "");
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

    }

    private void finalStep()
    {
        String defaultAddress = binding.addressTxt.getText().toString();
        if (defaultAddress.equalsIgnoreCase(getString(R.string.no_address_found))) {
            showSnackBar("Add Address First");
        }else {

        }
    }


}