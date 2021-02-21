package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.AddressAdapter;
import com.talla.santhamarket.adapters.HomeCategoryAdapter;
import com.talla.santhamarket.adapters.ProductAdapter;
import com.talla.santhamarket.databinding.ActivityAddressBookBinding;
import com.talla.santhamarket.databinding.ProfileDialogBinding;
import com.talla.santhamarket.interfaces.AddressItemListner;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.UserAddress;

import java.util.ArrayList;
import java.util.List;

public class AddressBookActivity extends AppCompatActivity implements AddressItemListner {
    private ActivityAddressBookBinding binding;
    private ProfileDialogBinding profileDialogBinding;
    private Dialog dialog;
    private String userId, userName, userPhone, alterPhone, country, state, city, pincode, streetAddress, latitude, longitude;
    private boolean isDefault = false;
    private String UID;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private ProgressDialog progressDialog;
    private AddressAdapter addressAdapter;
    List<UserAddress> userAddressList = new ArrayList<>();
    private String clickerAction = "Add";
    private int itemClickedPos;
    private static final String TAG = "AddressBookActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please Wait untill finish");
        progressDialog.setCancelable(false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getCurrentUser().getUid();

        getAddressBookListner();
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void addAddress(View view) {
        clickerAction = "Add";
        showAddressDialog();
    }

    private void showAddressDialog() {
        dialog = new Dialog(this, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen);
        dialog.setCancelable(true);
        profileDialogBinding = ProfileDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(profileDialogBinding.getRoot());
        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        if (clickerAction.equalsIgnoreCase("Add")) {
            profileDialogBinding.toolbarTitle.setText("Add New Address");
        }else {
            setDataToDialog();
        }

        String indiaStates[] = getResources().getStringArray(R.array.india_states);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddressBookActivity.this, R.layout.qty_custom_spin, R.id.spinText, indiaStates);
        profileDialogBinding.state.setAdapter(arrayAdapter);

        String indiaCities[] = getResources().getStringArray(R.array.india_top_places);
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<>(AddressBookActivity.this, R.layout.qty_custom_spin, R.id.spinText, indiaCities);
        profileDialogBinding.city.setAdapter(citiesAdapter);

        profileDialogBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        profileDialogBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                addDataToDb();
                progressDialog.dismiss();
            }
        });

    }

    private void addDataToDb() {
        userId = UID;
        userName = profileDialogBinding.userName.getText().toString().trim();
        country = profileDialogBinding.country.getText().toString().trim();
        state = profileDialogBinding.state.getText().toString().trim();
        city = profileDialogBinding.city.getText().toString().trim();
        userPhone = profileDialogBinding.phoneNumber.getText().toString().trim();
        alterPhone = profileDialogBinding.alternateNumber.getText().toString().trim();
        pincode = profileDialogBinding.pincode.getText().toString().trim();
        streetAddress = profileDialogBinding.streetAddress.getText().toString().trim();

        if (userName.length() == 0) {
            profileDialogBinding.userName.setError("Empty");
            profileDialogBinding.userName.requestFocus();
            return;
        } else if (country.length() == 0) {
            profileDialogBinding.country.setError("Empty");
            profileDialogBinding.country.requestFocus();
            return;
        } else if (state.length() == 0) {
            profileDialogBinding.state.setError("Empty");
            profileDialogBinding.state.requestFocus();
            return;
        } else if (city.length() == 0) {
            profileDialogBinding.city.setError("Empty");
            profileDialogBinding.city.requestFocus();
            return;
        } else if (userPhone.length() == 0 && alterPhone.length() == 0) {
            profileDialogBinding.phoneNumber.setError("Empty");
            profileDialogBinding.phoneNumber.requestFocus();
            return;
        } else if (pincode.length() == 0) {
            profileDialogBinding.pincode.setError("Empty");
            profileDialogBinding.pincode.requestFocus();
            return;
        } else if (streetAddress.length() == 0) {
            profileDialogBinding.streetAddress.setError("Empty");
            profileDialogBinding.streetAddress.requestFocus();
            return;
        } else {
            progressDialog.show();
            UserAddress userAddress = new UserAddress();
            userAddress.setUserId(userId);
            userAddress.setUser_name(userName);
            userAddress.setUser_country(country);
            userAddress.setUser_state(state);
            userAddress.setUser_city(city);
            userAddress.setUser_phone(userPhone);
            userAddress.setUser_alter_phone(alterPhone);
            userAddress.setUser_pincode(pincode);
            userAddress.setUser_streetAddress(streetAddress);
            userAddress.setDefaultAddress(false);
            if (clickerAction.equalsIgnoreCase("Add")) {
                insertIntoData(userAddress);
            } else {
                userAddress.setDocID(userAddressList.get(itemClickedPos).getDocID());
                updateData(userAddress);
            }
        }
    }

    private void insertIntoData(UserAddress userAddress) {
        firestore.collection("Address Book").document().set(userAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Sucessfully saved Address to server");
                showSnackBar("Succesfully Added");
                Toast.makeText(AddressBookActivity.this, "Succesfully Added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error Occured while saving address to server " + e.getMessage());
                showSnackBar("Error Occured " + e.getMessage());
                dialog.dismiss();
            }
        });
    }

    private void updateData(UserAddress userAddress) {
        Log.d(TAG, "UPDATE Documnet Address :" + userAddress.getDocID());
        firestore.collection("Address Book").document(userAddress.getDocID()).set(userAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Sucessfully Updated Address to server");
                showSnackBar("Succesfully Updated");
                Toast.makeText(AddressBookActivity.this, "Succesfully Updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error Occured while Updating address to server " + e.getMessage());
                showSnackBar("Error Occured " + e.getMessage());
                dialog.dismiss();
            }
        });
    }

    private void deleteItem(UserAddress userAddress) {
        firestore.collection("Address Book").document(userAddress.getDocID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        showSnackBar("Sucessfully Deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        showSnackBar("Error while Deleting " + e.getMessage());
                    }
                });
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void statusBarColor() {
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_white));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void getAddressBookListner() {
        firestore.collection("Address Book").whereEqualTo("userId", UID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange dc : value.getDocumentChanges()) {
                    Log.d(TAG, "Doc Key " + dc.getDocument().getId().toString());
                    String docId = dc.getDocument().getId();
                    switch (dc.getType()) {
                        case ADDED:
                            UserAddress userAddress = dc.getDocument().toObject(UserAddress.class);
                            userAddress.setDocID(docId);
                            userAddressList.add(userAddress);
                            Log.d(TAG, "UserModel data added to list: " + userAddress.toString());
                            break;
                        case MODIFIED:
                            UserAddress userAddress1 = dc.getDocument().toObject(UserAddress.class);
                            userAddress1.setDocID(docId);
                            Log.d(TAG, "UserModel data modified to list: " + userAddress1.toString());
                            for (int i = 0; i < userAddressList.size(); i++) {
                                if (userAddressList.get(i).getDocID().equalsIgnoreCase(userAddress1.getDocID())) {
                                    userAddressList.remove(i);
                                    userAddressList.add(i, userAddress1);
                                    break;
                                }
                            }
                            break;
                        case REMOVED:
                            UserAddress userAddress2 = dc.getDocument().toObject(UserAddress.class);
                            userAddress2.setDocID(docId);
                            Log.d(TAG, "UserModel data removed to list: " + userAddress2.toString());
                            for (int i = 0; i < userAddressList.size(); i++) {
                                if (userAddressList.get(i).getDocID().equalsIgnoreCase(userAddress2.getDocID())) {
                                    userAddressList.remove(i);
                                    break;
                                }
                            }
                            break;
                    }
                }
                binding.addressRCV.setHasFixedSize(true);
                addressAdapter = new AddressAdapter(AddressBookActivity.this, userAddressList, AddressBookActivity.this);
                binding.addressRCV.setAdapter(addressAdapter);
                if (userAddressList.size() == 0) {
                    binding.noAddressFound.setVisibility(View.VISIBLE);
                } else {
                    binding.noAddressFound.setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    public void addressItemListner(String item, int pos) {
        itemClickedPos = pos;
        if (item.equalsIgnoreCase("Edit")) {
            clickerAction = "Edit";
            showAddressDialog();
            return;
        } else if (item.equalsIgnoreCase("delete")) {
            deleteItem(userAddressList.get(pos));
            return;
        } else if (item.equalsIgnoreCase("Check")) {

            for (int i = 0; i < userAddressList.size(); i++) {
                progressDialog.show();
                if (userAddressList.get(pos).getDocID().equalsIgnoreCase(userAddressList.get(i).getDocID())) {
                    userAddressList.get(pos).setDefaultAddress(true);
                } else {
                    userAddressList.get(pos).setDefaultAddress(false);
                }
                firestore.collection("Address Book").document(userAddressList.get(i).getDocID()).update("defaultAddress", userAddressList.get(pos).isDefaultAddress()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                    }
                });
            }
            Toast.makeText(this, "Added as  Default Address", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDataToDialog() {
        UserAddress userAddress=userAddressList.get(itemClickedPos);
        profileDialogBinding.userName.setText(userAddress.getUser_name());
        profileDialogBinding.country.setText(userAddress.getUser_country());
        profileDialogBinding.state.setText(userAddress.getUser_state());
        profileDialogBinding.city.setText(userAddress.getUser_city());
        profileDialogBinding.phoneNumber.setText(userAddress.getUser_phone());
        profileDialogBinding.alternateNumber.setText(userAddress.getUser_alter_phone());
        profileDialogBinding.pincode.setText(userAddress.getUser_pincode());
        profileDialogBinding.streetAddress.setText(userAddress.getUser_streetAddress());
        profileDialogBinding.toolbarTitle.setText("Update Data");
        profileDialogBinding.saveBtn.setText("Update");
    }


}