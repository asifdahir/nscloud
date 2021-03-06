package com.nscloud.android.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.nscloud.android.MainApp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.cert.Certificate;
import javax.security.cert.X509Certificate;

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

    public static byte[] generateRandomKey() {
        byte[] b = new byte[32];
        new Random().nextBytes(b);
        return b;
    }

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
        return clientKey.substring(0, length);
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

    public static PrivateKey getPrivateKey() throws Exception {

        Context context;
        PKCS8EncodedKeySpec keySpec;
        KeyFactory keyFactory;
        PrivateKey privateKey;
        InputStream inputStream;
        DataInputStream dataInputStream;
        String keyBytesString;
        int fileId;
        byte[] keyBytes;
        byte[] encoded;

        context = MainApp.getAppContext();
        fileId = context.getResources().getIdentifier("private_key", "raw", context.getPackageName());
        inputStream = context.getResources().openRawResource(fileId);
        dataInputStream = new DataInputStream(inputStream);

        keyBytes = new byte[dataInputStream.available()];
        dataInputStream.readFully(keyBytes);
        dataInputStream.close();

        keyBytesString = new String(keyBytes);
        keyBytesString = keyBytesString.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
        keyBytesString = keyBytesString.replace("-----END RSA PRIVATE KEY-----", "");
        encoded = Base64.decode(keyBytesString, Base64.DEFAULT);

        keySpec = new PKCS8EncodedKeySpec(encoded);
        keyFactory = KeyFactory.getInstance("RSA", "BC");
        privateKey = keyFactory.generatePrivate(keySpec);

        return privateKey;
    }

    public static PublicKey getPublicKey() throws Exception {

        Context context;
        X509EncodedKeySpec x509EncodedKeySpec;
        KeyFactory keyFactory;
        PublicKey publicKey;
        InputStream inputStream;
        DataInputStream dataInputStream;
        String keyBytesString;
        int fileId;
        byte[] keyBytes;
        byte[] encoded;

        context = MainApp.getAppContext();
        fileId = context.getResources().getIdentifier("public_key", "raw", context.getPackageName());
        inputStream = context.getResources().openRawResource(fileId);
        dataInputStream = new DataInputStream(inputStream);

        keyBytes = new byte[dataInputStream.available()];
        dataInputStream.readFully(keyBytes);
        dataInputStream.close();

        keyBytesString = new String(keyBytes);
        keyBytesString = keyBytesString.replace("-----BEGIN PUBLIC KEY-----\n", "");
        keyBytesString = keyBytesString.replace("-----END PUBLIC KEY-----", "");
        encoded = Base64.decode(keyBytesString, Base64.DEFAULT);
        x509EncodedKeySpec = new X509EncodedKeySpec(encoded);
        keyFactory = KeyFactory.getInstance("RSA", "BC");
        publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        return publicKey;
    }

    public static byte[] rsaEncrypt(final byte[] input) throws Exception {
        PublicKey publicKey = getPublicKey();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(input);
    }

    public static byte[] rsaDecrypt(final byte[] input) throws Exception {
        PrivateKey privateKey = getPrivateKey();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(input);
    }

    public CryptoManager(byte[] key, byte[] salt) {
        try {
            //key = password.getBytes("UTF-8");
            this.key = key;
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
