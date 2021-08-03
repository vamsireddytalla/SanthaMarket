package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ActivityReviewBinding;
import com.talla.santhamarket.databinding.CustomProgressDialogBinding;
import com.talla.santhamarket.models.OrderModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.RatingModel;
import com.talla.santhamarket.models.UserModel;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    private ActivityReviewBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Dialog progressDialog;
    private OrderModel orderModel;
    private RatingModel ratingModel;
    private UserModel userModel;
    private static final String TAG = "ReviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        dialogIninit();
        Intent intent = getIntent();
        if (intent != null) {
            orderModel = (OrderModel) intent.getExtras().getSerializable(getString(R.string.order_status));
            if (orderModel != null) {
                ratingModel = orderModel.getRatingModel();
                binding.productName.setText(orderModel.getProduct_name());
                if (ratingModel != null) {
                    setDataToUi(ratingModel);
                }
            } else {
                finish();
            }
        }

        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getProfileData();

    }

    public void submitRatings(View view) {
        String reviewMessage = binding.reviewMessage.getText().toString().trim();
        float ratings = binding.myRatingBar.getRating();
        if (reviewMessage.isEmpty()) {
            binding.reviewMessage.setError("Empty");
            binding.reviewMessage.requestFocus();
            return;
        } else if (ratings < 0) {
            showSnackBar("Select Rating First");
            return;
        }
        RatingModel ratingModel = new RatingModel();
        ratingModel.setRatingMessage(reviewMessage);
        ratingModel.setRating(ratings);
        progressDialog.show();

        firebaseFirestore.runTransaction(new Transaction.Function<String>() {
            @Nullable
            @Override
            public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                //get avg rating and total rating from product model
                DocumentReference prodDocRef = firebaseFirestore.collection(getString(R.string.PRODUCTS)).document(orderModel.getProduct_id());
                ProductModel productModel = transaction.get(prodDocRef).toObject(ProductModel.class);
                //total rating
                double totalRatings = (double) productModel.getTotal_ratings() + 1;
                //compute new avg ratings
                double oldTotalRatings = (double) (productModel.getAvgRatings() * productModel.getTotal_ratings());
                double newAvgRating = ((oldTotalRatings + ratings) / totalRatings);

                productModel.setTotal_ratings(totalRatings);
                productModel.setAvgRatings(newAvgRating);

                //get rating model from products
                DocumentReference ratDocRef = firebaseFirestore.collection(getString(R.string.PRODUCTS)).document(orderModel.getProduct_id()).collection(getString(R.string.RATINGS)).document(productModel.getProduct_id());
                RatingModel ratingModel1 = transaction.get(ratDocRef).toObject(RatingModel.class);
                if (ratingModel1 == null) {
                    ratingModel1 = new RatingModel();
                }
                ratingModel1.setRating(newAvgRating);
                ratingModel1.setRatingMessage(reviewMessage);
                ratingModel1.setTimestamp(StaticUtills.getTimeStamp());
                if (userModel.getUser_name()!=null)
                {
                    ratingModel1.setUserName(userModel.getUser_name());
                }


                DocumentReference orderDocRef = firebaseFirestore.collection(getString(R.string.ORDERS)).document(orderModel.getOrder_doc_id());

                orderModel.setRatingModel(ratingModel1);
                transaction.set(orderDocRef, orderModel);
                transaction.update(prodDocRef, "total_ratings", totalRatings);
                transaction.update(prodDocRef, "avgRatings", newAvgRating);
                transaction.set(ratDocRef, ratingModel1);

                return "success";
            }
        }).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                progressDialog.dismiss();
                showDialog("Sucessfully Added Rating");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                showDialog(e.getMessage());
            }
        });

    }

    private void setDataToUi(RatingModel ratingModel) {
        binding.reviewMessage.setText(ratingModel.getRatingMessage());
        binding.myRatingBar.setRating((float) ratingModel.getRating());
    }

    private void getProfileData() {
        progressDialog.show();
        DocumentReference docProfRef = firebaseFirestore.collection(getString(R.string.USERS)).document(firebaseAuth.getCurrentUser().getUid());
        docProfRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userModel = document.toObject(UserModel.class);
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Log.d(TAG, "No such document");
                        showDialog("Error Occured Retry");
                    }
                } else {
                    progressDialog.dismiss();
                    Log.d(TAG, "get failed with ", task.getException());
                    showDialog("Error Occured Retry");
                }

            }
        });
    }


    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (message.equalsIgnoreCase("Sucessfully Added Rating")) {
                    dialog.cancel();
                    finish();
                } else if (message.equalsIgnoreCase("Error Occured Retry")) {
                    finish();
                } else {
                    dialog.cancel();
                }

            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void dialogIninit() {
        progressDialog = new Dialog(this);
        CustomProgressDialogBinding customProgressDialogBinding = CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

}