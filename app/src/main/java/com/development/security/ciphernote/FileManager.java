package com.development.security.ciphernote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.UserConfiguration;

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
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by danie on 1/18/2018.
 */

public class FileManager {
    private DataStructures.UserConfiguration userConfiguration = null;
    public Boolean checkForFirstRunFile(Context context) throws JSONException, IOException {
        byte[] data = readFromDataFile(context, "startup", true);
        if((new String(data)).equals("started")){
            return false;
        }
        return true;
    }
    public void writeToFirstRunFile(Context context){
        checkConfigDirectory(context);
        writeToDataFile(context, "started".getBytes(), "startup", true);
    }



    public void writeDataFile(Context context, String filename, byte[] data) throws JSONException {
        writeToDataFile(context, data, filename, false);
    }
    public byte[] readDataFile(Context context, String filename) throws JSONException, IOException {
        byte[] jsonBytes = readFromDataFile(context, filename, false);
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
        DatabaseManager databaseManager = new DatabaseManager(context);
        databaseManager.addUserConfiguration(new UserConfiguration(userConfiguration.getIterations(), userConfiguration.getPasswordHash(), userConfiguration.getSalt()));
    }
    private void readUserConfig(Context context) throws JSONException {
        DatabaseManager databaseManager = new DatabaseManager(context);

        List<UserConfiguration> list = databaseManager.getAllUserConfigurations();

        if(userConfiguration == null){
            userConfiguration = new DataStructures.UserConfiguration();
        }
        if(list.size() == 0){
            userConfiguration.setIterations(250000);
            userConfiguration.setSalt("");
            userConfiguration.setPasswordHash("");
        }else{
            UserConfiguration userConfig = databaseManager.getUserConfiguration(list.get(1).getID());

            userConfiguration.setIterations(userConfig.getIterations());
            userConfiguration.setPasswordHash(userConfig.getPassword_hash());
            userConfiguration.setSalt(userConfig.getSalt());
        }



//        String json = readFromFile("configurationFile", context);
//        JSONObject object = null;
//        if(json.equals("")){
//            userConfiguration = new DataStructures.UserConfiguration();
//            userConfiguration.setIterations(250000);
//            userConfiguration.setPasswordHash("");
//            userConfiguration.setSalt("");
//        }else{
//            object = new JSONObject(json);
//            userConfiguration = new DataStructures.UserConfiguration();
//            userConfiguration.setIterations(object.getInt("iterations"));
//            userConfiguration.setPasswordHash(object.getString("passwordHash"));
//            userConfiguration.setSalt(object.getString("salt"));
//        }
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
//        checkUserConfiguration(context);
//        return userConfiguration.getPasswordHash();
        DatabaseManager databaseManager = new DatabaseManager(context);
        String hash = databaseManager.getUserConfiguration(7).getPassword_hash();
        return hash;
    }

    public String getSalt(Context context) throws JSONException {
//        checkUserConfiguration(context);
//        if(userConfiguration.getSalt() == null){
        DatabaseManager databaseManager = new DatabaseManager(context);
        String salt = databaseManager.getUserConfiguration(7).getSalt();
        return salt;
//        }
//        return userConfiguration.getSalt();
    }

    public int getHashingIterations(Context context) throws JSONException {
//        checkUserConfiguration(context);
//        return userConfiguration.getIterations();
        DatabaseManager databaseManager = new DatabaseManager(context);
        int iterations = databaseManager.getUserConfiguration(7).getIterations();
        return iterations;
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
        //testCommit

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


    private void writeToDataFile(Context context, byte[] data, String fileName, Boolean configFlag) {
        try {
            File file = null;
            if(configFlag){
                file = new File(context.getFilesDir() + "/config/" + fileName+".txt");
            }else{
                file = new File(context.getFilesDir() + "/" + fileName+".txt");
            }


            if(data != null){
                FileOutputStream fos = new FileOutputStream(file);

                fos.write(data);
                fos.close();
            }

        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private byte[] readFromDataFile(Context context, String fileName, Boolean configFlag) throws IOException {
        File file = null;
        if(configFlag){
            file = new File(context.getFilesDir() + "/config/" + fileName+".txt");
        }else{
            file = new File(context.getFilesDir() + "/" + fileName+".txt");
        }
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

    public ArrayList<com.development.security.ciphernote.model.File> readFileManagmentData(SecurityManager securityManager, Context context) throws JSONException, ParseException, IOException, NoSuchAlgorithmException {
        DatabaseManager databaseManager = new DatabaseManager(context);
        ArrayList<com.development.security.ciphernote.model.File> list = databaseManager.getAllFiles();

//            try{
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//                parsedDate = formatter.parse(list.get(i).getAccessDate());
//            }catch (Exception e){
//                e.printStackTrace();
//                parsedDate = new Date();
//            }


        return list;
    }
//    public void writeFileManagmentData(SecurityManager securityManager, Context context, DataStructures.FileManagmentObject inputFile) throws JSONException {
//        DatabaseManager databaseManager = new DatabaseManager(context);
//        com.development.security.ciphernote.model.File file = new com.development.security.ciphernote.model.File();
//        file.setData("");
//        file.setFileName(inputFile.userDefinedFileName);
//        file.setAccessDate(fo);
//        databaseManager.addFile(file);
//    }


    private void writeToSP(Context context, byte[] data){
        String label = "filesData";

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(label, data);
//        editor.apply();
        writeToDataFile(context, data, label, true);

    }
    private byte[] readFromSP(Context context) throws JSONException, IOException {
        String label = "filesData";
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        String name = preferences.getString(label, null);
//        return name;
        return readFromDataFile(context, label, true);
    }


    public void writeToPasswordFile(Context context, byte[] data){
        String label = "devicePassword";
        writeToDataFile(context, data, label, true);
    }

    public byte[] readFromPasswordFile(Context context) throws JSONException, IOException {
        String label = "devicePassword";
        return readFromDataFile(context, label, true);
    }


    private void checkConfigDirectory(Context context){
        File direct = new File(context.getFilesDir() +  "/config");

        if(!direct.exists()) {
            if(direct.mkdir()); //directory is created;
        }
    }

    public Boolean deleteFile(Context context, String fileName, Boolean configFlag) {
        File file;
        Boolean status;
        if (configFlag) {
            file = new File(context.getFilesDir() + "/config/" + fileName+".txt");
        } else {
            file = new File(context.getFilesDir() + fileName+".txt");
        }

        return true;

//        if (file.delete()) {
//            System.out.println("File deleted successfully");
//            return true;
//        } else {
//            System.out.println("Failed to delete the file");
//            return false;
//        }
    }

}
