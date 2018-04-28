/*
 * My Digital Safe, the secure notepad Android app.
 * Copyright (C) 2018 Security First Designs
 *
 * My Digital Safe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <a href="www.gnu.org/licenses/">here</a>.
 */

package com.securityfirstdesigns.mydigitalsafe.app.notes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.DialogInterface;

import com.securityfirstdesigns.mydigitalsafe.app.core.HomeActivity;
import com.securityfirstdesigns.mydigitalsafe.app.core.MainActivity;
import com.securityfirstdesigns.mydigitalsafe.app.R;
import com.securityfirstdesigns.mydigitalsafe.app.model.DatabaseManager;
import com.securityfirstdesigns.mydigitalsafe.app.security.SecurityService;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {
    Context applicationContext;
    EditText userInput = null;
    SecurityService securityService;
    String fileName;
    String hashedFilename = "";
    DatabaseManager databaseManager;
    private AdView mAdView;

    com.securityfirstdesigns.mydigitalsafe.app.model.File file;
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

        Type type = new TypeToken<com.securityfirstdesigns.mydigitalsafe.app.model.File>() {
        }.getType();
        file = gson.fromJson(jsonForFile, type);


        if (savedInstanceState != null){
            if(savedInstanceState.getString("dataBeforeRotation") != null){
                file.setData(savedInstanceState.getString("dataBeforeRotation"));
            }
            if(savedInstanceState.getString("titleBeforeRotation") != null){
                file.setFileName(savedInstanceState.getString("titleBeforeRotation"));
            }
        }


        securityService = SecurityService.getInstance();
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
                Intent listIntent = new Intent(applicationContext, HomeActivity.class);
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
                    .setMessage("Are you sure you want to delete this note? You cannot get it back.")
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

            if(file.getFileName().equals("") || file.getFileName() == null){
                file.setFileName("My Secure Note");
            }
            if(file.getData().equals("") || file.getData().equals(null)){
                file.setData(" ");
            }

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String dateString = sdf.format(date);
            file.setAccessDate(dateString);
            long id = databaseManager.updateFile(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle onOrientChange){
        super.onSaveInstanceState(onOrientChange);
        onOrientChange.putString("dataBeforeRotation", file.getData());
        onOrientChange.putString("titleBeforeRotation", file.getFileName());
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
        Intent listIntent = new Intent(applicationContext, HomeActivity.class);
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

            Intent listIntent = new Intent(applicationContext, HomeActivity.class);
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
            Type type = new TypeToken<com.securityfirstdesigns.mydigitalsafe.app.model.File>() {
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
