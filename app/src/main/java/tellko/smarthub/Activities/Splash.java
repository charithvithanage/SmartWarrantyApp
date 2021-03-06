package tellko.smarthub.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import tellko.smarthub.R;
import tellko.smarthub.Utils;

import io.fabric.sdk.android.Fabric;

import static tellko.smarthub.Utils.isDeviceOnline;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        /**
         * Change status bar color programmatically
         */
        Utils.changeStatusBarColor(Splash.this, getWindow());


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
                            Utils.navigateWithoutHistory(Splash.this, AppListActivity.class);
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
                    finish();
                }
            });
        }


    }


}
