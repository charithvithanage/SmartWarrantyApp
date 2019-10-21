package com.info.charith.smartwarrantyapp.Activities;

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
import com.info.charith.smartwarrantyapp.Entities.ChangePassword;
import com.info.charith.smartwarrantyapp.Entities.DealerUser;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.UserService;
import com.info.charith.smartwarrantyapp.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.info.charith.smartwarrantyapp.Utils.getPasswordValidStatus;
import static com.info.charith.smartwarrantyapp.Utils.isPasswordMatch;
import static com.info.charith.smartwarrantyapp.Utils.isPasswordValid;
import static com.info.charith.smartwarrantyapp.Utils.navigateWithoutHistory;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
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
                Utils.navigateWithoutHistory(ChangePasswordActivity.this,MainActivity.class);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(passwordEditText.getText().toString(),confirmPasswordEditText.getText().toString(),oldPasswordEditText.getText().toString());
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

    private void setEditTextBGNormal() {

        confirmPasswordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        passwordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        oldPasswordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));

        errorPassword.setVisibility(View.GONE);
        errorConfirmPassword.setVisibility(View.GONE);
        errorOldPassword.setVisibility(View.GONE);
    }

    public void changePassword(String password,String confirmPassword, String oldPassword) {


        if (isPasswordValid(oldPassword)&&isPasswordValid(password) && isPasswordMatch(password, confirmPassword)) {

            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            Gson gson=new Gson();

            String dealerString=sharedPref.getString("loggedInUser","0");
            DealerUser dealerUserMock=gson.fromJson(dealerString, DealerUser.class);

            changePassword.setUsername(dealerUserMock.getUsername());
            changePassword.setOldPassword(oldPassword);
            changePassword.setNewPassword(password);


            new ChangePasswordAsync().execute();

        } else {

            if (!isPasswordValid(oldPassword)) {
                errorOldPassword.setVisibility(View.VISIBLE);

                errorOldPassword.setText(getPasswordValidStatus(ChangePasswordActivity.this,oldPassword));
                oldPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }


            if (!isPasswordValid(password)) {
                errorPassword.setVisibility(View.VISIBLE);
                errorPassword.setText(getPasswordValidStatus(ChangePasswordActivity.this,password));
                passwordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));

            }

            if(!password.matches(confirmPassword)){
                errorConfirmPassword.setVisibility(View.VISIBLE);
                errorConfirmPassword.setText("( New Password do not match. )");
                confirmPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }
        }

    }

    private class ChangePasswordAsync extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ChangePasswordActivity.this);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();
        }

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
                            navigateWithoutHistory(ChangePasswordActivity.this,MainActivity.class);

                        } else {
                            if(message.equals("Provided old password does not match")){
                                errorOldPassword.setVisibility(View.VISIBLE);
                                errorOldPassword.setText("Current Password incorrect");
                                oldPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                            }else {
                                errorOldPassword.setVisibility(View.VISIBLE);
                                errorOldPassword.setText(message);
                                oldPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                            }

                        }
                    } catch (JSONException e) {
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
                        }
                    });
                }
            });

            return null;
        }
    }

}
