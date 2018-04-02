package com.development.security.ciphernote.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import com.development.security.ciphernote.security.SecurityManager;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by danie on 2/6/2018.
 */

public class DatabaseManager extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ciphernote_db";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USERCONFIGURATION_TABLE = "CREATE TABLE " + UserConfiguration.TABLE_USERCONFIGURATION + "("
                + UserConfiguration.KEY_ID + " INTEGER PRIMARY KEY," + UserConfiguration.KEY_PASSWORD_HASH + " TEXT,"
                + UserConfiguration.KEY_ITERATIONS + " INTEGER,"
                + UserConfiguration.KEY_DEVICE_PASSWORD + " BLOB,"
                + UserConfiguration.KEY_SECURITY_QUESTION_DEVICE_PASSWORD + " BLOB,"
                + UserConfiguration.KEY_SALT + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_USERCONFIGURATION_TABLE);

        String CREATE_Files_TABLE = "CREATE TABLE " + File.TABLE_FILES + "("
                + File.KEY_ID + " INTEGER PRIMARY KEY," + File.KEY_FILE_NAME + " BLOB,"
                + File.KEY_ACCESS_DATE + " INTEGER,"
                + File.KEY_HASH + " TEXT,"
                + File.KEY_FOLDER + " TEXT,"
                + File.KEY_DATA + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_Files_TABLE);

        String CREATE_SECURITYQUESTIONS_TABLE = "CREATE TABLE " + SecurityQuestion.TABLE_SECURITYQUESTIONS + "("
                + SecurityQuestion.KEY_ID + " INTEGER PRIMARY KEY,"
                + SecurityQuestion.KEY_QUESTION + " TEXT,"
                + SecurityQuestion.KEY_ANSWER_HASH + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_SECURITYQUESTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserConfiguration.TABLE_USERCONFIGURATION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + File.TABLE_FILES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SecurityQuestion.TABLE_SECURITYQUESTIONS);

        // Create tables again
        onCreate(sqLiteDatabase);
    }


    //------------------------------ USER CONFIG -----------------

    // Adding new UserConfiguration
    public void addUserConfiguration(UserConfiguration config) {
        List<UserConfiguration> list = getAllUserConfigurations();
        for(int i = 0; i < list.size(); i++){
            deleteUserConfiguration(list.get(i).getID());
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserConfiguration.KEY_ITERATIONS, config.getIterations());
        values.put(UserConfiguration.KEY_PASSWORD_HASH, config.getPassword_hash());
        values.put(UserConfiguration.KEY_SALT, config.getSalt());
        values.put(UserConfiguration.KEY_DEVICE_PASSWORD, config.getDevicePassword());
        values.put(UserConfiguration.KEY_SECURITY_QUESTION_DEVICE_PASSWORD, config.getSecurityQuestionDevicePassword());

        // Inserting Row
        db.insert(UserConfiguration.TABLE_USERCONFIGURATION, null, values);
        db.close(); // Closing database connection
    }

    public void deleteUserConfiguration(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(UserConfiguration.TABLE_USERCONFIGURATION, UserConfiguration.KEY_ID + " = ?", new String[] { String.valueOf(id) });

        db.close();
    }

    // Adding new UserConfiguration
    public void updateUserConfiguration(UserConfiguration config) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserConfiguration.KEY_ITERATIONS, config.getIterations());
        values.put(UserConfiguration.KEY_PASSWORD_HASH, config.getPassword_hash());
        values.put(UserConfiguration.KEY_SALT, config.getSalt());
        values.put(UserConfiguration.KEY_DEVICE_PASSWORD, config.getDevicePassword());
        values.put(UserConfiguration.KEY_SECURITY_QUESTION_DEVICE_PASSWORD, config.getSecurityQuestionDevicePassword());

        // Inserting Row
        db.update(UserConfiguration.TABLE_USERCONFIGURATION, values, UserConfiguration.KEY_ID + " = ?", new String[] { String.valueOf(config.getID()) });
        db.close(); // Closing database connection
    }


    public List<UserConfiguration> getAllUserConfigurations() {
        List<UserConfiguration> configurationList = new ArrayList<UserConfiguration>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + UserConfiguration.TABLE_USERCONFIGURATION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                UserConfiguration userConfiguration = new UserConfiguration(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3));
                UserConfiguration userConfiguration = new UserConfiguration();
                userConfiguration.setID(Integer.parseInt(cursor.getString(0)));
                userConfiguration.setPassword_hash(cursor.getString(cursor.getColumnIndex(UserConfiguration.KEY_PASSWORD_HASH)));
                userConfiguration.setSalt(cursor.getString(cursor.getColumnIndex(UserConfiguration.KEY_SALT)));
                userConfiguration.setIterations(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UserConfiguration.KEY_ITERATIONS))));
                userConfiguration.setDevicePassword(cursor.getBlob(cursor.getColumnIndex(UserConfiguration.KEY_DEVICE_PASSWORD)));
                userConfiguration.setSecurityQuestionDevicePassword(cursor.getBlob(cursor.getColumnIndex(UserConfiguration.KEY_SECURITY_QUESTION_DEVICE_PASSWORD)));

                configurationList.add(userConfiguration);
            } while (cursor.moveToNext());
        }

        // return contact list
        return configurationList;
    }


    public UserConfiguration getUserConfiguration() {
        List<UserConfiguration> allConfigs = getAllUserConfigurations();
        if(allConfigs.size() == 0){
            return null;
        }
        return allConfigs.get(0);

    }


    //---------------------------------- SECURITY QUESTIONS -------------------------

    public void addSecurityQuestion(SecurityQuestion securityQuestion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SecurityQuestion.KEY_ANSWER_HASH, securityQuestion.getAnswerHash());
        values.put(SecurityQuestion.KEY_QUESTION, securityQuestion.getQuestion());

        // Inserting Row
        long id = db.insert(SecurityQuestion.TABLE_SECURITYQUESTIONS, null, values);
        db.close(); // Closing database connection
    }

    public void deleteSecurityQuestion(SecurityQuestion securityQuestion){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(SecurityQuestion.TABLE_SECURITYQUESTIONS, SecurityQuestion.KEY_ID + " = ?", new String[] { String.valueOf(securityQuestion._id) });

        db.close();
    }

    public List<SecurityQuestion> getAllSecurityQuestions() {
        List<SecurityQuestion> securityQuestions = new ArrayList<SecurityQuestion>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + SecurityQuestion.TABLE_SECURITYQUESTIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                UserConfiguration userConfiguration = new UserConfiguration(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3));
                SecurityQuestion securityQuestion = new SecurityQuestion();
                securityQuestion.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(SecurityQuestion.KEY_ID))));
                securityQuestion.setAnswerHash(cursor.getString(cursor.getColumnIndex(SecurityQuestion.KEY_ANSWER_HASH)));
                securityQuestion.setQuestion(cursor.getString(cursor.getColumnIndex(SecurityQuestion.KEY_QUESTION)));

                securityQuestions.add(securityQuestion);
            } while (cursor.moveToNext());
        }

        // return contact list
        return securityQuestions;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public long addFile(File file) throws ParseException, NoSuchAlgorithmException {
        SQLiteDatabase writeDatabase = this.getWritableDatabase();
        SecurityManager securityManager = SecurityManager.getInstance();

        file = securityManager.setFileHash(file);

        ContentValues values = new ContentValues();
        values.put(File.KEY_ACCESS_DATE, securityManager.encrypt(file.getAccessDate()));
        values.put(File.KEY_FILE_NAME, securityManager.encrypt(file.getFileName()));
        values.put(File.KEY_DATA, Base64.encode(securityManager.encrypt(file.getData()), Base64.DEFAULT));
        values.put(File.KEY_HASH, file.getHash());

        // Inserting Row
        long returnValue = writeDatabase.insert(File.TABLE_FILES, null, values);
        writeDatabase.close();
        return returnValue;
    }

    public long updateFile(File file) throws NoSuchAlgorithmException {
        SQLiteDatabase writeDatabase = this.getWritableDatabase();
        SecurityManager securityManager = SecurityManager.getInstance();

        ContentValues values = new ContentValues();

        byte[] fileNameCipher = securityManager.encrypt(file.getFileName());
        String dataString = new String(file.getData());
        byte[] dataCipher = securityManager.encrypt(dataString);

        file = securityManager.setFileHash(file);

        values.put(File.KEY_ACCESS_DATE, securityManager.encrypt(file.getAccessDate()));
        values.put(File.KEY_FILE_NAME, fileNameCipher);
        values.put(File.KEY_DATA, Base64.encodeToString(dataCipher, Base64.DEFAULT));
        values.put(File.KEY_HASH, file.getHash());

        // Inserting Row
        long id = writeDatabase.update(File.TABLE_FILES, values, File.KEY_ID + " = ?", new String[] { String.valueOf(file.getID()) });
        writeDatabase.close();
        return id;
    }

    public ArrayList<File> getAllFiles() throws ParseException, NoSuchAlgorithmException {
        SQLiteDatabase writeDatabase = this.getWritableDatabase();
        SecurityManager securityManager = SecurityManager.getInstance();

        ArrayList<File> fileList = new ArrayList<File>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + File.TABLE_FILES;

        Cursor cursor = writeDatabase.rawQuery(selectQuery, null);


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                UserConfiguration userConfiguration = new UserConfiguration(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3));
                File file = new File();
                file.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(File.KEY_ID))));
                byte[] accessDate = cursor.getBlob(cursor.getColumnIndex(File.KEY_ACCESS_DATE));

                file.setAccessDate(securityManager.decrypt(accessDate));

                file.setFileName(securityManager.decrypt(cursor.getBlob(cursor.getColumnIndex(File.KEY_FILE_NAME))));

                try{
                    file.setData(securityManager.decrypt(Base64.decode(cursor.getString(cursor.getColumnIndex(File.KEY_DATA)), Base64.DEFAULT)));
                }catch(Exception e){
                    e.printStackTrace();
                    file.setData("");
                }

                file.setHash(cursor.getString(cursor.getColumnIndex(File.KEY_HASH)));

                boolean fileStatus = securityManager.validateFileHash(file);

                if(!fileStatus){
                    file = null;
                }

                fileList.add(file);
            } while (cursor.moveToNext());
        }

        writeDatabase.close();
        // return contact list
        return fileList;
    }

    public void deleteFile(File file) throws ParseException, NoSuchAlgorithmException {
        SQLiteDatabase writeDatabase = this.getWritableDatabase();
        writeDatabase.delete(File.TABLE_FILES, File.KEY_ID + " = ?", new String[] { String.valueOf(file.getID()) });
        writeDatabase.close();
    }

//    private void validateDB(){
//        if(writeDatabase == null){
//            writeDatabase = this.getWritableDatabase();
//        }
//    }









    public Boolean checkForFirstRunFile(Context context) throws JSONException, IOException {
        byte[] data = readFromDataFile(context, "startup", true);
        if((new String(data)).equals("started")){
            return false;
        }
        return true;
    }



    private byte[] readFromDataFile(Context context, String fileName, Boolean configFlag) throws IOException {
        java.io.File file = null;
        if(configFlag){
            file = new java.io.File(context.getFilesDir() + "/config/" + fileName+".txt");
        }else{
            file = new java.io.File(context.getFilesDir() + "/" + fileName+".txt");
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
    public void writeToDataFile(Context context, byte[] data, String fileName, Boolean configFlag) {
        try {
            java.io.File file = null;
            if(configFlag){
                file = new java.io.File(context.getFilesDir() + "/config/" + fileName+".txt");
            }else{
                file = new java.io.File(context.getFilesDir() + "/" + fileName+".txt");
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


    public void checkConfigDirectory(Context context){
        java.io.File direct = new java.io.File(context.getFilesDir() +  "/config");

        if(!direct.exists()) {
            if(direct.mkdir()); //directory is created;
        }
    }

}
