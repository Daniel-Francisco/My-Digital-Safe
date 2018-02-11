package com.development.security.ciphernote;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.UserConfiguration;

import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
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
    private static SecurityManager singletonInstance = new SecurityManager();
    public static SecurityManager getInstance(){
        return singletonInstance;
    }

    private static final String ALGO = "AES";
    String salt = "test";
    SecretKey secret = null;


//    public byte[] createIV() throws InvalidParameterSpecException {
//        byte[] iv = new byte[256];
//        final SecureRandom theRNG = new SecureRandom();
//        theRNG.nextBytes(iv);
//        AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(iv);
//        iv = params.getParameterSpec(IvParameterSpec.class).getIV();
//        return iv;
//    }

    public String SHA256Hash(String clearText) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(clearText.getBytes());
        return Base64.encodeToString(encodedhash, Base64.DEFAULT);
    }


    /**
     * Encrypt a string with AES algorithm.
     *
     * @param data is a string
     * @return the encrypted string
     */
    public byte[] encrypt(String data){
        try{

            Log.d("help", "Data: " + data);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();

            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(data.getBytes("UTF-8"));

            byte[] finalMessage = new byte[ciphertext.length + iv.length];
            for(int i = 0; i<iv.length; i++){
                finalMessage[i] = iv[i];
            }
            for(int i = 0; i < ciphertext.length; i++){
                finalMessage[i + iv.length] = ciphertext[i];
            }

            return finalMessage;
        }catch(Exception e){
            e.printStackTrace();
            Log.d("help", "Error in Encrypt");
        }
        return null;
    }

    /**
     * Decrypt a string with AES algorithm.
     *
     *  is a string
     * @return the decrypted string
     */
    public String decrypt(byte[] data){
        try {
            byte[] iv = new byte[16];
            byte[] cipherText = new byte[data.length - iv.length];

            for(int i = 0; i < 16;  i++){
                iv[i] = data[i];
            }
            for(int i = 0; i < cipherText.length; i++){
                cipherText[i] = data[i + iv.length];
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            byte[] decryptedText = cipher.doFinal(cipherText);

            String plainText = new String(decryptedText);

            Log.d("help", plainText);

            return plainText;
        }catch(Exception e){
            e.printStackTrace();
            Log.d("help", "Error in decrypt");
        }
        return "";
    }

    public void generateNewKey(Context context, String userPassword, String devicePassword){
        try{
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            char[] passwordChars = userPassword.toCharArray();
            KeySpec spec = new PBEKeySpec(passwordChars, salt.getBytes(), 1, 256);
            SecretKey tmp = factory.generateSecret(spec);

            secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            FileManager fileManager = new FileManager();
//            byte[] passwordBytes = fileManager.readFromPasswordFile(context);
//            String dataString = Base64.encodeToString(passwordBytes, Base64.DEFAULT);
//            if(dataString.isEmpty() || dataString == null || dataString.equals("")){
            String newPassword = devicePassword;
            byte[] encryptedNewPassword = encrypt(newPassword);
            fileManager.writeToPasswordFile(context, encryptedNewPassword);
            byte[] passwordBytes = encryptedNewPassword;
//            }

            String passwordString = Base64.encodeToString(passwordBytes, Base64.DEFAULT);

            KeySpec spec2 = new PBEKeySpec(passwordString.toCharArray(), salt.getBytes(), 1, 256);
            SecretKey tmp2 = factory.generateSecret(spec2);

            secret = new SecretKeySpec(tmp2.getEncoded(), "AES");

            //        byte[] passwordBytes = password.getBytes();
//        return new SecretKeySpec(passwordBytes, ALGO);
        }catch(Exception e){
            e.printStackTrace();
            Log.d("Error", "Error in generateKey");
        }
    }


    /**
     * Generate a new encryption key.
     */
    public void generateKey(Context context){
        try{
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//            char[] passwordChars = password.toCharArray();
//            KeySpec spec = new PBEKeySpec(passwordChars, salt.getBytes(), 1, 256);
//            SecretKey tmp = factory.generateSecret(spec);


//            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, hashingIterations, keyLength);
//            userKey = skf.generateSecret(spec);

            secret = new SecretKeySpec(userKey.getEncoded(), "AES");

            DatabaseManager databaseManager = new DatabaseManager(context);
            UserConfiguration config = databaseManager.getUserConfiguration(1);


            byte[] passwordBytes = config.getDevicePassword();
            if(passwordBytes == null){
                passwordBytes = new byte[0];
            }
            String dataString = Base64.encodeToString(passwordBytes, Base64.DEFAULT);
            String passwordValue;
            if(dataString.isEmpty() || dataString == null || dataString.equals("")){
                passwordValue = generateSalt();
                byte[] encryptedNewPassword = encrypt(passwordValue);
                config.setDevicePassword(encryptedNewPassword);
                databaseManager.addUserConfiguration(config);
            }else{
                passwordValue = decrypt(passwordBytes);
                Log.d("help", "tet");
//                passwordValue = "hello";
            }


            KeySpec spec2 = new PBEKeySpec(passwordValue.toCharArray(), salt.getBytes(), 1, 256);
            SecretKey tmp2 = factory.generateSecret(spec2);

            secret = new SecretKeySpec(tmp2.getEncoded(), "AES");

            //        byte[] passwordBytes = password.getBytes();
//        return new SecretKeySpec(passwordBytes, ALGO);
        }catch(Exception e){
            e.printStackTrace();
            Log.d("Error", "Error in generateKey");
        }
    }

    public Boolean authenticateUser(String password, Context context, FileManager fileManager) throws JSONException {
        String userSalt = fileManager.getSalt(context);
        int iterations = fileManager.getHashingIterations(context);
        byte[] userSaltBytes;

        userSaltBytes = userSalt.getBytes();


        long startTime = System.nanoTime();

        byte[] hash = hashPassword(password, userSaltBytes, iterations);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        Log.d("help", "Hash duration: " + duration);

        String hashInFile = fileManager.readHash(context);
        String userHashEncoded  = Base64.encodeToString(hash, Base64.DEFAULT);


        if(userHashEncoded.equals(hashInFile)){

            generateKey(context);

            return true;
        }
        return false;
    }

    private SecretKey userKey = null;
    public byte[] hashPassword(String password, byte[] salt, int hashingIterations) {
        int keyLength = 256;
        try {
//            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, hashingIterations, keyLength);
            userKey = skf.generateSecret(spec);
            byte[] res = userKey.getEncoded();

            String hash = Base64.encodeToString(res, Base64.DEFAULT);
            String finalHash = SHA256Hash(hash);

            return finalHash.getBytes();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateSalt(){
        Random randomValue = new SecureRandom();
        byte[] salt = new byte[32];
        randomValue.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    public int calculatePasswordStrength(String password){

        //total score of password
        int iPasswordScore = 0;

        if( password.length() < 6 )
            return -1;
        else if( password.length() >= 10 )
            iPasswordScore += 2;

        //if it contains one digit, add 2 to total score
        if( password.matches("(?=.*[0-9]).*") )
            iPasswordScore += 2;

        //if it contains one lower case letter, add 2 to total score
        if( password.matches("(?=.*[a-z]).*") )
            iPasswordScore += 2;

        //if it contains one upper case letter, add 2 to total score
        if( password.matches("(?=.*[A-Z]).*") )
            iPasswordScore += 2;

        //if it contains one special character, add 2 to total score
        if( password.matches("(?=.*[~!@#$%^&*()_-]).*") )
            iPasswordScore += 2;

        if(iPasswordScore < 3){
            iPasswordScore = -2;
        }

        return iPasswordScore;

    }

//        String numberlessString = userPassword.replaceAll("[*0-9]", "");
//        if((userPassword.length() - numberlessString.length()) == 0){
//            return 2;
//        }

    public byte[] encryptWithAlternatePassword(String password, byte[] data, Boolean encryptOrDecryptFlag){
        try{
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secretBackup = secret;
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            byte[] cryptoData = null;
            if(encryptOrDecryptFlag){
                cryptoData = encrypt(Base64.encodeToString(data, Base64.DEFAULT));
            }else{
                cryptoData = decrypt(data).getBytes();
            }
            secret = secretBackup;
            return cryptoData;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }



}
