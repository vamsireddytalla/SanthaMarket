package com.talla.santhamarket.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.DetailOrderActivity;
import com.talla.santhamarket.activities.HomeActivity;
import com.talla.santhamarket.databinding.FragmentHomeBinding;
import com.talla.santhamarket.databinding.FragmentMyOrdersBinding;


public class MyOrdersFragment extends Fragment {
    private FragmentMyOrdersBinding binding;
    private HomeActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyOrdersBinding.inflate(inflater, container, false);

        binding.itemLayout.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailOrderActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = (HomeActivity) activity;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rootFrame, fragment);
        ft.commit();
    }

}