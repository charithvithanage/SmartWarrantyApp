package com.info.tellko.smartwarrantyapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.info.tellko.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.tellko.smartwarrantyapp.R;
import com.info.tellko.smartwarrantyapp.Services.DealerService;
import com.info.tellko.smartwarrantyapp.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.info.tellko.smartwarrantyapp.Utils.isDeviceOnline;
import static com.info.tellko.smartwarrantyapp.Utils.isUserNameValid;
import static com.info.tellko.smartwarrantyapp.Utils.showProgressDialog;

public class ForgotPassword extends AppCompatActivity {
    TextView titleView;
    Button btnSend;
    EditText etUsername;
    ImageButton backBtn;
    TextView errorNIC;
    TextWatcher usernameTextWatcher;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        /**
         * Change status bar color programmatically
         */
        Utils.changeStatusBarColor(ForgotPassword.this, getWindow());

        init();

    }

    private void init() {
        titleView = findViewById(R.id.title_view);
        titleView.setText("Forgot Password");
        btnSend = findViewById(R.id.btnRegister);
        etUsername = findViewById(R.id.username);
        backBtn = findViewById(R.id.backBtn);
        errorNIC = findViewById(R.id.errorUsernameLable);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isDeviceOnline(ForgotPassword.this)){
                    if (isUserNameValid(etUsername.getText().toString())) {
                        progressDialog=showProgressDialog(ForgotPassword.this);
                        btnSend.setEnabled(false);
                        new ForgotPaswordAsync().execute();
                    } else {
                        errorNIC.setVisibility(View.VISIBLE);
                        errorNIC.setText(getString(R.string.invalid_username));
                        etUsername.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                    }
                }else {
                    Utils.showAlertWithoutTitleDialog(ForgotPassword.this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        usernameTextWatcher = new TextWatcher() {
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

        etUsername.addTextChangedListener(usernameTextWatcher);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setEditTextBGNormal() {
        etUsername.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        errorNIC.setVisibility(View.GONE);
    }

    private class ForgotPaswordAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            DealerService.getInstance().fogotPassword(ForgotPassword.this, etUsername.getText().toString(), new AsyncListner() {
                @Override
                public void onSuccess(final Context context, final JSONObject jsonObject) {
                    progressDialog.dismiss();
                    try {
                        Boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        if (success) {
                            if (message.equals("Password reset successfull")) {
                                Utils.showAlertWithoutTitleDialog(context, "Password Reset Successfully.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                                                               onBackPressed();

                                    }
                                });
                            } else {
                                Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        onBackPressed();


                                    }
                                });
                            }

                        } else {
                            if (message.equals("No record found for given email.")) {
                                Utils.showAlertWithoutTitleDialog(context, "Invalid Username.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        btnSend.setEnabled(true);
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        btnSend.setEnabled(true);
                                        dialog.dismiss();
                                    }
                                });
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        btnSend.setEnabled(true);

                    }

                }

                @Override
                public void onError(Context context, String error) {
                    progressDialog.dismiss();

                    Utils.showAlertWithoutTitleDialog(context, error, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            btnSend.setEnabled(true);
                        }
                    });
                }
            });
            return null;
        }
    }
}

