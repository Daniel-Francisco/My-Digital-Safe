package com.development.security.ciphernote.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                + UserConfiguration.KEY_SALT + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_USERCONFIGURATION_TABLE);

        String CREATE_Files_TABLE = "CREATE TABLE " + File.TABLE_FILES + "("
                + File.KEY_ID + " INTEGER PRIMARY KEY," + File.KEY_FILE_NAME + " TEXT,"
                + File.KEY_ACCESS_DATE + " TEXT,"
                + File.KEY_DATA + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_Files_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserConfiguration.TABLE_USERCONFIGURATION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + File.TABLE_FILES);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    // Adding new UserConfiguration
    public void addUserConfiguration(UserConfiguration config) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserConfiguration.KEY_ITERATIONS, config.getIterations());
        values.put(UserConfiguration.KEY_PASSWORD_HASH, config.getPassword_hash());
        values.put(UserConfiguration.KEY_SALT, config.getSalt());

        // Inserting Row
        db.insert(UserConfiguration.TABLE_USERCONFIGURATION, null, values);
        db.close(); // Closing database connection
    }

    // Adding new UserConfiguration
    public void updateUserConfiguration(UserConfiguration config) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserConfiguration.KEY_ITERATIONS, config.getIterations());
        values.put(UserConfiguration.KEY_PASSWORD_HASH, config.getPassword_hash());
        values.put(UserConfiguration.KEY_SALT, config.getSalt());

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

                configurationList.add(userConfiguration);
            } while (cursor.moveToNext());
        }

        // return contact list
        return configurationList;
    }


    public UserConfiguration getUserConfiguration(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(UserConfiguration.TABLE_USERCONFIGURATION, new String[] { UserConfiguration.KEY_ID, UserConfiguration.KEY_ITERATIONS, UserConfiguration.KEY_PASSWORD_HASH, UserConfiguration.KEY_SALT }, UserConfiguration.KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        UserConfiguration userConfiguration = new UserConfiguration(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3));
        // return contact
        return userConfiguration;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addFile(File file) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(File.KEY_ACCESS_DATE, file.getAccessDate());
        values.put(File.KEY_FILE_NAME, file.getFileName());
        values.put(File.KEY_DATA, file.getData());

        // Inserting Row
        db.insert(File.TABLE_FILES, null, values);
        db.close(); // Closing database connection
    }

    public void updateFile(File file) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(File.KEY_ACCESS_DATE, file.getAccessDate());
        values.put(File.KEY_FILE_NAME, file.getFileName());
        values.put(File.KEY_DATA, file.getData());

        // Inserting Row
        db.update(File.TABLE_FILES, values, File.KEY_FILE_NAME + " = ?", new String[] { String.valueOf(file.getFileName()) });
        db.close(); // Closing database connection
    }

    public List<File> getAllFiles() {
        List<File> fileList = new ArrayList<File>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + File.TABLE_FILES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                UserConfiguration userConfiguration = new UserConfiguration(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3));
                File file = new File();
                file.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(File.KEY_ID))));
                file.setAccessDate(cursor.getString(cursor.getColumnIndex(File.KEY_ACCESS_DATE)));
                file.setFileName(cursor.getString(cursor.getColumnIndex(File.KEY_FILE_NAME)));
                file.setData(cursor.getString(cursor.getColumnIndex(File.KEY_DATA)));

                fileList.add(file);
            } while (cursor.moveToNext());
        }

        // return contact list
        return fileList;
    }

    public File getFileById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(File.TABLE_FILES, new String[] { File.KEY_ID, File.KEY_DATA, File.KEY_ACCESS_DATE, File.KEY_ACCESS_DATE }, UserConfiguration.KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        File file = new File();
        file.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(File.KEY_ID))));
        file.setAccessDate(cursor.getString(cursor.getColumnIndex(File.KEY_ACCESS_DATE)));
        file.setFileName(cursor.getString(cursor.getColumnIndex(File.KEY_FILE_NAME)));
        file.setData(cursor.getString(cursor.getColumnIndex(File.KEY_DATA)));

        return file;
    }

    public File getFileByName(String filename) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(File.TABLE_FILES, new String[] { File.KEY_ID, File.KEY_DATA, File.KEY_ACCESS_DATE }, File.KEY_FILE_NAME + "=?",
                new String[] { filename }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        File file = new File();
        file.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(File.KEY_ID))));
        file.setAccessDate(cursor.getString(cursor.getColumnIndex(File.KEY_ACCESS_DATE)));
        file.setFileName(filename);
        file.setData(cursor.getString(cursor.getColumnIndex(File.KEY_DATA)));

        return file;
    }

}
