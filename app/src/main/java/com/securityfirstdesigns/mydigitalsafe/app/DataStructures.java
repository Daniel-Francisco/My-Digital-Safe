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

package com.securityfirstdesigns.mydigitalsafe.app;

import java.util.Date;

/**
 * Created by danie on 1/19/2018.
 */

public class DataStructures {
    static class UserConfiguration{
        public UserConfiguration(){

        }
        private String passwordHash;
        private int iterations;
        private String salt;
        public void setPasswordHash(String value){
            passwordHash = value;
        }
        public String getPasswordHash(){
            return passwordHash;
        }
        public void setIterations(int value){
            iterations = value;
        }
        public int getIterations(){
            return iterations;
        }
        public void setSalt(String value){
            salt = value;
        }
        public String getSalt(){
            return salt;
        }

    }

    static class FileManagmentObject{
        public FileManagmentObject(){

        }
        public String userDefinedFileName;
        public Date lastAccessed;
    }

    class notePage {

    }
}
