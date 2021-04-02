package com.talla.santhamarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.talla.santhamarket.R;
import com.talla.santhamarket.adapters.ProductDetailsViewPagerAdapter;
import com.talla.santhamarket.databinding.ActivityPoductDescriptionBinding;
import com.talla.santhamarket.databinding.ProductDescriptionLayoutBinding;
import com.talla.santhamarket.fragments.DescriptionFragment;
import com.talla.santhamarket.fragments.SpecificationFragment;
import com.talla.santhamarket.models.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PoductDescriptionActivity extends AppCompatActivity {
    private ActivityPoductDescriptionBinding binding;
    private ProductDescriptionLayoutBinding productDescriptionLayoutBinding;
    private ProductDetailsViewPagerAdapter productDetailsViewPagerAdapter;
    private ProductModel productModel;
    private static String TAG="PoductDescriptionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPoductDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        productModel = (ProductModel) getIntent().getSerializableExtra("descObj");
        productDescriptionLayoutBinding = ProductDescriptionLayoutBinding.bind(binding.getRoot());
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        List<Fragment> fragmentList=new ArrayList<>();
        if (productModel.getDescription()!=null)
        {
            fragmentList.add(new DescriptionFragment(productModel.getDescription()));
        }
        if (productModel.getSpecifications()!=null)
        {
            List<Map.Entry<String, Object>> specList = new ArrayList(productModel.getSpecifications().entrySet());
            fragmentList.add(new SpecificationFragment(specList));
        }
        productDetailsViewPagerAdapter = new ProductDetailsViewPagerAdapter(getSupportFragmentManager(),fragmentList, productDescriptionLayoutBinding.specificationTabLayout.getTabCount());
        productDescriptionLayoutBinding.specificationViewPager.setAdapter(productDetailsViewPagerAdapter);
        productDescriptionLayoutBinding.specificationViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDescriptionLayoutBinding.specificationTabLayout));
        productDescriptionLayoutBinding.specificationTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDescriptionLayoutBinding.specificationViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

}