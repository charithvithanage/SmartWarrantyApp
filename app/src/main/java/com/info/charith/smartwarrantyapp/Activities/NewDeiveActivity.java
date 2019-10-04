package com.info.charith.smartwarrantyapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.Config;
import com.info.charith.smartwarrantyapp.Entities.Dealer;
import com.info.charith.smartwarrantyapp.Entities.Warranty;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Utils;

public class NewDeiveActivity extends AppCompatActivity {

    String previous_activity;
    String type;
    Button btnConfirm;
    String warrantyString, dealerString;
    Warranty warranty;
    Gson gson = new Gson();
    TextView tvBrand, tvModel, tvIMEI, tvDealerName, tvCity, tvDistric;
    EditText etCustomerName, etCustomerAddress, etCustomerContactNo, etCustomerEmail;
    Dealer dealer;
    TextView titleView;
    ImageButton backBtn;

    TextWatcher etContactNoTextWatcher, etEmailTextWatcher;

    TextView errorContactNo, errorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_deive);

        Utils.changeStatusBarColor(NewDeiveActivity.this, getWindow());

        type = getIntent().getStringExtra("type");
        warrantyString = getIntent().getStringExtra("warrantyString");
        dealerString = getIntent().getStringExtra("dealerString");
        previous_activity = getIntent().getStringExtra("previous_activity");

        warranty = gson.fromJson(warrantyString, Warranty.class);
        dealer = gson.fromJson(dealerString, Dealer.class);

        init();


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(etCustomerName.getText()) || !TextUtils.isEmpty(etCustomerAddress.getText()) || !TextUtils.isEmpty(etCustomerContactNo.getText()) || !TextUtils.isEmpty(etCustomerEmail.getText())) {

                    if (!TextUtils.isEmpty(etCustomerEmail.getText()) && !TextUtils.isEmpty(etCustomerContactNo.getText())) {

                        if (etCustomerEmail.getText().toString().matches(Config.Instance.emailPattern) && etCustomerContactNo.getText().toString().length() == 10) {
                            navigateToDeviceInfoActivity();
                        } else {

                            if (!etCustomerEmail.getText().toString().matches(Config.Instance.emailPattern)) {
                                errorEmail.setVisibility(View.VISIBLE);
                                errorEmail.setText(getString(R.string.wrong_email_pattern));
                                etCustomerEmail.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                            } else {
                                setEmailEditTextBGNormal();
                            }

                            if (etCustomerContactNo.getText().toString().length() != 10) {
                                errorContactNo.setVisibility(View.VISIBLE);
                                errorContactNo.setText(getString(R.string.invalid_contact_no));
                                etCustomerContactNo.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));

                            } else {
                                setContactNoEditTextBGNormal();

                            }
                        }

                    } else {
                        if (!TextUtils.isEmpty(etCustomerEmail.getText())) {
                            if (!etCustomerEmail.getText().toString().matches(Config.Instance.emailPattern)) {
                                errorEmail.setVisibility(View.VISIBLE);
                                errorEmail.setText(getString(R.string.wrong_email_pattern));
                                etCustomerEmail.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                            } else {
                                navigateToDeviceInfoActivity();
                            }
                        } else if (!TextUtils.isEmpty(etCustomerContactNo.getText())) {
                            if (etCustomerContactNo.getText().toString().length() != 10) {
                                errorContactNo.setVisibility(View.VISIBLE);
                                errorContactNo.setText(getString(R.string.invalid_contact_no));
                                etCustomerContactNo.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                            } else {
                                navigateToDeviceInfoActivity();
                            }
                        } else {
                            navigateToDeviceInfoActivity();
                        }
                    }

                } else {
                    Utils.showAlertWithoutTitleDialog(NewDeiveActivity.this, getString(R.string.enter_single_value), new DialogInterface.OnClickListener() {
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

    private void navigateToDeviceInfoActivity() {
        warranty.setCustomerName(etCustomerName.getText().toString());
        warranty.setContactNo(etCustomerContactNo.getText().toString());
        warranty.setAddress(etCustomerAddress.getText().toString());
        warranty.setEmail(etCustomerEmail.getText().toString());

        Intent intent = new Intent(NewDeiveActivity.this, DeivceInfoActivity.class);
        intent.putExtra("warrantyString", gson.toJson(warranty));
        intent.putExtra("dealerString", dealerString);
        intent.putExtra("type", type);
        intent.putExtra("previous_activity", "new_device_activity");
        startActivity(intent);
    }

    private void init() {
        backBtn = findViewById(R.id.backBtn);

        titleView = findViewById(R.id.title_view);
        titleView.setText(Utils.stringCapitalize(type));



        btnConfirm = findViewById(R.id.btnConfirm);
        tvBrand = findViewById(R.id.brand);
        tvModel = findViewById(R.id.model);
        tvIMEI = findViewById(R.id.imei);
        tvDealerName = findViewById(R.id.dealerName);
        tvCity = findViewById(R.id.city);
        tvDistric = findViewById(R.id.distric);
        etCustomerAddress = findViewById(R.id.customerAddress);
        etCustomerContactNo = findViewById(R.id.customerContactNo);
        etCustomerEmail = findViewById(R.id.customerEmail);
        etCustomerName = findViewById(R.id.customerName);
        errorContactNo = findViewById(R.id.errorContactNo);
        errorEmail = findViewById(R.id.errorEmail);


        setValues();

        etContactNoTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setEditTextBGNormal();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String contactNo = etCustomerContactNo.getText().toString();

                if (contactNo.length() > 0) {

                    String firstLetter = s.toString().substring(0, 1);

                    if (!firstLetter.equals("0")) {
                        errorContactNo.setVisibility(View.VISIBLE);
                        errorContactNo.setText(getString(R.string.contact_no_start_wrong));
                        etCustomerContactNo.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                    }
                }

            }
        };

        etEmailTextWatcher = new TextWatcher() {
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


        etCustomerContactNo.addTextChangedListener(etContactNoTextWatcher);
        etCustomerEmail.addTextChangedListener(etEmailTextWatcher);
    }

    private void setValues() {

        tvBrand.setText(warranty.getBrand());
        tvModel.setText(warranty.getModel());
        tvIMEI.setText(warranty.getImei());

        tvDealerName.setText(dealer.getDealerName());
        tvCity.setText(dealer.getCity());
        tvDistric.setText(dealer.getDistrict());


    }

    private void setEditTextBGNormal() {

        etCustomerContactNo.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        etCustomerEmail.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        errorContactNo.setVisibility(View.GONE);
        errorEmail.setVisibility(View.GONE);
    }

    private void setEmailEditTextBGNormal() {
        etCustomerEmail.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        errorEmail.setVisibility(View.GONE);
    }

    private void setContactNoEditTextBGNormal() {
        etCustomerContactNo.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        errorContactNo.setVisibility(View.GONE);
    }


}
