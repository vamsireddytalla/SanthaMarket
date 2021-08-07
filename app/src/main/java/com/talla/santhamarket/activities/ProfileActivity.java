package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ActivityProfileBinding;
import com.talla.santhamarket.databinding.ProfileBottomSheetBinding;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.UserAddress;
import com.talla.santhamarket.models.UserModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST_CODE = 500;
    private ActivityProfileBinding binding;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private String UID;
    private FirebaseAuth auth;
    private Uri pickedImageUri;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private ListenerRegistration profileDataListner,addressListner;
    private Dialog progressDialog;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialogIninit();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getCurrentUser().getUid();
        documentReference = firestore.collection(getString(R.string.USERS)).document(UID);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference(getString(R.string.PROFILE_PICS));

        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            int version = pInfo.versionCode;
           binding.appVersion.setText("App Version :"+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        getProfileData();

    }

    private void getProfileData() {
        progressDialog.show();
        profileDataListner = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "onEvent: GetProfileData " + error.getMessage());
                    progressDialog.dismiss();
                    showSnackBar(error.getMessage());
                } else {
                    if (snapshot != null && snapshot.exists()) {
                        UserModel userModel = snapshot.toObject(UserModel.class);
                        if (userModel != null)
                            setDataToUi(userModel);
                    } else {
                        Log.d(TAG, "Current data: null");
                        progressDialog.dismiss();
                        showSnackBar("Current data: Empty");
                    }
                }
            }
        });
    }

    private void getAddressData() {
        addressListner=firestore.collection(getString(R.string.ADDRESS_BOOK)).whereEqualTo("userId", UID).whereEqualTo("defaultAddress", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "onEvent: getAddressData " + error.getMessage());
                    progressDialog.dismiss();
                    showSnackBar(error.getMessage());
                } else {
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            UserAddress userAddress = doc.toObject(UserAddress.class);
                            binding.addressProfile.setText(userAddress.getUser_country() + "," + userAddress.getUser_state() + "\n" + userAddress.getUser_city() + "," + userAddress.getUser_pincode() + "\n" + userAddress.getUser_streetAddress());
                        }
                    }
                }
            }
        });
        progressDialog.dismiss();
    }

    private void setDataToUi(UserModel userModel) {
        Glide.with(this).load(userModel.getImage_url()).fitCenter().placeholder(getResources().getDrawable(R.drawable.male_icon)).into(binding.profileImageView);
        binding.profileUsernameText.setText(userModel.getUser_name());
        binding.profileUserNameEdit.setText(userModel.getUser_name());
        binding.profileEmailText.setText(userModel.getUser_email());
        binding.profilePhoneEdit.setText(userModel.getUser_number());
        getAddressData();
        progressDialog.dismiss();
    }

    public void uploadProfileImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pickedImageUri = data.getData();
            compressAndUpload(pickedImageUri);
        }
    }

    private void compressAndUpload(Uri uri) {
        if (uri != null) {
            progressDialog.show();
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 70, baos);
            byte[] data = baos.toByteArray();
//            Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
            storageReference.child(UID).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    do {
                    } while (!uriTask.isComplete());
                    String newImagePath = ((Uri) uriTask.getResult()).toString();
                    documentReference.update("image_url", newImagePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Succesfully Uploaded Image", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            getProfileData();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Uploaded Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            });

        }
    }

    private void profileDataBottomSheet(final String heading, String value) {
        View profileBottomView;
        final ProfileBottomSheetBinding profileBottomSheetBinding;
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomSheetStyle);

        profileBottomSheetBinding = ProfileBottomSheetBinding.inflate(getLayoutInflater());
        profileBottomView = profileBottomSheetBinding.getRoot();
        bottomSheetDialog.setContentView(profileBottomView);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        profileBottomSheetBinding.profileUpdateName.setText(heading);
        profileBottomSheetBinding.profileUpdateEditText.setText(value);
        profileBottomSheetBinding.profileUpdateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        profileBottomSheetBinding.profileUpdateEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String changedValue = profileBottomSheetBinding.profileUpdateEditText.getText().toString().trim();
                if (changedValue != null && !changedValue.isEmpty()) {
                    documentReference.update(heading, changedValue).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            getProfileData();
                        }
                    });
                    bottomSheetDialog.dismiss();
                } else {
                    profileBottomSheetBinding.profileUpdateEditText.setError("Check Data");
                    profileBottomSheetBinding.profileUpdateEditText.requestFocus();
                }
            }
        });
        bottomSheetDialog.show();
    }

    public void logout(View view) {
        auth.signOut();
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void changeProfileUserName(View view) {
        profileDataBottomSheet("user_name", binding.profileUserNameEdit.getText().toString());
    }

    public void profileEmail(View view) {
        profileDataBottomSheet("user_email", binding.profileEmailText.getText().toString());
    }

    public void addDefAddress(View view) {
        Intent intent = new Intent(this, AddressBookActivity.class);
        startActivity(intent);
    }

    public void profileBackBtnClick(View view) {
        finish();
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void dialogIninit() {
        progressDialog = new Dialog(this);
        com.talla.santhamarket.databinding.CustomProgressDialogBinding customProgressDialogBinding = com.talla.santhamarket.databinding.CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileDataListner.remove();
        addressListner.remove();
    }


}