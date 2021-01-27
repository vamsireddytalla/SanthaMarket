package com.talla.santhamarket.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.AuthenticationActivity;
import com.talla.santhamarket.activities.CartActivity;
import com.talla.santhamarket.activities.OtpActivity;
import com.talla.santhamarket.adapters.HomeBannerAdapter;
import com.talla.santhamarket.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ActionBarDrawerToggle toggle;
    private HomeBannerAdapter homeBannerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.homeToolbar);
        binding.homeToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toggle = new ActionBarDrawerToggle(getActivity(), binding.drawerLayout, binding.homeToolbar, R.string.open, R.string.close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        List<String> stringList = new ArrayList<>();
        stringList.add("https://cdni.iconscout.com/illustration/premium/thumb/halloween-offer-banner-997852.png");
        stringList.add("https://previews.123rf.com/images/alhovik/alhovik1708/alhovik170800009/84049519-weekend-sale-banner-this-weekend-special-offer-banner-template.jpg");
        stringList.add("https://www.bannerbatterien.com/upload/filecache/Banner-Batterien-Windrder2-web_06b2d8d686e91925353ddf153da5d939.webp");
        stringList.add("https://cdn.pixabay.com/photo/2017/12/28/15/06/background-3045402__340.png");
        homeBannerAdapter = new HomeBannerAdapter(getContext(), stringList);
        binding.viewPagerHome.setAdapter(homeBannerAdapter);
        new TabLayoutMediator(binding.tabIndicator, binding.viewPagerHome, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        }).attach();


        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        binding.navigationView.getMenu().getItem(0).setChecked(true);
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.shareApp:
                        try {
                            String shareMessage = "";
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + "\n\n";
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.rate_us:
                        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        myAppLinkToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(myAppLinkToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                        }
                        break;
                    case R.id.about_us:
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return false;
            }
        });


        binding.cartItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }


}