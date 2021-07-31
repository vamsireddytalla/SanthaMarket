package com.talla.santhamarket.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.DetailOrderActivity;
import com.talla.santhamarket.activities.HomeActivity;
import com.talla.santhamarket.activities.OrderSummaryActivity;
import com.talla.santhamarket.adapters.OrdersAdapter;
import com.talla.santhamarket.adapters.SummaryAdapter;
import com.talla.santhamarket.databinding.CustomProgressDialogBinding;
import com.talla.santhamarket.databinding.FragmentMyOrdersBinding;
import com.talla.santhamarket.interfaces.OnFragmentListner;
import com.talla.santhamarket.models.CartModel;
import com.talla.santhamarket.models.OrderModel;

import java.util.ArrayList;
import java.util.List;


public class MyOrdersFragment extends Fragment {
    private FragmentMyOrdersBinding binding;
    private HomeActivity activity;
    private OnFragmentListner fragmentListner;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private FirebaseAuth auth;
    private String UID;
    private Dialog progressDialog;
    private OrdersAdapter ordersAdapter;
    private List<OrderModel> orderModelList = new ArrayList<>();
    private ListenerRegistration ordersListner;
    private static String TAG = "MyOrdersFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyOrdersBinding.inflate(inflater, container, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getUid();
        dialogIninit();
        getOrders();
        return binding.getRoot();
    }


    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = (HomeActivity) activity;
        try {
            fragmentListner = (OnFragmentListner) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
        fragmentListner.fragmentChangeListner(1);
    }


    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rootFrame, fragment);
        ft.commit();
    }


    private void getOrders() {
        if (!orderModelList.isEmpty()) {
            orderModelList.clear();
        }
        binding.progressCircle.setVisibility(View.VISIBLE);
        ordersListner = firestore.collection(activity.getResources().getString(R.string.ORDERS)).whereEqualTo("userId", UID).orderBy("ordered_date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    binding.progressCircle.setVisibility(View.VISIBLE);
                    Log.d(TAG, "ERROR :" + error.getLocalizedMessage());
                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, dc.getDocument().toObject(Object.class).toString());
                                OrderModel orderModel = dc.getDocument().toObject(OrderModel.class);
                                orderModelList.add(orderModel);
                                Log.d(TAG, "Ordered data added to list: " + orderModel.toString());
                                break;
                            case MODIFIED:
                                OrderModel orderModelModified = dc.getDocument().toObject(OrderModel.class);
                                Log.d(TAG, "Ordered data modified to list: " + orderModelModified.toString());
                                for (int i = 0; i < orderModelList.size(); i++) {
                                    if (orderModelList.get(i).getOrder_id().equalsIgnoreCase(orderModelModified.getOrder_id())) {
                                        orderModelList.remove(i);
                                        orderModelList.add(i, orderModelModified);
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                OrderModel orderModelRemoved = dc.getDocument().toObject(OrderModel.class);
                                Log.d(TAG, "Ordered data removed to list: " + orderModelRemoved.toString());
                                for (int i = 0; i < orderModelList.size(); i++) {
                                    if (orderModelList.get(i).getOrder_id().equalsIgnoreCase(orderModelRemoved.getOrder_id())) {
                                        orderModelList.remove(i);
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                    if (orderModelList.isEmpty()) {
                        binding.progressCircle.setVisibility(View.GONE);
                        binding.noOrdersFound.setVisibility(View.VISIBLE);
                    }
                    binding.itemsRCV.setHasFixedSize(true);
                    ordersAdapter = new OrdersAdapter(activity, orderModelList);
                    binding.itemsRCV.setAdapter(ordersAdapter);
                    binding.progressCircle.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        ordersListner.remove();
    }


    public void dialogIninit() {
        progressDialog = new Dialog(activity);
        CustomProgressDialogBinding customProgressDialogBinding = CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }
}