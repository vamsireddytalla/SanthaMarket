package com.talla.santhamarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ActivityOrderSucessBinding;

import java.util.UUID;

public class OrderSucessActivity extends AppCompatActivity {
    private ActivityOrderSucessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSucessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void continueBtn(View view) {
        Intent nextScreen = new Intent(OrderSucessActivity.this, HomeActivity.class);
        nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(nextScreen);
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}