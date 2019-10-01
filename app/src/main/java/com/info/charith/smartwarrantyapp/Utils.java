package com.info.charith.smartwarrantyapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    //Change status bar color
    public static void changeStatusBarColor(Context context, Window window) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    // A placeholder password validation check
    public static boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        } else {
            return !password.trim().isEmpty();
        }
    }

    // A placeholder username validation check
    public static boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder username validation check
    public static boolean isDealerCodeValid(String dealerCode) {
        if (dealerCode == null) {
            return false;
        } else {
            return !dealerCode.trim().isEmpty();
        }
    }


    // A placeholder username validation check
    public static boolean isUserNICValid(String userNIC) {
        if (userNIC == null) {
            return false;
        } else {
            return !userNIC.trim().isEmpty();
        }
    }

    public static boolean isPasswordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public static void navigateWithoutHistory(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void showAlertWithoutTitleDialog(Context context, String message, final DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Whatever...
                        listener.onClick(dialog,which);
                    }
                }).show();
    }

     public static String convertDateTimeStringToString(String dateTimeString) {

        SimpleDateFormat sdf = new SimpleDateFormat(Config.date_time_pattern, Locale.ENGLISH);
        SimpleDateFormat patern = new SimpleDateFormat(Config.standard_date_time_pattern, Locale.ENGLISH);
        Date date=null;
        try {
            date=sdf.parse(dateTimeString);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return patern.format(date);
    }

    public static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "mipmap", c.getPackageName()));
    }
}
