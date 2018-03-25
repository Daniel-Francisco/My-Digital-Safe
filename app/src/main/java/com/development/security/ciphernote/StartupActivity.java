package com.development.security.ciphernote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.UserConfiguration;

import org.json.JSONException;

public class StartupActivity extends AppCompatActivity {
    Context applicationContext;
    WebView browser;
    final SecurityManager securityManager = SecurityManager.getInstance();

    String firstPassword = null;
    String secondPassword = null;

    int lastScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        applicationContext = getApplicationContext();
        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new StartupActivity.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/StartupPage.html");
    }


    private class AsyncAccountCreation extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                if (firstPassword.equals(secondPassword) && lastScore > 3) {
                    long start_time = System.nanoTime();
                    int iterations = 100000;

                    String salt = securityManager.generateSalt();

                    DataStructures.UserConfiguration userConfiguration = new DataStructures.UserConfiguration();
                    userConfiguration.setPasswordHash("");
                    userConfiguration.setSalt(salt);
                    userConfiguration.setIterations(iterations);
                    DatabaseManager databaseManager = new DatabaseManager(applicationContext);
                    databaseManager.addUserConfiguration(new UserConfiguration(userConfiguration.getIterations(), userConfiguration.getPasswordHash(), userConfiguration.getSalt()));

                    String saltFromFile = databaseManager.getUserConfiguration().getSalt();

                    byte[] newHash = securityManager.hashPassword(firstPassword, saltFromFile.getBytes(), iterations);

                    databaseManager.checkConfigDirectory(applicationContext);
                    databaseManager.writeToDataFile(applicationContext, "started".getBytes(), "startup", true);


                    UserConfiguration currentUserConfig = databaseManager.getUserConfiguration();
                    String hash = Base64.encodeToString(newHash, Base64.DEFAULT);
                    currentUserConfig.setPassword_hash(hash);
                    databaseManager.addUserConfiguration(currentUserConfig);

                    long end_time = System.nanoTime();
                    double difference = (end_time - start_time) / 1e6;
                    int loginTime = (int) difference;
                    writeLoginTime(loginTime);


                    return true;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return false;

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            if(status){
                Intent loginIntent = new Intent(applicationContext, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }else{
                if(firstPassword.equals(secondPassword)){
                    CharSequence failedAuthenticationString = getString(R.string.passwordTooShort);

                    Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    CharSequence failedAuthenticationString = getString(R.string.passwordsDoNotMatch);

                    Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
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




    protected void androidCreatePassword(String passwordOne, String passwordTwo){
       firstPassword = passwordOne;
       secondPassword = passwordTwo;

        new StartupActivity.AsyncAccountCreation().execute("");
    }

    private int androidCheckPasswordStrength(String password){
        password = password.trim();

        //total score of password
        int iPasswordScore = 0;

        if (password.length() < 6) {
            return -1;
        }else{
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
        if (password.matches("(?=.*[0-9]).*")){
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

       private void writeLoginTime(int time){
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
        @JavascriptInterface
        public void createPassword(String passwordOne, String passwordTwo) {
            androidCreatePassword(passwordOne, passwordTwo);
        }

        @JavascriptInterface
        public int checkPasswordStrength(String password){
            return androidCheckPasswordStrength(password);
        }
    }
}
