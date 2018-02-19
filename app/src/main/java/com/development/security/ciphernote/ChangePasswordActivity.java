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

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.UserConfiguration;

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


    private void androidUpdatePassword(String currentPassword, String newPasswordOne, String newPasswordTwo, String levelValue){
        try{
            if(newPasswordOne.equals(newPasswordTwo)){
                SecurityManager securityManager = SecurityManager.getInstance();
                FileManager fileManager = new FileManager();
                if(securityManager.authenticateUser(currentPassword, context, fileManager)){

                    DatabaseManager databaseManager = new DatabaseManager(context);
                    UserConfiguration config = databaseManager.getUserConfiguration(1);


                    byte[] passwordBytes = config.getDevicePassword();
                    if(passwordBytes == null){
                        passwordBytes = new byte[0];
                    }
                    String dataString = Base64.encodeToString(passwordBytes, Base64.DEFAULT);

                    byte[] passwordValue = securityManager.encryptWithAlternatePassword(currentPassword, passwordBytes, config.getSalt().getBytes(), config.getIterations(), false);

                    byte[] encryptedNewPassword = securityManager.encryptWithAlternatePassword(newPasswordOne, passwordValue, config.getSalt().getBytes(), config.getIterations(), true);
//                    byte[] encryptedNewPassword = securityManager.encrypt(passwordValue);


                    config.setDevicePassword(encryptedNewPassword);
                    databaseManager.addUserConfiguration(config);


                    byte[] newHash = securityManager.hashPassword(newPasswordOne, config.getSalt().getBytes(), config.getIterations());

//                    fileManager.saveHashInfo(context, Base64.encodeToString(newHash, Base64.DEFAULT), config.getSalt(), config.getIterations());


//                    byte[] encryptedNewPassword = securityManager.encrypt(passwordValue);
//                    config.setDevicePassword(encryptedNewPassword);
//                    databaseManager.addUserConfiguration(config);


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
