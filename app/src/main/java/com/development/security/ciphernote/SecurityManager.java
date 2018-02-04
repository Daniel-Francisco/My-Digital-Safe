package com.development.security.ciphernote;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

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

    int hashingIterations = 5000;



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


    /**
     * Generate a new encryption key.
     */
    public void generateKey(Context context, String password){
        try{
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            char[] passwordChars = password.toCharArray();
            KeySpec spec = new PBEKeySpec(passwordChars, salt.getBytes(), 1, 256);
            SecretKey tmp = factory.generateSecret(spec);

            secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            FileManager fileManager = new FileManager();
            byte[] passwordBytes = fileManager.readFromPasswordFile(context);
            String dataString = Base64.encodeToString(passwordBytes, Base64.DEFAULT);
            if(dataString.isEmpty() || dataString == null || dataString.equals("")){
                String newPassword = generateSalt();
                byte[] encryptedNewPassword = encrypt(newPassword);
                fileManager.writeToPasswordFile(context, encryptedNewPassword);
                passwordBytes = encryptedNewPassword;
            }

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

    public Boolean authenticateUser(String password, Context context, FileManager fileManager) throws JSONException {
        String userSalt = fileManager.getSalt(context);
        byte[] userSaltBytes;

        userSaltBytes = userSalt.getBytes();

        byte[] hash = hashPassword(password, userSaltBytes);
        String hashInFile = fileManager.readHash(context);
        String userHashEncoded  = Base64.encodeToString(hash, Base64.DEFAULT);


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
        byte[] salt = new byte[32];
        randomValue.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    /*
        Validates the users passwords against the decided password entropy rules for the app.
        User's password must:
           1. Cannot be null or empty
	       2. 7+ characters in length
	       3. Upper and lower case

	       Return cases:
	       0 = Good
	       1 = Bad
	       2 = Comment on lack of number
	       3 = Comment on the number being 50 - 99 (easy to predict)
     */
    public Boolean validatePassword(String userPassword){
        Boolean nullEmptyFlag = false;
        Boolean lengthFlag = false;
        Boolean caseFlag = false;

        if(!(userPassword.length() > 6)){
            lengthFlag = true;
        }
        if(userPassword.isEmpty() || userPassword.equals(null) || userPassword.equals("")){
            nullEmptyFlag = true;
        }
        if(userPassword.toLowerCase().equals(userPassword) || userPassword.toUpperCase().equals(userPassword)){
            caseFlag = true;
        }

        if((nullEmptyFlag || lengthFlag || caseFlag)){
            return true;
        }

//        String numberlessString = userPassword.replaceAll("[*0-9]", "");
//        if((userPassword.length() - numberlessString.length()) == 0){
//            return 2;
//        }

        return false;
    }

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
