package com.development.security.ciphernote.settings;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;

import com.development.security.ciphernote.ListActivity;
import com.development.security.ciphernote.R;
import com.development.security.ciphernote.security.ConfigureSecurityQuestionsActivity;

public class SettingsActivity extends PreferenceActivity {
    private AppCompatDelegate mDelegate;
    private Context applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.pref_general);
        applicationContext = this;

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

        Preference configureSecurityQuestionsButton = findPreference(getString(R.string.preferenceConfigureSecurityQuestions));
        configureSecurityQuestionsButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                Intent configureSecurityQuestionsIntent = new Intent(applicationContext, ConfigureSecurityQuestionsActivity.class);
                startActivity(configureSecurityQuestionsIntent);
                return true;
            }
        });

        ImageView saveButton = (ImageView) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(applicationContext, ListActivity.class);
                startActivity(listIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // your code.
        Intent listIntent = new Intent(applicationContext, ListActivity.class);
        startActivity(listIntent);
        finish();
    }

}
