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
        int dataReadSize;
        int plainFileSize;

        try {
            encryptedFilePath = Common.appendStringToFileName(plainFilePath, "_enc");
            tmpFilePath = Common.appendStringToFileName(plainFilePath, "_tmp");

            cryptoManager = new CryptoManager(CryptoManager.KEY_CLIENT, CryptoManager.SALT);

            inputStream = new FileInputStream(plainFilePath);
            outputStream = new FileOutputStream(encryptedFilePath);

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
        int dataReadSize;
        int encryptedFileSize;

        try {
            decryptedFilePath = Common.appendStringToFileName(encryptedFilePath, "_dec");
            tmpFilePath = Common.appendStringToFileName(encryptedFilePath, "_tmp");

            cryptoManager = new CryptoManager(CryptoManager.KEY_CLIENT, CryptoManager.SALT);

            inputStream = new FileInputStream(encryptedFilePath);
            outputStream = new FileOutputStream(decryptedFilePath);

            file = new File(encryptedFilePath);
            encryptedFileSize = (int) file.length();
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
