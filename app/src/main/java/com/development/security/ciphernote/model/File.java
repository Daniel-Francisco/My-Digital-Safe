/*
 * My Digital Safe, the secure notepad Android app.
 * Copyright (C) 2018 Security First Designs
 *
 * My Digital Safe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <a href="www.gnu.org/licenses/">here</a>.
 */

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
    public static final String KEY_FOLDER = "file_folder";

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

}
