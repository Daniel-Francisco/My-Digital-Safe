package com.development.security.ciphernote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.UserConfiguration;

public class ChangePasswordActivity extends MenuActivity {
    Context context;
    WebView browser;
    final SecurityManager securityManager = SecurityManager.getInstance();
    final FileManager fileManager = new FileManager();

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    private void androidUpdatePassword(String currentPassword, String newPasswordOne, String newPasswordTwo) {
        try {
            if (newPasswordOne.equals(newPasswordTwo)) {
                SecurityManager securityManager = SecurityManager.getInstance();
                FileManager fileManager = new FileManager();
                if (securityManager.authenticateUser(currentPassword, context, fileManager)) {

                    DatabaseManager databaseManager = new DatabaseManager(context);
                    int score = securityManager.calculatePasswordStrength(newPasswordOne);

                    if (newPasswordOne.equals(newPasswordTwo) && score > 0) {
                        long start_time = System.nanoTime();
                        int iterations = 100000;
//                        if (levelValue.equals("high")) {
//                            iterations = 250000;
//                        } else if (levelValue.equals("medium")) {
//                            iterations = 75000;
//                        } else {
//                            iterations = 10000;
//                        }


//                        String salt = securityManager.generateSalt();
//                        Log.d("help", "StartupActivity salt: " + salt);

                        UserConfiguration userConfiguration = databaseManager.getUserConfiguration(1);
                        userConfiguration.setIterations(iterations);
                        userConfiguration.setPassword_hash("");
//                        userConfiguration.setSalt(salt);
                        databaseManager.addUserConfiguration(userConfiguration);
//                        fileManager.updateHashInfo(applicationContext, "", Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), iterations);

                        String saltFromFile = fileManager.getSalt(applicationContext);

                        byte[] newHash = securityManager.hashPassword(newPasswordOne, saltFromFile.getBytes(), iterations);
                        String newHashString = Base64.encodeToString(newHash, Base64.DEFAULT);

                        userConfiguration.setPassword_hash(newHashString);
                        databaseManager.addUserConfiguration(userConfiguration);

                        Log.d("help", "Startup ran");


//                        fileManager.updateHashInfo(applicationContext, Base64.encodeToString(newHash, Base64.DEFAULT), Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), iterations);

                        long end_time = System.nanoTime();
                        double difference = (end_time - start_time) / 1e6;
                        int loginTime = (int) difference;
                        writeLoginTime(loginTime);
                    }


                    securityManager.changePassword(context, currentPassword, newPasswordOne);

                    Intent loginActivity = new Intent(context, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }
            } else {
                CharSequence failedAuthenticationString = getString(R.string.failed_login_toast);

                Toast toast = Toast.makeText(context, failedAuthenticationString, Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        browser.post(new Runnable() {
            @Override
            public void run() {
                browser.loadUrl("javascript:clearFields()");
            }
        });

    }

    private int androidCheckPasswordStrength(String password) {
        return SecurityManager.getInstance().calculatePasswordStrength(password);
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
