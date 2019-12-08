package tellko.smarthub.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONObject;

import tellko.smarthub.AsyncTasks.LogoutAsync;
import tellko.smarthub.Entities.Dealer;
import tellko.smarthub.Entities.DealerUser;
import tellko.smarthub.Interfaces.AsyncListner;
import tellko.smarthub.R;
import tellko.smarthub.Utils;

import static tellko.smarthub.Utils.capEachWord;
import static tellko.smarthub.Utils.isDeviceOnline;

public class AppListActivity extends AppCompatActivity implements View.OnClickListener {
    public Toolbar toolbar;

    public DrawerLayout drawerLayout;

    public NavController navController;

    public NavigationView navigationView;

    LinearLayout logoutBtn;

    DealerUser dealerUserMock;
    Dealer dealer;

    Gson gson = new Gson();

    LinearLayout nav_home, nav_about, nav_settings;
    TextView tvUserName, tvDealerName;

    private static final String TAG = "SmartWarrantyApp";

    boolean homeScreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        Log.d(TAG, capEachWord("the sentence you want to apply caps to"));

        /**
         * Change status bar color programmatically
         */
        Utils.changeStatusBarColor(AppListActivity.this, getWindow());


        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String loggedInUser = sharedPref.getString("loggedInUser", null);
        String userDealer = sharedPref.getString("userDealer", null);

        dealerUserMock = gson.fromJson(loggedInUser, DealerUser.class);
        dealer = gson.fromJson(userDealer, Dealer.class);

        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    private void init() {

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        logoutBtn = navigationView.findViewById(R.id.nav_logout);
        tvUserName = navigationView.findViewById(R.id.tvUserName);
        tvDealerName = navigationView.findViewById(R.id.tvDealerName);

        nav_home = navigationView.findViewById(R.id.nav_home);
        nav_about = navigationView.findViewById(R.id.nav_about);
        nav_settings = navigationView.findViewById(R.id.nav_settings);

        tvUserName.setText(dealerUserMock.getUsername());
        tvDealerName.setText(dealer.getDealerName());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AppListActivity.this)
                        .setMessage(getString(R.string.logout_confirmation_message))
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (isDeviceOnline(AppListActivity.this)) {
                                    new LogoutAsync(AppListActivity.this, dealerUserMock, new AsyncListner() {
                                        @Override
                                        public void onSuccess(Context context, JSONObject jsonObject) {
                                            SharedPreferences sharedPref = context.getSharedPreferences(
                                                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("logoutTime", null);
                                            editor.putString("loggedInUser", "0");
                                            editor.putString("accessToken", "0");
                                            editor.putString("refreshToken", "0");
                                            editor.putString("userDealer", "0");
                                            editor.putString("logoutTime", null);
                                            editor.commit();
                                            Utils.navigateWithoutHistory(context, LoginActivity.class);
                                        }

                                        @Override
                                        public void onError(final Context context, String error) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new AlertDialog.Builder(context)
                                                            .setMessage(context.getString(R.string.server_error))
                                                            .setCancelable(false)
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();

                                                                }
                                                            }).show();
                                                }
                                            });
                                        }
                                    }).execute();
                                } else {
                                    Utils.showAlertWithoutTitleDialog(AppListActivity.this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                }

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
        nav_about.setOnClickListener(this);
        nav_settings.setOnClickListener(this);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

    }

    @Override
    public boolean onSupportNavigateUp() {

//        if (!homeScreen) {
//            new AlertDialog.Builder(AppListActivity.this)
//                    .setMessage(getString(R.string.back_confirmation_message))
//                    .setCancelable(false)
//                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            homeScreen = NavigationUI.navigateUp(Navigation.findNavController(AppListActivity.this, R.id.nav_host_fragment), drawerLayout);
//                        }
//                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    homeScreen = false;
//
//                }
//            }).show();
//        } else {
//            homeScreen = NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
//        }
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (homeScreen) {
                new AlertDialog.Builder(AppListActivity.this)
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
            } else {
                homeScreen = true;
                navController.navigate(R.id.nav_home);
            }


        }
    }


    @Override
    public void onClick(View v) {

        drawerLayout.closeDrawers();

        int id = v.getId();

        switch (id) {

            case R.id.nav_home:
                homeScreen = true;
                navController.navigate(R.id.nav_home);
                break;

            case R.id.nav_about:
                homeScreen = false;

                navController.navigate(R.id.nav_about);
                break;

            case R.id.nav_settings:
                homeScreen = false;

                navController.navigate(R.id.nav_settings);
                break;
        }
    }


}
