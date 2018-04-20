/*
 * My Digital Safe, the secure notepad Android app.
 * Copyright (C) 2018 Security First Designs
 *
 * My Digital Safe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <a href="www.gnu.org/licenses/">here</a>.
 */

package com.development.security.ciphernote.security;

import android.accounts.AuthenticatorException;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.File;
import com.development.security.ciphernote.model.SecurityQuestion;
import com.development.security.ciphernote.model.UserConfiguration;

import org.json.JSONException;

import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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


    public UserConfiguration setSecurityQuestion(UserConfiguration userConfiguration, Context context,
                                                 String password, int numberOfQuestions, String q1,
                                                 String r1, String q2, String r2, String q3, String r3,
                                                 String q4, String r4, String q5, String r5) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidParameterSpecException, JSONException, AuthenticatorException {
        byte[] devicePassword = userConfiguration.getDevicePassword();

        DatabaseManager databaseManager = new DatabaseManager(context);

        if (devicePassword == null) {
            devicePassword = new byte[0];
        }

        boolean authentication = authenticateUser(password, context);
        if (!authentication) {
            throw new AuthenticatorException();
        }

        String recoveryKey = "";
        ArrayList<SecurityQuestion> questionList = new ArrayList<>();
        if (numberOfQuestions > 0) {
            recoveryKey = recoveryKey + r1;
            questionList.add(generateQuestion(userConfiguration, q1, r1));
        }
        if (numberOfQuestions > 1) {
            recoveryKey = recoveryKey + r2;
            questionList.add(generateQuestion(userConfiguration, q2, r2));
        }
        if (numberOfQuestions > 2) {
            recoveryKey = recoveryKey + r3;
            questionList.add(generateQuestion(userConfiguration, q3, r3));
        }
        if (numberOfQuestions > 3) {
            recoveryKey = recoveryKey + r4;
            questionList.add(generateQuestion(userConfiguration, q4, r4));
        }
        if (numberOfQuestions > 4) {
            recoveryKey = recoveryKey + r5;
            questionList.add(generateQuestion(userConfiguration, q5, r5));
        }
        byte[] decryptedDevicePassword = encryptWithAlternatePassword(password, devicePassword, userConfiguration.getSalt().getBytes(), userConfiguration.getIterations(), false);

        byte[] reEncryptedDevicePassword = encryptWithAlternatePassword(recoveryKey, decryptedDevicePassword, userConfiguration.getSalt().getBytes(), userConfiguration.getIterations(), true);

        userConfiguration.setSecurityQuestionDevicePassword(reEncryptedDevicePassword);


        List<SecurityQuestion> securityQuestions = databaseManager.getAllSecurityQuestions();
        for (int i = 0; i < securityQuestions.size(); i++) {
            databaseManager.deleteSecurityQuestion(securityQuestions.get(i));
        }

        for (int i = 0; i < questionList.size(); i++) {
            SecurityQuestion securityQuestion = questionList.get(i);
            securityQuestion.setQuestionOrder(i + 1);
            databaseManager.addSecurityQuestion(securityQuestion);
        }

        return userConfiguration;
    }

    private SecurityQuestion generateQuestion(UserConfiguration userConfiguration, String question, String response) {
        byte[] hashedResponse = hashSecurityQuestion(response, userConfiguration.getSalt().getBytes(), userConfiguration.getIterations());
        String hashString = Base64.encodeToString(hashedResponse, Base64.DEFAULT);

        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setQuestion(question);
        securityQuestion.setAnswerHash(hashString);

        return securityQuestion;
    }

    public void resetPasswordWithSecurityQuestion(List<String> responses, String newPassword, Context context) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidParameterSpecException {
        DatabaseManager databaseManager = new DatabaseManager(context);
        UserConfiguration configuration = databaseManager.getUserConfiguration();

        String responseKey = "";
        for (int i = 0; i < responses.size(); i++) {
            responseKey = responseKey + responses.get(i);
        }


        byte[] encryptedDevicePassword = configuration.getSecurityQuestionDevicePassword();

        byte[] decryptedDevicePassword = encryptWithAlternatePassword(responseKey, encryptedDevicePassword, configuration.getSalt().getBytes(), configuration.getIterations(), false);

        byte[] reEncryptedDevicePassword = encryptWithAlternatePassword(newPassword, decryptedDevicePassword, configuration.getSalt().getBytes(), configuration.getIterations(), true);

        configuration.setDevicePassword(reEncryptedDevicePassword);

        byte[] newHash = hashPassword(newPassword, configuration.getSalt().getBytes(), configuration.getIterations());
        String newHashString = Base64.encodeToString(newHash, Base64.DEFAULT);

        configuration.setPassword_hash(newHashString);

        databaseManager.updateUserConfiguration(configuration);

    }

    public boolean compareSecurityQuestionResponse(List<String> responses, Context context) {
        DatabaseManager databaseManager = new DatabaseManager(context);

        UserConfiguration configuration = databaseManager.getUserConfiguration();
        List<SecurityQuestion> securityQuestions = databaseManager.getAllSecurityQuestions();

        if (securityQuestions.size() > 0) {
            for (int i = 0; i < securityQuestions.size(); i++) {
                SecurityQuestion securityQuestion = securityQuestions.get(i);
                String presetResponse = securityQuestion.getAnswerHash();

                byte[] inputtedResponse = hashSecurityQuestion(responses.get(i), configuration.getSalt().getBytes(), configuration.getIterations());
                String responseHashAsString = Base64.encodeToString(inputtedResponse, Base64.DEFAULT);

                if (!presetResponse.equals(responseHashAsString)) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public boolean changePasswordSecurityLevel(Context context, String password, int newIterations) {
        DatabaseManager databaseManager = new DatabaseManager(context);

        UserConfiguration userConfiguration = databaseManager.getUserConfiguration();
        String userSalt = userConfiguration.getSalt();
        byte[] userSaltBytes;

        userSaltBytes = userSalt.getBytes();
        int iterations = userConfiguration.getIterations();
        byte[] hash = hashPassword(password, userSaltBytes, iterations);

        String hashInFile = databaseManager.getUserConfiguration().getPassword_hash();
        String userHashEncoded = Base64.encodeToString(hash, Base64.DEFAULT);


        if (userHashEncoded.equals(hashInFile)) {
            byte[] newHash = hashPassword(password, userSaltBytes, newIterations);
            String newHashString = Base64.encodeToString(newHash, Base64.DEFAULT);

            userConfiguration.setIterations(newIterations);
            userConfiguration.setPassword_hash(newHashString);

            databaseManager.updateUserConfiguration(userConfiguration);

            changePassword(context, password, password);
            return true;
        }
        return false;
    }

    public void changePassword(Context context, String userPassword, String newPassword) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            DatabaseManager databaseManager = new DatabaseManager(context);
            UserConfiguration config = databaseManager.getUserConfiguration();


            byte[] passwordBytes = config.getDevicePassword();
            if (passwordBytes == null) {
                passwordBytes = new byte[0];
            }
            byte[] decryptedDevicePassword = encryptWithAlternatePassword(userPassword, passwordBytes, config.getSalt().getBytes(), config.getIterations(), false);


            byte[] reEncryptedDevicePassword = encryptWithAlternatePassword(newPassword, decryptedDevicePassword, config.getSalt().getBytes(), config.getIterations(), true);

            config.setDevicePassword(reEncryptedDevicePassword);

            databaseManager.addUserConfiguration(config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] startup(Context context, String password, byte[] salt, int hashingIterations){
        byte[] hashedPassword = hashPassword(password, salt, hashingIterations);
        generateKey(context);
        return hashedPassword;
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

    public Boolean authenticateUser(String password, Context context) throws JSONException {
        DatabaseManager databaseManager = new DatabaseManager(context);
        String userSalt = databaseManager.getUserConfiguration().getSalt();
        int iterations = databaseManager.getUserConfiguration().getIterations();
        byte[] userSaltBytes;

        userSaltBytes = userSalt.getBytes();

        byte[] hash = hashPassword(password, userSaltBytes, iterations);

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
        SecretKey userKeyBackup = userKey;
        SecretKey secretBackup = secret;
        secret = new SecretKeySpec(userKey.getEncoded(), "AES");

        byte[] cryptoData = null;
        if (encryptOrDecryptFlag) {
            cryptoData = encrypt(Base64.encodeToString(data, Base64.DEFAULT));
        } else {
            cryptoData = Base64.decode(decrypt(data), Base64.DEFAULT);
        }
        secret = secretBackup;
        userKey = userKeyBackup;
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


    public UserConfiguration generateUnlockString(UserConfiguration userConfiguration, int failCount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        int allowedFails = userConfiguration.getAllowedFailedLoginCount();
        Log.d("allowedFails", String.valueOf(allowedFails));
        if (allowedFails == 0) {
            allowedFails = 5;
        }
        if (failCount >= allowedFails) {
            int difference = failCount - allowedFails;

            int failLevel = difference++;
            double lockoutMinutes = Math.pow(2, failLevel);
            int lockoutMinutesInt = (int) lockoutMinutes;
            Calendar c = Calendar.getInstance();
            c.setTime(new Date()); // Now use today date.
            c.add(Calendar.MINUTE, lockoutMinutesInt);
            Date date = c.getTime();
            userConfiguration.setKeyLockoutTime(sdf.format(date));
            return userConfiguration;

        }
        userConfiguration.setKeyLockoutTime(null);
        return userConfiguration;
    }

    public boolean checkIfPastDate(String dateString) throws ParseException {
        if (dateString != null && !dateString.equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = sdf.parse(dateString);
            Date now = new Date();
            if (now.after(date)) {
                return true;
            }
        } else {
            return true;
        }

        return false;
    }
}
