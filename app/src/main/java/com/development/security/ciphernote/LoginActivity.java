package com.development.security.ciphernote;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;
import android.webkit.JavascriptInterface;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.File;
import com.development.security.ciphernote.model.UserConfiguration;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class LoginActivity extends Activity {
    Context applicationContext;
    SharedPreferences prefs = null;


    final SecurityManager securityManager = SecurityManager.getInstance();
    DataStructures dataStructures = new DataStructures();
    final FileManager fileManager = new FileManager();
    WebView browser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Log.d("help", "EditNoteActivity ran");
        prefs = getSharedPreferences("com.security.test", MODE_PRIVATE);
        applicationContext = getApplicationContext();


        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/loginPage.html");

//        DatabaseManager databaseManager = new DatabaseManager(this.getApplicationContext());
//        File file = new File();
//        file.setAccessDate("");
//        byte[] dataBytes = "data".getBytes();
//        String dataString = Base64.encodeToString(dataBytes, Base64.DEFAULT);
//        String dataString2 = new String(dataBytes);
//        file.setData("data".getBytes());
//        file.setFileName("file");
//
//        File file2 = new File();
//        file2.setAccessDate("");
//        byte[] dataBytes2 = "data2".getBytes();
//        String dataString3 = Base64.encodeToString(dataBytes, Base64.DEFAULT);
//        String dataString4 = new String(dataBytes);
//        file2.setData("data".getBytes());
//        file2.setFileName("file");
//
//        long id = databaseManager.addFile(file);
//        file.setID(id);
//        long id2 = databaseManager.addFile(file2);
//        file.setID(id2);
//
//        ArrayList<File> list1 = databaseManager.getAllFiles();
//
//        databaseManager.deleteFile(file);
//
//        ArrayList<File> list2 = databaseManager.getAllFiles();

        Log.d("help", "thing");

//        DatabaseHandler db = new DatabaseHandler(this);
//
//        /**
//         * CRUD Operations
//         * */
//        // Inserting Contacts
//        Log.d("Insert: ", "Inserting ..");
//        db.addContact(new Contact("Ravi", "9100000000"));
//        db.addContact(new Contact("Srinivas", "9199999999"));
//        db.addContact(new Contact("Tommy", "9522222222"));
//        db.addContact(new Contact("Karthik", "9533333333"));
//
//        // Reading all contacts
//        Log.d("Reading: ", "Reading all contacts..");
//        List<Contact> contacts = db.getAllContacts();
//
//        for (Contact cn : contacts) {
//            String log = "Id: " + cn.getID() + " ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
//            // Writing Contacts to log
//            Log.d("Name: ", log);
//
//        }


//        DatabaseManager databaseManager = new DatabaseManager(this);
//
//        databaseManager.addUserConfiguration(new UserConfiguration(10, "password_hash", "salt"));
//
////        UserConfiguration readValue = databaseManager.getUserConfiguration(0);
//        List<UserConfiguration> list = databaseManager.getAllUserConfigurations();
//
//
//        databaseManager.addFile(new File("name", "", "data"));
//
////        File file = databaseManager.getFileById(0);
//        List<File> fileList = databaseManager.getAllFiles();

        Log.d("help", "test");
    }

    public void androidAuthenticateUser(String password){
       //Call ASYNC task

        browser.post(new Runnable() {
            @Override
            public void run() {
                browser.loadUrl("javascript:spinnerToggle(true)");
            }
        });
        new AsyncLogin().execute(password);
    }


    private class AsyncLogin extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                Boolean authentication = null;
                String password = strings[0];

                authentication = securityManager.authenticateUser(password, applicationContext, fileManager);

                if(authentication){
                    Log.d("help", "Successful authentication!");
//                    securityManager.generateKey(applicationContext, password);
                    Intent landingIntent = new Intent(applicationContext, ListActivity.class);
                    startActivity(landingIntent);
                    finish();
                }else{
                    Log.d("help", "Failed authentication");
//                passwordField.setText("");
                    return false;
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            browser.post(new Runnable() {
                @Override
                public void run() {
                    browser.loadUrl("javascript:spinnerToggle(false)");
                }
            });

            if(!status){
                CharSequence failedAuthenticationString = getString(R.string.failed_login_toast);

                Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }


    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public void authenticateUser(String password) {
            androidAuthenticateUser(password);
        }
    }

}
