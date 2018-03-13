package com.development.security.ciphernote;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by danie on 2/18/2018.
 */

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public Context applicationContext;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent landingIntent = new Intent(applicationContext, ListActivity.class);
            startActivity(landingIntent);

            finish();
        }

        else if (id == R.id.changePassword) {
            Intent changePasswordIntent = new Intent(applicationContext, ChangePasswordActivity.class);
            startActivity(changePasswordIntent);

            finish();
        } else if (id == R.id.about) {
            Intent aboutIntent = new Intent(applicationContext, AboutActivity.class);
            startActivity(aboutIntent);

            finish();
        } else if (id == R.id.logout) {
            Intent mainActivityIntent = new Intent(applicationContext, MainActivity.class);
            startActivity(mainActivityIntent);

            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
