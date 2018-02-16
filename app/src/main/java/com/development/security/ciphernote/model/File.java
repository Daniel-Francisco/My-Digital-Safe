package com.development.security.ciphernote.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by danie on 2/6/2018.
 */

public class File {
    public static final String TABLE_FILES = "files";

    // UserConfiguration Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_FILE_NAME = "file_name";
    public static final String KEY_ACCESS_DATE = "access_date";
    public static final String KEY_DATA = "file_data";

    long _id;
    String file_name;
    long access_date;
    String data;

    public File(){}

    public void setID(long id){
        _id = id;
    }
    public long getID(){
        return _id;
    }

    public void setFileName(String file_name){
        this.file_name = file_name;
    }
    public String getFileName(){
        return file_name;
    }

    public void setAccessDate(Date date) throws ParseException {
        this.access_date = date.getTime();
    }
    public void setAccessDate(long date) throws ParseException {
        this.access_date = date;
    }
    public Date getAccessDate() {
        Date date = new Date(access_date);
        return date;
    }

    public void setData(String data){
        this.data = data;
    }
    public String getData(){
        return data;
    }

}
