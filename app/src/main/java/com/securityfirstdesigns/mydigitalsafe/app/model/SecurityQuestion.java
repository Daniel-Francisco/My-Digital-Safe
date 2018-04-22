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
 * Created by danie on 3/31/2018.
 */

public class SecurityQuestion {
    public static final String TABLE_SECURITYQUESTIONS = "security_questions";

    // UserConfiguration Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_ANSWER_HASH = "answer_hash";
    public static final String KEY_QUESTION_ORDER = "question_order";

    int _id;
    String question;
    String answerHash;
    int questionOrder;

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

    public void setQuestionOrder(int questionOrder){
        this.questionOrder = questionOrder;
    }
    public int getQuestionOrder(){
        return questionOrder;
    }


}
