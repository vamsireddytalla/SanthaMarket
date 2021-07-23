package com.talla.santhamarket.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.PoductDescriptionActivity;
import com.talla.santhamarket.adapters.SpecificationAdapter;
import com.talla.santhamarket.databinding.FragmentHomeBinding;
import com.talla.santhamarket.databinding.FragmentSpecificationBinding;
import com.talla.santhamarket.models.ProductModel;

import java.util.List;
import java.util.Map;


public class SpecificationFragment extends Fragment {
    private FragmentSpecificationBinding binding;
    private SpecificationAdapter specificationAdapter;
    private PoductDescriptionActivity activity;
    private List<Map.Entry<String, Object>> specList;


    public SpecificationFragment(List<Map.Entry<String, Object>> specList) {
        this.specList = specList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSpecificationBinding.inflate(inflater, container, false);
        specificationAdapter = new SpecificationAdapter(activity, specList);
        binding.specRCV.setAdapter(specificationAdapter);
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = (PoductDescriptionActivity) activity;
    }


}