package com.development.security.ciphernote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.development.security.ciphernote.DataStructures;
import com.development.security.ciphernote.FileManager;
import com.development.security.ciphernote.R;
import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.File;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//android.app.ListActivity
public class ListActivity extends Activity {
    ArrayList<File> list;
    Context applicationContext;
    FileManager fileManager;
    WebView browser;

    File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        applicationContext = this.getBaseContext();

        list = new ArrayList<>();
        fileManager = new FileManager();

        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new ListActivity.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/ListPage.html");

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        browser.post(new Runnable() {
            @Override
            public void run() {
                browser.loadUrl("javascript:renderList()");
            }
        });
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
            list = fileManager.readFileManagmentData(SecurityManager.getInstance(), applicationContext);
            Gson gson = new Gson();
            return gson.toJson(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    protected void androidListClickOccurred(String name) {
        int index = findIndex(name);
        if (index != -1) {
            try {
                selectedFile = list.get(index);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Intent landingIntent = new Intent(applicationContext, EditNoteActivity.class);

            landingIntent.putExtra("fileName", name);

            Gson gson = new Gson();
            String json = gson.toJson(selectedFile);

            landingIntent.putExtra("fileObject", json);

            startActivity(landingIntent);
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

                startActivity(landingIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        public void listClickOccurred(String value) {
            androidListClickOccurred(value);
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
    }

}