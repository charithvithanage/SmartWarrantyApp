package com.info.charith.smartwarrantyapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.Entities.Dealer;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.UserService;
import com.info.charith.smartwarrantyapp.Utils;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static com.info.charith.smartwarrantyapp.Utils.dateTimeToString;
import static com.info.charith.smartwarrantyapp.Utils.endOfDay;

public class DealerInfoActivity extends AppCompatActivity {
    private static final String TAG = "SmartWarrantyApp";

    Button btnConfirm;
    String dealerUserMockString;
    String dealerString;
    Dealer dealer;

    Gson gson = new Gson();
    TextView tvDealerCode, tvDealerName, tvAddress, tvCity, tvDistric, tvOwnerName, tvNIC, tvContactNo, tvEmail, tvDealerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_info);

        dealerUserMockString = getIntent().getStringExtra("dealerUserMockString");
        dealerString = getIntent().getStringExtra("dealerString");

        dealer = gson.fromJson(dealerString, Dealer.class);

        init();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * If dealer details are correct
                 * User can register by clicking confirm button
                 */
                new RegisterAsync().execute();
            }
        });
    }

    private void init() {
        btnConfirm = findViewById(R.id.btnRegister);
        tvDealerCode = findViewById(R.id.dealerCode);
        tvDealerName = findViewById(R.id.dealerName);
        tvAddress = findViewById(R.id.address);
        tvCity = findViewById(R.id.city);
        tvDistric = findViewById(R.id.distric);
        tvOwnerName = findViewById(R.id.ownerName);
        tvNIC = findViewById(R.id.nicNo);
        tvContactNo = findViewById(R.id.contactNo);
        tvEmail = findViewById(R.id.email);
        tvDealerStatus = findViewById(R.id.dealerStatus);

        setValues();
    }

    private void setValues() {
        tvDealerCode.setText(dealer.getDealerCode());
        tvDealerName.setText(dealer.getDealerName());
        tvAddress.setText(dealer.getAddress());
        tvCity.setText(dealer.getCity());
        tvDistric.setText(dealer.getDistrict());
        tvOwnerName.setText(dealer.getOwnerName());
        tvNIC.setText(dealer.getNic());
        tvContactNo.setText(dealer.getContactNo());
        tvEmail.setText(dealer.getEmail());
        tvDealerStatus.setText(checkStatus(dealer.isActive()));
    }

    /**
     * Check the dealer active status
     * @param active Dealer active status
     * @return Active / Inactive string
     */
    private String checkStatus(boolean active) {

        String status = "Active";

        if (!active) {
            status = "Inactive";
        }
        return status;
    }

    private class RegisterAsync extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DealerInfoActivity.this);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            UserService.getInstance().registerUser(DealerInfoActivity.this, dealerUserMockString, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Log.d(TAG, jsonObject.toString());

                    progressDialog.dismiss();
                    String loggedInUser;
                    String accessToken;
                    String refreshToken;
                    String userDealer;


                    try {


                        boolean success = jsonObject.getBoolean("success");

                        if (success) {

                            DateTime dateTime=new DateTime();
                            loggedInUser = jsonObject.getString("objectOne");
                            accessToken = jsonObject.getString("accessToken");
                            refreshToken = jsonObject.getString("refreshToken");
                            userDealer = jsonObject.getString("objectTwo");

                            SharedPreferences sharedPref = context.getSharedPreferences(
                                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("loggedInUser", loggedInUser);
                            editor.putString("accessToken", accessToken);
                            editor.putString("refreshToken", refreshToken);
                            editor.putString("userDealer", userDealer);
                            editor.putString("logoutTime",dateTimeToString(endOfDay(dateTime)));

                            editor.commit();


                            Utils.navigateWithoutHistory(DealerInfoActivity.this, MainActivity.class);
                        }else {
                            String message = jsonObject.getString("message");

                            Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }


                    } catch (JSONException e) {
                        Utils.showAlertWithoutTitleDialog(context, e.toString(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
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
