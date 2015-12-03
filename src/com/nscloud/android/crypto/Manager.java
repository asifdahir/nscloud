package com.nscloud.android.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 11/20/2015.
 */
public class Manager {
    public static final String keyServer = "44444444444444444444444444444444";
    public static final String keyClient = "88888888888888888888888888888888";

    byte[] key = null;
    byte[] iv = null;

    public Manager(String password, byte[] salt) {
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
