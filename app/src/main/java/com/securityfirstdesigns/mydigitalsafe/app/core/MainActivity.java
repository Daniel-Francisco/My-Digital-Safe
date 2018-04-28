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

package com.securityfirstdesigns.mydigitalsafe.app.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.securityfirstdesigns.mydigitalsafe.app.R;
import com.securityfirstdesigns.mydigitalsafe.app.model.DatabaseManager;
import com.securityfirstdesigns.mydigitalsafe.app.security.LoginActivity;

/*
    MainActivity runs first every time, and will either direct user to startup or login depending
    on whether or not this is the first run
 */
public class MainActivity extends AppCompatActivity {
    WebView webView;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseManager databaseManager = new DatabaseManager(this.getApplicationContext());
        try{
            if(databaseManager.checkForFirstRunFile(this.getApplicationContext())){
                Intent startupIntent = new Intent(this, StartupActivity.class);
                startActivity(startupIntent);
                finish();
            }else{
                Intent loginActivity = new Intent(this, LoginActivity.class);
                startActivity(loginActivity);
                finish();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
