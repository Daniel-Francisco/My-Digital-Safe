package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileManager fileManager = new FileManager();

        try{
            if(fileManager.checkForFirstRunFile(this.getApplicationContext())){
                fileManager.writeToFirstRunFile(this.getApplicationContext());
                Log.d("help", "FirstRun again");

                Intent startupIntent = new Intent(this, StartupActivity.class);
                startActivity(startupIntent);
            }else{
                Log.d("help", "Going to call landingActivity");

                Intent loginActivity = new Intent(this, LoginActivity.class);
                startActivity(loginActivity);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

//    @Override
//    public void onResume() {
//        super.onResume();  // Always call the superclass method first
//
//        Log.d("help", "Going to call landingActivity");
//
//        Intent loginActivity = new Intent(this, LoginActivity.class);
//        startActivity(loginActivity);
//    }
}
