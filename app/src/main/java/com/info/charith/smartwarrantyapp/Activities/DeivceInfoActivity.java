package com.info.charith.smartwarrantyapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.Config;
import com.info.charith.smartwarrantyapp.Entities.Dealer;
import com.info.charith.smartwarrantyapp.Entities.Warranty;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.UserService;
import com.info.charith.smartwarrantyapp.Utils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

//        if(type.equals("new device")&&previous_activity.equals("new_device_activity")){
//            warrantyEditLayout.setVisibility(View.VISIBLE);
//            warrantyViewLayout.setVisibility(View.GONE);
//        }else if(type.equals("sold device")&&previous_activity.equals("scan_activity")||type.equals("activated device")&&previous_activity.equals("new_device_activity")){
//            warrantyEditLayout.setVisibility(View.GONE);
//            warrantyViewLayout.setVisibility(View.VISIBLE);
//        }


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equals("sold device")) {
                    Utils.navigateWithoutHistory(DeivceInfoActivity.this, MainActivity.class);
                } else {
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

        titleView.setText(getString(R.string.device_info));

        setValues();

    }

    private void setValues() {
        tvBrand.setText(warranty.getBrand());
        tvModel.setText(warranty.getModel());
        tvIMEI.setText(warranty.getImei());

        if(warranty.getAddress()!=null){
            tvCustomerAddress.setText(warranty.getAddress());
        }


        if(warranty.getContactNo()!=null){
            tvCustomerContactNo.setText(warranty.getContactNo());

        }

        if(warranty.getEmail()!=null){
            tvCustomerEmail.setText(warranty.getEmail());
        }


        if(warranty.getCustomerName()!=null){
            tvCustomerName.setText(warranty.getCustomerName());
        }

        tvDealerName.setText(dealer.getDealerName());
        tvCity.setText(dealer.getCity());
        tvDistric.setText(dealer.getDistrict());


        tvFer.setText(warranty.getReferenceNo());

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Config.date_time_pattern, Locale.ENGLISH);
        SimpleDateFormat patern = new SimpleDateFormat(Config.standard_date_time_pattern, Locale.ENGLISH);

        warranty.setWarrantyActivatedDate(sdf.format(c.getTime()));
        tvActivatedDate.setText(Utils.convertDateTimeStringToString(warranty.getWarrantyActivatedDate()));



        tvAccessoryWStatus.setText(warranty.getAccessoryWarrantyStatus());
        tvDeviceWStatus.setText(warranty.getDeviceWarrantyStatus());
        tvServiceWStatus.setText(warranty.getServiceWarrantyStatus());


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
