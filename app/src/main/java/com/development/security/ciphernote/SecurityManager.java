package com.development.security.ciphernote;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

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
    AlgorithmParameterSpec ivParameterSpec = null;
    AlgorithmParameters params;
    byte[] testCipherText = null;

    int hashingIterations = 5000;



    public void createIV() {
        final byte[] iv = new byte[256];
        final SecureRandom theRNG = new SecureRandom();
        theRNG.nextBytes(iv);
        ivParameterSpec = new IvParameterSpec(iv);
    }


    /**
     * Encrypt a string with AES algorithm.
     *
     * @param data is a string
     * @return the encrypted string
     */
    public String encrypt(String data){
        try{

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            params = cipher.getParameters();
            ivParameterSpec = params.getParameterSpec(IvParameterSpec.class);
            byte[] ciphertext = cipher.doFinal(data.getBytes("UTF-8"));
            testCipherText = ciphertext;

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
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, params.getParameterSpec(IvParameterSpec.class));
            String plaintext = new String(cipher.doFinal(testCipherText), "UTF-8");

            return plaintext;
        }catch(Exception e){
            e.printStackTrace();
            Log.d("Error", "Error in decrypt");
        }
        return "";
    }

    /**
     * Generate a new encryption key.
     */
    public void generateKey(String password){
        try{
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
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

    public Boolean authenticateUser(String password, Context context, FileManager fileManager) throws JSONException {
        String userSalt = fileManager.getSalt(context);
        byte[] userSaltBytes;

        userSaltBytes = userSalt.getBytes();

        Log.d("help", "Authenticate salt: " + userSalt);
        byte[] hash = hashPassword(password, userSaltBytes);
        String hashInFile = fileManager.readHash(context);
        String userHashEncoded  = Base64.encodeToString(hash, Base64.DEFAULT);
        Log.d("help", "Authenticate hashed password: " + userHashEncoded);
        Log.d("help", "Authenticate hashInFile: " + hashInFile);
        Log.d("help", "Authenticate calculated hash: " + hash);

        if(userHashEncoded.equals(hashInFile)){
            return true;
        }
        return false;
    }

    public byte[] hashPassword(String password, byte[] salt) {
        int keyLength = 512;
        try {
//            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, hashingIterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateSalt(){
        Random randomValue = new SecureRandom();
        byte[] salt = new byte[8];
        randomValue.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

}
