package com.development.security.ciphernote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

    String passwordCurrent = null;
    String passwordOne = null;
    String passwordTwo = null;

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
       passwordCurrent = currentPassword;
       passwordOne = newPasswordOne;
       passwordTwo = newPasswordTwo;

        new ChangePasswordActivity.AsyncChangePassword().execute("");

    }

    private class AsyncChangePassword extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                if (passwordOne.equals(passwordTwo)) {
                    SecurityManager securityManager = SecurityManager.getInstance();
//                FileManager fileManager = new FileManager();
                    if (securityManager.authenticateUser(passwordCurrent, context)) {

                        DatabaseManager databaseManager = new DatabaseManager(context);
                        int score = securityManager.calculatePasswordStrength(passwordOne);

                        if (passwordOne.equals(passwordTwo) && score > 0) {
                            long start_time = System.nanoTime();
                            int iterations = 100000;

                            UserConfiguration userConfiguration = databaseManager.getUserConfiguration();
                            userConfiguration.setIterations(iterations);
                            userConfiguration.setPassword_hash("");
                            databaseManager.addUserConfiguration(userConfiguration);

                            String saltFromFile =  databaseManager.getUserConfiguration().getSalt();

                            byte[] newHash = securityManager.hashPassword(passwordOne, saltFromFile.getBytes(), iterations);
                            String newHashString = Base64.encodeToString(newHash, Base64.DEFAULT);

                            userConfiguration.setPassword_hash(newHashString);
                            databaseManager.addUserConfiguration(userConfiguration);

                            long end_time = System.nanoTime();
                            double difference = (end_time - start_time) / 1e6;
                            int loginTime = (int) difference;
                            writeLoginTime(loginTime);
                        }


                        securityManager.changePassword(context, passwordCurrent, passwordOne);

                        return true;
                    }
                } else {
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
            if(status){
                Intent loginActivity = new Intent(context, LoginActivity.class);
                startActivity(loginActivity);
                finish();
            }else{
                CharSequence failedAuthenticationString = getString(R.string.failed_login_toast);

                Toast toast = Toast.makeText(context, failedAuthenticationString, Toast.LENGTH_LONG);
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
