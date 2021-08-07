package com.talla.santhamarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ActivityOtpBinding;
import com.talla.santhamarket.fcm.FirebaseTokenGneration;
import com.talla.santhamarket.models.UserAddress;
import com.talla.santhamarket.models.UserModel;
import com.talla.santhamarket.utills.CheckInternet;
import com.talla.santhamarket.utills.SharedEncryptUtills;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    private ActivityOtpBinding binding;
    private FirebaseAuth auth;
    private String phnNumber;
    private String codeSent;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private CountDownTimer countDownTimer;
    private int count = 60;
    private String android_id;
    private ProgressDialog progressDialog;
    private SharedEncryptUtills sharedEncryptUtills;
    private static final String TAG = "OtpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        android_id = Settings.Secure.getString(OtpActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Bundle bundle = getIntent().getExtras();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please Wait untill finish");
        progressDialog.setCancelable(false);
        sharedEncryptUtills = SharedEncryptUtills.getInstance(this);
        sharedEncryptUtills.deleteData(SharedEncryptUtills.FCM_TOKEN);
        if (bundle != null) {
            phnNumber = bundle.getString("phnNumber");
            setUpVerificationCallBacks();
            countDownTimer();
            PhoneAuthProvider.verifyPhoneNumber(
                    PhoneAuthOptions
                            .newBuilder(FirebaseAuth.getInstance())
                            .setActivity(this)
                            .setPhoneNumber(phnNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setCallbacks(mCallbacks)
                            .setForceResendingToken(resendToken)
                            .build());

        } else {
            finish();
        }
        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOTP(binding.getRoot());
            }
        });
    }

    private void countDownTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                binding.timeOut.setText(count + " Seconds");
                count--;
            }

            public void onFinish() {
                count = 60;
                binding.coundDownSms.setVisibility(View.GONE);
                binding.resendLayout.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public void checkOTP(View view) {
        String enteredOTP = binding.firstPinView.getText().toString();
        if (enteredOTP != null && !enteredOTP.isEmpty()) {
            progressDialog.show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, enteredOTP);
            signInWithPhoneAuthCredential(credential);
        } else {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            binding.firstPinView.setError("check Otp");
            binding.firstPinView.requestFocus();
        }
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(OtpActivity.this, "Login Sucess", Toast.LENGTH_SHORT).show();
                            final FirebaseUser user = task.getResult().getUser();
                            FirebaseUserMetadata metadata = auth.getCurrentUser().getMetadata();

                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNewUser) {
                                documentReference = firestore.collection(getString(R.string.USERS)).document(user.getUid());
                                UserModel userModel = new UserModel();
                                userModel.setUser_number(phnNumber);
                                userModel.setUser_alter_no("");
                                userModel.setUser_id(auth.getUid());
                                userModel.setWalletBal("0");
                                userModel.setUser_email("");
                                userModel.setUser_name("");
                                userModel.setImage_url("");
                                userModel.setDeviceId(android_id);

                                documentReference.set(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        countDownTimer.cancel();
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(OtpActivity.this, "Fail" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        showSnackBar("Failed");
                                    }
                                });
                            } else {
                                DocumentReference ref = firestore.collection(getString(R.string.USERS)).document(auth.getUid());
                                ref.update("deviceId", android_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(OtpActivity.this, "Welcome back", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                                        countDownTimer.cancel();
                                        progressDialog.dismiss();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });

                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            showSnackBar("Sign-In Failed");
                            Toast.makeText(OtpActivity.this, "Sign-In Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(OtpActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                showSnackBar("Invalid OTP");
                            }
                        }
                    }
                });
    }

    private void setUpVerificationCallBacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                Toast.makeText(OtpActivity.this, phoneAuthCredential.getSmsCode() + "code", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OnVerificationCompleted : " + phoneAuthCredential.getSmsCode());
                progressDialog.dismiss();
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(OtpActivity.this, "Failed" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "FirebaseException  " + e.getMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d(TAG, "Invalid Request");
                    showSnackBar("Invalid Request");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.d(TAG, "Too Many Requests attempted retry tomorrow");
                    showSnackBar("Too Many Requests attempted retry tomorrow");
                }

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                codeSent = s;
                resendToken = forceResendingToken;
                Toast.makeText(OtpActivity.this, "Otp Sent", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCodeSent : " + codeSent);
                progressDialog.dismiss();
                binding.resendLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                progressDialog.dismiss();
                Log.d(TAG, "onCodeAutoRetrievalTimeOut   " + s);
                binding.coundDownSms.setVisibility(View.GONE);
                countDownTimer.cancel();
                binding.resendLayout.setVisibility(View.VISIBLE);
            }
        };
    }

    public void resendCode(View view) {
        showSnackBar("Resend  OTP Succesfull");
        if (CheckInternet.checkInternet(this) && !CheckInternet.checkVPN(this)) {
            progressDialog.show();
            setUpVerificationCallBacks();
            binding.resendLayout.setVisibility(View.GONE);
            binding.coundDownSms.setVisibility(View.VISIBLE);
            countDownTimer();
            PhoneAuthProvider.verifyPhoneNumber(
                    PhoneAuthOptions
                            .newBuilder(FirebaseAuth.getInstance())
                            .setActivity(this)
                            .setPhoneNumber(phnNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setCallbacks(mCallbacks)
                            .setForceResendingToken(resendToken)
                            .build());
        } else {
            showSnackBar("Check Internet");
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

}