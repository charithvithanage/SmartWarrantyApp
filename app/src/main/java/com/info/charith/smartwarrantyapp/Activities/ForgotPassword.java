package com.info.charith.smartwarrantyapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ForgotPassword extends AppCompatActivity {
    TextView titleView;
    Button btnSend;
    EditText etUsername;
    ImageButton backBtn;

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

    private void init(){
        titleView = findViewById(R.id.title_view);
        titleView.setText("Forgot Password");
        btnSend = findViewById(R.id.btnRegister);
        etUsername = findViewById(R.id.username);
        backBtn=findViewById(R.id.backBtn);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(etUsername.getText())){
                    new ForgotPaswordAsync().execute();
                }else {
                    Utils.showAlertWithoutTitleDialog(ForgotPassword.this, getString(R.string.invalid_username), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }


            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private class ForgotPaswordAsync extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DealerService.getInstance().fogotPassword(ForgotPassword.this, etUsername.getText().toString(), new AsyncListner() {
                @Override
                public void onSuccess(final Context context, final JSONObject jsonObject) {
                    try {
                        Boolean success=jsonObject.getBoolean("success");
                        String message=jsonObject.getString("message");

                        if(success){
                            Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Utils.navigateWithoutHistory(ForgotPassword.this,LoginActivity.class);

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

