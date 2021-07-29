package com.talla.santhamarket.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ActivityPaymentBinding;
import com.talla.santhamarket.models.SCModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    private ActivityPaymentBinding binding;
    private String testKey = "rzp_test_Ym3KgKwBQCOoGX";
    private String secretKey = "rZ6sMPv7x7WlFs07mpsUO3De";
    private String amount = "100";
    private int finalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Checkout.preload(this);
//        startPayment();


        new Thread(new Runnable() {
            @Override
            public void run() {
                String orderID = makeCurl();
                if (orderID != null && !orderID.isEmpty()) {
                    startPayment(orderID);
                }
            }
        }).start();
    }

    private String makeCurl() {
        finalAmount = Math.round(Float.parseFloat(amount) * 100);
        String culString = executeCommand("curl -v -u " + testKey + ":" + secretKey + " -X POST https://api.razorpay.com/v1/orders -H \"content-type: application/json\" -d '{\"amount\":" + "100" + ",\"currency\":\"INR\",\"receipt\": \"1\"}'");
        Gson gson = new Gson();
        gson.toJson(culString);
        SCModel convertedObject = new Gson().fromJson(culString, SCModel.class);
        Log.d("PAYMENT", convertedObject.toString());
        String orderID = convertedObject.getId();
        return orderID;
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

    public void startPayment(String orderID) {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_Ym3KgKwBQCOoGX");
//        checkout.setImage(R.drawable.btn_bg);
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Test");
            options.put("description", "cgchcjh");
            options.put("theme.color", R.color.red);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            Log.d("PAYMENT_FINAL_ORDER_ID", orderID);
            options.put("order_id", orderID);
            options.put("currency", "INR");
            options.put("amount", "100");
            options.put("prefill.email", "vamsip140@gmail.com");
            String userPhone = "8885965414";
            options.put("prefill.contact", userPhone);
            Log.d("OPTIONS", options.toString());
            checkout.open(this, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        Log.d("PAYMENT", "Sucess" + System.currentTimeMillis());
        showDialog("Sucess", s);

    }

    @Override
    public void onPaymentError(int i, String s) {
        showDialog("Payment Error Occured", s);
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

}