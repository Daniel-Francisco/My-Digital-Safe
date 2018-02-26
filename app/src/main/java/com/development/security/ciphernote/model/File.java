package com.development.security.ciphernote.model;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    public static final String KEY_HASH = "file_hash";

    long _id;
    String file_name;
    String access_date;
    String data;
    String hash;

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

    public void setHash(String hash){
        this.hash = hash;
    }
    public String getHash(){
        return hash;
    }

//    @Override
//    public int compareTo(File o) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        try {
//            Date thisOne = sdf.parse(this.getAccessDate());
//            Date thatOne = sdf.parse(o.getAccessDate());
//            if (thisOne.after(thatOne)) {
//                return 1;
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return 0;
//    }
}
