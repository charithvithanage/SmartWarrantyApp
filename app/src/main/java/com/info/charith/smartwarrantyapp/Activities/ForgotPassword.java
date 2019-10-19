package com.info.charith.smartwarrantyapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.DealerService;
import com.info.charith.smartwarrantyapp.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.info.charith.smartwarrantyapp.Utils.isUserNameValid;

public class ForgotPassword extends AppCompatActivity {
    TextView titleView;
    Button btnSend;
    EditText etUsername;
    ImageButton backBtn;
    TextView errorNIC;
    TextWatcher usernameTextWatcher;

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

                if (isUserNameValid(etUsername.getText().toString())) {
                    new ForgotPaswordAsync().execute();
                } else {
                    errorNIC.setVisibility(View.VISIBLE);
                    errorNIC.setText(getString(R.string.invalid_username));
                    etUsername.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
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

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ForgotPassword.this);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

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
                            if (message.equals("No record found for given email.")) {
                                Utils.showAlertWithoutTitleDialog(context, "No record found for the given Username", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Utils.navigateWithoutHistory(ForgotPassword.this, LoginActivity.class);

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

