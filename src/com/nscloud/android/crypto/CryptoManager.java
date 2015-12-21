package com.nscloud.android.crypto;

import android.content.Context;
import android.content.SharedPreferences;

import com.nscloud.android.MainApp;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 11/20/2015.
 */
public class CryptoManager {
    public static final String SF_KEY = "SF_KEY";
    public static final String SF_KEY_LENGTH_BY_USER = "SF_KEY_LENGTH_BY_USER";
    //public static final String KEY_SERVER = "44444444444444444444444444444444";
    //public static final String KEY_CLIENT = "88888888888888888888888888888888";
    public static final byte[] SALT = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static final int BLOCK_SIZE = 16;

    byte[] key = null;
    byte[] iv = null;

    public static String getClientKey() {
        SharedPreferences sharedPreferences;
        String clientKey;

        sharedPreferences = MainApp.getAppContext().getSharedPreferences(CryptoManager.SF_KEY, Context.MODE_PRIVATE);
        clientKey = sharedPreferences.getString(CryptoManager.SF_KEY, "");
        if (clientKey.equals("")) {
            setClientKey("android");
        }
        return clientKey;
    }

    public static String getClientKeyOriginal() {
        SharedPreferences sharedPreferences;
        String clientKey;
        int length;

        sharedPreferences = MainApp.getAppContext().getSharedPreferences(CryptoManager.SF_KEY, Context.MODE_PRIVATE);
        clientKey = sharedPreferences.getString(CryptoManager.SF_KEY, "");
        length = sharedPreferences.getInt(CryptoManager.SF_KEY_LENGTH_BY_USER, 0);
        clientKey.substring(0, length);
        return clientKey;
    }

    public static void setClientKey(String clientKey) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        StringBuilder stringBuilder;
        SecureRandom secureRandom;
        String randomString;
        int i;

        secureRandom = new SecureRandom();
        randomString = new BigInteger(130, secureRandom).toString(32);

        stringBuilder = new StringBuilder(clientKey);
        for (i = 0; i < (32 - clientKey.length()); i++) {
            stringBuilder.append(randomString.charAt(i));
        }
        sharedPreferences = MainApp.getAppContext().getSharedPreferences(CryptoManager.SF_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(CryptoManager.SF_KEY, stringBuilder.toString());
        editor.putInt(CryptoManager.SF_KEY_LENGTH_BY_USER, clientKey.length());
        editor.apply();
    }

    public CryptoManager(String password, byte[] salt) {
        try {
            key = password.getBytes("UTF-8");
            iv = salt;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public byte[] encrypt(byte[] input) {
        SecretKeySpec secretKeySpec = null;
        Cipher cipher = null;

        try {
            secretKeySpec = new SecretKeySpec(key, "AES/CBC/PKCS7Padding");
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return cipher.doFinal(input);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(byte[] input) {
        SecretKeySpec secretKeySpec = null;
        Cipher cipher = null;
        try {
            secretKeySpec = new SecretKeySpec(key, "AES/CBC/PKCS7Padding");
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return cipher.doFinal(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
