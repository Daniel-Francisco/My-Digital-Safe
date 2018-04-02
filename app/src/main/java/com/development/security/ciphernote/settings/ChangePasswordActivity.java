package com.development.security.ciphernote.settings;

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

import com.development.security.ciphernote.security.LoginActivity;
import com.development.security.ciphernote.MainActivity;
import com.development.security.ciphernote.R;
import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.UserConfiguration;
import com.development.security.ciphernote.security.SecurityManager;

public class ChangePasswordActivity extends AppCompatActivity {
    Context context;
    WebView browser;
    final SecurityManager securityManager = SecurityManager.getInstance();

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

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

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

//    @Override
//    public void onBackPressed() {
//        // your code.
//        Intent listIntent = new Intent(applicationContext, ListActivity.class);
//        startActivity(listIntent);
//        finish();
//    }


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
                    SecurityManager securityManager = SecurityManager.getInstance();
//                FileManager fileManager = new FileManager();
                    if (securityManager.authenticateUser(passwordCurrent, context)) {

                        DatabaseManager databaseManager = new DatabaseManager(context);

                        long start_time = System.nanoTime();
                        int iterations = 100000;

                        UserConfiguration userConfiguration = databaseManager.getUserConfiguration();
                        userConfiguration.setIterations(iterations);
                        userConfiguration.setPassword_hash("");
                        databaseManager.addUserConfiguration(userConfiguration);

                        String saltFromFile = databaseManager.getUserConfiguration().getSalt();

                        byte[] newHash = securityManager.hashPassword(passwordOne, saltFromFile.getBytes(), iterations);
                        String newHashString = Base64.encodeToString(newHash, Base64.DEFAULT);

                        userConfiguration.setPassword_hash(newHashString);
                        databaseManager.addUserConfiguration(userConfiguration);

                        long end_time = System.nanoTime();
                        double difference = (end_time - start_time) / 1e6;
                        int loginTime = (int) difference;
                        writeLoginTime(loginTime);


                        securityManager.changePassword(context, passwordCurrent, passwordOne);

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
                Intent loginActivity = new Intent(context, LoginActivity.class);
                startActivity(loginActivity);
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
