package com.talla.santhamarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.talla.santhamarket.databinding.ActivityFavouriteBinding;

public class FavouriteActivity extends AppCompatActivity {
    private ActivityFavouriteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}