package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends android.app.ListActivity {
    ArrayList<DataStructures.FileManagmentObject> list;
    Context applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        applicationContext = this.getBaseContext();

        try{

            final FileManager fileManager = new FileManager();
            list = fileManager.readFileManagmentData(applicationContext);

            listViewRefresh();


            Button newNoteButton = (Button) findViewById(R.id.createNewNoteButton);
            final EditText noteNameEditText = (EditText) findViewById(R.id.newNoteName);
            newNoteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    try {
                        String name = noteNameEditText.getText().toString();
                        DataStructures.FileManagmentObject fileManagmentObject = new DataStructures.FileManagmentObject();
                        fileManagmentObject.userDefinedFileName = name;
                        list.add(fileManagmentObject);
                        fileManager.writeFileManagmentData(applicationContext, list);
                        listViewRefresh();

                        noteNameEditText.setText("");


                        Intent landingIntent = new Intent(applicationContext, LandingActivity.class);
                        landingIntent.putExtra("fileName", name);
                        startActivity(landingIntent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

        Intent landingIntent = new Intent(applicationContext, LandingActivity.class);
        landingIntent.putExtra("fileName", itemValue);
        startActivity(landingIntent);
    }
}
