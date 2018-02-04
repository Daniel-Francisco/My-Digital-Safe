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


    private void androidChangePassword(String currentPassword, String newPasswordOne, String newPasswordTwo){
        try{
            if(newPasswordOne.equals(newPasswordTwo)){
                SecurityManager securityManager = SecurityManager.getInstance();
                FileManager fileManager = new FileManager();
                if(securityManager.authenticateUser(currentPassword, context, fileManager)){
                    byte[] passwordFileEncrypt = fileManager.readFromPasswordFile(context);
                    byte[] passwordFileClean = securityManager.encryptWithAlternatePassword(currentPassword, passwordFileEncrypt, false);
                    byte[] newPasswordFileEncrypt = securityManager.encryptWithAlternatePassword(newPasswordOne, passwordFileClean, true);
                    fileManager.writeToPasswordFile(context, newPasswordFileEncrypt);


                    String salt = securityManager.generateSalt();
                    Log.d("help", "StartupActivity salt: " + salt);
                    fileManager.saveHashInfo(context, "", Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), 5000);

                    String saltFromFile = fileManager.getSalt(context);

                    byte[] newHash = securityManager.hashPassword(newPasswordOne, saltFromFile.getBytes());

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

    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void changePassword(String currentPassword, String newPasswordOne, String newPasswordTwo){
            androidChangePassword(currentPassword, newPasswordOne, newPasswordTwo);
        }
    }
}
