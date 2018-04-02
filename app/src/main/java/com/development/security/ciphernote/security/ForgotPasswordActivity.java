package com.development.security.ciphernote.security;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.development.security.ciphernote.ListActivity;
import com.development.security.ciphernote.R;
import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.SecurityQuestion;
import com.google.gson.Gson;

import java.util.List;

public class ForgotPasswordActivity extends AppCompatActivity {
    Context applicationContext = null;
    WebView browser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        applicationContext = this;

        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new ForgotPasswordActivity.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/forgotPasswordPage.html");
    }

    protected String userResponse = "";
    protected String newPassword = "";

    private class AsyncCheckSecurityQuestion extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                SecurityManager securityManager = new SecurityManager();
                return securityManager.compareSecurityQuestionResponse(userResponse, applicationContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            if (status) {
                Log.d("status", "good");
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:successfulResponse()");
                    }
                });
            }else{
                Log.d("status", "bad");
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:failedResponse()");
                    }
                });
                CharSequence successToast = "Incorrect response!";

                Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

    private class AsyncResetPassword extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                SecurityManager securityManager = new SecurityManager();
                securityManager.resetPasswordWithSecurityQuestion(userResponse, newPassword, applicationContext);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
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
            }else{
                CharSequence successToast = "Something went wrong!";

                Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

    int lastScore = 0;
    private int androidCheckPasswordStrength(String password){
        password = password.trim();

        //total score of password
        int iPasswordScore = 0;

        if (password.length() < 6) {
            return -1;
        }else{
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearLength()");
                }
            });
        }
        if (password.length() >= 10) {
            iPasswordScore += 2;
        }

        //if it contains one digit, add 2 to total score
        if (password.matches("(?=.*[0-9]).*")){
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearBadNumber()");
                }
            });
            iPasswordScore += 2;
        }

        //if it contains one lower case letter, add 2 to total score
        if (password.matches("(?=.*[a-z]).*")) {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearLowerCase()");
                }
            });
            iPasswordScore += 2;
        }
        //if it contains one upper case letter, add 2 to total score
        if (password.matches("(?=.*[A-Z]).*")) {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearUpperCase()");
                }
            });
            iPasswordScore += 2;
        }

        //if it contains one special character, add 2 to total score
        if (password.matches("(?=.*[~!@#$%^&*()_-]).*")) {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:clearBadSymbol()");
                }
            });
            iPasswordScore += 2;
        }

        if (iPasswordScore < 3) {
            iPasswordScore = -2;
        }

        lastScore = iPasswordScore;
        return iPasswordScore;
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public String fetchQuestions() {
            DatabaseManager databaseManager = new DatabaseManager(applicationContext);
            List<SecurityQuestion> securityQuestions = databaseManager.getAllSecurityQuestions();
            if(securityQuestions.size() > 0){
                SecurityQuestion securityQuestion = securityQuestions.get(0);
                Gson gson = new Gson();
                return gson.toJson(securityQuestion);
            }
            return "";
        }

        @JavascriptInterface
        public void checkSecurityQuestion(String response) {
            userResponse = response;
            new AsyncCheckSecurityQuestion().execute();
        }

        @JavascriptInterface
        public void resetPassword(String password1, String password2){
            if(password1.equals(password2)){
                newPassword = password1;
                new AsyncResetPassword().execute();
            }
        }

        @JavascriptInterface
        public int checkPasswordStrength(String password){
            return androidCheckPasswordStrength(password);
        }
    }
}
