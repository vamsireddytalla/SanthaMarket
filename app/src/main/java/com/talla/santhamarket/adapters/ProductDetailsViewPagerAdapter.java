package com.talla.santhamarket.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.talla.santhamarket.fragments.DescriptionFragment;
import com.talla.santhamarket.fragments.SpecificationFragment;

import java.util.List;

public class ProductDetailsViewPagerAdapter extends FragmentPagerAdapter
{
    private int totalTabs;
   private List<Fragment> fragmentList;


   public ProductDetailsViewPagerAdapter(@NonNull FragmentManager fm,List<Fragment> fragmentList, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
        this.fragmentList=fragmentList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
               return fragmentList.get(position);
            case 1:
               return fragmentList.get(position);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

}
