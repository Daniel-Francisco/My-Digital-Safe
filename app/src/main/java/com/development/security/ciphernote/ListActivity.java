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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

//        byte[] testBytes = "test".getBytes();
//        DatabaseManager databaseManagerTest = new DatabaseManager(applicationContext);
//        File fileWrite = new File("test23", "", testBytes);
//        databaseManagerTest.addFile(fileWrite);

//        File sampleRead = databaseManagerTest.getFileByName(fileWrite.getFileName());



        try{

            fileManager = new FileManager();
            try {
                list = fileManager.readFileManagmentData(SecurityManager.getInstance(), applicationContext);
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

                        if(findIndex(name) == -1){
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
                            listViewRefresh();

                            noteNameEditText.setText("");


                            try{
                                Intent landingIntent = new Intent(applicationContext, EditNoteActivity.class);

                                String realFilename = SecurityManager.getInstance().SHA256Hash(name);

                                landingIntent.putExtra("fileName", name);

                                Gson gson = new Gson();
                                String json = gson.toJson(newFile);

                                landingIntent.putExtra("fileObject", json);
                                startActivity(landingIntent);
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                        }else{

                            CharSequence failedAuthenticationString = getString(R.string.noteAlreadyExists);

                            Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
                            toast.show();

                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Button passwordResetButton = (Button) findViewById(R.id.passwordResetLink);
            passwordResetButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent landingIntent = new Intent(applicationContext, ChangePasswordActivity.class);
                    startActivity(landingIntent);
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void listViewRefresh(){
        String[] entities = new String[list.size()];

        for(int i = 0; i < list.size(); i++){
            entities[i] = list.get(i).getFileName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, entities);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String  itemValue = (String) l.getItemAtPosition(position);

        int index = findIndex(itemValue);
        if(index != -1){
            try{
                selectedFile = list.get(index);
//                list.remove(index);
//                list.add(object);
//
//                fileManager.writeFileManagmentData(SecurityManager.getInstance(), applicationContext, list, false);
                listViewRefresh();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try{
            Intent landingIntent = new Intent(applicationContext, EditNoteActivity.class);

            String realFilename = SecurityManager.getInstance().SHA256Hash(itemValue);

            landingIntent.putExtra("fileName", itemValue);

            Gson gson = new Gson();
            String json = gson.toJson(selectedFile);

            landingIntent.putExtra("fileObject", json);

            startActivity(landingIntent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            try {
                list = fileManager.readFileManagmentData(SecurityManager.getInstance(), applicationContext);
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
        return "data wooo";
    }
    protected void androidListClickOccurred(String value){
        Log.d("help", "List click on value: " + value);
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
    }

}
