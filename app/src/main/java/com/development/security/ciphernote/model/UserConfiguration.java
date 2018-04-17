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

/**
 * Created by danie on 2/6/2018.
 */

public class UserConfiguration {
    // UserConfiguration table name
    public static final String TABLE_USERCONFIGURATION = "user_configuration";

    // UserConfiguration Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_ITERATIONS = "iterations";
    public static final String KEY_PASSWORD_HASH = "password_hash";
    public static final String KEY_SALT = "salt";
    public static final String KEY_DEVICE_PASSWORD = "device_password";
    public static final String KEY_SECURITY_QUESTION_DEVICE_PASSWORD = "security_question_d_p";
    public static final String KEY_LOCKOUT_TIME = "lockout_time";
    public static final String KEY_LOCKOUT_FLAG = "lockout_flag";
    public static final String KEY_ALLOWED_NUMBER_OF_FAILED_LOGINS = "allowed_failed_login_count";
    public static final String KEY_FAILED_LOGIN_COUNT = "failed_login_count";

    int _id;
    int iterations;
    String password_hash;
    String salt;
    byte[] devicePassword;
    byte[] securityQuestionDevicePassword;
    String lockoutTime;
    int lockoutFlag;
    int allowedFailedLoginCount;
    int failedLoginCount;

    public UserConfiguration(){}
    public UserConfiguration(int id, int iterations, String password_hash, String salt){
        _id = id;
        this.iterations = iterations;
        this.password_hash = password_hash;
        this.salt = salt;
    }
    public UserConfiguration(int iterations, String password_hash, String salt){
        this.iterations = iterations;
        this.password_hash = password_hash;
        this.salt = salt;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public void setIterations(int iterations){
        this.iterations = iterations;
    }
    public int getIterations(){
        return iterations;
    }

    public void setPassword_hash(String password_hash){
        this.password_hash = password_hash;
    }
    public String getPassword_hash(){
        return password_hash;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }
    public String getSalt(){
        return salt;
    }

    public void setDevicePassword(byte[] devicePassword){
        this.devicePassword = devicePassword;
    }
    public byte[] getDevicePassword(){
        return devicePassword;
    }

    public void setSecurityQuestionDevicePassword(byte[] securityQuestionDevicePassword){
        this.securityQuestionDevicePassword = securityQuestionDevicePassword;
    }
    public byte[] getSecurityQuestionDevicePassword(){
        return securityQuestionDevicePassword;
    }

    public void setLockoutFlag(int lockoutFlag){
        this.lockoutFlag = lockoutFlag;
    }
    public int getLockoutFlag(){
        return lockoutFlag;
    }

    public void setKeyLockoutTime(String lockoutTime){
        this.lockoutTime = lockoutTime;
    }
    public String getLockoutTime(){
        return lockoutTime;
    }

    public void setAllowedFailedLoginCount(int allowedFailedLoginCount){
        this.allowedFailedLoginCount = allowedFailedLoginCount;
    }
    public int getAllowedFailedLoginCount(){
        return allowedFailedLoginCount;
    }

    public void setFailedLoginCount(int failedLoginCount){
        this.failedLoginCount = failedLoginCount;
    }
    public int getFailedLoginCount(){
        return failedLoginCount;
    }
}
