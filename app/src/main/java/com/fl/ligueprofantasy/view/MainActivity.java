package com.fl.ligueprofantasy.view;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fl.ligueprofantasy.R;
import com.fl.ligueprofantasy.controller.Screenshot;
import com.fl.ligueprofantasy.model.Constants;
import com.fl.ligueprofantasy.model.Team;
import com.fl.ligueprofantasy.model.UserData;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity
 implements NavigationView.OnNavigationItemSelectedListener {
        private Team currentTeamShown;
        private Team team;
        UserData userData = UserData.get();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            team = userData.getTeam();

            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = TeamDisplayFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View hView =  navigationView.getHeaderView(0);

            TextView teamNameSidebar = (TextView) hView.findViewById(R.id.nav_header_teamName);
            teamNameSidebar.setText(team.getName());

            TextView teamOwnerSidebar = (TextView) hView.findViewById(R.id.nav_header_owner);
            teamOwnerSidebar.setText(team.getOwner());

            Menu nav_Menu = navigationView.getMenu();
            if (userData.getAdminedTeam() == 0 && !userData.isSuperAdmin()) {
                nav_Menu.findItem(R.id.nav_admin).setVisible(false);
            } else if (!userData.isSuperAdmin()) {
              //  nav_Menu.findItem(R.id.nav_set_stats).setVisible(false);
                nav_Menu.findItem(R.id.nav_add_player).setVisible(false);
            }
        }

        @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                super.onBackPressed();
            }
        }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            int screenshotNum = sharedPref.getInt("screenshotNum", 0);
            screenshotNum++;
            editor.putInt("screenShotNum", screenshotNum);
            View rootView = findViewById(R.id.flContent);
            Screenshot screenshot = new Screenshot();
            Bitmap screenshotImage = screenshot.getScreenShot(rootView);
            File file = screenshot.store(screenshotImage, "ICFantasyScreenshot" + screenshotNum + ".png");
            shareImage(file);

            return super.onOptionsItemSelected(item);
        }


        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {

            // Handle navigation view item clicks here.
            int id = item.getItemId();
            Fragment fragment = null;
            Class fragmentClass = TeamDisplayFragment.class;
            Bundle args = new Bundle();
            SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            if (id == R.id.nav_my_team) {
                fragmentClass = TeamDisplayFragment.class;
            } else if (id == R.id.nav_players) {
                fragmentClass = PlayerListFragment.class;
            } else if (id == R.id.nav_teams) {
                fragmentClass = TeamListFragment.class;
            } else if (id == R.id.nav_table) {
                fragmentClass = TablesFragment.class;
            } else if (id == R.id.nav_log_out) {
                userData.clearUserData();
                editor.remove("username");
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_share) {
                int screenshotNum = sharedPref.getInt("screenshotNum", 0);
                screenshotNum++;
                editor.putInt("screenShotNum", screenshotNum);
                View rootView = findViewById(R.id.flContent);
                Screenshot screenshot = new Screenshot();
                Bitmap screenshotImage = screenshot.getScreenShot(rootView);
                File file = screenshot.store(screenshotImage, "FantasyScreenshot" + screenshotNum + ".png");
                shareImage(file);
                return true;
            } else if (id == R.id.nav_add_player) {
                //fragmentClass = PlayerAddFragment.class;

                return true;
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            fragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        public void setCurrentTeamShown(Team currentTeamShown) {
            this.currentTeamShown = currentTeamShown;
        }

        public Team getCurrentTeamShown() {
            return currentTeamShown;
        }

        public void shareImage(File file){
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");

            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Fantasy Football");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, "I've got " + team.getPoints() + " points in Fantasy Football!");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            try {
                startActivity(Intent.createChooser(intent, "Share Screenshot"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
            }
        }
}
