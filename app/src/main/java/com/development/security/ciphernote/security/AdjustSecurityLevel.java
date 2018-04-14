package com.development.security.ciphernote.security;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.development.security.ciphernote.ListActivity;
import com.development.security.ciphernote.R;
import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.SecurityQuestion;

import java.util.ArrayList;
import java.util.List;

public class AdjustSecurityLevel extends AppCompatActivity {
    Context applicationContext = null;
    WebView browser = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_security_level);

        applicationContext = this;

        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new AdjustSecurityLevel.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/AdjustSecurityLevel.html");


        ImageView backButton = (ImageView) findViewById(R.id.cancelButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected boolean updatePassword(String password, int iterations){
        SecurityManager securityManager = SecurityManager.getInstance();
        return securityManager.changePasswordSecurityLevel(applicationContext, password, iterations);
    }
    String userPassword = "";
    int userIterations = 100000;
    private class AsyncUpdatePassword extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            return updatePassword(userPassword, userIterations);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            if (status) {
                CharSequence successToast = "Password has been reset!";

                Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                toast.show();

                Intent loginActivity = new Intent(applicationContext, LoginActivity.class);
                startActivity(loginActivity);
                finish();
            } else {
                CharSequence successToast = "Something went wrong!";

                Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void updatePasswordStrength(String password, String strength) {
            int iterations = 200000;
            if(strength.equals("low")){
                iterations = 80000;
            }else if(strength.equals("medium")){
                iterations = 120000;
            }else{
                iterations = 220000;
            }
            userPassword = password;
            userIterations = iterations;
            new AsyncUpdatePassword().execute();
        }

    }
}
