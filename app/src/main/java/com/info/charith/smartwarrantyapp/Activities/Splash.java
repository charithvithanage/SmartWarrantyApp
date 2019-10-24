package com.info.charith.smartwarrantyapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.DealerService;
import com.info.charith.smartwarrantyapp.Utils;

import org.joda.time.DateTime;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

import static com.info.charith.smartwarrantyapp.Utils.dateStringToDateTime;
import static com.info.charith.smartwarrantyapp.Utils.isDeviceOnline;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if(isDeviceOnline(Splash.this)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences sharedPref = getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    String logoutTimeString = sharedPref.getString("logoutTime", null);

                    final String loggedInLawyer = sharedPref.getString("loggedInUser", "0");
                    final String accessToken = sharedPref.getString("accessToken", "0");

                    if (logoutTimeString != null) {

                        if (!loggedInLawyer.equals("0") && !accessToken.equals("0")) {
                            Utils.navigateWithoutHistory(Splash.this, MainActivity.class);
                        } else {
                            Utils.navigateWithoutHistory(Splash.this, LoginActivity.class);
                        }

                    }else {
                        Utils.navigateWithoutHistory(Splash.this, LoginActivity.class);
                    }


                }
            }, 1000);
        }else {
            Utils.showAlertWithoutTitleDialog(Splash.this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }


    }


}
