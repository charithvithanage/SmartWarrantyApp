package com.info.charith.smartwarrantyapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.AsyncTasks.GetDealerAsync;
import com.info.charith.smartwarrantyapp.AsyncTasks.GetProductsAsync;
import com.info.charith.smartwarrantyapp.Config;
import com.info.charith.smartwarrantyapp.Entities.Credential;
import com.info.charith.smartwarrantyapp.Entities.Dealer;
import com.info.charith.smartwarrantyapp.Entities.DealerUser;
import com.info.charith.smartwarrantyapp.Entities.Product;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.UserService;
import com.info.charith.smartwarrantyapp.Utils;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.info.charith.smartwarrantyapp.Utils.dateTimeToString;
import static com.info.charith.smartwarrantyapp.Utils.endOfDay;
import static com.info.charith.smartwarrantyapp.Utils.getPasswordValidStatus;
import static com.info.charith.smartwarrantyapp.Utils.isPasswordValid;
import static com.info.charith.smartwarrantyapp.Utils.isUserNameValid;
import static com.info.charith.smartwarrantyapp.Utils.sortProductList;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton;
    TextView signUpButton;
    Credential credential;
    TextView errorUserName, errorPassword, forgotPasswordLink;
    TextWatcher userNameTextWatcher, userPasswordTextWatcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /**
         * Change status bar color programmatically
         */
        Utils.changeStatusBarColor(LoginActivity.this, getWindow());

        init();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
                login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, DealerSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        credential = new Credential();
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        signUpButton = findViewById(R.id.btnRegister);
        errorUserName = findViewById(R.id.errorUserNameLable);
        errorPassword = findViewById(R.id.errorPasswordLable);
        forgotPasswordLink = findViewById(R.id.fogotPasswordLink);

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

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

        passwordEditText.addTextChangedListener(userPasswordTextWatcher);
        usernameEditText.addTextChangedListener(userNameTextWatcher);
    }

    /**
     * Set edit text background to normal state
     */
    private void setEditTextBGNormal() {
        usernameEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        passwordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));

        errorPassword.setVisibility(View.GONE);
        errorUserName.setVisibility(View.GONE);
    }

    /**
     * Login method
     *
     * @param username
     * @param password
     */
    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        if (isUserNameValid(username) && isPasswordValid(password)) {

            /**
             * If password length is greater than 5
             * User can login
             */
            credential.setUsername(username);
            credential.setPassword(password);
            new LoginUserAsync().execute();

        } else {
            if (!isUserNameValid(username)) {
                errorUserName.setVisibility(View.VISIBLE);
                errorUserName.setText(getString(R.string.invalid_username));
                usernameEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }

            if (!isPasswordValid(password)) {
                errorPassword.setVisibility(View.VISIBLE);
                errorPassword.setText(getPasswordValidStatus(LoginActivity.this, password));
                passwordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));

            }
        }


    }

    /**
     * User login to the server
     */
    private class LoginUserAsync extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        Dealer dealer;
        Gson gson;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            UserService.getInstance().loginUser(LoginActivity.this, credential, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {

                    progressDialog.dismiss();

                    try {
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {
                            String loggedInUser = null;
                            String userDealer = null;
                            String accessToken = null;
                            String refreshToken = null;
                            DateTime dateTime = new DateTime();
                            try {
                                loggedInUser = jsonObject.getString("objectOne");
                                accessToken = jsonObject.getString("accessToken");
                                refreshToken = jsonObject.getString("refreshToken");
                                userDealer = jsonObject.getString("objectTwo");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            /**
                             * Set logged in user ,access token ,refresh token, dealer, and automatically logout time in shared preference
                             */
                            SharedPreferences sharedPref = context.getSharedPreferences(
                                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("loggedInUser", loggedInUser);
                            editor.putString("accessToken", accessToken);
                            editor.putString("refreshToken", refreshToken);
                            editor.putString("userDealer", userDealer);
                            editor.putString("logoutTime", dateTimeToString(endOfDay(dateTime)));

                            editor.commit();

                            gson = new Gson();
                            DealerUser dealerUserMock = gson.fromJson(loggedInUser, DealerUser.class);
                            dealer = gson.fromJson(userDealer, Dealer.class);

                            new GetDealerAsync(LoginActivity.this, dealerUserMock.getDealerCode(), new AsyncListner() {
                                @Override

                                public void onSuccess(Context context, JSONObject jsonObject) {
                                    String objectOne = null;

                                    try {
                                        boolean success = jsonObject.getBoolean("success");
                                        String message = jsonObject.getString("message");

                                        if (success) {
                                            objectOne = jsonObject.getString("object");

                                            dealer = gson.fromJson(objectOne, Dealer.class);

                                            SharedPreferences sharedPref = context.getSharedPreferences(
                                                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("userDealer", gson.toJson(dealer));
                                            editor.commit();
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    new GetProductsAsync(LoginActivity.this, dealer.getDealerCode(), new AsyncListner() {
                                        @Override
                                        public void onSuccess(Context context, JSONObject jsonObject) {
                                            String object = null;

                                            try {
                                                object = jsonObject.getString("object");

                                                JSONArray jsonArray = new JSONArray(object);
                                                List<Product> brands = new ArrayList<>();


                                                if (jsonArray.length() > 0) {
                                                    brands = new ArrayList<>();

                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        Product dealer = gson.fromJson(jsonArray.getString(i), Product.class);
                                                        brands.add(dealer);
                                                    }
                                                }


                                                SharedPreferences sharedPref = context.getSharedPreferences(
                                                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString("enabledBrands", object);
                                                editor.commit();
                                                Config.Instance.setEnabledBrands(sortProductList(brands));
                                                Utils.navigateWithoutHistory(context, MainActivity.class);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(Context context, String error) {

                                        }
                                    }).execute();


                                }

                                @Override
                                public void onError(Context context, String error) {
                                }
                            }).execute();

                        } else {
                            String message = jsonObject.getString("message");

                            if(message.equals("Invalid Credentials or Inactive User.")){
                                Utils.showAlertWithoutTitleDialog(context, "Invalid Credentials", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                            }else {
                                Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(Context context, String error) {
                    progressDialog.dismiss();

                    Utils.showAlertWithoutTitleDialog(context, getString(R.string.server_error), new DialogInterface.OnClickListener() {
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

//    private class GetProductsAsync extends AsyncTask<Void, Void, Void> {
//
//        String dealerCode;
//        Gson gson = new Gson();
//
//        public GetProductsAsync(String dealerCode) {
//            this.dealerCode = dealerCode;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            DealerService.getInstance().getProductList(LoginActivity.this, dealerCode, new AsyncListner() {
//                @Override
//                public void onSuccess(Context context, JSONObject jsonObject) {
//                    String object = null;
//
//                    try {
//                        object = jsonObject.getString("object");
//
//                        JSONArray jsonArray = new JSONArray(object);
//                        List<Product> brands = new ArrayList<>();
//
//
//                        if (jsonArray.length() > 0) {
//                            brands = new ArrayList<>();
//
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                Product dealer = gson.fromJson(jsonArray.getString(i), Product.class);
//                                brands.add(dealer);
//                            }
//                        }
//
//
//                        SharedPreferences sharedPref = context.getSharedPreferences(
//                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putString("enabledBrands", object);
//                        editor.commit();
//                        Config.Instance.setEnabledBrands(brands);
//                        Utils.navigateWithoutHistory(LoginActivity.this, MainActivity.class);
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void onError(Context context, String error) {
//
//                }
//            });
//            return null;
//        }
//    }

}
