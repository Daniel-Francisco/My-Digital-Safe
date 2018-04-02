package com.development.security.ciphernote.security;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;
import android.webkit.JavascriptInterface;

import com.development.security.ciphernote.ListActivity;
import com.development.security.ciphernote.MainActivity;
import com.development.security.ciphernote.R;
import com.development.security.ciphernote.security.SecurityManager;


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
    }

    public void androidAuthenticateUser(String password){
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
            try{


                Boolean authentication = null;
                String password = strings[0];

                authentication = securityManager.authenticateUser(password, applicationContext);
                securityManager.generateKey(applicationContext);

                if(authentication){
                    Intent landingIntent = new Intent(applicationContext, ListActivity.class);
                    startActivity(landingIntent);
                    finish();
                }else{
                    long end_time = System.nanoTime();
                    double difference = (end_time - start_time) / 1e6;
                    loginTime = (int) difference;
                    return false;
                }

                long end_time = System.nanoTime();
                double difference = (end_time - start_time) / 1e6;
                loginTime = (int) difference;
                return true;
            }catch (Exception e){
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
            if(!status){
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:failedLogin()");
                    }
                });
                CharSequence failedAuthenticationString = getString(R.string.failed_login_toast);

                Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

    private void writeLoginTime(int time){
        SharedPreferences sp = getSharedPreferences("digital_safe", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("login_time", time);
        editor.commit();
    }
    private int readLoginTime(){
        SharedPreferences sp = getSharedPreferences("digital_safe", Activity.MODE_PRIVATE);
        int loginTime = sp.getInt("login_time", -1);

        return loginTime;
    }

    private void androidForgotPassword(){
        Intent forgotPasswordIntent = new Intent(applicationContext, ForgotPasswordActivity.class);
        startActivity(forgotPasswordIntent);
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
        public int getloginTime(){
            return readLoginTime();
        }

        @JavascriptInterface
        public void forgotPassword(){
            androidForgotPassword();
        }
    }

}
