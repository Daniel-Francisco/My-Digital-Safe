package com.development.security.ciphernote.model;

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
    String access_date;
    String data;

    public File(){}
    public File(int id, String file_name, String access_date, String data){
        _id = id;
        this.file_name = file_name;
        this.access_date = access_date;
        this.data = data;
    }
    public File(String file_name, String access_date, String data){
        this.file_name = file_name;
        this.access_date = access_date;
        this.data = data;
    }

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

    public void setAccessDate(String access_date){
        this.access_date = access_date;
    }
    public String getAccessDate(){
        return access_date;
    }

    public void setData(String data){
        this.data = data;
    }
    public String getData(){
        return data;
    }
}
