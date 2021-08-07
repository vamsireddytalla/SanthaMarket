package com.talla.santhamarket.utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedEncryptUtills
{
    private static SharedEncryptUtills sharedUtills;
    private SharedPreferences sharedPreferences;
    private static String masterKeyAlias;
    public static final String BASE_URL = "BASE_URL";
    public static final String FIRST_TIME = "firsttime";
    public static final String DAY_FIRST_TIME = "DAY_FIRST_TIME";
    public static final String FCM_TOKEN = "FCM_TOKEN";


    public static SharedEncryptUtills getInstance(Context context) {
        if (sharedUtills == null) {
            sharedUtills = new SharedEncryptUtills(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sharedUtills;
    }

    private SharedEncryptUtills(Context context) {
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "SanthaMarket.db",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public String getData(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }

    public void deleteData(String key) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.remove(key);
        prefsEditor.commit();
    }

    public void saveBooleanData(String key,boolean val)
    {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(key, val);
        prefsEditor.commit();
    }

    public boolean getBooleanData(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, false);
        }
        return false;
    }

}
