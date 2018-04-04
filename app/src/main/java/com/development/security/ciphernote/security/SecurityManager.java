package com.development.security.ciphernote.security;

import android.accounts.AuthenticatorException;
import android.content.Context;
import android.util.Base64;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.File;
import com.development.security.ciphernote.model.SecurityQuestion;
import com.development.security.ciphernote.model.UserConfiguration;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

    public static SecurityManager getInstance() {
        return singletonInstance;
    }

    private static final String ALGO = "AES";
    String salt = "test";
    SecretKey secret = null;


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
    public byte[] encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidParameterSpecException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();

        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] ciphertext = cipher.doFinal(data.getBytes("UTF-8"));

        byte[] finalMessage = new byte[ciphertext.length + iv.length];
        for (int i = 0; i < iv.length; i++) {
            finalMessage[i] = iv[i];
        }
        for (int i = 0; i < ciphertext.length; i++) {
            finalMessage[i + iv.length] = ciphertext[i];
        }

        return finalMessage;
    }

    /**
     * Decrypt a string with AES algorithm.
     * <p>
     * is a string
     *
     * @return the decrypted string
     */
    public String decrypt(byte[] data) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        byte[] iv = new byte[16];
        byte[] cipherText = new byte[data.length - iv.length];

        for (int i = 0; i < 16; i++) {
            iv[i] = data[i];
        }
        for (int i = 0; i < cipherText.length; i++) {
            cipherText[i] = data[i + iv.length];
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        byte[] decryptedText = cipher.doFinal(cipherText);

        String plainText = new String(decryptedText);
        return plainText;

    }


    public UserConfiguration setSecurityQuestion(UserConfiguration userConfiguration, Context context, String password, String question, String response) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidParameterSpecException, JSONException, AuthenticatorException {
        byte[] devicePassword = userConfiguration.getDevicePassword();

        DatabaseManager databaseManager = new DatabaseManager(context);

        if (devicePassword == null) {
            devicePassword = new byte[0];
        }

        boolean authentication = authenticateUser(password, context);
        if(!authentication){
            throw new AuthenticatorException();
        }

        byte[] decryptedDevicePassword = encryptWithAlternatePassword(password, devicePassword, userConfiguration.getSalt().getBytes(), userConfiguration.getIterations(), false);

        byte[] reEncryptedDevicePassword = encryptWithAlternatePassword(response, decryptedDevicePassword, userConfiguration.getSalt().getBytes(), userConfiguration.getIterations(), true);

        userConfiguration.setSecurityQuestionDevicePassword(reEncryptedDevicePassword);


        byte[] hashedResponse = hashSecurityQuestion(response, userConfiguration.getSalt().getBytes(), userConfiguration.getIterations());
        String hashString = Base64.encodeToString(hashedResponse, Base64.DEFAULT);

        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setQuestion(question);
        securityQuestion.setAnswerHash(hashString);

        List<SecurityQuestion> securityQuestions = databaseManager.getAllSecurityQuestions();
        for (int i = 0; i < securityQuestions.size(); i++) {
            databaseManager.deleteSecurityQuestion(securityQuestions.get(i));
        }

        databaseManager.addSecurityQuestion(securityQuestion);

        return userConfiguration;
    }

    public void resetPasswordWithSecurityQuestion(String response, String newPassword, Context context) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidParameterSpecException {
        DatabaseManager databaseManager = new DatabaseManager(context);
        UserConfiguration configuration = databaseManager.getUserConfiguration();
        byte[] encryptedDevicePassword = configuration.getSecurityQuestionDevicePassword();

        byte[] decryptedDevicePassword = encryptWithAlternatePassword(response, encryptedDevicePassword, configuration.getSalt().getBytes(), configuration.getIterations(), false);

        byte[] reEncryptedDevicePassword = encryptWithAlternatePassword(newPassword, decryptedDevicePassword, configuration.getSalt().getBytes(), configuration.getIterations(), true);

        configuration.setDevicePassword(reEncryptedDevicePassword);

        byte[] newHash = hashPassword(newPassword, configuration.getSalt().getBytes(), configuration.getIterations());
        String newHashString = Base64.encodeToString(newHash, Base64.DEFAULT);

        configuration.setPassword_hash(newHashString);

        databaseManager.updateUserConfiguration(configuration);

    }

    public boolean compareSecurityQuestionResponse(String response, Context context) {
        DatabaseManager databaseManager = new DatabaseManager(context);

        UserConfiguration configuration = databaseManager.getUserConfiguration();
        List<SecurityQuestion> securityQuestions = databaseManager.getAllSecurityQuestions();

        SecurityQuestion securityQuestion = null;
        if (securityQuestions.size() > 0) {
            securityQuestion = securityQuestions.get(0);
        } else {
            return false;
        }

        String presetResponse = securityQuestion.getAnswerHash();

        byte[] inputtedResponse = hashSecurityQuestion(response, configuration.getSalt().getBytes(), configuration.getIterations());
        String responseHashAsString = Base64.encodeToString(inputtedResponse, Base64.DEFAULT);

        if (presetResponse.equals(responseHashAsString)) {
            return true;
        }

        return false;
    }


    public void changePassword(Context context, String userPassword, String newPassword) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            secret = new SecretKeySpec(userKey.getEncoded(), "AES");

            DatabaseManager databaseManager = new DatabaseManager(context);
            UserConfiguration config = databaseManager.getUserConfiguration();


            byte[] passwordBytes = config.getDevicePassword();
            if (passwordBytes == null) {
                passwordBytes = new byte[0];
            }
            String passwordString = Base64.encodeToString(passwordBytes, Base64.DEFAULT);
            String passwordValue;
            byte[] decryptedDevicePassword = encryptWithAlternatePassword(userPassword, passwordBytes, config.getSalt().getBytes(), config.getIterations(), false);
            passwordValue = new String(decryptedDevicePassword);


            byte[] reEncryptedDevicePassword = encryptWithAlternatePassword(newPassword, decryptedDevicePassword, config.getSalt().getBytes(), config.getIterations(), true);

            config.setDevicePassword(reEncryptedDevicePassword);

            databaseManager.addUserConfiguration(config);
