package com.talla.santhamarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.talla.santhamarket.databinding.ActivityOrderSummaryBinding;

public class OrderSummaryActivity extends AppCompatActivity {
    private ActivityOrderSummaryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


}