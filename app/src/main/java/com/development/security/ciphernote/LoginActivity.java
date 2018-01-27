package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;

import java.util.EventListener;

public class LoginActivity extends AppCompatActivity {
    Context applicationContext;
    SharedPreferences prefs = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d("help", "LandingActivity ran");

        prefs = getSharedPreferences("com.security.test", MODE_PRIVATE);

        applicationContext = getApplicationContext();

        final SecurityManager securityManager = SecurityManager.getInstance();
        DataStructures dataStructures = new DataStructures();
        final FileManager fileManager = new FileManager();


        try{
            String password = "password";


            final EditText passwordField =  (EditText) findViewById(R.id.password);
            Button loginButton = (Button) findViewById(R.id.button);

            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    try {
//                        String userPassword = passwordField.getText().toString();
                        String userPassword = "password";
                        Boolean authentication = null;

                            authentication = securityManager.authenticateUser(userPassword, applicationContext, fileManager);

                        if(authentication){
                            Log.d("help", "Successful authentication!");
                            securityManager.generateKey(userPassword);
                            Intent landingIntent = new Intent(applicationContext, LandingActivity.class);
                            startActivity(landingIntent);
                        }else{
                            Log.d("help", "Failed authentication");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