//            UserConfiguration config = databaseManager.getUserConfiguration(1);
//            byte[] encryptedNewPassword = encrypt(passwordValue);
//            config.setDevicePassword(encryptedNewPassword);
//            databaseManager.addUserConfiguration(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Generate a new encryption key.
     */
    public void generateKey(Context context) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            secret = new SecretKeySpec(userKey.getEncoded(), "AES");

            DatabaseManager databaseManager = new DatabaseManager(context);
            UserConfiguration config = databaseManager.getUserConfiguration();


            byte[] passwordBytes = config.getDevicePassword();
            if (passwordBytes == null) {
                passwordBytes = new byte[0];
            }
            String dataString = Base64.encodeToString(passwordBytes, Base64.DEFAULT);
            String passwordValue;
            if (dataString.isEmpty() || dataString == null || dataString.equals("")) {
                passwordValue = generateSalt();
                byte[] encryptedNewPassword = encrypt(passwordValue);
                config.setDevicePassword(encryptedNewPassword);
                databaseManager.addUserConfiguration(config);
            } else {
                passwordValue = decrypt(passwordBytes);
            }


            KeySpec spec2 = new PBEKeySpec(passwordValue.toCharArray(), salt.getBytes(), 1, 256);
            SecretKey tmp2 = factory.generateSecret(spec2);

            secret = new SecretKeySpec(tmp2.getEncoded(), "AES");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean authenticateUser(String password, Context context) throws JSONException {
        DatabaseManager databaseManager = new DatabaseManager(context);
        String userSalt = databaseManager.getUserConfiguration().getSalt();
        int iterations = databaseManager.getUserConfiguration().getIterations();
        byte[] userSaltBytes;

        userSaltBytes = userSalt.getBytes();


        long startTime = System.nanoTime();

        byte[] hash = hashPassword(password, userSaltBytes, iterations);

        String hashString = Base64.encodeToString(hash, Base64.DEFAULT);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        String hashInFile = databaseManager.getUserConfiguration().getPassword_hash();
        String userHashEncoded = Base64.encodeToString(hash, Base64.DEFAULT);


        if (userHashEncoded.equals(hashInFile)) {

            return true;
        }
        return false;
    }


    private SecretKey userKey = null;

    private byte[] hashSecurityQuestion(String response, byte[] salt, int hashingIterations) {
        int keyLength = 256;
        try {

            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(response.toCharArray(), salt, hashingIterations, keyLength);
            SecretKey secret = skf.generateSecret(spec);
            byte[] res = secret.getEncoded();

            String hash = Base64.encodeToString(res, Base64.DEFAULT);
            String finalHash = SHA256Hash(hash);

            return finalHash.getBytes();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] hashPassword(String password, byte[] salt, int hashingIterations) {
        int keyLength = 256;
        try {

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

    public String generateSalt() {
        Random randomValue = new SecureRandom();
        byte[] salt = new byte[32];
        randomValue.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    public byte[] encryptWithAlternatePassword(String password, byte[] data, byte[] salt, int hashingIterations, Boolean encryptOrDecryptFlag) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, UnsupportedEncodingException {
        int keyLength = 256;
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, hashingIterations, keyLength);
        userKey = skf.generateSecret(spec);
        SecretKey secretBackup = secret;
        secret = new SecretKeySpec(userKey.getEncoded(), "AES");

        byte[] cryptoData = null;
        if (encryptOrDecryptFlag) {
            cryptoData = encrypt(Base64.encodeToString(data, Base64.DEFAULT));
        } else {
            cryptoData = Base64.decode(decrypt(data), Base64.DEFAULT);
        }
        secret = secretBackup;
        return cryptoData;

    }

    public File setFileHash(File file) throws NoSuchAlgorithmException {
        String concatString = file.getAccessDate() + file.getID() + file.getData() + file.getData();
        String hash = SHA256Hash(concatString);
        file.setHash(hash);

        return file;
    }

    public boolean validateFileHash(File file) throws NoSuchAlgorithmException {
        String concatString = file.getAccessDate() + file.getID() + file.getData() + file.getData();
        String hash = SHA256Hash(concatString);
        if (hash.equals(file.getHash())) {
            return true;
        }
        return false;
    }


}
