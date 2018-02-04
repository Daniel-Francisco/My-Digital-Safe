package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

public class StartupActivity extends AppCompatActivity {
    Context applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        applicationContext = getApplicationContext();

        final SecurityManager securityManager = SecurityManager.getInstance();
        final FileManager fileManager = new FileManager();

        final EditText passwordOne = (EditText) findViewById(R.id.passwordOne);
        final EditText passwordTwo = (EditText) findViewById(R.id.passwordTwo);
        Button createPasswordButton = (Button) findViewById(R.id.creatPassword);

        createPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String passwordOneValue = passwordOne.getText().toString();
                    String passwordTwoValue = passwordTwo.getText().toString();

                    //Boolean passwordVaidate = validatePassword(passwordOneValue);
                    if(passwordOneValue.equals(passwordTwoValue)){
                        String salt = securityManager.generateSalt();
                        Log.d("help", "StartupActivity salt: " + salt);
                        fileManager.saveHashInfo(applicationContext, "", Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), 5000);

                        String saltFromFile = fileManager.getSalt(applicationContext);

                        byte[] newHash = securityManager.hashPassword(passwordOneValue, saltFromFile.getBytes());

                        Log.d("help", "Startup ran");

                        fileManager.writeToFirstRunFile(applicationContext);

                        fileManager.saveHashInfo(applicationContext, Base64.encodeToString(newHash, Base64.DEFAULT), Base64.encodeToString(salt.getBytes(), Base64.DEFAULT), 5000);
                        Intent loginIntent = new Intent(applicationContext, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }else{
                        passwordOne.setText("");
                        passwordTwo.setText("");

                        CharSequence failedAuthenticationString = getString(R.string.passwordsDoNotMatch);

                        Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                        toast.show();
                    }


                }catch (Exception e){
                        e.printStackTrace();
                }
            }
        });


    }

    protected void androidCreatePassword(String password){
        Log.d("help", "Android create password called!");
    }
    protected Boolean androidValidatePassword(){
        return true;
    }

    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public void createPassword(String password) {
            androidValidatePassword();
        }

        @JavascriptInterface
        public Boolean validatePassword(String value){
            return androidValidatePassword();
        }
    }
}
