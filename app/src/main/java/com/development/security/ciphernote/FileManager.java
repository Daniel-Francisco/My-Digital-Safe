package com.development.security.ciphernote;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

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

/**
 * Created by danie on 1/18/2018.
 */

public class FileManager {
    private DataStructures.UserConfiguration userConfiguration = null;

    public void writeDataFile(String filename, byte[] data) throws JSONException {
//        JSONObject fileObject = new JSONObject();
//        fileObject.put("lastModified", "");
//        fileObject.put("data", data);
        writeToDataFile(data, filename);
    }
    public byte[] readDataFile(String filename) throws JSONException {
        byte[] jsonBytes = readFromDataFile(filename);
        return jsonBytes;
//        String json = Base64.encodeToString(jsonBytes, Base64.DEFAULT);
//        if(isJSONValid(json)){
//            JSONObject object = new JSONObject(json);
//            return object;
//        }else{
//            JSONObject object = new JSONObject();
//            object.put("lastModified", "");
//            object.put("data", "");
//            return object;
//        }
    }
    private boolean isJSONValid(String test){
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            return false;
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


    private void writeToDataFile(byte[] data, String fileName) {
        try {
//            Log.d("fileLocation", "Path: "+NotebookEdit.this.getFilesDir().getAbsolutePath());
            if(data != null){
                FileOutputStream fos = new FileOutputStream("/data/data/security.ca.CipherNote/files/" + fileName + ".txt");//openFileOutput(fileName+".txt", Context.MODE_PRIVATE);
                fos.write(data);
                fos.close();
            }

        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private byte[] readFromDataFile(String fileName) {
        File file = new File("/data/data/security.ca.CipherNote/files/"+fileName+".txt");
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

}
