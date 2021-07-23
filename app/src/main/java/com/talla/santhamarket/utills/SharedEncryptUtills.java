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
    public static final String NOTIFICATION_ON_OFF = "NOTIFICATION_ON_OFF";
    public static final String DEFAULT_PLAYER = "DEFAULT_PLAYER";
    public static final String IS_NEW_USER = "IS_NEW_USER";
    public static final String TELEGRAM_CHANNEL = "TELEGRAM_CHANNEL";
    public static final String TELEGRAM_GROUP = "TELEGRAM_GROUP";
    public static final String WEB_SITE = "WEB_SITE";
    public static final String ADD_MOB_ID = "ADD_MOB_ID";
    public static final String APP_VERSION = "APP_VERSION";
    public static final String USER_NAME = "USER_NAME";
    public static final String SHOW_ADS = "SHOW_ADS";


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
