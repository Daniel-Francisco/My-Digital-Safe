package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
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
import android.widget.Toast;
import android.widget.Toolbar;

import com.development.security.ciphernote.model.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditNoteActivity extends AppCompatActivity {
    Context applicationContext;
    EditText userInput = null;
    FileManager fileManager;
    SecurityManager securityManager;
    String fileName;
    String hashedFilename = "";
    DatabaseManager databaseManager;

    String noteValue = "";

    com.development.security.ciphernote.model.File file;

    boolean changeOccured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        String jsonForFile = intent.getStringExtra("fileObject");

        Gson gson = new Gson();

        Type type = new TypeToken<com.development.security.ciphernote.model.File>() {}.getType();
        file = gson.fromJson(jsonForFile, type);

//        file = new com.development.security.ciphernote.model.File();

        fileManager = new FileManager();
        securityManager = SecurityManager.getInstance();
        applicationContext = this.getBaseContext();
        databaseManager = new DatabaseManager(applicationContext);


//        file = databaseManager.getFileByName(fileName);

        hashedFilename = fileName;

        Log.d("help", "Filename: " + fileName + " hash: " + hashedFilename);

        setTitle(fileName);

        setContentView(R.layout.activity_landing);

        WebView browser;
        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/EditNotePage.html");

        setTitle("Digital Safe");
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(changeOccured){
                if(file.getData().equals("")){
                    file.setData(" ");
                }

                long id = databaseManager.updateFile(file);
                Log.d("help", "Id: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchFileString(){
        try {
//            byte[] fileJson = fileManager.readDataFile(applicationContext, hashedFilename);
//            noteValue = securityManager.decrypt(fileJson);

            noteValue = file.getData();
            return noteValue;
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    private void androidDelete(){
        try {
            noteValue = "";
            byte[] deleteData = noteValue.getBytes();
            fileManager.writeDataFile(applicationContext, fileName, deleteData);

            file.setFileName("redacted");
            file.setAccessDate("redacted");
            file.setData("redacted");
            databaseManager.updateFile(file);

            databaseManager.deleteFile(file);

            CharSequence failedAuthenticationString = getString(R.string.deleteFileSuccess);
            Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
            toast.show();

            Intent landingIntent = new Intent(applicationContext, ListActivity.class);
            startActivity(landingIntent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();

            CharSequence failedAuthenticationString = getString(R.string.deleteFileFailed);
            Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void updateFile(String fileString){
            if(!changeOccured){
                changeOccured = true;
            }
            Gson gson = new Gson();
            Type type = new TypeToken<com.development.security.ciphernote.model.File>() {}.getType();
            file = gson.fromJson(fileString, type);
            Log.d("help", "time");
        }
        @JavascriptInterface
        public String getFile(){
            Gson gson = new Gson();
            String fileJson = gson.toJson(file);
            return fileJson;
        }
    }


}
