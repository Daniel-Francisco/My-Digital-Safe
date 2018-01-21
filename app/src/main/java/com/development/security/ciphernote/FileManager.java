package com.development.security.ciphernote;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by danie on 1/18/2018.
 */

public class FileManager {
    private DataStructures.UserConfiguration userConfiguration = null;

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
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

}
