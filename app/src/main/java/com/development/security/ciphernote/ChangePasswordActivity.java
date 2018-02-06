package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {
    Context context;
    WebView browser;
    final SecurityManager securityManager = SecurityManager.getInstance();
    final FileManager fileManager = new FileManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        context = this.getApplicationContext();

        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new ChangePasswordActivity.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/ChangePassword.html");

    }

//
//    protected void androidUpdatePassword(String currentPassword, String passwordOne, String passwordTwo, String levelValue){
//        try {
//            if(passwordOne.equals(passwordTwo)) {
//                //Boolean passwordVaidate = validatePassword(passwordOneValue);
//                if (securityManager.authenticateUser(currentPassword, context, fileManager)) {
//                    byte[] passwordFileEncrypt = fileManager.readFromPasswordFile(context);
//                    String fileEncrypt = Base64.encodeToString(passwordFileEncrypt, Base64.DEFAULT);
//                    String passwordFileClean = securityManager.decrypt(passwordFileEncrypt);
//
//                    securityManager.generateNewKey(context, passwordOne, passwordFileClean);
//
//                    byte[] newPasswordFileEncrypt = securityManager.encrypt(passwordFileClean);
//                    String fileReEncrypt = Base64.encodeToString(newPasswordFileEncrypt, Base64.DEFAULT);
//                    byte[] testBytes = securityManager.encryptWithAlternatePassword(passwordOne, newPasswordFileEncrypt, false);
//                    String testString = Base64.encodeToString(testBytes, Base64.DEFAULT);
//
//                    fileManager.writeToPasswordFile(context, newPasswordFileEncrypt);
//
//                    int score = securityManager.calculatePasswordStrength(passwordOne);
//
//                    if (passwordOne.equals(passwordTwo) && score > 0) {
//                        int iterations;
//                        if (levelValue.equals("high")) {
//                            iterations = 250000;
//                        } else if (levelValue.equals("medium")) {
//                            iterations = 75000;
//                        } else {
//                            iterations = 10000;
//                        }
//
//
//
//                    String salt = securityManager.generateSalt();
//                    Log.d("help", "StartupActivity salt: " + salt);
//                    fileManager.saveHashInfo(context, "", Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), iterations);
//
//                    String saltFromFile = fileManager.getSalt(context);
//                    int hashingIteraions = fileManager.getHashingIterations(context);
//
//                    byte[] newHash = securityManager.hashPassword(passwordOne, saltFromFile.getBytes(), hashingIteraions);
//
//                    Log.d("help", "Startup ran");
//
//                    fileManager.writeToFirstRunFile(context);
//
//                    fileManager.saveHashInfo(context, Base64.encodeToString(newHash, Base64.DEFAULT), Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), hashingIteraions);
//
//
//
////                        String salt = securityManager.generateSalt();
////                        Log.d("help", "StartupActivity salt: " + salt);
////                        fileManager.saveHashInfo(context, "", Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), iterations);
////
////                        String saltFromFile = fileManager.getSalt(context);
////
////                        byte[] newHash = securityManager.hashPassword(passwordOne, saltFromFile.getBytes(), iterations);
////
////                        Log.d("help", "Startup ran");
////
////                        fileManager.writeToFirstRunFile(context);
////
////                        fileManager.saveHashInfo(context, Base64.encodeToString(newHash, Base64.DEFAULT), Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), iterations);
//                        Intent loginIntent = new Intent(context, LoginActivity.class);
//                        startActivity(loginIntent);
//                        finish();
//                    } else {
//                        if (passwordOne.equals(passwordTwo)) {
//                            CharSequence failedAuthenticationString = getString(R.string.passwordsDoNotMatch);
//
//                            Toast toast = Toast.makeText(context, failedAuthenticationString, Toast.LENGTH_LONG);
//                            toast.show();
//                        } else {
//                            CharSequence failedAuthenticationString = getString(R.string.passwordTooShort);
//
//                            Toast toast = Toast.makeText(context, failedAuthenticationString, Toast.LENGTH_LONG);
//                            toast.show();
//                        }
//                        browser.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                browser.loadUrl("javascript:clearFields()");
//                            }
//                        });
//
//
//                    }
//                } else {
//                    CharSequence failedAuthenticationString = getString(R.string.failed_login_toast);
//
//                    Toast toast = Toast.makeText(context, failedAuthenticationString, Toast.LENGTH_LONG);
//                    toast.show();
//                }
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }


    private void androidUpdatePassword(String currentPassword, String newPasswordOne, String newPasswordTwo, String levelValue){
        try{
            if(newPasswordOne.equals(newPasswordTwo)){
                SecurityManager securityManager = SecurityManager.getInstance();
                FileManager fileManager = new FileManager();
                if(securityManager.authenticateUser(currentPassword, context, fileManager)){
                    byte[] passwordFileEncrypt = fileManager.readFromPasswordFile(context);
                    byte[] passwordFileClean = securityManager.encryptWithAlternatePassword(currentPassword, passwordFileEncrypt, false);

                    String passwordInFile = Base64.encodeToString(passwordFileClean, Base64.DEFAULT);

                    byte[] newPasswordFileEncrypt = securityManager.encryptWithAlternatePassword(newPasswordOne, passwordFileClean, true);

                    String encryptedPasswordInFile = Base64.encodeToString(newPasswordFileEncrypt, Base64.DEFAULT);

                    fileManager.writeToPasswordFile(context, newPasswordFileEncrypt);


                    String salt = securityManager.generateSalt();
                    Log.d("help", "StartupActivity salt: " + salt);
                    fileManager.saveHashInfo(context, "", Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), 5000);

                    String saltFromFile = fileManager.getSalt(context);
                    int hashingIteraions = fileManager.getHashingIterations(context);

                    byte[] newHash = securityManager.hashPassword(newPasswordOne, saltFromFile.getBytes(), hashingIteraions);

                    Log.d("help", "Startup ran");

                    fileManager.writeToFirstRunFile(context);

                    fileManager.saveHashInfo(context, Base64.encodeToString(newHash, Base64.DEFAULT), Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), 5000);



//                    String saltFromFileOld = fileManager.getSalt(context);
//
//                    fileManager.saveHashInfo(context, "", Base64.encodeToString(saltFromFileOld.getBytes(), Base64.DEFAULT), 5000);
//
//                    String saltFromFile = fileManager.getSalt(context);
//
//                    byte[] newHash = securityManager.hashPassword(newPasswordOne, saltFromFile.getBytes());
//                    String newHashString = Base64.encodeToString(newHash, Base64.DEFAULT);
//                    fileManager.saveHashInfo(context, newHashString, Base64.encodeToString(saltFromFile.getBytes(), Base64.DEFAULT), 5000);

                    Intent loginActivity = new Intent(context, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }
            }else{
                CharSequence failedAuthenticationString = getString(R.string.failed_login_toast);

                Toast toast = Toast.makeText(context, failedAuthenticationString, Toast.LENGTH_LONG);
                toast.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        browser.post(new Runnable() {
            @Override
            public void run() {
                browser.loadUrl("javascript:clearFields()");
            }
        });

    }

    private int androidCheckPasswordStrength(String password){
        return SecurityManager.getInstance().calculatePasswordStrength(password);
    }

    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }

        //Android.changePassword(currentPassword, passwordOne, passwordTwo, dropDownValue);
        @JavascriptInterface
        public void changePassword(String currentPassword, String newPasswordOne, String newPasswordTwo, String levelValue){
            androidUpdatePassword(currentPassword, newPasswordOne, newPasswordTwo, levelValue);
        }

        @JavascriptInterface
        public int checkPasswordStrength(String password){
            return androidCheckPasswordStrength(password);
        }
    }
}
