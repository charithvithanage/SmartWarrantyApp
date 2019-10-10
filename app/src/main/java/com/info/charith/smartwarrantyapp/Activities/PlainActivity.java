package com.info.charith.smartwarrantyapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.DealerService;
import com.info.charith.smartwarrantyapp.Utils;

import org.joda.time.DateTime;
import org.json.JSONObject;

import static com.info.charith.smartwarrantyapp.Utils.dateStringToDateTime;

public class PlainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String logoutTimeString = sharedPref.getString("logoutTime", null);

        DateTime now=new DateTime();
        if (logoutTimeString != null) {

            if(now.isAfter(dateStringToDateTime(logoutTimeString))){
                new AlertDialog.Builder(PlainActivity.this)
                        .setMessage(getString(R.string.access_token_expired_message))
                        .setCancelable(false)
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.navigateWithoutHistory(PlainActivity.this, LoginActivity.class);
                            }
                        }).show();
            }else {
                Utils.navigateWithoutHistory(PlainActivity.this, MainActivity.class);
            }
        }else {
            Utils.navigateWithoutHistory(PlainActivity.this, LoginActivity.class);
        }
//        new CheckAccessToken().execute();

    }

    private class CheckAccessToken extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().checkAccessToken(PlainActivity.this, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Utils.navigateWithoutHistory(PlainActivity.this, MainActivity.class);
                }

                @Override
                public void onError(Context context, String error) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PlainActivity.this)
                                    .setMessage(getString(R.string.access_token_expired_message))
                                    .setCancelable(false)
                                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Utils.navigateWithoutHistory(PlainActivity.this, LoginActivity.class);
                                        }
                                    }).show();
                        }
                    });

                }
            });

            return null;
        }
    }
}
