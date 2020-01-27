package tellko.smarthub.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import tellko.smarthub.Entities.ChangePassword;
import tellko.smarthub.Entities.DealerUser;
import tellko.smarthub.Interfaces.AsyncListner;
import tellko.smarthub.R;
import tellko.smarthub.Services.UserService;
import tellko.smarthub.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static tellko.smarthub.Utils.getPasswordValidStatus;
import static tellko.smarthub.Utils.isDeviceOnline;
import static tellko.smarthub.Utils.isPasswordMatch;
import static tellko.smarthub.Utils.isPasswordValid;
import static tellko.smarthub.Utils.navigateWithoutHistory;
import static tellko.smarthub.Utils.showProgressDialog;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText passwordEditText, oldPasswordEditText, confirmPasswordEditText;
    Button signUpButton;
    private static final String TAG = "SmartWarrantyApp";

    TextWatcher oldPasswordTextWatcher, userPasswordTextWatcher, confirmPasswordTextWatcher;
    TextView errorPassword, errorConfirmPassword, errorOldPassword;
    ChangePassword changePassword;
    ImageButton backBtn;
    ImageButton homeBtn;
    TextView titleView;
    ProgressDialog progressDialog;

    String previousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        previousActivity = getIntent().getStringExtra("activityName");
/**
 * Change status bar color programmatically
 */

        Utils.changeStatusBarColor(ChangePasswordActivity.this, getWindow());

        init();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigateWithoutHistory(ChangePasswordActivity.this,AppListActivity.class);

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDeviceOnline(ChangePasswordActivity.this)) {
                    progressDialog = showProgressDialog(ChangePasswordActivity.this);
                    progressDialog.show();
                    signUpButton.setEnabled(false);
                    changePassword(passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString(), oldPasswordEditText.getText().toString());
                } else {
                    Utils.showAlertWithoutTitleDialog(ChangePasswordActivity.this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }


    private void init() {

        changePassword = new ChangePassword();

        passwordEditText = findViewById(R.id.password);
        oldPasswordEditText = findViewById(R.id.oldPassword);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);

        errorConfirmPassword = findViewById(R.id.errorConfirmPasswordLable);
        errorPassword = findViewById(R.id.errorUserPasswordLable);
        errorOldPassword = findViewById(R.id.errorOldPasswordLable);

        backBtn = findViewById(R.id.backBtn);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setVisibility(View.VISIBLE);

        setEditTextBGNormal();

        signUpButton = findViewById(R.id.btnRegister);
        titleView = findViewById(R.id.title_view);

        titleView.setText("Change Password");


        userPasswordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setEditTextBGNormal();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        oldPasswordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setEditTextBGNormal();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        confirmPasswordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setEditTextBGNormal();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        passwordEditText.addTextChangedListener(userPasswordTextWatcher);
        oldPasswordEditText.addTextChangedListener(oldPasswordTextWatcher);
        confirmPasswordEditText.addTextChangedListener(confirmPasswordTextWatcher);
    }

    /**
     * Display error stroke(Red color border) on background
     */
    private void setEditTextBGNormal() {

        confirmPasswordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        passwordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        oldPasswordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));

        errorPassword.setVisibility(View.GONE);
        errorConfirmPassword.setVisibility(View.GONE);
        errorOldPassword.setVisibility(View.GONE);
    }

    /**
     * Change the validation of a password and if it is ok password change
     * @param password
     * @param confirmPassword
     * @param oldPassword
     */
    public void changePassword(String password, String confirmPassword, String oldPassword) {


        if (isPasswordValid(oldPassword) && isPasswordValid(password) && isPasswordMatch(password, confirmPassword)) {

            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            Gson gson = new Gson();

            String dealerString = sharedPref.getString("loggedInUser", "0");
            DealerUser dealerUserMock = gson.fromJson(dealerString, DealerUser.class);

            changePassword.setUsername(dealerUserMock.getUsername());
            changePassword.setOldPassword(oldPassword);
            changePassword.setNewPassword(password);

            new ChangePasswordAsync().execute();

        } else {

            progressDialog.dismiss();
            signUpButton.setEnabled(true);

            if (!isPasswordValid(oldPassword)) {
                errorOldPassword.setVisibility(View.VISIBLE);

                errorOldPassword.setText(getPasswordValidStatus(ChangePasswordActivity.this, oldPassword));
                oldPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }


            if (!isPasswordValid(password)) {
                errorPassword.setVisibility(View.VISIBLE);
                errorPassword.setText(getPasswordValidStatus(ChangePasswordActivity.this, password));
                passwordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));

            }

            if (!password.matches(confirmPassword)) {
                errorConfirmPassword.setVisibility(View.VISIBLE);
                errorConfirmPassword.setText("( New Password do not match. )");
                confirmPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }
        }

    }

    /**
     * Call to the dealer user change password end point using AsyncTask
     */
    private class ChangePasswordAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            UserService.getInstance().changePassword(ChangePasswordActivity.this, changePassword, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Log.d(TAG, jsonObject.toString());
                    progressDialog.dismiss();
                    try {
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");


                        if (success) {
//                            findNavController(getParentFragment().getView())
//                                    .navigate(R.id.nav_home);

                            Utils.showAlertWithoutTitleDialog(context, "Password Changed Successfully.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    signUpButton.setEnabled(true);
                                    if (previousActivity.equals("AppListActivity")) {
                                        navigateWithoutHistory(ChangePasswordActivity.this, AppListActivity.class);
                                    } else {
                                        navigateWithoutHistory(ChangePasswordActivity.this, MainActivity.class);
                                    }
                                }
                            });

                        } else {
                            signUpButton.setEnabled(true);
                            if (message.equals("Provided old password does not match")) {
                                errorOldPassword.setVisibility(View.VISIBLE);
                                errorOldPassword.setText("Current Password incorrect");
                                oldPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                            } else {
                                errorOldPassword.setVisibility(View.VISIBLE);
                                errorOldPassword.setText(message);
                                oldPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                            }

                        }
                    } catch (JSONException e) {
                        signUpButton.setEnabled(true);
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(Context context, String error) {
                    progressDialog.dismiss();

                    Utils.showAlertWithoutTitleDialog(context, error, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            signUpButton.setEnabled(true);

                        }
                    });
                }
            });

            return null;
        }
    }

}
