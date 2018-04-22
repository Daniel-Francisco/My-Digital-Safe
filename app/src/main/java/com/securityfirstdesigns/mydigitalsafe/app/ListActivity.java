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

package com.securityfirstdesigns.mydigitalsafe.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.securityfirstdesigns.mydigitalsafe.app.model.DatabaseManager;
import com.securityfirstdesigns.mydigitalsafe.app.model.File;
import com.securityfirstdesigns.mydigitalsafe.app.model.QuickNoteFile;
import com.securityfirstdesigns.mydigitalsafe.app.security.SecurityManager;
import com.securityfirstdesigns.mydigitalsafe.app.settings.ChangePasswordActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ListActivity extends MenuActivity {
    ArrayList<File> list;
    WebView browser;
    private AdView mAdView;

    File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        applicationContext = this.getBaseContext();

        list = new ArrayList<>();

        checkForQuickNotes();


        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new ListActivity.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/ListPage.html");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidAddNote("My Secure Note");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



//        SharedPreferences sp = getSharedPreferences("digital_safe", Activity.MODE_PRIVATE);
//        boolean firstNoteFlag = sp.getBoolean("first_note_add_flag", true);
//        if(firstNoteFlag){
//            try {
//                DatabaseManager databaseManager = new DatabaseManager(applicationContext);
//                File firstNote = new File();
//                firstNote.setData("First note what we recommend you do note. Test new line");
//                firstNote.setFileName("Welcome to My Digital Safe!");
//                Date date = new Date();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                String dateString = sdf.format(date);
//                firstNote.setAccessDate(dateString);
//                databaseManager.addFile(firstNote);
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putBoolean("first_note_add_flag", false);
//            editor.commit();
//        }

        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        setTitle("Home");
    }

    private void checkForQuickNotes() {
        try {
            DatabaseManager databaseManager = new DatabaseManager(applicationContext);
            ArrayList<QuickNoteFile> quickNoteFiles = databaseManager.getAllQuickNoteFiles();
            for (int i = 0; i < quickNoteFiles.size(); i++) {
//                File file = new File();
                File newFile = createEmptyFile(quickNoteFiles.get(i).getQuickNoteFileName());

                long id = databaseManager.addFile(newFile);
                newFile.setID(id);
                list.add(newFile);


                try {

                    if(newFile.getFileName().equals("") || newFile.getFileName() == null){
                        newFile.setFileName("My Quick Note");
                    }
                    if(newFile.getData().equals("") || newFile.getData().equals(null)){
                        newFile.setData(" ");
                    }

                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    String dateString = sdf.format(date);
                    newFile.setAccessDate(dateString);
                    newFile.setData(quickNoteFiles.get(i).getQuickNoteData());
                    long idAfter = databaseManager.updateFile(newFile);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                databaseManager.deleteQuickNoteFile(quickNoteFiles.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        browser.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        browser.setVisibility(View.VISIBLE);
        browser.post(new Runnable() {
            @Override
            public void run() {
                browser.loadUrl("javascript:renderList()");
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();

        Intent mainActivityIntent = new Intent(applicationContext, MainActivity.class);
        startActivity(mainActivityIntent);

        finish();
    }

    private int findIndex(String filename) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFileName().equals(filename)) {
                return i;
            }
        }
        return -1;
    }

    protected String androidGetData() {
        try {
            DatabaseManager databaseManager = new DatabaseManager(applicationContext);
            list = databaseManager.getAllFiles();

            sortList();

            for (int i = 0; i < list.size(); i++) {
                list.get(i).setAccessDate(beautifyDate(list.get(i).getAccessDate()));
            }

            Gson gson = new Gson();
            return gson.toJson(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }


    private void sortList() {
        Collections.sort(list, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date dateOne = new Date();
                Date dateTwo = new Date();
                try {
                    dateOne = sdf.parse(o1.getAccessDate());
                    dateTwo = sdf.parse(o2.getAccessDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return dateTwo.compareTo(dateOne);
            }
        });
    }


    private String beautifyDate(String date) throws ParseException {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Date then = sdf.parse(date);
        int diff = now.getDate() - then.getDate();


        long diffSeconds = getDateDiff(then, now, TimeUnit.SECONDS);//diff / 1000 % 60;
        long diffMinutes = getDateDiff(then, now, TimeUnit.MINUTES);//diff / (60 * 1000) % 60;

        String responseString = "";
        if (diffSeconds < 30) {
            responseString = "Last opened a few moments ago.";
        } else if (diffSeconds < 60) {
            responseString = ("Opened " + diffSeconds + " seconds ago.");
        } else if (diffMinutes < 60) {
            responseString = ("Opened " + diffMinutes + " minutes ago.");
        } else if (diffMinutes < 1440) {
            //less than a day
            long hours = diffMinutes / 60;
            responseString = "Opened " + hours + " hours ago.";
        } else {
            long days = (diffMinutes / 1440);
            responseString = "Opened " + days + " days ago.";
        }
        return responseString;
    }


    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    protected void androidListClickOccurred(File file) {
        selectedFile = file;

        try {
            Intent landingIntent = new Intent(applicationContext, EditNoteActivity.class);
            landingIntent.putExtra("newNoteFlag", false);

//            landingIntent.putExtra("fileName", name);

            Gson gson = new Gson();
            String json = gson.toJson(selectedFile);

            landingIntent.putExtra("fileObject", json);

            startActivity(landingIntent);

//            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void androidChangePassword() {
        Intent landingIntent = new Intent(applicationContext, ChangePasswordActivity.class);
        startActivity(landingIntent);
    }

    private File createEmptyFile(String name){
        File newFile = new File();
        Date date = new Date();
        newFile.setFileName(name);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        newFile.setAccessDate(dateFormat.format(date));
        newFile.setData(" ");
        return newFile;
    }

    protected void androidAddNote(String name) {
        try {

            DatabaseManager databaseManager = new DatabaseManager(applicationContext);

            File newFile = createEmptyFile(name);

            long id = databaseManager.addFile(newFile);
            newFile.setID(id);
            list.add(newFile);

            try {
                Intent landingIntent = new Intent(applicationContext, EditNoteActivity.class);

                String realFilename = SecurityManager.getInstance().SHA256Hash(name);

                landingIntent.putExtra("fileName", name);

                Gson gson = new Gson();
                String json = gson.toJson(newFile);

                landingIntent.putExtra("fileObject", json);
                landingIntent.putExtra("newNoteFlag", true);

                startActivity(landingIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void androidDelete(File file) {
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

            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:renderList()");
                }
            });

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
        public String getData() {
            return androidGetData();
        }

        @JavascriptInterface
        public void listClickOccurred(String fileString) {
            Gson gson = new Gson();
            Type type = new TypeToken<File>() {
            }.getType();
            File file = gson.fromJson(fileString, type);
            androidListClickOccurred(file);
        }

        @JavascriptInterface
        public void addNote(String noteName) {
            androidAddNote(noteName);
        }

        @JavascriptInterface
        public void androidChangePassword() {
            androidChangePassword();
        }

        @JavascriptInterface
        public String androidDataModel() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", File.KEY_ID);
                jsonObject.put("name", File.KEY_FILE_NAME);
                jsonObject.put("date", File.KEY_ACCESS_DATE);
                return jsonObject.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @JavascriptInterface
        public void deleteNote(String fileString) {
            Gson gson = new Gson();
            Type type = new TypeToken<File>() {
            }.getType();
            File file = gson.fromJson(fileString, type);
            androidDelete(file);
        }
    }

}