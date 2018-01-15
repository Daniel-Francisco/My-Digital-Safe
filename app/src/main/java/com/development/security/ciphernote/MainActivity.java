package com.development.security.ciphernote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            SecurityManager securityManager = new SecurityManager();
            String crypto = securityManager.encrypt("Hello World");
            Log.d("Crypto:", crypto);
            String result = securityManager.decrypt(crypto);
            Log.d("Result:", result);
        }catch(Exception e){
            e.printStackTrace();
        }


    }
}
