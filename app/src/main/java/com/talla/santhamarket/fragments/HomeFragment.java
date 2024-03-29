package com.talla.santhamarket.fragments;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.AddressBookActivity;
import com.talla.santhamarket.activities.CategoriesActivity;
import com.talla.santhamarket.activities.CityActivity;
import com.talla.santhamarket.activities.DetailProductActivity;
import com.talla.santhamarket.activities.HomeActivity;
import com.talla.santhamarket.activities.LocalShopActivity;
import com.talla.santhamarket.activities.MultiCartActivity;
import com.talla.santhamarket.activities.OrderSummaryActivity;
import com.talla.santhamarket.activities.SearchProductActivity;
import com.talla.santhamarket.adapters.CategoriesAdapter;
import com.talla.santhamarket.adapters.HomeBannerAdapter;
import com.talla.santhamarket.adapters.HomeCategoryAdapter;
import com.talla.santhamarket.databinding.FragmentHomeBinding;
import com.talla.santhamarket.interfaces.OnFragmentListner;
import com.talla.santhamarket.models.BannerModel;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.DashBoardModel;
import com.talla.santhamarket.models.GraphView;
import com.talla.santhamarket.utills.CheckInternet;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.SharedEncryptUtills;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
    private Dialog progressDialog;
    private ListenerRegistration categoriesListner, cartItemsCountListner,dashBoardDataListner;
    private OnFragmentListner fragmentListner;
    private int totalCart_items;
    private List<BannerModel> bannerModelList = new ArrayList<>();
    List<String> cateList = new ArrayList<>();
    private SharedEncryptUtills sharedEncryptUtills;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        sharedEncryptUtills = SharedEncryptUtills.getInstance(homeActivity);
        dialogIninit();
        homeBannerAdapter = new HomeBannerAdapter(homeActivity, bannerModelList);
        binding.viewPagerHome.setOffscreenPageLimit(2);
        binding.viewPagerHome.setAdapter(homeBannerAdapter);
        new TabLayoutMediator(binding.tabIndicator, binding.viewPagerHome, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        }).attach();

        binding.localShop.setBackground(homeActivity.getResources().getDrawable(R.drawable.local_home_icon));

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        binding.navigationView.getMenu().getItem(0).setChecked(true);
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.address:
                        Intent intent = new Intent(getContext(), AddressBookActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.shareApp:
                        try {
                            String shareMessage = "";
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                            shareMessage = shareMessage + homeActivity.getString(R.string.PLAYSTORE_BASE_URL) + getActivity().getPackageName() + "\n\n";
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
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(homeActivity.getString(R.string.PLAYSTORE_BASE_URL) + getActivity().getPackageName())));
                        }
                        break;
                    case R.id.about_us:
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                        getHelp();
                        break;
                }
                return false;
            }
        });


        binding.cartItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(homeActivity, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        binding.localShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CityActivity.class);
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


        binding.cartItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalCart_items > 0) {
                    Intent intent = new Intent(homeActivity, MultiCartActivity.class);
                    startActivity(intent);
                }
            }
        });

        binding.cartItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalCart_items > 0) {
                    Intent intent = new Intent(homeActivity, MultiCartActivity.class);
                    startActivity(intent);
                }
            }
        });

        getDashBoardData();

        return binding.getRoot();
    }

    public void getHelp()
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.admin_email)});
        if (intent.resolveActivity(homeActivity.getPackageManager()) != null) {
            startActivity(intent);
        }

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

    private void getCartItemsCount() {
        Query query = firestore.collection(homeActivity.getString(R.string.CART_ITEMS)).whereEqualTo("user_id", UID);
        cartItemsCountListner = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error :" + error.getMessage());
                } else {
                    totalCart_items = value.getDocuments().size();
                    binding.cartItemsCount.setText(totalCart_items + "");
                }
            }
        });
        sendVisitorsCount();
    }

    private void sendVisitorsCount() {
        String data = sharedEncryptUtills.getData(SharedEncryptUtills.DAY_FIRST_TIME);
        if (!data.equalsIgnoreCase(CheckUtill.getSystemTime(homeActivity))) {
            if (CheckInternet.checkInternet(homeActivity)) {
                //this is for auto increment value
                checkDocument();
            } else {
                Toast.makeText(homeActivity, "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkDocument() {
        DocumentReference docIdRef = firestore.collection(homeActivity.getResources().getString(R.string.DAILY_VIEWS)).document(CheckUtill.getSystemTime(homeActivity));
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document exists!");
                        final DocumentReference cref1 = firestore.collection(homeActivity.getResources().getString(R.string.DAILY_VIEWS)).document(CheckUtill.getSystemTime(homeActivity));
                        cref1.update("appViews", FieldValue.increment(1));
                        sharedEncryptUtills.saveData(SharedEncryptUtills.DAY_FIRST_TIME, CheckUtill.getSystemTime(homeActivity));
                        showSnackBar("Welcome !");
                    } else {
                        Log.d(TAG, "Document does not exist!");
                        GraphView graphView = new GraphView();
                        graphView.setAppViews(1);
                        graphView.setDailyDate(CheckUtill.getSystemTime(homeActivity));
                        final DocumentReference cref = firestore.collection(homeActivity.getResources().getString(R.string.DAILY_VIEWS)).document(CheckUtill.getSystemTime(homeActivity));
                        cref.set(graphView).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showSnackBar("Welcome !");
                                sharedEncryptUtills.saveData(SharedEncryptUtills.DAY_FIRST_TIME, CheckUtill.getSystemTime(homeActivity));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showSnackBar(e.getMessage());
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

    }

    private void getDashBoardData() {
        if (cateList != null) {
            cateList.clear();
        }
        if (bannerModelList != null) {
            bannerModelList.clear();
        }
        dashBoardDataListner=firestore.collection(homeActivity.getResources().getString(R.string.DASHBOARD)).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    progressDialog.dismiss();
                    Log.d(TAG, "getDashBoardData: " + error.getMessage());
                } else {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        DashBoardModel dashBoardModel = snapshot.toObject(DashBoardModel.class);
                        bannerModelList = dashBoardModel.getBannerModelList();
                        Map<String, Object> catList = dashBoardModel.getCategoriesList();
                        for (Map.Entry<String, Object> set : catList.entrySet()) {
                            cateList.add(set.getValue().toString());
                        }
                    }
                    progressDialog.dismiss();
                    homeBannerAdapter.setBannerModelList(bannerModelList);
                    getCategoriesData();
                }
            }
        });
    }

    private void getCategoriesData() {
        if (categoryModelList != null) {
            categoryModelList.clear();
        }
        if (cateList.size() > 0) {
            for (String set : cateList) {
                categoriesListner=firestore.collection(getString(R.string.CATEGORIES)).whereEqualTo("id", set).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                        if (categoryModelList.get(i).getId().equals(categoryModel1.getId())) {
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
                                        if (categoryModelList.get(i).getId().equals(categoryModel2.getId())) {
                                            categoryModelList.remove(i);
                                            break;
                                        }
                                    }
                                    break;
                            }
                        }
                        binding.categoryRCV.setHasFixedSize(true);
                        homeCategoryAdapter = new HomeCategoryAdapter(getContext(), categoryModelList);
                        binding.categoryRCV.setAdapter(homeCategoryAdapter);
                        cateList.clear();
                        getCartItemsCount();
                    }
                });
            }
        } else {
            progressDialog.dismiss();
        }
    }

    public void dialogIninit() {
        progressDialog = new Dialog(homeActivity);
        com.talla.santhamarket.databinding.CustomProgressDialogBinding customProgressDialogBinding = com.talla.santhamarket.databinding.CustomProgressDialogBinding.inflate(this.getLayoutInflater());
        progressDialog.setContentView(customProgressDialogBinding.getRoot());
        progressDialog.setCancelable(false);
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(homeActivity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        categoriesListner.remove();
        cartItemsCountListner.remove();
        dashBoardDataListner.remove();
    }
}