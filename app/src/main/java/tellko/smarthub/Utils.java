package tellko.smarthub;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;
import android.view.ContextThemeWrapper;
import android.view.Window;
import android.view.WindowManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tellko.smarthub.Entities.Product;
import tellko.smarthub.Entities.Warranty;

public class Utils {

    /**
     * Change status bar color
     *
     * @param context Context of the activity
     * @param window
     */
    public static void changeStatusBarColor(Context context, Window window) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /**
     * A placeholder password validation check
     *
     * @param password Password of the user
     * @return true or false
     */
    public static String getPasswordValidStatus(Context context, String password) {

        String validStatus = null;

        if (password == null) {
            validStatus = context.getString(R.string.password_empty);
        } else {

            if (password.length() > 5 && password.matches(Config.Instance.passwordPattern)) {
                validStatus = "Valid";
            } else {
                if (!password.matches(Config.Instance.passwordPattern)) {
                    validStatus = context.getString(R.string.password_pattern_wrong);
                } else {
                    if (password.length() < 6) {
                        validStatus = context.getString(R.string.password_length_wrong);
                    }
                }
            }
        }

        return validStatus;
    }

    public static boolean isPasswordValid(String password) {

        boolean valid = false;

        if (password == null) {
            valid = false;
        } else {

            if (password.length() > 5 && password.matches(Config.Instance.passwordPattern)) {
                valid = true;
            } else {
                if (!password.matches(Config.Instance.passwordPattern)) {
                    valid = false;
                } else {
                    if (password.length() < 6) {
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }

    /**
     * A placeholder username validation check
     *
     * @param username Username of the user
     * @return true or false
     */
    public static boolean isUserNameValid(String username) {
        Boolean valid;
        if (username == null) {
            valid = false;
        } else {

            if (username.length() > 2) {
                valid = true;
            } else {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * A placeholder user nic validation check
     *
     * @param userNIC NIC of the user
     * @return true or false
     */
    public static boolean isUserNICValid(String userNIC) {
        Boolean valid = null;
        if (userNIC == null) {
            valid = false;
        } else {

            if (userNIC.length() == 12) {

                if (!userNIC.matches("[0-9]+")) {
                    valid = false;
                } else {
                    valid = true;
                }

            } else if (userNIC.length() == 10) {

                String first9Characters = userNIC.substring(0, 9);

                String lastCharacter = userNIC.substring(userNIC.length() - 1);
                if (first9Characters.matches("[0-9]+")) {
                    if (lastCharacter.equals("V")) {
                        valid = true;
                    } else if (lastCharacter.equals("X")) {
                        valid = true;
                    } else {
                        valid = false;
                    }
                } else {
                    valid = false;
                }


            } else {
                valid = false;

            }
        }
        return valid;
    }

    public static String getUserNICValidStatus(String userNIC) {
        String valid = null;
        if (userNIC == null) {
            valid = "Empty User NIC";
        } else {

            if (userNIC.length() == 12) {

                if (!userNIC.matches("[0-9]+")) {
                    valid = "Invalid NIC";
                } else {
                    valid = "Valid";
                }

            } else if (userNIC.length() == 10) {

                String lastCharacter = userNIC.substring(userNIC.length() - 1);
                if (lastCharacter.toLowerCase().equals("v")) {
                    valid = "Valid";
                } else if (lastCharacter.toLowerCase().equals("x")) {
                    valid = "Valid";
                } else {
                    valid = "Invalid NIC";
                }

            } else {
                valid = "Invalid NIC";
            }
        }
        return valid;
    }

    /**
     * Check whether the password mathes
     *
     * @param password        User password
     * @param confirmPassword Confirmation of the given password
     * @return
     */
    public static boolean isPasswordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    /**
     * Navigate to another activity without navigation history
     *
     * @param context  Context of the current activity
     * @param activity Context of the second activity
     */
    public static void navigateWithoutHistory(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void navigateToAnotherActivity(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    /**
     * Display alert dialog without title
     *
     * @param context  Context of the activity
     * @param message  String of the messsage body
     * @param listener OK button click event listner
     */
    public static void showAlertWithoutTitleDialog(Context context, String message, final DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Whatever...
                        listener.onClick(dialog, which);
                    }
                }).show();
    }

    /**
     * Returns an Image drawable by the given brand name
     *
     * @param c         Context of the activity
     * @param ImageName Image name
     * @return Drawable
     */
    public static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "mipmap", c.getPackageName()));
    }

    /**
     * First letter of the every word convert to UpperCase
     *
     * @param str Title string
     * @return Capitalized string
     */
    public static String stringCapitalize(String str) {
        StringBuilder result = new StringBuilder(str.length());
        String words[] = str.split("\\ ");
        for (int i = 0; i < words.length; i++) {
            result.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1)).append(" ");

        }

        return result.toString();

    }

    /**
     * Set date time to 23.50
     *
     * @param date selected date time
     * @return end of the day time
     */
    public static DateTime endOfDay(DateTime date) {

        return date.withTimeAtStartOfDay().plusDays(1).minusMinutes(1);
//        return date.plusMinutes(1);

    }

    public static String dateTimeToString(DateTime dateTime) {
        String str = null;
        if (dateTime != null) {
            str = dateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        }
        return str;
    }


    public static DateTime dateStringToDateTime(String dateTime) {
        DateTime date = null;

        if (dateTime != null) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .withLocale(Locale.getDefault());
            date = fmt.parseDateTime(dateTime);
        }

        return date;
    }


    /**
     * Sort acsending order product list by the priority
     *
     * @param lsit
     * @return sorted list
     */
    public static List<Product> sortProductList(List<Product> lsit) {

        Collections.sort(lsit, new Comparator<Product>() {
            public int compare(Product obj1, Product obj2) {
                // ## Ascending order
                return obj1.getPriority().compareTo(obj2.getPriority()); // To compare string values
            }
        });
        return lsit;
    }

    public static String capEachWord(String source) {
        String result = "";
        String[] splitString = source.split(" ");
        for (String target : splitString) {
            result += Character.toUpperCase(target.charAt(0)) + target.substring(1) + " ";
        }
        return result;
    }

    /**
     * Convert first letter of each an every word of a sentence
     *
     * @param capString String to be capitalized
     * @return formatted string
     */
    public static String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.waiting));
        return progressDialog;
    }


    public static String getActivationTime(String activationTime) {

        if (activationTime.length() == 5) {
            activationTime = activationTime.substring(0, 5) + ":00";
        } else {
            activationTime = activationTime.substring(0, 8);

        }
        return activationTime;
    }

    public static DateTime stringToDateTime(String date, String time) {
        String dateTimeString = date + " " + time;
        DateTime dateTime = DateTime.parse(dateTimeString, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));

        return dateTime;
    }

    /**
     * Sort activity report by the activation date
     *
     * @param lsit
     * @return sorted list
     */
    public static List<Warranty> sortWarrantyList(List<Warranty> lsit) {

        Collections.sort(lsit, new Comparator<Warranty>() {
            public int compare(Warranty obj1, Warranty obj2) {
                // ## Ascending order
                return (stringToDateTime(obj2.getWarrantyActivatedDate(), getActivationTime(obj2.getActivationTime()))).compareTo(stringToDateTime(obj1.getWarrantyActivatedDate(), getActivationTime(obj1.getActivationTime()))); // To compare string values
            }
        });
        return lsit;
    }

    public static String convertImageToBase64(Context context, Integer imgRes) {
        //encode image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgRes);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static Bitmap convertBase64ToBitMap(String base64String) {
        byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
