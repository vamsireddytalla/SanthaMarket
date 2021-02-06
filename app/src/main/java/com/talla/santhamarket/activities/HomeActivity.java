package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ActivityHomeBinding;
import com.talla.santhamarket.fragments.HomeFragment;
import com.talla.santhamarket.fragments.MyOrdersFragment;
import com.talla.santhamarket.interfaces.OnFragmentListner;

public class HomeActivity extends AppCompatActivity implements OnFragmentListner {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        addFragment(new HomeFragment());

        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.rootFrame);
                switch (item.getItemId()) {
                    case R.id.home:
                        if (currentFrag instanceof HomeFragment)
                            return true;
                        replacefragment(new HomeFragment(), "HomeFag");
                        return true;
                    case R.id.orders:
                        replacefragment(new MyOrdersFragment(), "MyOrderFrag");
                        return true;
                    case R.id.favourite:
                        Intent favIntent = new Intent(HomeActivity.this, FavouriteActivity.class);
                        startActivity(favIntent);
                        return true;
                    case R.id.profile:
                        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        return false;
                }
                return true;
            }
        });

    }


    private void addFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rootFrame, fragment);
        ft.commit();
    }

    private void replacefragment(Fragment fragment, String tagName) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rootFrame, fragment);
        ft.addToBackStack(tagName);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(index);
            String tag = backStackEntry.getName();
            Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.rootFrame);
            if (currentFrag instanceof HomeFragment) {
                binding.bottomNav.getMenu().getItem(1).setChecked(true);
            } else if (currentFrag instanceof MyOrdersFragment) {
                binding.bottomNav.getMenu().getItem(0).setChecked(true);
            }
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_Light_Dialog_MinWidth);
            builder.setTitle("Do you want to Exit !");
            builder.setBackground(this.getResources().getDrawable(R.drawable.white_bg));
            builder.setCancelable(false);
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    @Override
    public void fragmentChangeListner(int fragNo) {
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.rootFrame);
        binding.bottomNav.getMenu().getItem(fragNo).setChecked(true);
    }

}