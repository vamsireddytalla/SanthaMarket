package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.OrderStatusAdapter;
import com.talla.santhamarket.databinding.ActivityDetailOrderBinding;
import com.talla.santhamarket.databinding.CancelledDialogBinding;
import com.talla.santhamarket.databinding.CustomProgressDialogBinding;
import com.talla.santhamarket.models.Data;
import com.talla.santhamarket.models.DeliveryModel;
import com.talla.santhamarket.models.FcmResponse;
import com.talla.santhamarket.models.NotificationModel;
import com.talla.santhamarket.models.OrderModel;
import com.talla.santhamarket.models.TokenModel;
import com.talla.santhamarket.models.UserAddress;
import com.talla.santhamarket.serverCalls.ApiClient;
import com.talla.santhamarket.serverCalls.ApiInterface;
import com.talla.santhamarket.utills.CheckInternet;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrderActivity extends AppCompatActivity {
    private ActivityDetailOrderBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String UID;
    private Dialog progressDialog;
    private OrderModel orderModel;
    private OrderStatusAdapter orderStatusAdapter;
    private ApiInterface apiInterface;
    private static final String TAG = "DetailOrderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialogIninit();
        firebaseAuth = FirebaseAuth.getInstance();
        UID = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            orderModel = (OrderModel) intent.getExtras().getSerializable(getString(R.string.order_status));
            if (orderModel != null) {
                setDataToUi(orderModel);
            } else {
                finish();
            }
        }

        binding.cancelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternet.checkInternet(DetailOrderActivity.this)) {
                    cancelProductDialog(orderModel);
                } else {
                    showSnackBar("Check internet");
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setDataToUi(OrderModel orderModel) {

        //set data to product
        binding.productName.setText(orderModel.getProduct_name());
        binding.sellerName.setText(orderModel.getSeller_name());
        binding.quantity.setText("Qty : " + orderModel.getSelected_quantity());
        binding.paymentMethod.setText("Paymnt Mode : " + orderModel.getPayment_method());
        //selected Color
        String selectedColor = orderModel.getSelectedColor();
        if (selectedColor != null && !selectedColor.isEmpty()) {
            binding.itemColor.setVisibility(View.VISIBLE);
            binding.colorItem.setBackgroundColor(Integer.parseInt(selectedColor));
        } else {
            binding.itemColor.setVisibility(View.GONE);
        }

        //size
        if (orderModel.getSelectedSize() != null) {
            String selectedSIze = orderModel.getSelectedSize();
            binding.prodSize.setVisibility(View.VISIBLE);
            binding.prodSize.setText("Size : " + selectedSIze);
        }

        //prod image
        String imgUrl = orderModel.getProduct_image();
        Glide.with(this).load(imgUrl).fitCenter().into(binding.orderedItemImg);

        UserAddress userAddress = orderModel.getDelvery_address();
        binding.addressText.setText(userAddress.getUser_name() + "\n" + userAddress.getUser_country() + " , " + userAddress.getUser_state() + "\n" + userAddress.getUser_city() + " , " + userAddress.getUser_pincode() + "\n" + userAddress.getUser_streetAddress());

        //cancel button checker
        if (orderModel.isDelivered()) {
            binding.cancelItem.setVisibility(View.GONE);
        } else {
            int deliverSize = orderModel.getDeliveryModelList().size();
            String title = orderModel.getDeliveryModelList().get(deliverSize - 1).getDeliveryTitle();
            if (title.equalsIgnoreCase(getString(R.string.CANCELLED))) {
                binding.cancelItem.setVisibility(View.GONE);
            }
        }

        //set order id
        binding.orderId.setText(orderModel.getOrder_id());

        //set price details clearly
        double selling_price = orderModel.getProduct_price();
        double mrp_price = orderModel.getMrp_price();
        int res = StaticUtills.discountPercentage(selling_price, mrp_price);
        binding.sellingPrice.setText(this.getResources().getString(R.string.rs_symbol) + CheckUtill.FormatCost((int) Math.round(selling_price)));
        binding.listPrice.setText(this.getResources().getString(R.string.rs_symbol) + CheckUtill.FormatCost((int) Math.round(mrp_price)));
        binding.listPrice.setPaintFlags(binding.listPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.discountPrice.setText(res + "%OFF");
        binding.deliveryPrice.setText(this.getResources().getString(R.string.rs_symbol) + CheckUtill.FormatCost(orderModel.getDeliveryCharges()));
        binding.totalCalcAmount.setText(CheckUtill.FormatCost((int) (orderModel.getTotalProductPrice()+orderModel.getDeliveryCharges())) + this.getResources().getString(R.string.Rs));
        int priceQty=(int) (orderModel.getSelected_quantity()*orderModel.getProduct_price());
        binding.totItemsQty.setText(("("+orderModel.getSelected_quantity()+"*"+((int)orderModel.getProduct_price())+")")+"="+priceQty+this.getResources().getString(R.string.rs_symbol));


        List<DeliveryModel> deliveryModelList = orderModel.getDeliveryModelList();
        orderStatusAdapter = new OrderStatusAdapter(this, deliveryModelList);
        binding.statusRCV.setAdapter(orderStatusAdapter);
    }

    private void RunAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.animate_text);
        a.reset();
//        binding.process.clearAnimation();
//        binding.process.startAnimation(a);
    }

    public void dialogIninit() {
        progressDialog = new Dialog(this);
        CustomProgressDialogBinding customProgressDialogBinding = CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void showDialog(final String title, String message) {
        final MaterialAlertDialogBuilder materialDialog = new MaterialAlertDialogBuilder(this);
        materialDialog.setTitle(title);
        materialDialog.setMessage(message);
        materialDialog.setCancelable(false);
        materialDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (title.equalsIgnoreCase("Upload Completed")) {
                    dialogInterface.dismiss();
                    finish();
                } else {
                    dialogInterface.dismiss();
                    finish();
                }

            }
        });
        materialDialog.show();
    }

    private void cancelProductDialog(final OrderModel orderModel) {
        final Dialog dialog = new Dialog(this);
        final CancelledDialogBinding cancelledDialogBinding = CancelledDialogBinding.inflate(this.getLayoutInflater());
        dialog.setContentView(cancelledDialogBinding.getRoot());
        dialog.setCancelable(true);
        dialog.show();


        cancelledDialogBinding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cancelMessage = cancelledDialogBinding.reason.getText().toString().trim();
                if (cancelMessage.length() > 5) {
                    progressDialog.show();
                    DocumentReference docref = firebaseFirestore.collection(getString(R.string.ORDERS)).document(orderModel.getOrder_doc_id());
                    DeliveryModel deliveryModel = new DeliveryModel();
                    deliveryModel.setDeliveryDate(StaticUtills.getTimeStamp());
                    deliveryModel.setDeliveryTitle(getString(R.string.CANCELLED));
                    deliveryModel.setDeliveryMessage("Order is cancelled due to " + cancelMessage + "\n" + "Cancelled by : " +"USER");
                    List<DeliveryModel> deliveryModelList = new ArrayList<>();
                    List<DeliveryModel> deliveryModelList1 = orderModel.getDeliveryModelList();
                    deliveryModelList1.add(deliveryModel);
                    deliveryModelList.addAll(deliveryModelList1);
                    docref.update("deliveryModelList", deliveryModelList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                getFcmToken(orderModel.getSeller_id(),getString(R.string.CANCELLED)+"\n"+orderModel.getProduct_name(),"Order is cancelled due to " + cancelMessage + "\n" + "Cancelled by : " +"USER");
                                dialog.dismiss();
                            } else {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                showSnackBar("Error Occured Retry");
                            }
                        }
                    });
                } else {
                    cancelledDialogBinding.reason.setError("Empty");
                    cancelledDialogBinding.reason.requestFocus();
                }

            }
        });

    }


    private void getFcmToken(String userID,String title,String message) {
        firebaseFirestore.collection(getString(R.string.FCM_TOKENS)).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    TokenModel tokenModel = task.getResult().toObject(TokenModel.class);
                    sendNotification(tokenModel.getUserToken(),title,message);
                } else {
                    progressDialog.dismiss();
                    showSnackBar(task.getException().toString());
                }
            }
        });
    }

    private void sendNotification(String fcm_token,String title,String message) {
        apiInterface = ApiClient.getClient(getString(R.string.FCM_BASE_URL)).create(ApiInterface.class);
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setCollapseKey("type_a");
        notificationModel.setTo(fcm_token);
        Data data = new Data();
        data.setTitle("Order : "+title);
        data.setBody(message);
        data.setOpenScreen(getString(R.string.OrdersActivity));
        notificationModel.setData(data);
        apiInterface.sendNotifcation(notificationModel).enqueue(new Callback<FcmResponse>() {
            @Override
            public void onResponse(Call<FcmResponse> call, Response<FcmResponse> response) {
                if (response.isSuccessful()) {
                    FcmResponse responseBody = response.body();
                    if (responseBody.getSuccess()==1)
                    {
                        Toast.makeText(DetailOrderActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                    finish();

                } else {
                    progressDialog.dismiss();
                    showSnackBar(response.message());
                }
            }

            @Override
            public void onFailure(Call<FcmResponse> call, Throwable t) {
                progressDialog.dismiss();
                showSnackBar(t.getMessage());
            }
        });
    }

    public void getHelp(View view)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.admin_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Help me with this order Id : " + orderModel.getOrder_id()+"\n"+"Order Doc Id "+orderModel.getOrder_doc_id());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }
}