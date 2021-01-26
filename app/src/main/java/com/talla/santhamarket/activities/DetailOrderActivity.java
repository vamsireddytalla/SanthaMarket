package com.talla.santhamarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.talla.santhamarket.databinding.ActivityDetailOrderBinding;

public class DetailOrderActivity extends AppCompatActivity {
    private ActivityDetailOrderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}