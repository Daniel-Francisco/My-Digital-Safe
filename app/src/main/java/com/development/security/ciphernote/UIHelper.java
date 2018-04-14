package com.development.security.ciphernote;

import java.util.Random;

/**
 * Created by danie on 4/9/2018.
 */

public class UIHelper {
    private String[] Recommendations = {
            "Have you configured your Forgot Password security questions yet?",
            "Enjoying the app? Give us a great rating! We'd appreciate it!",
            "To protect your notes from pesky onlookers, you can setup My Digital Safe to lock your data from excessive failed login's.",
            "Remember, your 'Quick Notes' from the login page will not be encrypted until you login again.",
            "We proudly keep all of your data right here, on your phone. Not on our servers, not in the cloud."
    };

    public String randomlyGenerateRecommendation() {
        Random rand = new Random();
        int random = rand.nextInt(Recommendations.length);
        return Recommendations[random];
    }
}
