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

package com.securityfirstdesigns.mydigitalsafe.app.security;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.securityfirstdesigns.mydigitalsafe.app.R;
import com.securityfirstdesigns.mydigitalsafe.app.model.DatabaseManager;
import com.securityfirstdesigns.mydigitalsafe.app.model.SecurityQuestion;
import com.securityfirstdesigns.mydigitalsafe.app.model.UserConfiguration;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity {
    Context applicationContext = null;
    WebView browser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        applicationContext = this;

        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new ForgotPasswordActivity.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/forgotPasswordPage.html");

        setTitle("Reset password");
    }

    protected String r1 = "";
    protected String r2 = "";
    protected String r3 = "";
    protected String r4 = "";
    protected String r5 = "";
    protected String newPassword = "";

    private class AsyncCheckSecurityQuestion extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                SecurityService securityService = new SecurityService();
                ArrayList<String> responses = new ArrayList<>();
                responses.add(r1);
                responses.add(r2);
                responses.add(r3);
                responses.add(r4);
                responses.add(r5);
                return securityService.compareSecurityQuestionResponse(responses, applicationContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            if (status) {
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:successfulResponse()");
                    }
                });
            } else {
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:failedResponse()");
                    }
                });
                CharSequence successToast = "Incorrect response!";

                Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                toast.show();

                SecurityService securityService = new SecurityService();
                DatabaseManager databaseManager = new DatabaseManager(applicationContext);
                UserConfiguration userConfiguration = databaseManager.getUserConfiguration();
                if (userConfiguration.getLockoutFlag() == 1) {
                    int failedCount = userConfiguration.getFailedLoginCount() + 1;
                    userConfiguration.setFailedLoginCount(failedCount);
                    userConfiguration = securityService.generateUnlockString(userConfiguration, failedCount);
                    databaseManager.updateUserConfiguration(userConfiguration);

                    displayLockoutCountdown();
                }

            }

        }
    }

    private void displayLockoutCountdown(){
        DatabaseManager databaseManager = new DatabaseManager(applicationContext);
        UserConfiguration userConfiguration = databaseManager.getUserConfiguration();
        int currentFails = userConfiguration.getFailedLoginCount();
        int allowedFails = userConfiguration.getAllowedFailedLoginCount();
        int failsLeft = allowedFails - currentFails;

        if(currentFails >= allowedFails){
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:userLockedOut()");
                }
            });
        } else {
            CharSequence failsLeftToast = "Incorrect password! You will be locked out in " + failsLeft + " more failed attempts!";
            Toast toast = Toast.makeText(applicationContext, failsLeftToast, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private class AsyncResetPassword extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                SecurityService securityService = new SecurityService();
                ArrayList<String> responses = new ArrayList<>();
                responses.add(r1);
                responses.add(r2);
                responses.add(r3);
                responses.add(r4);
                responses.add(r5);
                securityService.resetPasswordWithSecurityQuestion(responses, newPassword, applicationContext);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            if (status) {
                CharSequence successToast = "Password has been reset!";

                Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                toast.show();

                Intent loginActivity = new Intent(applicationContext, LoginActivity.class);
                startActivity(loginActivity);
                finish();
            } else {
                CharSequence successToast = "Something went wrong!";
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:failedResponse()");
                    }
                });

                Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

    int lastScore = 0;

    private int androidCheckPasswordStrength(String password) {
        password = password.trim();

        //total score of password
        int iPasswordScore = 0;

        if (password.length() < 6) {
            return -1;
        } else {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearLength()");
                }
            });
        }
        if (password.length() >= 10) {
            iPasswordScore += 2;
        }

        //if it contains one digit, add 2 to total score
        if (password.matches("(?=.*[0-9]).*")) {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearBadNumber()");
                }
            });
            iPasswordScore += 2;
        }

        //if it contains one lower case letter, add 2 to total score
        if (password.matches("(?=.*[a-z]).*")) {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearLowerCase()");
                }
            });
            iPasswordScore += 2;
        }
        //if it contains one upper case letter, add 2 to total score
        if (password.matches("(?=.*[A-Z]).*")) {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearUpperCase()");
                }
            });
            iPasswordScore += 2;
        }

        //if it contains one special character, add 2 to total score
        if (password.matches("(?=.*[~!@#$%^&*()_-]).*")) {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearBadSymbol()");
                }
            });
            iPasswordScore += 2;
        }

        if (iPasswordScore < 3) {
            iPasswordScore = -2;
        }

        lastScore = iPasswordScore;
        return iPasswordScore;
    }

    private String beautifyLockoutDateString() throws ParseException {
        DatabaseManager databaseManager = new DatabaseManager(applicationContext);
        UserConfiguration userConfiguration = databaseManager.getUserConfiguration();

        String lockoutDate = userConfiguration.getLockoutTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date unlockTime = sdf.parse(lockoutDate);
        Date now = new Date();
        long minutes = getDateDiff(now, unlockTime, TimeUnit.MINUTES);
        long seconds = getDateDiff(now, unlockTime, TimeUnit.SECONDS);

        if (seconds < 60) {
            return "Due to an excessive number of failed login attempts, we have locked your safe. Your Digital Safe unlocks in " + seconds + " second(s).";
        } else if (minutes < 60) {
            return "Due to an excessive number of failed login attempts, we have locked your safe. Your Digital Safe unlocks in " + minutes + " minute(s).";
        } else if (minutes < 1440) {
            long hourDiff = getDateDiff(unlockTime, now, TimeUnit.HOURS);
            return "Due to an excessive number of failed login attempts, we have locked your safe. Your Digital Safe unlocks in " + hourDiff + " hour(s).";
        } else {
            return "Due to an excessive number of failed login attempts, we have locked your safe. Your Digital Safe unlocks at " + lockoutDate;
        }
    }

    private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public String fetchQuestions() {
            DatabaseManager databaseManager = new DatabaseManager(applicationContext);
            List<SecurityQuestion> securityQuestions = databaseManager.getAllSecurityQuestions();
            if (securityQuestions.size() > 0) {
                Gson gson = new Gson();
                return gson.toJson(securityQuestions);
            }
            return "";
        }

        @JavascriptInterface
        public void checkSecurityQuestion(String responseOne, String responseTwo, String responseThree,
                                          String responseFour, String responseFive) {
            r1 = responseOne;
            r2 = responseTwo;
            r3 = responseThree;
            r4 = responseFour;
            r5 = responseFive;
            new AsyncCheckSecurityQuestion().execute();
        }

        @JavascriptInterface
        public void resetPassword(String password1, String password2) {
            if (password1.equals(password2) && lastScore > 3) {
                newPassword = password1;
                new AsyncResetPassword().execute();
            } else {
                if (!password1.equals(password2)) {
                    CharSequence successToast = "Passwords do not match!";

                    Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    CharSequence successToast = "Password is not complicated enough!";

                    Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                    toast.show();
                }


                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:clearFields()");
                    }
                });
            }
        }

        @JavascriptInterface
        public int checkPasswordStrength(String password) {
            return androidCheckPasswordStrength(password);
        }

        @JavascriptInterface
        public String getLockoutString() {
            try {
                DatabaseManager databaseManager = new DatabaseManager(applicationContext);
                UserConfiguration userConfiguration = databaseManager.getUserConfiguration();
                String lockoutDate = userConfiguration.getLockoutTime();
                SecurityService securityService = new SecurityService();
                boolean dateFlag = securityService.checkIfPastDate(lockoutDate);
                if (!dateFlag)
                    return beautifyLockoutDateString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
