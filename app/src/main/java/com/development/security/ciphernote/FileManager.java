package com.development.security.ciphernote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by danie on 1/18/2018.
 */

public class FileManager {
    private DataStructures.UserConfiguration userConfiguration = null;
    public Boolean checkForFirstRunFile(Context context) throws JSONException {
        byte[] data = readFromDataFile(context, "startup");
        if((new String(data)).equals("started")){
            return false;
        }
        return true;
    }
    public void writeToFirstRunFile(Context context){
        writeToDataFile(context, "started".getBytes(), "startup");
    }



    public void writeDataFile(Context context, String filename, byte[] data) throws JSONException {
//        JSONObject fileObject = new JSONObject();
//        fileObject.put("lastModified", "");
//        fileObject.put("data", data);
        writeToDataFile(context, data, filename);
    }
    public byte[] readDataFile(Context context, String filename) throws JSONException {
        byte[] jsonBytes = readFromDataFile(context, filename);
        return jsonBytes;
    }
    private boolean isJSONValid(String test){
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try{
                new JSONArray(test);
            }catch(Exception e){
                return false;
            }
        }
        return true;
    }

    public void writeUserConfig(Context context) throws JSONException {
        JSONObject fileObject = new JSONObject();
        fileObject.put("passwordHash", userConfiguration.getPasswordHash());
        fileObject.put("salt", userConfiguration.getSalt());
        fileObject.put("iterations", userConfiguration.getIterations());
        writeToFile(fileObject.toString(), "configurationFile", context);
    }
    private void readUserConfig(Context context) throws JSONException {
        String json = readFromFile("configurationFile", context);
        JSONObject object = null;
        if(json.equals("")){
            userConfiguration = new DataStructures.UserConfiguration();
            userConfiguration.setIterations(250000);
            userConfiguration.setPasswordHash("");
            userConfiguration.setSalt("");
        }else{
            object = new JSONObject(json);
            userConfiguration = new DataStructures.UserConfiguration();
            userConfiguration.setIterations(object.getInt("iterations"));
            userConfiguration.setPasswordHash(object.getString("passwordHash"));
            userConfiguration.setSalt(object.getString("salt"));
        }
    }
    private void checkUserConfiguration(Context context) throws JSONException {
        if(userConfiguration == null) {
            readUserConfig(context);
        }
    }

    public void saveHashInfo(Context context, String hash, String salt, int iterations) throws JSONException {
        checkUserConfiguration(context);
        userConfiguration.setPasswordHash(hash);
        userConfiguration.setSalt(salt);
        userConfiguration.setIterations(iterations);
        writeUserConfig(context);
    }

    public void updateHashInfo(Context context, String hash, String salt, int iterations) throws JSONException {
        checkUserConfiguration(context);
        userConfiguration.setPasswordHash(hash);
        userConfiguration.setSalt(salt);
        userConfiguration.setIterations(iterations);
    }

    public String readHash(Context context) throws JSONException {
        checkUserConfiguration(context);
        return userConfiguration.getPasswordHash();
    }

    public String getSalt(Context context) throws JSONException {
        checkUserConfiguration(context);
        return userConfiguration.getSalt();
    }


    private void writeToFile(String data, String filename, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput((filename + ".txt"), Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(String filename, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename + ".txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }


    private void writeToDataFile(Context context, byte[] data, String fileName) {
        try {
            if(data != null){
                FileOutputStream fos = new FileOutputStream(context.getFilesDir() + fileName + ".txt");//openFileOutput(fileName+".txt", Context.MODE_PRIVATE);
                fos.write(data);
                fos.close();
            }

        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private byte[] readFromDataFile(Context context, String fileName) {
        File file = new File(context.getFilesDir()+fileName+".txt");
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

    public ArrayList<DataStructures.FileManagmentObject> readFileManagmentData(Context context) throws JSONException {
        ArrayList<DataStructures.FileManagmentObject> filesArray = new ArrayList<>();
        String spData = readFromSP(context, "filesData");

        if(spData == null){
            spData = "";
        }

        if(isJSONValid(spData)){
            JSONArray jsonArray = new JSONArray(spData);
            for(int i = 0; i<jsonArray.length(); i++){
                DataStructures.FileManagmentObject fileManagmentObject = new DataStructures.FileManagmentObject();
                fileManagmentObject.userDefinedFileName = jsonArray.getJSONObject(i).getString("userDefinedFileName");
                filesArray.add(fileManagmentObject);
            }
        }
        return filesArray;
    }
    public void writeFileManagmentData(Context context, ArrayList<DataStructures.FileManagmentObject> files) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i<files.size(); i++){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userDefinedFileName", files.get(i).userDefinedFileName);
            jsonArray.put(jsonObject);
        }
        String jsonString = jsonArray.toString();
        Log.d("help", "JSON String: " + jsonString);
        writeToSP(context, "filesData", jsonString);
    }


    private void writeToSP(Context context, String label, String data){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(label, data);
        editor.apply();
    }
    private String readFromSP(Context context, String label){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String name = preferences.getString(label, null);
        return name;
    }

}
