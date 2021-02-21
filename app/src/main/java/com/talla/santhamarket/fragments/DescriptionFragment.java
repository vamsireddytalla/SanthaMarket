package com.talla.santhamarket.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.FragmentDescriptionBinding;
import com.talla.santhamarket.databinding.FragmentHomeBinding;


public class DescriptionFragment extends Fragment {
    private FragmentDescriptionBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDescriptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}