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

package com.securityfirstdesigns.mydigitalsafe.app.model;

/**
 * Created by danie on 4/4/2018.
 */

public class QuickNoteFile {
    public static final String TABLE_QUICK_NOTE_FILES = "quick_note_files";

    public static final String KEY_ID = "id";
    public static final String KEY_QUICK_NOTE_FILE_NAME = "quick_note_file_name";
    public static final String KEY_QUICK_NOTE_DATA = "quick_note_data";

    long _id;
    String quick_note_file_name;
    String quick_note_data;

    public void setID(long id){
        _id = id;
    }
    public long getID(){
        return _id;
    }

    public void setQuickNoteFileName(String quick_note_file_name){
        this.quick_note_file_name = quick_note_file_name;
    }
    public String getQuickNoteFileName(){
        return quick_note_file_name;
    }

    public void setQuickNoteData(String quick_note_data){ this.quick_note_data = quick_note_data; }
    public String getQuickNoteData(){ return quick_note_data; }
}
