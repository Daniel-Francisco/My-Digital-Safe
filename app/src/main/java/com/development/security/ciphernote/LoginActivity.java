package com.development.security.ciphernote;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.webkit.JavascriptInterface;

import org.json.JSONException;

import java.util.EventListener;

public class LoginActivity extends Activity {
    Context applicationContext;
    SharedPreferences prefs = null;


    final SecurityManager securityManager = SecurityManager.getInstance();
    DataStructures dataStructures = new DataStructures();
    final FileManager fileManager = new FileManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Log.d("help", "LandingActivity ran");
        prefs = getSharedPreferences("com.security.test", MODE_PRIVATE);
        applicationContext = getApplicationContext();

        WebView browser;
        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/loginPage.html");
    }

    public void androidAuthenticateUser(String password){
        try {
            if(password == null){
                password = "";
            }

            Boolean authentication = null;

            authentication = securityManager.authenticateUser(password, applicationContext, fileManager);

            if(authentication){
                Log.d("help", "Successful authentication!");
                securityManager.generateKey(password);
                Intent landingIntent = new Intent(applicationContext, ListActivity.class);
                startActivity(landingIntent);
            }else{
                Log.d("help", "Failed authentication");
//                passwordField.setText("");


                CharSequence failedAuthenticationString = getString(R.string.failed_login_toast);

                Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
