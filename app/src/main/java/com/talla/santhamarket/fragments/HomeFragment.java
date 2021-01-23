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

import com.google.android.material.navigation.NavigationView;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment
{
    private FragmentHomeBinding binding;
    private ActionBarDrawerToggle toggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.homeToolbar);
        binding.homeToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toggle=new ActionBarDrawerToggle(getActivity(),binding.drawerLayout,binding.homeToolbar,R.string.open,R.string.close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();


        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        binding.navigationView.getMenu().getItem(0).setChecked(true);
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.shareApp:
                        try {
                            String shareMessage="";
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName() +"\n\n";
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.rate_us:
                        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        myAppLinkToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(myAppLinkToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+getActivity().getPackageName())));
                        }
                        break;
                    case R.id.about_us:
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return false;
            }
        });


        return binding.getRoot();
    }




}