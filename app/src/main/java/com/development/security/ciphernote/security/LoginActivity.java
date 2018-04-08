package com.development.security.ciphernote.security;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;
import android.webkit.JavascriptInterface;

import com.development.security.ciphernote.EditNoteActivity;
import com.development.security.ciphernote.ListActivity;
import com.development.security.ciphernote.MainActivity;
import com.development.security.ciphernote.QuickNoteEdit;
import com.development.security.ciphernote.R;
import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.File;
import com.development.security.ciphernote.model.QuickNoteFile;
import com.development.security.ciphernote.model.UserConfiguration;
import com.development.security.ciphernote.security.SecurityManager;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class LoginActivity extends Activity {
    Context applicationContext;
    SharedPreferences prefs = null;
    int loginTime = -1;


    final SecurityManager securityManager = SecurityManager.getInstance();
    WebView browser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefs = getSharedPreferences("com.security.test", MODE_PRIVATE);
        applicationContext = getApplicationContext();


        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/loginPage.html");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent quickNoteEditIntent = new Intent(applicationContext, QuickNoteEdit.class);
                startActivity(quickNoteEditIntent);
            }
        });

    }

    public void androidAuthenticateUser(String password) {
        //Call ASYNC task
        new AsyncLogin().execute(password);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity();
        }
    }

    private class AsyncLogin extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            long start_time = System.nanoTime();
            try {


                Boolean authentication = null;
                String password = strings[0];

                authentication = securityManager.authenticateUser(password, applicationContext);
                securityManager.generateKey(applicationContext);
                long end_time = System.nanoTime();
                double difference = (end_time - start_time) / 1e6;
                loginTime = (int) difference;
                if (authentication) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            long end_time = System.nanoTime();
            double difference = (end_time - start_time) / 1e6;
            loginTime = (int) difference;
            return false;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            writeLoginTime(loginTime);

            boolean lockedOutFlag = false;

            DatabaseManager databaseManagerCheck = new DatabaseManager(applicationContext);
            UserConfiguration userConfigurationCheck = databaseManagerCheck.getUserConfiguration();

            if (userConfigurationCheck.getLockoutFlag() == 1) {
                try {
                    String lockoutDate = userConfigurationCheck.getLockoutTime();
                    boolean dateFlag = checkIfPastDate(lockoutDate);
                    if (!dateFlag) {
                        //locked out

                        browser.post(new Runnable() {
                            @Override
                            public void run() {
                                browser.loadUrl("javascript:lockedOutLogin()");
                            }
                        });
                        CharSequence failedAuthenticationString = "Account locked out!";

                        Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                        toast.show();
                        lockedOutFlag = true;


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!lockedOutFlag) {
                if (!status) {
                    browser.post(new Runnable() {
                        @Override
                        public void run() {
                            browser.loadUrl("javascript:failedLogin()");
                        }
                    });
                    CharSequence failedAuthenticationString = getString(R.string.failed_login_toast);

                    Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                    toast.show();

                    DatabaseManager databaseManager = new DatabaseManager(applicationContext);
                    UserConfiguration userConfiguration = databaseManager.getUserConfiguration();
                    if (userConfiguration.getLockoutFlag() == 1) {
                        int failedCount = userConfiguration.getFailedLoginCount() + 1;
                        userConfiguration.setFailedLoginCount(failedCount);
                        userConfiguration = generateUnlockString(userConfiguration, failedCount);
                        databaseManager.updateUserConfiguration(userConfiguration);
                    }
                } else {
                    DatabaseManager databaseManager = new DatabaseManager(applicationContext);
                    UserConfiguration userConfiguration = databaseManager.getUserConfiguration();

                    userConfiguration.setFailedLoginCount(0);
                    databaseManager.updateUserConfiguration(userConfiguration);

                    Intent landingIntent = new Intent(applicationContext, ListActivity.class);
                    startActivity(landingIntent);
                    finish();


                }
            }


        }
    }

    private UserConfiguration generateUnlockString(UserConfiguration userConfiguration, int failCount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        int allowedFails = userConfiguration.getAllowedFailedLoginCount();
        Log.d("allowedFails", String.valueOf(allowedFails));
        if (allowedFails == 0) {
            allowedFails = 5;
        }
        if (failCount >= allowedFails) {
            int difference = failCount - allowedFails;
            int checkInt = (difference % 3);
            if (difference == 0) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date()); // Now use today date.
                c.add(Calendar.MINUTE, 1);
                Date date = c.getTime();
                userConfiguration.setKeyLockoutTime(sdf.format(date));
                return userConfiguration;
            } else if (checkInt == 0) {
                int failLevel = difference / 3;
                double lockoutMinutes = Math.pow(2, failLevel);
                int lockoutMinutesInt = (int) lockoutMinutes;
                Calendar c = Calendar.getInstance();
                c.setTime(new Date()); // Now use today date.
                c.add(Calendar.MINUTE, lockoutMinutesInt);
                Date date = c.getTime();
                userConfiguration.setKeyLockoutTime(sdf.format(date));
                return userConfiguration;
            }
        }
        userConfiguration.setKeyLockoutTime(null);
        return userConfiguration;
    }

    private boolean checkIfPastDate(String dateString) throws ParseException {
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

    private void writeLoginTime(int time) {
        SharedPreferences sp = getSharedPreferences("digital_safe", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("login_time", time);
        editor.commit();
    }

    private int readLoginTime() {
        SharedPreferences sp = getSharedPreferences("digital_safe", Activity.MODE_PRIVATE);
        int loginTime = sp.getInt("login_time", -1);

        return loginTime;
    }

    private void androidForgotPassword() {
        Intent forgotPasswordIntent = new Intent(applicationContext, ForgotPasswordActivity.class);
        startActivity(forgotPasswordIntent);
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

        if(seconds < 60){
            return "Due to an excessive number of failed login attempts, we have locked your safe. Your Digital Safe unlocks in " + seconds + " second(s).";
        }else if(minutes < 60){
            return "Due to an excessive number of failed login attempts, we have locked your safe. Your Digital Safe unlocks in " + minutes + " minute(s).";
        }else if(minutes < 1440){
            long hourDiff = getDateDiff(unlockTime, now, TimeUnit.HOURS);
            return "Due to an excessive number of failed login attempts, we have locked your safe. Your Digital Safe unlocks in " + hourDiff + " hour(s).";
        }else{
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
        public void authenticateUser(String password) {
            androidAuthenticateUser(password);
        }

        @JavascriptInterface
        public int getloginTime() {
            return readLoginTime();
        }

        @JavascriptInterface
        public void forgotPassword() {
            androidForgotPassword();
        }

        @JavascriptInterface
        public String getLockoutString() {
            try {
                return beautifyLockoutDateString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return "Your Digital Safe is locked due to too many failed login attempts.";
        }
    }

}
