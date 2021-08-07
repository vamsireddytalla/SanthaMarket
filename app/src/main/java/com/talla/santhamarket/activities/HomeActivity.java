package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ActivityHomeBinding;
import com.talla.santhamarket.fcm.FirebaseTokenGneration;
import com.talla.santhamarket.fragments.HomeFragment;
import com.talla.santhamarket.fragments.MyOrdersFragment;
import com.talla.santhamarket.interfaces.OnFragmentListner;
import com.talla.santhamarket.models.ServerModel;
import com.talla.santhamarket.utills.SharedEncryptUtills;

public class HomeActivity extends AppCompatActivity implements OnFragmentListner {
    private ActivityHomeBinding binding;
    private String UID;
    private FirebaseAuth auth;
    private SharedEncryptUtills sharedEncryptUtills;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        sharedEncryptUtills = SharedEncryptUtills.getInstance(this);
        if (auth.getCurrentUser() != null) {
            UID = auth.getCurrentUser().getUid();
            sendFCMToken();
        } else {
            finish();
        }
        getDeviceInfo();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String val = bundle.getString("fromNotification");
            if (val.equalsIgnoreCase(getString(R.string.orderFragment))) {
                addFragment(new MyOrdersFragment());
            }else {
                addFragment(new HomeFragment());
            }
        } else {
            addFragment(new HomeFragment());
        }

        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.rootFrame);
                switch (item.getItemId()) {
                    case R.id.home:
                        if (currentFrag instanceof HomeFragment) {
                            return true;
                        } else {
                            replacefragment(new HomeFragment(), "HomeFag");
                            return true;
                        }
                    case R.id.orders:
                        if (currentFrag instanceof MyOrdersFragment) {
                            return true;
                        } else {
                            replacefragment(new MyOrdersFragment(), "MyOrderFrag");
                            return true;
                        }
                    case R.id.favourite:
                        Intent favIntent = new Intent(HomeActivity.this, FavouriteActivity.class);
                        startActivity(favIntent);
                        return false;
                    case R.id.profile:
                        if (auth.getCurrentUser() != null) {
                            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
                            startActivity(intent);
                        }
                        return false;
                }
                return false;
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

    private void sendFCMToken() {
        FirebaseMessaging.getInstance().subscribeToTopic("SanthaMarket");

        String fcm_token = sharedEncryptUtills.getData(SharedEncryptUtills.FCM_TOKEN);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (!fcm_token.equalsIgnoreCase(refreshedToken)) {
            // Get new Instance ID token
            FirebaseTokenGneration ftg = new FirebaseTokenGneration(this);
            ftg.updateToken(refreshedToken);
        }
        getDeviceInfo();

//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                String token = instanceIdResult.getToken();
//                Log.i("FCM Token", token);
//                // Get new Instance ID token
//                FirebaseTokenGneration ftg = new FirebaseTokenGneration();
//                ftg.updateToken(HomeActivity.this, token);
//            }
//        });
    }





    public void getDeviceInfo() {
        DocumentReference ref = firebaseFirestore.collection(getString(R.string.DEVICE_INFO)).document("54321");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ServerModel serverModel = document.toObject(ServerModel.class);
                        Log.d(TAG, "DocumentSnapshot data: " + serverModel.toString());
                        if (serverModel != null) {
                            PackageInfo pInfo = null;
                            try {
                                pInfo = HomeActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                                int version = pInfo.versionCode;
                                if (version != Integer.parseInt(serverModel.getApp_version())) {
                                    showDialog("Update App", "Please Update app from Playstore");
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "onComplete: " + task.getResult());
                }
            }
        });
    }

    private void showDialog(final String title, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://details?id=" + HomeActivity.this.getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                myAppLinkToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(HomeActivity.this.getString(R.string.PLAYSTORE_BASE_URL) + HomeActivity.this.getPackageName())));
                }
            }
        });
        alertDialogBuilder.show();
    }


}