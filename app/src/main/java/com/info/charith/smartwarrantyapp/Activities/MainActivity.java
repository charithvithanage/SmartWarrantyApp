package com.info.charith.smartwarrantyapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.Adapters.CustomExpandableListAdapter;
import com.info.charith.smartwarrantyapp.Entities.DealerUserMock;
import com.info.charith.smartwarrantyapp.Entities.ExpandableListDataPump;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.DealerService;
import com.info.charith.smartwarrantyapp.Utils;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.info.charith.smartwarrantyapp.Utils.dateStringToDateTime;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;

    public DrawerLayout drawerLayout;

    public NavController navController;

    public NavigationView navigationView;

    LinearLayout logoutBtn;

    DealerUserMock dealerUserMock;

    Gson gson = new Gson();

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    LinearLayout nav_home, nav_reports, nav_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Change status bar color programmatically
         */
        Utils.changeStatusBarColor(MainActivity.this, getWindow());


        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String logoutTimeString = sharedPref.getString("logoutTime", null);
        String loggedInUser = sharedPref.getString("loggedInUser", null);

        dealerUserMock = gson.fromJson(loggedInUser, DealerUserMock.class);

        DateTime now = new DateTime();

        /**
         * Get saved logout time(This time will save when user logged in)
         * Compare both time
         * If current time is after the saved time
         * Display a alert to the user to logout from the app
         * And navigate to login page
         */
        if (now.isAfter(dateStringToDateTime(logoutTimeString))) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(getString(R.string.access_token_expired_message))
                    .setCancelable(false)
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new LogoutAsync().execute();
                        }
                    }).show();
        } else {
            init();
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
            NavigationUI.setupWithNavController(navigationView, navController);

        }


    }

    private void init() {

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        logoutBtn = navigationView.findViewById(R.id.nav_logout);

        nav_home = navigationView.findViewById(R.id.nav_home);
        nav_reports = navigationView.findViewById(R.id.nav_report);
        nav_about = navigationView.findViewById(R.id.nav_about);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(getString(R.string.logout_confirmation_message))
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPref = getSharedPreferences(
                                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("loggedInUser", "0");
                                editor.putString("accessToken", "0");
                                editor.putString("refreshToken", "0");
                                editor.putString("userDealer", "0");
                                editor.putString("logoutTime", null);
                                editor.commit();
                                Utils.navigateWithoutHistory(MainActivity.this, LoginActivity.class);

                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });

        nav_home.setOnClickListener(this);
        nav_reports.setOnClickListener(this);
        nav_about.setOnClickListener(this);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                expandableListView.collapseGroup(groupPosition);
                drawerLayout.closeDrawers();
                navController.navigate(R.id.nav_settings);
                return false;
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(getString(R.string.back_confirmation_message))
                    .setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }


    @Override
    public void onClick(View v) {

        drawerLayout.closeDrawers();

        int id = v.getId();

        switch (id) {

            case R.id.nav_home:
                navController.navigate(R.id.nav_home);
                break;

            case R.id.nav_about:
                navController.navigate(R.id.nav_about);
                break;

            case R.id.nav_report:
                navController.navigate(R.id.nav_report);
                break;
        }
    }

    private class LogoutAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().logout(MainActivity.this, dealerUserMock.getUsername(), new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Utils.navigateWithoutHistory(MainActivity.this, LoginActivity.class);
                }

                @Override
                public void onError(Context context, String error) {
                    Utils.navigateWithoutHistory(MainActivity.this, LoginActivity.class);
                }
            });

            return null;
        }
    }


}
