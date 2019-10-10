package com.info.charith.smartwarrantyapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.Entities.Dealer;
import com.info.charith.smartwarrantyapp.Entities.DealerUserMock;
import com.info.charith.smartwarrantyapp.Entities.Warranty;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.UserService;
import com.info.charith.smartwarrantyapp.Utils;

import org.json.JSONObject;

public class DeivceInfoActivity extends AppCompatActivity {
    private static final String TAG = "SmartWarrantyApp";

    String previous_activity;
    String type;
    Button btnSubmit;

    TextView tvBrand, tvModel, tvIMEI, tvDealerName, tvCity, tvDistric;
    TextView tvCustomerName, tvCustomerAddress, tvCustomerContactNo, tvCustomerEmail;
    TextView tvFer, tvActivatedDate, tvAccessoryWStatus, tvDeviceWStatus, tvServiceWStatus;
    String warrantyString, dealerString;
    Warranty warranty;
    Dealer dealer;

    Gson gson = new Gson();

    TextView titleView;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deivce_info);


        Utils.changeStatusBarColor(DeivceInfoActivity.this, getWindow());

        warrantyString = getIntent().getStringExtra("warrantyString");
        dealerString = getIntent().getStringExtra("dealerString");
        type = getIntent().getStringExtra("type");
        previous_activity = getIntent().getStringExtra("previous_activity");

        warranty = gson.fromJson(warrantyString, Warranty.class);
        dealer = gson.fromJson(dealerString, Dealer.class);

        init();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equals("sold device")) {
                    Utils.navigateWithoutHistory(DeivceInfoActivity.this, MainActivity.class);
                } else {

                    if (warranty.getActivationStatus().equals("Enable") || warranty.getActivationStatus().equals("Enable with Date")) {

                        SharedPreferences sharedPref = getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

                        Gson gson = new Gson();

                        String dealerString = sharedPref.getString("loggedInUser", "0");
                        DealerUserMock dealerUserMock = gson.fromJson(dealerString, DealerUserMock.class);

                        warranty.setDealerCode(dealer.getDealerCode());
                        warranty.setDealerUserName(dealerUserMock.getUsername());
                    }

                    new UpdateWarrantyAsync().execute();
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


    private void init() {
        titleView = findViewById(R.id.title_view);
        backBtn = findViewById(R.id.backBtn);

        btnSubmit = findViewById(R.id.btnSubmit);
        tvBrand = findViewById(R.id.brand);
        tvModel = findViewById(R.id.model);
        tvIMEI = findViewById(R.id.imei);
        tvDealerName = findViewById(R.id.dealerName);
        tvCity = findViewById(R.id.city);
        tvDistric = findViewById(R.id.distric);
        tvCustomerAddress = findViewById(R.id.customerAddress);
        tvCustomerContactNo = findViewById(R.id.customerContactNo);
        tvCustomerEmail = findViewById(R.id.customerEmail);
        tvCustomerName = findViewById(R.id.customerName);

        tvFer = findViewById(R.id.tvRef);
        tvActivatedDate = findViewById(R.id.tvDate);
        tvAccessoryWStatus = findViewById(R.id.tvAccessoryWStatus);
        tvDeviceWStatus = findViewById(R.id.tvDeviceWStatus);
        tvServiceWStatus = findViewById(R.id.tvServiceWStatus);

        titleView.setText(Utils.stringCapitalize(type));

        setValues();

        /**
         * This is a common ui for the device information
         * Check previous activity
         * Hide relevant ui elements
         */
        if (previous_activity.equals("activation_list_activity")) {
            btnSubmit.setVisibility(View.GONE);
            tvDistric.setVisibility(View.GONE);
            tvCity.setVisibility(View.GONE);
            tvDealerName.setVisibility(View.GONE);
            findViewById(R.id.textView15).setVisibility(View.GONE);
            findViewById(R.id.textView17).setVisibility(View.GONE);
            findViewById(R.id.textView29).setVisibility(View.GONE);
            findViewById(R.id.textView13).setVisibility(View.GONE);
        }

    }

    private void setValues() {
        tvBrand.setText(warranty.getBrand());
        tvModel.setText(warranty.getModel());
        tvIMEI.setText(warranty.getImei());

        if (warranty.getAddress() != null) {
            tvCustomerAddress.setText(warranty.getAddress());
        }


        if (warranty.getContactNo() != null) {
            tvCustomerContactNo.setText(warranty.getContactNo());

        }

        if (warranty.getEmail() != null) {
            tvCustomerEmail.setText(warranty.getEmail());
        }


        if (warranty.getCustomerName() != null) {
            tvCustomerName.setText(warranty.getCustomerName());
        }

        tvFer.setText(warranty.getReferenceNo());
        warranty.setWarrantyActivatedDate(warranty.getWarrantyActivatedDate());
        tvActivatedDate.setText(warranty.getWarrantyActivatedDate());
        tvAccessoryWStatus.setText(warranty.getAccessoryWarrantyStatus());
        tvDeviceWStatus.setText(warranty.getDeviceWarrantyStatus());
        tvServiceWStatus.setText(warranty.getServiceWarrantyStatus());

        if (dealer != null) {
            tvDealerName.setText(dealer.getDealerName());
            tvCity.setText(dealer.getCity());
            tvDistric.setText(dealer.getDistrict());
        }
    }

    private class UpdateWarrantyAsync extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DeivceInfoActivity.this);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            UserService.getInstance().updateWarranty(DeivceInfoActivity.this, warranty, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Log.d(TAG, jsonObject.toString());
                    progressDialog = new ProgressDialog(DeivceInfoActivity.this);
                    progressDialog.setMessage(getString(R.string.waiting));
                    progressDialog.show();
                    Intent intent = new Intent(DeivceInfoActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onError(Context context, String error) {
                    progressDialog = new ProgressDialog(DeivceInfoActivity.this);
                    progressDialog.setMessage(getString(R.string.waiting));
                    progressDialog.show();
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
