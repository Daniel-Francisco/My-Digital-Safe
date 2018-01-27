package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class LandingActivity extends AppCompatActivity {
    Context applicationContext;
    SharedPreferences prefs = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        final EditText userInput = (EditText) findViewById(R.id.userInput);
        final TextView cipherView = (TextView) findViewById(R.id.cipher);
        final TextView textView = (TextView) findViewById(R.id.plain);
        Button encryptButton = (Button) findViewById(R.id.encrypt);

        try{
            applicationContext = this.getBaseContext();

            final SecurityManager securityManager = SecurityManager.getInstance();

            String sampleData = "Hello World!";

            byte[] cipher = securityManager.encrypt(sampleData);
            Log.d("help", "Cipher: " + cipher);
            String unEncrypted = securityManager.decrypt(cipher);

            Log.d("help", "Un-encrypted version: " + unEncrypted);


//            FileManager fileManager = new FileManager();
//
//            JSONObject fileJson = fileManager.readDataFile(applicationContext, "tempFile");
//            String fileData = fileJson.getString("data");
//
//            String decrypted = securityManager.decrypt(fileData.getBytes());
//            textView.setText(decrypted);


            encryptButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    try {
                        String plain = userInput.getText().toString();
                        byte[] userCipher = securityManager.encrypt(plain);
                        cipherView.setText(Base64.encodeToString(userCipher, Base64.DEFAULT));


                        FileManager fileManager = new FileManager();
                        fileManager.writeDataFile("tempFile2",userCipher);

                        byte[] fileJson = fileManager.readDataFile("tempFile2");
//                        String fileData = fileJson.getString("data");


//                        byte[] fromFile = fileData.getBytes();
                        String decrypted = securityManager.decrypt(fileJson);
                        textView.setText(decrypted);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
