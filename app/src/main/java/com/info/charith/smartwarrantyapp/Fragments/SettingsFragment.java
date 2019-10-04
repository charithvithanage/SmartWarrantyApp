package com.info.charith.smartwarrantyapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.Activities.DealerInfoActivity;
import com.info.charith.smartwarrantyapp.Activities.SignUpActivity;
import com.info.charith.smartwarrantyapp.Entities.ChangePassword;
import com.info.charith.smartwarrantyapp.Entities.DealerUserMock;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.UserService;
import com.info.charith.smartwarrantyapp.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.info.charith.smartwarrantyapp.Utils.isPasswordMatch;
import static com.info.charith.smartwarrantyapp.Utils.isPasswordValid;
import static com.info.charith.smartwarrantyapp.Utils.isUserNICValid;
import static com.info.charith.smartwarrantyapp.Utils.isUserNameValid;

public class SettingsFragment extends Fragment {
    EditText usernameEditText, passwordEditText, oldPasswordEditText, confirmPasswordEditText;
    Button signUpButton;
    Gson gson = new Gson();
    private static final String TAG = "SmartWarrantyApp";

    TextWatcher userNameTextWatcher, oldPasswordTextWatcher, userPasswordTextWatcher, confirmPasswordTextWatcher;
    TextView errorUserName, errorPassword, errorConfirmPassword, errorOldPassword;
    ChangePassword changePassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        init(root);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(usernameEditText.getText().toString(),passwordEditText.getText().toString(),confirmPasswordEditText.getText().toString(),oldPasswordEditText.getText().toString());
            }
        });

        return root;
    }

    private void init(View view) {

        changePassword = new ChangePassword();


        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        oldPasswordEditText = view.findViewById(R.id.oldPassword);
        confirmPasswordEditText = view.findViewById(R.id.confirmPassword);

        errorConfirmPassword = view.findViewById(R.id.errorConfirmPasswordLable);
        errorPassword = view.findViewById(R.id.errorUserPasswordLable);
        errorOldPassword = view.findViewById(R.id.errorOldPasswordLable);
        errorUserName = view.findViewById(R.id.errorUserNameLable);

        setEditTextBGNormal();

        signUpButton = view.findViewById(R.id.btnRegister);

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

        usernameEditText.addTextChangedListener(userNameTextWatcher);
        passwordEditText.addTextChangedListener(userPasswordTextWatcher);
        oldPasswordEditText.addTextChangedListener(oldPasswordTextWatcher);
        confirmPasswordEditText.addTextChangedListener(confirmPasswordTextWatcher);
    }

    private void setEditTextBGNormal() {

        confirmPasswordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        usernameEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        passwordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        oldPasswordEditText.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));

        errorPassword.setVisibility(View.GONE);
        errorConfirmPassword.setVisibility(View.GONE);
        errorUserName.setVisibility(View.GONE);
        errorOldPassword.setVisibility(View.GONE);
    }

    public void changePassword(String username, String password,String confirmPassword, String oldPassword) {

        if (isPasswordValid(oldPassword)&&isPasswordValid(password) && isPasswordMatch(password, confirmPassword) && isUserNameValid(username)) {

            if (password.length() > 5) {
                changePassword.setUsername(username);
                changePassword.setOldPassword(oldPassword);
                changePassword.setNewPassword(password);


                new ChangePasswordAsync().execute();
            } else {
                errorPassword.setVisibility(View.VISIBLE);
                errorPassword.setText(getString(R.string.password_length_wrong));
                passwordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }


        } else {

            if (!isPasswordValid(oldPassword)) {
                errorOldPassword.setVisibility(View.VISIBLE);
                errorOldPassword.setText(getString(R.string.invalid_password));
                oldPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }

            if (!isUserNameValid(username)) {
                errorUserName.setVisibility(View.VISIBLE);
                errorUserName.setText(getString(R.string.invalid_username));
                usernameEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));


            }

            if (!isPasswordValid(password)) {
                errorPassword.setVisibility(View.VISIBLE);
                errorPassword.setText(getString(R.string.invalid_user_password));
                passwordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));

            }

            if (!isPasswordMatch(password, confirmPassword)) {
                errorConfirmPassword.setVisibility(View.VISIBLE);
                errorConfirmPassword.setText(getString(R.string.invalid_user_password_not_match));
                confirmPasswordEditText.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
            }
        }

    }

    private class ChangePasswordAsync extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            UserService.getInstance().changePassword(getActivity(), changePassword, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Log.d(TAG, jsonObject.toString());
                    progressDialog.dismiss();
                    try {
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");


                        if (success) {
                            getActivity().onBackPressed();

                        } else {
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
