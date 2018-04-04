package com.development.security.ciphernote.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.development.security.ciphernote.ListActivity;
import com.development.security.ciphernote.R;
import com.development.security.ciphernote.model.DatabaseManager;
import com.development.security.ciphernote.model.SecurityQuestion;
import com.development.security.ciphernote.security.ConfigureSecurityQuestionsActivity;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    private AppCompatDelegate mDelegate;
    private Context applicationContext;
    private boolean criticalChangesMadeFlag = false;

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

        Preference enableForgotPasswordPreference = findPreference(getString(R.string.preferenceSecurityQuestionsToggle));
        enableForgotPasswordPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object isVibrateOnObject) {
                criticalChangesMadeFlag = true;
                boolean isSecurityQuestionResetOn = (Boolean) isVibrateOnObject;
                configureSecurityQuestionsButton.setEnabled(isSecurityQuestionResetOn);
                prefs.edit().putBoolean("passwordResetToggle", isSecurityQuestionResetOn).commit();
                Log.d("valueThing", String.valueOf(isSecurityQuestionResetOn));
                return true;
            }
        });


        ImageView saveButton = (ImageView) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save stuff
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
                if (!prefs.getBoolean("passwordResetToggle", false)) {
                    DatabaseManager databaseManager = new DatabaseManager(applicationContext);
                    List<SecurityQuestion> securityQuestionList = databaseManager.getAllSecurityQuestions();
                    for (int i = 0; i < securityQuestionList.size(); i++) {
                        databaseManager.deleteSecurityQuestion(securityQuestionList.get(i));
                    }
                }

                Intent listIntent = new Intent(applicationContext, ListActivity.class);
                startActivity(listIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (criticalChangesMadeFlag) {
            new android.app.AlertDialog.Builder(applicationContext)
                    .setTitle("Cancel changes?")
                    .setMessage("Are you sure you want to throw away any changes? To save your changes, click the save button in the toolbar.")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent listIntent = new Intent(applicationContext, ListActivity.class);
                            startActivity(listIntent);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }else{
            finish();
        }
    }

//    @Override
//    public void onBackPressed() {
//        // your code.
//        Intent listIntent = new Intent(applicationContext, ListActivity.class);
//        startActivity(listIntent);
//        finish();
//    }

}
