package com.development.security.ciphernote;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by dfrancisco on 11/2/2016.
 */
public class SecurityManager {
    private static final String ALGO = "AES";
    private Key generatedKey = null;
    String salt = "test";
    SecretKey secret = null;
    byte[] iv = null;
    /**
     * Encrypt a string with AES algorithm.
     *
     * @param data is a string
     * @return the encrypted string
     */
    public String encrypt(String data){
        try{
            //        Cipher c = Cipher.getInstance(ALGO);
//        c.init(Cipher.ENCRYPT_MODE, generatedKey);
//        byte[] encVal = c.doFinal(data.getBytes());
//        return Base64.encodeToString(encVal, 0);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal("Hello, World!".getBytes("UTF-8"));

            return Base64.encodeToString(ciphertext, 0);
        }catch(Exception e){
            e.printStackTrace();
            Log.d("Error", "Error in Encrypt");
        }
        return "";
    }

    /**
     * Decrypt a string with AES algorithm.
     *
     * @param encryptedData is a string
     * @return the decrypted string
     */
    public String decrypt(String encryptedData){
        try {
            //        Cipher c = Cipher.getInstance(ALGO);
//        c.init(Cipher.DECRYPT_MODE, generatedKey);
//        byte[] decordedValue = Base64.decode(encryptedData, 0);
//        byte[] decValue = c.doFinal(decordedValue);
//        return new String(decValue);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            String plaintext = new String(cipher.doFinal(encryptedData.getBytes()), "UTF-8");
            return plaintext;
        }catch(Exception e){
            e.printStackTrace();
            Log.d("Error", "Error in decrypt");
        }
        return "";
    }

//    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
//    KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
//    SecretKey tmp = factory.generateSecret(spec);
//    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");


    /* Encrypt the message. */
//    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//    cipher.init(Cipher.ENCRYPT_MODE, secret);
//    AlgorithmParameters params = cipher.getParameters();
//    byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
//    byte[] ciphertext = cipher.doFinal("Hello, World!".getBytes("UTF-8"));

    /* Decrypt the message, given derived key and initialization vector. */
//    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
//    String plaintext = new String(cipher.doFinal(ciphertext), "UTF-8");
//    System.out.println(plaintext);


    /**
     * Generate a new encryption key.
     */
    private void generateKey(String password){
        try{
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            char[] passwordChars = password.toCharArray();
            KeySpec spec = new PBEKeySpec(passwordChars, salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");
//        byte[] passwordBytes = password.getBytes();
//        return new SecretKeySpec(passwordBytes, ALGO);
        }catch(Exception e){
            e.printStackTrace();
            Log.d("Error", "Error in generateKey");
        }
    }
}
