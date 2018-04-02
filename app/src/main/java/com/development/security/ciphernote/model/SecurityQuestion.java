package com.development.security.ciphernote.model;

/**
 * Created by danie on 3/31/2018.
 */

public class SecurityQuestion {
    public static final String TABLE_SECURITYQUESTIONS = "security_questions";

    // UserConfiguration Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_ANSWER_HASH = "answer_hash";

    int _id;
    String question;
    String answerHash;

    public SecurityQuestion(){}
    public SecurityQuestion(int id, String question, String answerHash){
        _id = id;
        this.question = question;
        this.answerHash = answerHash;
    }

    public int getID(){
        return this._id;
    }
    public void setID(int id){
        this._id = id;
    }

    public void setAnswerHash(String answerHash){
        this.answerHash = answerHash;
    }
    public String getAnswerHash(){
        return answerHash;
    }

    public void setQuestion(String question){
        this.question = question;
    }
    public String getQuestion(){
        return question;
    }


}
