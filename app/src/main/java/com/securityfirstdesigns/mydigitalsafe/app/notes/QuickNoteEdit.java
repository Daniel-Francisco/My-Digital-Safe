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
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;

import com.securityfirstdesigns.mydigitalsafe.app.R;
import com.securityfirstdesigns.mydigitalsafe.app.model.DatabaseManager;
import com.securityfirstdesigns.mydigitalsafe.app.model.QuickNoteFile;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class QuickNoteEdit extends AppCompatActivity {
    Context applicationContext;
    DatabaseManager databaseManager;
    private AdView mAdView;

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
