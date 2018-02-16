package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.File;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ListActivity extends android.app.ListActivity {
    ArrayList<File> list;
    Context applicationContext;
    FileManager fileManager;
    File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        applicationContext = this.getBaseContext();




        try{

            fileManager = new FileManager();
            try {
                list = fileManager.readFileManagmentData(SecurityManager.getInstance(), applicationContext);
                sortList();
            }catch(Exception e){
                e.printStackTrace();
                list = new ArrayList<>();
            }
            listViewRefresh();


            Button newNoteButton = (Button) findViewById(R.id.createNewNoteButton);
            final EditText noteNameEditText = (EditText) findViewById(R.id.newNoteName);
            newNoteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    try {
                        String name = noteNameEditText.getText().toString();
                        androidAddNote(name);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Button passwordResetButton = (Button) findViewById(R.id.passwordResetLink);
            passwordResetButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    androidChangePassword();
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String timeDiff(Date date) throws ParseException {
        String returnString = "";
        Date now = new Date();
        Date then = date;

        long diff = now.getTime() - then.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if(diffHours > 25){
            returnString = (diffDays + " days");
        }else if(diffMinutes > 60){
            returnString = (diffMinutes + " minutes");
        }else if(diffSeconds > 60){
            returnString = (diffMinutes + " minutes");
        }else{
            returnString = (diffSeconds + " seconds");
        }

        return returnString;
    }

    private void sortList(){
        Collections.sort(list, new Comparator<File>(){

            @Override
            public int compare(File o1, File o2) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                Date dateOne = new Date();
                Date dateTwo = new Date();
                dateOne = o1.getAccessDate();
                dateTwo = o2.getAccessDate();


                return dateTwo.compareTo(dateOne);
            }
        });
    }

    private void listViewRefresh(){
        String[] entities = new String[list.size()];

        try {
            for (int i = 0; i < list.size(); i++) {
                entities[i] = (list.get(i).getFileName() + "  11  " + timeDiff(list.get(i).getAccessDate()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, entities);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String  itemValue = (String) l.getItemAtPosition(position);
        String realValue = itemValue.split("  11  ")[0];
        androidListClickOccurred(realValue);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            try {
                list = fileManager.readFileManagmentData(SecurityManager.getInstance(), applicationContext);
                sortList();
            }catch(Exception e){
                e.printStackTrace();
                list = new ArrayList<>();
            }
            listViewRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int findIndex(String filename){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getFileName().equals(filename)){
                return i;
            }
        }
        return -1;
    }

    protected String androidGetData(){
        try{
            list = fileManager.readFileManagmentData(SecurityManager.getInstance(), applicationContext);
            sortList();
            Gson gson = new Gson();
            return gson.toJson(list);
        }catch (Exception e){
            e.printStackTrace();
        }

        return "error";
    }
    protected void androidListClickOccurred(String name){
        int index = findIndex(name);
        if(index != -1){
            try{
                selectedFile = list.get(index);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try{
            Intent landingIntent = new Intent(applicationContext, EditNoteActivity.class);

            landingIntent.putExtra("fileName", name);

            Gson gson = new Gson();
            String json = gson.toJson(selectedFile);

            landingIntent.putExtra("fileObject", json);

            startActivity(landingIntent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    protected void androidChangePassword(){
        Intent landingIntent = new Intent(applicationContext, ChangePasswordActivity.class);
        startActivity(landingIntent);
    }

    protected  void androidAddNote(String name){
        try {
            if(findIndex(name) == -1){
                File newFile = new File();
                newFile.setFileName(name);
                newFile.setAccessDate(new Date());
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

            } else {

                CharSequence failedAuthenticationString = getString(R.string.noteAlreadyExists);

                Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                toast.show();

            }
        }catch(Exception e){
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
        public void listClickOccurred(String value){
            androidListClickOccurred(value);
        }

        @JavascriptInterface
        public void addNote(String noteName){
            androidAddNote(noteName);
        }

        @JavascriptInterface
        public void androidChangePassword(){
            androidChangePassword();
        }

        @JavascriptInterface
        public String androidDataModel(){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", File.KEY_ID);
                jsonObject.put("name", File.KEY_FILE_NAME);
                jsonObject.put("date", File.KEY_ACCESS_DATE);
                return jsonObject.toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            return "";
        }
    }

}
