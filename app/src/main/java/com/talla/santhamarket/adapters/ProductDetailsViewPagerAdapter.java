package com.talla.santhamarket.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.talla.santhamarket.fragments.DescriptionFragment;
import com.talla.santhamarket.fragments.SpecificationFragment;

public class ProductDetailsViewPagerAdapter extends FragmentPagerAdapter
{
    private int totalTabs;

    public ProductDetailsViewPagerAdapter(@NonNull FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
               return new DescriptionFragment();
            case 1:
               return new SpecificationFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

}
