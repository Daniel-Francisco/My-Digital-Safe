package com.development.security.ciphernote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.security.LoginActivity;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseManager databaseManager = new DatabaseManager(this.getApplicationContext());
        try{
            if(databaseManager.checkForFirstRunFile(this.getApplicationContext())){
                Intent startupIntent = new Intent(this, StartupActivity.class);
                startActivity(startupIntent);
                finish();
            }else{
                Intent loginActivity = new Intent(this, LoginActivity.class);
                startActivity(loginActivity);
                finish();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
