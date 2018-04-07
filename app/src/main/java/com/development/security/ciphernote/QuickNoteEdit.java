package com.development.security.ciphernote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.File;
import com.development.security.ciphernote.model.QuickNoteFile;
import com.development.security.ciphernote.security.SecurityManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class QuickNoteEdit extends AppCompatActivity {
    Context applicationContext;
    DatabaseManager databaseManager;

    QuickNoteFile quickNoteFile = new QuickNoteFile();
    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_note_edit);

        quickNoteFile.setQuickNoteFileName("");
        quickNoteFile.setQuickNoteData("");

        applicationContext = this;
        databaseManager = new DatabaseManager(applicationContext);

        setContentView(R.layout.activity_landing);


        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new QuickNoteEdit.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/QuickNoteEditPage.html");


        ImageView saveButton = (ImageView) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DatabaseManager databaseManager = new DatabaseManager(applicationContext);
                    databaseManager.addQuickNoteFile(quickNoteFile);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finish();
            }
        });


        ImageView deleteButton = (ImageView) findViewById(R.id.deleteNoteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.app.AlertDialog.Builder(applicationContext)
                        .setTitle("Delete this note?")
                        .setMessage("Are you sure you want to throw away your quick note?")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        try{
            DatabaseManager databaseManager = new DatabaseManager(applicationContext);
            databaseManager.addQuickNoteFile(quickNoteFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        finish();
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void updateFile(String fileString) {
            Gson gson = new Gson();
            Type type = new TypeToken<QuickNoteFile>() {
            }.getType();
            quickNoteFile = gson.fromJson(fileString, type);
        }

        @JavascriptInterface
        public String getFile() {
            Gson gson = new Gson();
            String fileJson = gson.toJson(quickNoteFile);
            return fileJson;
        }
    }
}
