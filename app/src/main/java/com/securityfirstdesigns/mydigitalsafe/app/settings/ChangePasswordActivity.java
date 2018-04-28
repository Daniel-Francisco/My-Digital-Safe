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

package com.securityfirstdesigns.mydigitalsafe.app.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.securityfirstdesigns.mydigitalsafe.app.core.HomeActivity;
import com.securityfirstdesigns.mydigitalsafe.app.core.MainActivity;
import com.securityfirstdesigns.mydigitalsafe.app.R;
import com.securityfirstdesigns.mydigitalsafe.app.model.DatabaseManager;
import com.securityfirstdesigns.mydigitalsafe.app.model.UserConfiguration;
import com.securityfirstdesigns.mydigitalsafe.app.security.SecurityService;

public class ChangePasswordActivity extends AppCompatActivity {
    Context context;
    WebView browser;
    final SecurityService securityService = SecurityService.getInstance();

    String passwordCurrent = null;
    String passwordOne = null;
    String passwordTwo = null;

    private Context applicationContext = null;

    int lastScore = 0;

//    final FileManager fileManager = new FileManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        context = this.getApplicationContext();
        applicationContext = context;

        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new ChangePasswordActivity.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/ChangePassword.html");

    }

    @Override
    public void onPause() {
        super.onPause();
        browser.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        browser.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRestart() {
        super.onRestart();

        Intent mainActivityIntent = new Intent(applicationContext, MainActivity.class);
        startActivity(mainActivityIntent);

        finish();
    }

    @Override
    public void onBackPressed() {
        Intent settingsIntent = new Intent(applicationContext, SettingsActivity.class);
        startActivity(settingsIntent);
        finish();
    }


    private void androidUpdatePassword(String currentPassword, String newPasswordOne, String newPasswordTwo) {
        passwordCurrent = currentPassword;
        passwordOne = newPasswordOne;
        passwordTwo = newPasswordTwo;

        new ChangePasswordActivity.AsyncChangePassword().execute("");

    }

    private class AsyncChangePassword extends AsyncTask<String, String, Boolean> {
        int failCode = 0;
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                if (passwordOne.equals(passwordTwo) && lastScore > 3) {
                    SecurityService securityService = SecurityService.getInstance();
//                FileManager fileManager = new FileManager();
                    if (securityService.authenticateUser(passwordCurrent, context)) {

                        DatabaseManager databaseManager = new DatabaseManager(context);

                        long start_time = System.nanoTime();
                        int iterations = 100000;

                        UserConfiguration userConfiguration = databaseManager.getUserConfiguration();
                        userConfiguration.setIterations(iterations);
                        userConfiguration.setPassword_hash("");
                        databaseManager.addUserConfiguration(userConfiguration);

                        String saltFromFile = databaseManager.getUserConfiguration().getSalt();

                        byte[] newHash = securityService.hashPassword(passwordOne, saltFromFile.getBytes(), iterations);
                        String newHashString = Base64.encodeToString(newHash, Base64.DEFAULT);

                        userConfiguration.setPassword_hash(newHashString);
                        databaseManager.addUserConfiguration(userConfiguration);

                        long end_time = System.nanoTime();
                        double difference = (end_time - start_time) / 1e6;
                        int loginTime = (int) difference;
                        writeLoginTime(loginTime);


                        securityService.changePassword(context, passwordCurrent, passwordOne);

                        return true;
                    }else{
                        failCode = 2;
                    }
                } else {
                    if(lastScore > 3){
                        failCode = 3;
                    }else{
                        failCode = 1;
                    }
                    return false;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            if (status) {
                Intent listActivity = new Intent(context, HomeActivity.class);
                startActivity(listActivity);
                finish();
            } else {
                if(failCode == 1){
                    CharSequence failedAuthenticationString = getString(R.string.passwordTooShort);

                    Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                    toast.show();
                }else if(failCode == 3){
                    CharSequence failedAuthenticationString = getString(R.string.passwordsDoNotMatch);

                    Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    CharSequence failedAuthenticationString = getString(R.string.failed_login_toast);

                    Toast toast = Toast.makeText(context, failedAuthenticationString, Toast.LENGTH_LONG);
                    toast.show();
                }

            }
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearFields()");
                }
            });

        }
    }

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

    private void writeLoginTime(int time) {
        SharedPreferences sp = getSharedPreferences("digital_safe", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("login_time", time);
        editor.commit();
    }


    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        //Android.changePassword(currentPassword, passwordOne, passwordTwo, dropDownValue);
        @JavascriptInterface
        public void changePassword(String currentPassword, String newPasswordOne, String newPasswordTwo) {
            androidUpdatePassword(currentPassword, newPasswordOne, newPasswordTwo);
        }

        @JavascriptInterface
        public int checkPasswordStrength(String password) {
            return androidCheckPasswordStrength(password);
        }
    }
}
