package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class EditNoteActivity extends AppCompatActivity {
    Context applicationContext;
    EditText userInput = null;
    FileManager fileManager;
    SecurityManager securityManager;
    String fileName;

    String noteValue = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");

        setContentView(R.layout.activity_landing);

        fileManager = new FileManager();
        securityManager = SecurityManager.getInstance();
        applicationContext = this.getBaseContext();



        WebView browser;
        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/EditNotePage.html");


    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            String plain = noteValue;
            byte[] userCipher = securityManager.encrypt(plain);

            Log.d("help", "Cipher: " + Base64.encodeToString(userCipher, Base64.DEFAULT));

            fileManager.writeDataFile(applicationContext, fileName, userCipher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchFileString(){
        try {
            byte[] fileJson = fileManager.readDataFile(applicationContext, fileName);
            noteValue = securityManager.decrypt(fileJson);
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

            if(fileManager.deleteFile(applicationContext, fileName, false)){
                CharSequence failedAuthenticationString = getString(R.string.deleteFileSuccess);
                Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                toast.show();


                ArrayList<DataStructures.FileManagmentObject> list = fileManager.readFileManagmentData(SecurityManager.getInstance(), applicationContext);

                int index = findIndex(list, fileName);
                list.remove(index);

                fileManager.writeFileManagmentData(SecurityManager.getInstance(), applicationContext, list);

                Intent landingIntent = new Intent(applicationContext, ListActivity.class);
                startActivity(landingIntent);
                finish();

            }else{
                CharSequence failedAuthenticationString = getString(R.string.deleteFileFailed);
                Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                toast.show();
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
        public String fetchContents() {
            return fetchFileString();
        }

        @JavascriptInterface
        public void updateNoteValue(String value){
            noteValue = value;
        }

        @JavascriptInterface
        public void deleteNote(){
            androidDelete();
        }
    }

    private int findIndex(ArrayList<DataStructures.FileManagmentObject> list, String filename){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).userDefinedFileName.equals(filename)){
                return i;
            }
        }
        return -1;
    }


}
