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

package com.development.security.ciphernote.security;

import android.accounts.AuthenticatorException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.development.security.ciphernote.ListActivity;
import com.development.security.ciphernote.MainActivity;
import com.development.security.ciphernote.R;
import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.SecurityQuestion;
import com.development.security.ciphernote.model.UserConfiguration;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ConfigureSecurityQuestionsActivity extends AppCompatActivity {
    Context applicationContext = null;
    WebView browser = null;
    boolean changeMadeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_security_questions);

        applicationContext = this;

        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new ConfigureSecurityQuestionsActivity.WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/securityQuestionConfigure.html");

        ImageView deleteButton = (ImageView) findViewById(R.id.cancelButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeMadeFlag) {
                    new android.app.AlertDialog.Builder(applicationContext)
                            .setTitle("Cancel changes?")
                            .setMessage("Are you sure you want to throw away any changes?")
                            .setIcon(android.R.drawable.ic_delete)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    finish();
                }
            }
        });

    }

    private void setSecurityQuestion(String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidParameterSpecException, JSONException, AuthenticatorException {
        DatabaseManager databaseManager = new DatabaseManager(applicationContext);
        UserConfiguration userConfiguration = databaseManager.getUserConfiguration();

        SecurityManager securityManager = new SecurityManager();
        userConfiguration = securityManager.setSecurityQuestion(userConfiguration, applicationContext, password, securityQuestionCount,
                questionOneP, answerOneP, questionTwoP, answerTwoP, questionThreeP, answerThreeP,
                questionFourP, answerFourP, questionFiveP, answerFiveP);
        databaseManager.updateUserConfiguration(userConfiguration);
    }

    protected String userPassword = "";
    protected String questionOneP = "";
    protected String answerOneP = "";
    protected String questionTwoP = "";
    protected String answerTwoP = "";
    protected String questionThreeP = "";
    protected String answerThreeP = "";
    protected String questionFourP = "";
    protected String answerFourP = "";
    protected String questionFiveP = "";
    protected String answerFiveP = "";
    protected int securityQuestionCount = 0;

    private class AsyncSetSecurityQuestion extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                setSecurityQuestion(userPassword);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean status) {
            if (status) {
                Log.d("status", "good");
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:wrapUp()");
                    }
                });
                CharSequence successToast = "Security questions setup successfully!";

                Toast toast = Toast.makeText(applicationContext, successToast, Toast.LENGTH_LONG);
                toast.show();
            } else {
                Log.d("status", "bad");
                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:clearInputs()");
                    }
                });
                CharSequence failedToast = "Incorrect password!";

                Toast toast = Toast.makeText(applicationContext, failedToast, Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }


    @Override
    public void onBackPressed() {
        if (changeMadeFlag) {
            new android.app.AlertDialog.Builder(applicationContext)
                    .setTitle("Cancel changes?")
                    .setMessage("Are you sure you want to throw away any changes?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            finish();
        }
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }
//answer

        @JavascriptInterface
        public void setSecurityQuestion(String password, int numberOfSecurityQuestions, String questionOne, String answerOne,
                                        String questionTwo, String answerTwo, String questionThree,
                                        String answerThree, String questionFour, String answerFour,
                                        String questionFive, String answerFive) {
            userPassword = password;
            questionOneP = questionOne;
            questionTwoP = questionTwo;
            questionThreeP = questionThree;
            questionFourP = questionFour;
            questionFiveP = questionFive;

            answerOneP = answerOne;
            answerTwoP = answerTwo;
            answerThreeP = answerThree;
            answerFourP = answerFour;
            answerFiveP = answerFive;

            securityQuestionCount = numberOfSecurityQuestions;

            new AsyncSetSecurityQuestion().execute();
        }

        @JavascriptInterface
        public void goHome() {
            Intent listActivityIntent = new Intent(applicationContext, ListActivity.class);
            startActivity(listActivityIntent);

            finish();
        }

        @JavascriptInterface
        public void keyPressOccured() {
            changeMadeFlag = true;
        }

        @JavascriptInterface
        public boolean checkQuestions() {
            DatabaseManager databaseManager = new DatabaseManager(applicationContext);
            List<SecurityQuestion> securityQuestions = databaseManager.getAllSecurityQuestions();
            if (securityQuestions.size() > 0) {
                return true;
            }
            return false;
        }
    }
}
