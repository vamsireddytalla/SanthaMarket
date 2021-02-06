package com.talla.santhamarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.talla.santhamarket.databinding.ActivityDetailProductBinding;

public class DetailProductActivity extends AppCompatActivity {
    private ActivityDetailProductBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}