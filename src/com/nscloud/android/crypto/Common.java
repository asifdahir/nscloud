package com.nscloud.android.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by intelligentsystems on 03/12/15.
 */
public class Common {

    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static String getFileExtension(String file) {
        String filenameArray[] = file.split("\\.");
        return filenameArray[filenameArray.length - 1];
    }

    public static String getFileNameWithoutExtension(String file) {

        if (file == null)
            return null;

        int pos = file.lastIndexOf(".");
        if (pos == -1)
            return file;

        return file.substring(0, pos);
    }

    public static String appendStringToFileName(String file, String append) {
        return Common.getFileNameWithoutExtension(file) + append + "." + Common.getFileExtension(file);
    }

    public static void encryptAndReplacePlainFile(String plainFilePath) {
        CryptoManager cryptoManager;
        File fileFrom;
        File fileTo;
        File file;
        InputStream inputStream;
        OutputStream outputStream;
        String encryptedFilePath;
        String tmpFilePath;
        byte[] buf;
        byte[] outBuff;
        byte[] randomKey;
        byte[] encryptedRandomKey;
        int dataReadSize;
        int plainFileSize;

        try {

            // generate random bytes to be used as symmetric key
            randomKey = CryptoManager.generateRandomKey();
            // encrypt symmetric key with public key
            encryptedRandomKey = CryptoManager.rsaEncrypt(randomKey);

            encryptedFilePath = Common.appendStringToFileName(plainFilePath, "_enc");
            tmpFilePath = Common.appendStringToFileName(plainFilePath, "_tmp");

            // initialise crypto manager with random bytes
            cryptoManager = new CryptoManager(randomKey, CryptoManager.SALT);

            inputStream = new FileInputStream(plainFilePath);
            outputStream = new FileOutputStream(encryptedFilePath);

            // on encrypted file first write random bytes key encrypted with public key
            // initial 4096 bytes of file are its encrypted key
            outputStream.write(encryptedRandomKey);

            file = new File(plainFilePath);
            plainFileSize = (int) file.length();
            buf = new byte[plainFileSize];
            dataReadSize = inputStream.read(buf, 0, buf.length);
            if (dataReadSize == plainFileSize) {
                outBuff = cryptoManager.encrypt(buf);

                outputStream.write(outBuff);
                outputStream.close();
                inputStream.close();

                fileFrom = new File(plainFilePath);
                fileTo = new File(tmpFilePath);
                Common.copyFile(fileFrom, fileTo);

                fileFrom.delete();

                fileFrom = new File(encryptedFilePath);
                fileTo = new File(plainFilePath);
                fileFrom.renameTo(fileTo);

                fileFrom = new File(tmpFilePath);
                fileFrom.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void decryptAndReplaceEncryptedFile(String encryptedFilePath) {
        CryptoManager cryptoManager;
        File fileFrom;
        File fileTo;
        File file;
        InputStream inputStream;
        OutputStream outputStream;
        String decryptedFilePath;
        String tmpFilePath;
        byte[] buf;
        byte[] outBuff;
        byte[] randomKey;
        byte[] encryptedRandomKey;
        int dataReadSize;
        int encryptedFileSize;

        try {
            decryptedFilePath = Common.appendStringToFileName(encryptedFilePath, "_dec");
            tmpFilePath = Common.appendStringToFileName(encryptedFilePath, "_tmp");

            inputStream = new FileInputStream(encryptedFilePath);
            outputStream = new FileOutputStream(decryptedFilePath);

            encryptedRandomKey = new byte[512];
            inputStream.read(encryptedRandomKey, 0, encryptedRandomKey.length);
            randomKey = CryptoManager.rsaDecrypt(encryptedRandomKey);
            cryptoManager = new CryptoManager(randomKey, CryptoManager.SALT);

            file = new File(encryptedFilePath);
            encryptedFileSize = (int) file.length() - 512;
            buf = new byte[encryptedFileSize];
            dataReadSize = inputStream.read(buf, 0, buf.length);
            if (dataReadSize == encryptedFileSize) {
                outBuff = cryptoManager.decrypt(buf);

                outputStream.write(outBuff);
                outputStream.close();
                inputStream.close();

                fileFrom = new File(encryptedFilePath);
                fileTo = new File(tmpFilePath);
                Common.copyFile(fileFrom, fileTo);

                fileFrom.delete();

                fileFrom = new File(decryptedFilePath);
                fileTo = new File(encryptedFilePath);
                fileFrom.renameTo(fileTo);

                fileFrom = new File(tmpFilePath);
                fileFrom.delete();
            }
        } catch (Exception ex) {

        }
    }
}
