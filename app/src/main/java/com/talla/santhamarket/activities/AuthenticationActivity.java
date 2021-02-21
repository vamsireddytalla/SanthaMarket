package com.talla.santhamarket.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ActivityAuthenticationBinding;
import com.talla.santhamarket.fragments.HomeFragment;
import com.talla.santhamarket.utills.CheckInternet;

public class AuthenticationActivity extends AppCompatActivity {
    private ActivityAuthenticationBinding binding;
    private String enteredNumber;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBarColor();
        }
        auth = FirebaseAuth.getInstance();
        binding.getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOtp();
            }
        });
    }

    private void sendOtp() {
        enteredNumber = binding.inputNumber.getText().toString();
        if (enteredNumber == null || enteredNumber.length() < 10) {
            showSnackBar("Check Entered Number");
            binding.inputNumber.setError("Check Number");
            binding.inputNumber.requestFocus();
            return;
        } else {
            String phnNumber = "+91"+enteredNumber;
            if (CheckInternet.checkInternet(this) && !CheckInternet.checkVPN(this)) {
                Intent intent = new Intent(this, OtpActivity.class);
                intent.putExtra("phnNumber", phnNumber);
                startActivity(intent);
            } else {
                showSnackBar("Check Internet");
            }
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void statusBarColor() {
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_white));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public void skipItem(View view) {
        Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}