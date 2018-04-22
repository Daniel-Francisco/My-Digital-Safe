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

package com.securityfirstdesigns.mydigitalsafe.app.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.securityfirstdesigns.mydigitalsafe.app.ListActivity;
import com.securityfirstdesigns.mydigitalsafe.app.MainActivity;
import com.securityfirstdesigns.mydigitalsafe.app.R;
import com.securityfirstdesigns.mydigitalsafe.app.model.DatabaseManager;
import com.securityfirstdesigns.mydigitalsafe.app.model.SecurityQuestion;
import com.securityfirstdesigns.mydigitalsafe.app.model.UserConfiguration;
import com.securityfirstdesigns.mydigitalsafe.app.security.ConfigureSecurityQuestionsActivity;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    private AppCompatDelegate mDelegate;
    private Context applicationContext;

    private boolean criticalChangesMadeFlag = false;
    private Boolean exponentialLockoutEnableFlag = null;
    private Boolean securityQuestionsEnableFlag = null;
    private Integer exponentialLockoutAllowedFails = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.pref_general);

        applicationContext = this;

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        Preference button = findPreference(getString(R.string.preferenceChangePassword));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                Intent changePasswordIntent = new Intent(applicationContext, ChangePasswordActivity.class);
                startActivity(changePasswordIntent);
                return true;
            }
        });

        final Preference configureSecurityQuestionsButton = findPreference(getString(R.string.preferenceConfigureSecurityQuestions));

        configureSecurityQuestionsButton.setEnabled(prefs.getBoolean("passwordResetToggle", false));
        Log.d("valueThing", "From thing = " + prefs.getBoolean("passwordResetToggle", false));

        configureSecurityQuestionsButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                Intent configureSecurityQuestionsIntent = new Intent(applicationContext, ConfigureSecurityQuestionsActivity.class);
                startActivity(configureSecurityQuestionsIntent);
                return true;
            }
        });

//        configureSecurityQuestionsButton.setC

        Preference enableForgotPasswordPreference = findPreference(getString(R.string.preferenceSecurityQuestionsToggle));
        enableForgotPasswordPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object isVibrateOnObject) {
                criticalChangesMadeFlag = true;
                boolean isSecurityQuestionResetOn = (Boolean) isVibrateOnObject;

                securityQuestionsEnableFlag = isSecurityQuestionResetOn;
                configureSecurityQuestionsButton.setEnabled(securityQuestionsEnableFlag);

                if(securityQuestionsEnableFlag){
                    Intent configureSecurityQuestionsIntent = new Intent(applicationContext, ConfigureSecurityQuestionsActivity.class);
                    startActivity(configureSecurityQuestionsIntent);
                }

//                configureSecurityQuestionsButton.setEnabled(isSecurityQuestionResetOn);
//                prefs.edit().putBoolean("passwordResetToggle", isSecurityQuestionResetOn).commit();
//                Log.d("valueThing", String.valueOf(isSecurityQuestionResetOn));
                return true;
            }
        });


        final ListPreference allowedFailList = (ListPreference) findPreference(getString(R.string.preferenceExponentialLockoutAllowedFails));

        allowedFailList.setEnabled(prefs.getBoolean("exponentialLockoutFlag", false));
        allowedFailList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String listValue = (String) newValue;
                int listValueInt = Integer.parseInt(listValue);
                exponentialLockoutAllowedFails = listValueInt;

                DatabaseManager databaseManager = new DatabaseManager(applicationContext);
                UserConfiguration userConfiguration = databaseManager.getUserConfiguration();

                userConfiguration.setAllowedFailedLoginCount(exponentialLockoutAllowedFails);

                databaseManager.updateUserConfiguration(userConfiguration);
                return true;
            }
        });


        Preference enableExponentialLockoutPreference = findPreference(getString(R.string.preferenceSecurityExponentialLockout));
        enableExponentialLockoutPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object isExponentialLockoutObject) {
                criticalChangesMadeFlag = true;
                boolean isExponentialLockoutEnabled = (Boolean) isExponentialLockoutObject;

                exponentialLockoutEnableFlag = isExponentialLockoutEnabled;
                allowedFailList.setEnabled(exponentialLockoutEnableFlag);

                DatabaseManager databaseManager = new DatabaseManager(applicationContext);
                UserConfiguration userConfiguration = databaseManager.getUserConfiguration();
                prefs.edit().putBoolean("exponentialLockoutFlag", exponentialLockoutEnableFlag).commit();


                if (exponentialLockoutEnableFlag) {
                    userConfiguration.setLockoutFlag(1);
                } else {
                    userConfiguration.setLockoutFlag(0);
                }

                if(exponentialLockoutAllowedFails != null){
                    userConfiguration.setAllowedFailedLoginCount(exponentialLockoutAllowedFails);

                }

                databaseManager.updateUserConfiguration(userConfiguration);

                return true;
            }
        });


        ImageView saveButton = (ImageView) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save stuff
                Intent listIntent = new Intent(applicationContext, ListActivity.class);
                startActivity(listIntent);
                finish();
            }
        });

    }

    @Override
    public void onRestart() {
        super.onRestart();

        Intent mainActivityIntent = new Intent(applicationContext, MainActivity.class);
        startActivity(mainActivityIntent);

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        if (securityQuestionsEnableFlag != null) {
            prefs.edit().putBoolean("passwordResetToggle", securityQuestionsEnableFlag).commit();
            Log.d("valueThing", String.valueOf(securityQuestionsEnableFlag));

            if (!securityQuestionsEnableFlag) {
                DatabaseManager databaseManager = new DatabaseManager(applicationContext);
                List<SecurityQuestion> securityQuestionList = databaseManager.getAllSecurityQuestions();
                for (int i = 0; i < securityQuestionList.size(); i++) {
                    databaseManager.deleteSecurityQuestion(securityQuestionList.get(i));
                }
            }
        }


    }

    @Override
    public void onBackPressed() {
        Intent listIntent = new Intent(applicationContext, ListActivity.class);
        startActivity(listIntent);
        finish();
    }

//    @Override
//    public void onBackPressed() {
//        // your code.
//        Intent listIntent = new Intent(applicationContext, ListActivity.class);
//        startActivity(listIntent);
//        finish();
//    }

}
