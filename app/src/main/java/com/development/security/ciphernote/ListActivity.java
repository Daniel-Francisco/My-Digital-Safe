package com.development.security.ciphernote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.File;
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

    File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        applicationContext = this.getBaseContext();

        list = new ArrayList<>();

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

    }

    @Override
    public void onPause(){
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
    public void onRestart(){
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
        if(diffSeconds < 30){
            responseString = "Last viewed a few moments ago.";
        }else if (diffSeconds < 60) {
            responseString = (diffSeconds + " seconds since last viewed.");
        } else if (diffMinutes < 60) {
            responseString = (diffMinutes + " minutes since last viewed.");
        } else if (diffMinutes < 1440) {
            //less than a day
            long hours = diffMinutes / 60;
            responseString = (hours + " hours since last viewed.");
        } else {
            long days = (diffMinutes / 1440);
            responseString = (days + " days since last viewed.");
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

    protected void androidAddNote(String name) {
        try {
            File newFile = new File();
            Date date = new Date();
            newFile.setFileName(name);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            newFile.setAccessDate(dateFormat.format(date));
            newFile.setData("");
            DatabaseManager databaseManager = new DatabaseManager(applicationContext);


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