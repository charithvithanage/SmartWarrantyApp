package com.info.tellko.smartwarrantyapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.info.tellko.smartwarrantyapp.Entities.Dealer;
import com.info.tellko.smartwarrantyapp.Entities.DealerUserMock;
import com.info.tellko.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.tellko.smartwarrantyapp.R;
import com.info.tellko.smartwarrantyapp.Services.UserService;
import com.info.tellko.smartwarrantyapp.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.info.tellko.smartwarrantyapp.Utils.getPasswordValidStatus;
import static com.info.tellko.smartwarrantyapp.Utils.isDeviceOnline;
import static com.info.tellko.smartwarrantyapp.Utils.isPasswordMatch;
import static com.info.tellko.smartwarrantyapp.Utils.isPasswordValid;
import static com.info.tellko.smartwarrantyapp.Utils.isUserNICValid;
import static com.info.tellko.smartwarrantyapp.Utils.isUserNameValid;
import static com.info.tellko.smartwarrantyapp.Utils.showAlertWithoutTitleDialog;
import static com.info.tellko.smartwarrantyapp.Utils.showProgressDialog;

public class SignUpActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText, userNICEditText, confirmPasswordEditText;
    Button signUpButton;
    TextView loginButton;
    DealerUserMock dealerUserMock;
    String dealerString;
    Dealer dealer;
    Gson gson = new Gson();
    private static final String TAG = "SmartWarrantyApp";

    TextWatcher userNameTextWatcher, userNICTextWatcher, userPasswordTextWatcher, confirmPasswordTextWatcher;
    TextView errorUserName, errorPassword, errorConfirmPassword, errorNIC;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        /**
         * Change status bar color programmatically
         */
        Utils.changeStatusBarColor(SignUpActivity.this, getWindow());

        dealerString = getIntent().getStringExtra("dealerString");
        dealer = gson.fromJson(dealerString, Dealer.class);

        init();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isDeviceOnline(SignUpActivity.this)){
                    if(dealer.isActive()){
                        progressDialog=showProgressDialog(SignUpActivity.this);
                        progressDialog.show();
                        signUpButton.setEnabled(false);
                        signup(userNICEditText.getText().toString(), usernameEditText.getText().toString(),
                                passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString());
                    }else {
                        showAlertWithoutTitleDialog(SignUpActivity.this, getString(R.string.dealer_not_active), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                onBackPressed();
                            }
                        });
                    }
                }else {
                    Utils.showAlertWithoutTitleDialog(SignUpActivity.this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }



            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigateWithoutHistory(SignUpActivity.this, LoginActivity.class);
            }
        });

        userNICEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus){
                    if (usernameEditText.hasFocus()) {
                        if (!isUserNameValid(usernameEditText.getText().toString())) {
                            errorUserName.setVisibility(View.VISIBLE);
                            errorUserName.setText(getString(R.string.invalid_username));
                            usernameEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                        }
                    }
                }

            }
        });

        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!isUserNICValid(userNICEditText.getText().toString())) {
                        errorNIC.setVisibility(View.VISIBLE);
                        errorNIC.setText(getString(R.string.invalid_user_nic));
                        userNICEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                    }
                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!isUserNICValid(userNICEditText.getText().toString())) {
                        errorNIC.setVisibility(View.VISIBLE);
                        errorNIC.setText(getString(R.string.invalid_user_nic));
                        userNICEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                    }

                    if (!isUserNameValid(usernameEditText.getText().toString())) {
                        errorUserName.setVisibility(View.VISIBLE);
                        errorUserName.setText(getString(R.string.invalid_username));
                        usernameEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                    }
                }
            }
        });


    }

    private void init() {

        dealerUserMock = new DealerUserMock();


        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        userNICEditText = findViewById(R.id.userNIC);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);

        errorConfirmPassword = findViewById(R.id.errorConfirmPasswordLable);
        errorPassword = findViewById(R.id.errorUserPasswordLable);
        errorNIC = findViewById(R.id.errorUserNICLable);
        errorUserName = findViewById(R.id.errorUserNameLable);

        setEditTextBGNormal();

        signUpButton = findViewById(R.id.btnRegister);
        loginButton = findViewById(R.id.signIn);

        userNameTextWatcher = new TextWatcher() {
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

        userNICTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setEditTextBGNormal();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s=et.toString();
                if(!s.equals(s.toUpperCase()))
                {
                    s=s.toUpperCase();
                    userNICEditText.setText(s);
                    userNICEditText.setSelection(userNICEditText.length()); //fix reverse texting
                }
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

        usernameEditText.addTextChangedListener(userNameTextWatcher);
        passwordEditText.addTextChangedListener(userPasswordTextWatcher);
        userNICEditText.addTextChangedListener(userNICTextWatcher);
        confirmPasswordEditText.addTextChangedListener(confirmPasswordTextWatcher);
    }

    private void setEditTextBGNormal() {

        confirmPasswordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        usernameEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        passwordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        userNICEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));

        errorPassword.setVisibility(View.GONE);
        errorConfirmPassword.setVisibility(View.GONE);
        errorUserName.setVisibility(View.GONE);
        errorNIC.setVisibility(View.GONE);
    }

    public void signup(String userNIC, String username, String password, String confirmPassword) {

        if (isUserNICValid(userNIC) && isPasswordValid(password) && isPasswordMatch(password, confirmPassword) && isUserNameValid(username)) {
            dealerUserMock.setDealerCode(dealer.getDealerCode());
            dealerUserMock.setNic(userNIC);
            dealerUserMock.setUsername(username);
            dealerUserMock.setPassword(password);

            new ConfirmRegistrationDataAsync().execute();

        } else {

            progressDialog.dismiss();
            signUpButton.setEnabled(true);

            if (!isUserNICValid(userNIC)) {
                errorNIC.setVisibility(View.VISIBLE);
                errorNIC.setText(getString(R.string.invalid_user_nic));
                userNICEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }


            if (!isUserNameValid(username)) {
                errorUserName.setVisibility(View.VISIBLE);
                errorUserName.setText(getString(R.string.invalid_username));
                usernameEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }


            if (!isPasswordValid(password)) {
                errorPassword.setVisibility(View.VISIBLE);
                errorPassword.setText(getPasswordValidStatus(SignUpActivity.this, password));
                passwordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));

            }

            if (!isPasswordMatch(password, confirmPassword)) {
                errorConfirmPassword.setVisibility(View.VISIBLE);
                errorConfirmPassword.setText(getString(R.string.invalid_user_password_not_match));
                confirmPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }
        }

    }


    private class ConfirmRegistrationDataAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            UserService.getInstance().confirmRegistrationData(SignUpActivity.this, dealerUserMock, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Log.d(TAG, jsonObject.toString());
                    progressDialog.dismiss();
                    try {
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");


                        if (success) {
                            Intent intent = new Intent(SignUpActivity.this, DealerInfoActivity.class);
                            intent.putExtra("dealerString", jsonObject.getString("dealer"));
                            intent.putExtra("dealerUserMockString", jsonObject.getString("dealerUserMock"));
                                                   startActivity(intent);
                        } else {
                            Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    signUpButton.setEnabled(true);
                                    dialog.dismiss();

                                }
                            });
                        }
                    } catch (JSONException e) {
                        signUpButton.setEnabled(true);
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(Context context, String error) {
                    progressDialog.dismiss();

                    Utils.showAlertWithoutTitleDialog(context, getString(R.string.server_error), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            signUpButton.setEnabled(true);
                            dialog.dismiss();
                        }
                    });
                }
            });

            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        signUpButton.setEnabled(true);
    }
}


