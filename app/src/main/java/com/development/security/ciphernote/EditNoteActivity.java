package com.development.security.ciphernote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.content.DialogInterface;

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
    SecurityManager securityManager;
    String fileName;
    String hashedFilename = "";
    DatabaseManager databaseManager;

    String noteValue = "";

    com.development.security.ciphernote.model.File file;
    WebView browser;

    boolean newNoteFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        newNoteFlag = intent.getBooleanExtra("newNoteFlag", false);
        String jsonForFile = intent.getStringExtra("fileObject");

        Gson gson = new Gson();

        Type type = new TypeToken<com.development.security.ciphernote.model.File>() {
        }.getType();
        file = gson.fromJson(jsonForFile, type);
        securityManager = SecurityManager.getInstance();
        applicationContext = this;
        databaseManager = new DatabaseManager(applicationContext);
        hashedFilename = fileName;

        setContentView(R.layout.activity_landing);


        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/EditNotePage.html");


        android.support.v7.widget.Toolbar myChildToolbar =
                (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);


        setTitle("");


        ImageView saveButton = (ImageView) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(applicationContext, ListActivity.class);
                startActivity(listIntent);
                finish();
            }
        });


        ImageView deleteButton = (ImageView) findViewById(R.id.deleteNoteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new android.app.AlertDialog.Builder(applicationContext)
                    .setTitle("Delete this note?")
                    .setMessage("Are you sure you wish to delete this note? This is permanent.")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            androidDelete();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();

        browser.setVisibility(View.GONE);

        try {
            Date date = new Date();


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            if(file.getFileName().equals("") || file.getFileName() == null){
                file.setFileName("My Secure Note");
            }
            if(file.getData().equals("") || file.getData().equals(null)){
                file.setData(" ");
            }

            String dateString = sdf.format(date);
            file.setAccessDate(dateString);
            long id = databaseManager.updateFile(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        browser.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRestart(){
        super.onRestart();

        Intent mainActivityIntent = new Intent(applicationContext, MainActivity.class);
        startActivity(mainActivityIntent);

        finish();
    }

    @Override
    public void onBackPressed() {
        // your code.
        Intent listIntent = new Intent(applicationContext, ListActivity.class);
        startActivity(listIntent);
        finish();
    }

    private void androidDelete(){
        try {
            DatabaseManager databaseManager = new DatabaseManager(applicationContext);

            file.setFileName("redacted");
            file.setAccessDate("redacted");
            file.setData("redacted");
            databaseManager.updateFile(file);

            databaseManager.deleteFile(file);

            CharSequence failedAuthenticationString = getString(R.string.deleteFileSuccess);
            Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
            toast.show();

            Intent listIntent = new Intent(applicationContext, ListActivity.class);
            startActivity(listIntent);
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
        public void updateFile(String fileString) {
            Gson gson = new Gson();
            Type type = new TypeToken<com.development.security.ciphernote.model.File>() {
            }.getType();
            file = gson.fromJson(fileString, type);
        }

        @JavascriptInterface
        public String getFile() {
            Gson gson = new Gson();
            String fileJson = gson.toJson(file);
            return fileJson;
        }
    }


}
