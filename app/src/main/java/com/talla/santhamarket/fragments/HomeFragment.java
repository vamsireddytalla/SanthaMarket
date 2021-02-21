package com.talla.santhamarket.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.CartActivity;
import com.talla.santhamarket.activities.DetailProductActivity;
import com.talla.santhamarket.activities.HomeActivity;
import com.talla.santhamarket.activities.SearchProductActivity;
import com.talla.santhamarket.activities.ViewProductsActivity;
import com.talla.santhamarket.adapters.HomeBannerAdapter;
import com.talla.santhamarket.adapters.HomeCategoryAdapter;
import com.talla.santhamarket.databinding.FragmentHomeBinding;
import com.talla.santhamarket.interfaces.OnFragmentListner;
import com.talla.santhamarket.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ActionBarDrawerToggle toggle;
    private HomeBannerAdapter homeBannerAdapter;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private FirebaseAuth auth;
    private String UID;
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private HomeCategoryAdapter homeCategoryAdapter;
    private static final String TAG = "HOME_FRAGMENT_DASHBOARD";
    private HomeActivity homeActivity;
    private ListenerRegistration listenerRegistration;
    private OnFragmentListner fragmentListner;

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
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        UID = auth.getUid();
        List<String> stringList = new ArrayList<>();
        stringList.add("https://cdni.iconscout.com/illustration/premium/thumb/halloween-offer-banner-997852.png");
        stringList.add("https://previews.123rf.com/images/alhovik/alhovik1708/alhovik170800009/84049519-weekend-sale-banner-this-weekend-special-offer-banner-template.jpg");
        stringList.add("https://www.bannerbatterien.com/upload/filecache/Banner-Batterien-Windrder2-web_06b2d8d686e91925353ddf153da5d939.webp");
        stringList.add("https://cdn.pixabay.com/photo/2017/12/28/15/06/background-3045402__340.png");
        homeBannerAdapter = new HomeBannerAdapter(getContext(), stringList);
        binding.viewPagerHome.setOffscreenPageLimit(2);
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

        binding.productItem.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailProductActivity.class);
                startActivity(intent);
            }
        });

        binding.searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchProductActivity.class);
                startActivity(intent);
            }
        });

        getCategoriesData();

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        homeActivity = (HomeActivity) activity;
        try {
            fragmentListner = (OnFragmentListner) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
        fragmentListner.fragmentChangeListner(0);
    }


    private void getCategoriesData() {
        if (categoryModelList != null) {
            categoryModelList.clear();
        }
        firestore.collection("CATEGORIES").orderBy("index").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            CategoryModel categoryModel = dc.getDocument().toObject(CategoryModel.class);
                            categoryModelList.add(categoryModel);
                            Log.d(TAG, "Category data added to list: " + categoryModel.toString());
                            break;
                        case MODIFIED:
                            CategoryModel categoryModel1 = dc.getDocument().toObject(CategoryModel.class);
                            Log.d(TAG, "Category data modified to list: " + categoryModel1.toString());
                            for (int i = 0; i < categoryModelList.size(); i++) {
                                if (categoryModelList.get(i).getIndex() == categoryModel1.getIndex()) {
                                    categoryModelList.remove(i);
                                    categoryModelList.add(i, categoryModel1);
                                    break;
                                }
                            }
                            break;
                        case REMOVED:
                            CategoryModel categoryModel2 = dc.getDocument().toObject(CategoryModel.class);
                            Log.d(TAG, "Category data removed to list: " + categoryModel2.toString());
                            for (int i = 0; i < categoryModelList.size(); i++) {
                                if (categoryModelList.get(i).getIndex() == categoryModel2.getIndex()) {
                                    categoryModelList.remove(i);
                                    break;
                                }
                            }
                            break;
                    }
                }
                binding.categoryRCV.setHasFixedSize(true);
                binding.categoryRCV.setLayoutManager(new GridLayoutManager(getContext(), 4));
                homeCategoryAdapter = new HomeCategoryAdapter(getContext(), categoryModelList);
                binding.categoryRCV.setAdapter(homeCategoryAdapter);

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}