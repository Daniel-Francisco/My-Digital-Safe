package com.securityfirstdesigns.mydigitalsafe.app;

import com.securityfirstdesigns.mydigitalsafe.app.core.StartupActivity;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by danie on 4/9/2018.
 */

public class SecurityTests {
    @Test
    public void encryptDecrypt() throws Exception {
        String testData = generateRandomString();
        SecurityManager securityManager = new SecurityManager();

        StartupActivity startupActivity = new StartupActivity();


    }

    protected String generateRandomString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890qwertyuiopasdfghjklzxcvbnm!@#$%^&*()-+=";
        StringBuilder randomString = new StringBuilder();
        Random rnd = new Random();
        int randomSize = rnd.nextInt(400)+20;
        while (randomString.length() < randomSize) { // length of the random string.
            int index = (int) (rnd.nextFloat() * chars.length());
            randomString.append(chars.charAt(index));
        }
        String returnString = randomString.toString();
        return returnString;

    }

}
