package com.talla.santhamarket.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.FragmentHomeBinding;
import com.talla.santhamarket.databinding.FragmentSpecificationBinding;


public class SpecificationFragment extends Fragment
{
    private FragmentSpecificationBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentSpecificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}