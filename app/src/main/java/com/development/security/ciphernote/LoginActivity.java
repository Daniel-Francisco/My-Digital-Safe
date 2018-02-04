package com.development.security.ciphernote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;
import android.webkit.JavascriptInterface;

import org.json.JSONException;

public class LoginActivity extends Activity {
    Context applicationContext;
    SharedPreferences prefs = null;


    final SecurityManager securityManager = SecurityManager.getInstance();
    DataStructures dataStructures = new DataStructures();
    final FileManager fileManager = new FileManager();
    WebView browser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Log.d("help", "LandingActivity ran");
        prefs = getSharedPreferences("com.security.test", MODE_PRIVATE);
        applicationContext = getApplicationContext();


        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/loginPage.html");
    }

    public void androidAuthenticateUser(String password){
       //Call ASYNC task

        browser.post(new Runnable() {
            @Override
            public void run() {
                browser.loadUrl("javascript:spinnerToggle(true)");
            }
        });
        new AsyncLogin().execute(password);
    }


    private class AsyncLogin extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                Boolean authentication = null;
                String password = strings[0];

                authentication = securityManager.authenticateUser(password, applicationContext, fileManager);

                if(authentication){
                    Log.d("help", "Successful authentication!");
                    securityManager.generateKey(applicationContext, password);
                    Intent landingIntent = new Intent(applicationContext, ListActivity.class);
                    startActivity(landingIntent);
                    finish();
                }else{
                    Log.d("help", "Failed authentication");
//                passwordField.setText("");
                    return false;
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:spinnerToggle(false)");
                }
            });

            if(!status){
                CharSequence failedAuthenticationString = getString(R.string.failed_login_toast);

                Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                toast.show();
            }

        }
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
    }

}
