package com.info.charith.smartwarrantyapp.Activities;

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

import com.info.charith.smartwarrantyapp.Entities.Credential;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.UserService;
import com.info.charith.smartwarrantyapp.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.info.charith.smartwarrantyapp.Utils.isPasswordValid;
import static com.info.charith.smartwarrantyapp.Utils.isUserNameValid;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton;
    TextView signUpButton;
    Credential credential;
    TextView errorUserName, errorPassword;
    TextWatcher userNameTextWatcher,  userPasswordTextWatcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

    private void setEditTextBGNormal() {
        usernameEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        passwordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));

        errorPassword.setVisibility(View.GONE);
        errorUserName.setVisibility(View.GONE);
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        if (isUserNameValid(username) && isPasswordValid(password)) {

            if(password.length()>5){
                credential.setUsername(username);
                credential.setPassword(password);
                new LoginUserAsync().execute();
            }else {
                errorPassword.setVisibility(View.VISIBLE);
                errorPassword.setText(getString(R.string.password_length_wrong));
                passwordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }

        } else {
            if (!isUserNameValid(username)) {
                errorUserName.setVisibility(View.VISIBLE);
                errorUserName.setText(getString(R.string.invalid_username));
                usernameEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }

            if (!isPasswordValid(password)) {
                errorPassword.setVisibility(View.VISIBLE);
                errorPassword.setText(getString(R.string.invalid_password));
                passwordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));

            }
        }


    }

    private class LoginUserAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            UserService.getInstance().loginUser(LoginActivity.this, credential, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {

                    try {
                        boolean success = jsonObject.getBoolean("success");

                        if(success){
                            String loggedInUser = null;
                            String userDealer=null;
                            String accessToken = null;
                            String refreshToken = null;
                            try {
                                loggedInUser = jsonObject.getString("objectOne");
                                accessToken = jsonObject.getString("accessToken");
                                refreshToken = jsonObject.getString("refreshToken");
                                userDealer = jsonObject.getString("objectTwo");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            SharedPreferences sharedPref = context.getSharedPreferences(
                                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("loggedInUser", loggedInUser);
                            editor.putString("accessToken", accessToken);
                            editor.putString("refreshToken", refreshToken);
                            editor.putString("userDealer", userDealer);
                            editor.commit();

                            Utils.navigateWithoutHistory(LoginActivity.this, MainActivity.class);
                        }else {
                            String message=jsonObject.getString("message");

                            Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(Context context, String error) {
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
