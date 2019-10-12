package com.info.charith.smartwarrantyapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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
import com.info.charith.smartwarrantyapp.Entities.Dealer;
import com.info.charith.smartwarrantyapp.Entities.DealerUserMock;
import com.info.charith.smartwarrantyapp.Entities.Warranty;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.DealerService;
import com.info.charith.smartwarrantyapp.Utils;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import static com.info.charith.smartwarrantyapp.Utils.dateStringToDateTime;
import static com.info.charith.smartwarrantyapp.Utils.dateTimeToString;
import static com.info.charith.smartwarrantyapp.Utils.endOfDay;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;

    public DrawerLayout drawerLayout;

    public NavController navController;

    public NavigationView navigationView;

    LinearLayout logoutBtn;

    DealerUserMock dealerUserMock;

    Gson gson=new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String logoutTimeString = sharedPref.getString("logoutTime", null);
        String loggedInUser=sharedPref.getString("loggedInUser",null);

        dealerUserMock=gson.fromJson(loggedInUser,DealerUserMock.class);

        DateTime now=new DateTime();

        /**
         * Get saved logout time(This time will save when user logged in)
         * Compare both time
         * If current time is after the saved time
         * Display a alert to the user to logout from the app
         * And navigate to login page
         */
        if(now.isAfter(dateStringToDateTime(logoutTimeString))){
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(getString(R.string.access_token_expired_message))
                    .setCancelable(false)
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new LogoutAsync().execute();
                        }
                    }).show();
        }else {
            init();
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
            NavigationUI.setupWithNavController(navigationView, navController);
            navigationView.setNavigationItemSelectedListener(this);

        }


    }

    private void init() {

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        logoutBtn = navigationView.findViewById(R.id.nav_logout);

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

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();

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

            case R.id.nav_settings:
                navController.navigate(R.id.nav_settings);
                break;


        }
        return true;

    }

    private class LogoutAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().logout(MainActivity.this, dealerUserMock.getUsername(),new AsyncListner() {
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
