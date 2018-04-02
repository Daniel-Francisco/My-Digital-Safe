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

    int _id;
    int iterations;
    String password_hash;
    String salt;
    byte[] devicePassword;
    byte[] securityQuestionDevicePassword;

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
}
