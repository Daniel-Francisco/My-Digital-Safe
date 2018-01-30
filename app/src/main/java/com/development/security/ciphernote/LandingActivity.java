package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class LandingActivity extends AppCompatActivity {
    Context applicationContext;
    EditText userInput = null;
    FileManager fileManager;
    SecurityManager securityManager;
    String fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");

        setContentView(R.layout.activity_landing);

        fileManager = new FileManager();
        securityManager = SecurityManager.getInstance();
        applicationContext = this.getBaseContext();



        WebView browser;
        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/EditNotePage.html");


    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            String plain = userInput.getText().toString();
            byte[] userCipher = securityManager.encrypt(plain);

            Log.d("help", "Cipher: " + Base64.encodeToString(userCipher, Base64.DEFAULT));

            fileManager.writeDataFile(applicationContext, fileName, userCipher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchFileString(){
        try {
            byte[] fileJson = fileManager.readDataFile(applicationContext, fileName);
            String decrypted = securityManager.decrypt(fileJson);
            return decrypted;
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public String fetchContents() {
            return fetchFileString();
        }
    }
}
