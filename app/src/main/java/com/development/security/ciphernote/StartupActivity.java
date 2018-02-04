package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;

public class StartupActivity extends AppCompatActivity {
    Context applicationContext;
    WebView browser;
    final SecurityManager securityManager = SecurityManager.getInstance();
    final FileManager fileManager = new FileManager();

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

    protected void androidCreatePassword(String passwordOne, String passwordTwo){
        try {
            //Boolean passwordVaidate = validatePassword(passwordOneValue);
            if (passwordOne.equals(passwordTwo)) {
                String salt = securityManager.generateSalt();
                Log.d("help", "StartupActivity salt: " + salt);
                fileManager.saveHashInfo(applicationContext, "", Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), 5000);

                String saltFromFile = fileManager.getSalt(applicationContext);

                byte[] newHash = securityManager.hashPassword(passwordOne, saltFromFile.getBytes());

                Log.d("help", "Startup ran");

                fileManager.writeToFirstRunFile(applicationContext);

                fileManager.saveHashInfo(applicationContext, Base64.encodeToString(newHash, Base64.DEFAULT), Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), 5000);
                Intent loginIntent = new Intent(applicationContext, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            } else {
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:clearFields()");
                    }
                });

                CharSequence failedAuthenticationString = getString(R.string.passwordsDoNotMatch);

                Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                toast.show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
    }
}
