package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class ListActivity extends android.app.ListActivity {
    ArrayList<DataStructures.FileManagmentObject> list;
    Context applicationContext;
    FileManager fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        applicationContext = this.getBaseContext();


        try{

            fileManager = new FileManager();
            list = fileManager.readFileManagmentData(SecurityManager.getInstance(), applicationContext);

            listViewRefresh();


            Button newNoteButton = (Button) findViewById(R.id.createNewNoteButton);
            final EditText noteNameEditText = (EditText) findViewById(R.id.newNoteName);
            newNoteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    try {
                        String name = noteNameEditText.getText().toString();

                        if(findIndex(name) == -1){
                            DataStructures.FileManagmentObject fileManagmentObject = new DataStructures.FileManagmentObject();
                            Date date = new Date();
                            fileManagmentObject.userDefinedFileName = name;
                            fileManagmentObject.lastAccessed = date;
                            list.add(fileManagmentObject);
                            fileManager.writeFileManagmentData(SecurityManager.getInstance(), applicationContext, list);
                            listViewRefresh();

                            noteNameEditText.setText("");


                            try{
                                Intent landingIntent = new Intent(applicationContext, EditNoteActivity.class);

                                String realFilename = SecurityManager.getInstance().SHA256Hash(name);

                                landingIntent.putExtra("fileName", name);
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
            entities[i] = list.get(i).userDefinedFileName;
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
                DataStructures.FileManagmentObject object = list.get(index);
                list.remove(index);
                list.add(object);

                fileManager.writeFileManagmentData(SecurityManager.getInstance(), applicationContext, list);
                listViewRefresh();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try{
            Intent landingIntent = new Intent(applicationContext, EditNoteActivity.class);

            String realFilename = SecurityManager.getInstance().SHA256Hash(itemValue);

            landingIntent.putExtra("fileName", itemValue);
            startActivity(landingIntent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private int findIndex(String filename){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).userDefinedFileName.equals(filename)){
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
