package com.info.charith.smartwarrantyapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.AsyncTasks.GetDealerAsync;
import com.info.charith.smartwarrantyapp.Config;
import com.info.charith.smartwarrantyapp.Entities.Dealer;
import com.info.charith.smartwarrantyapp.Entities.DealerUser;
import com.info.charith.smartwarrantyapp.Entities.Warranty;
import com.info.charith.smartwarrantyapp.Entities.WarrantyRequest;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.DealerService;
import com.info.charith.smartwarrantyapp.Services.UserService;
import com.info.charith.smartwarrantyapp.Utils;
import com.info.charith.smartwarrantyapp.WordUtils;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import static com.info.charith.smartwarrantyapp.Utils.isDeviceOnline;
import static com.info.charith.smartwarrantyapp.Utils.showProgressDialog;

public class NewDeiveActivity extends AppCompatActivity {

    private static final String TAG = "SmartWarrantyApp";
    String previous_activity;
    String type;
    Button btnConfirm;
    String warrantyString;
    Warranty warranty;
    Gson gson = new Gson();
    TextView tvBrand, tvModel, tvIMEI, tvDealerName, tvCity, tvDistric;
    EditText etCustomerName, etCustomerAddress, etCustomerContactNo, etCustomerEmail;
    Dealer dealer;
    TextView titleView;
    ImageButton backBtn;
    TextWatcher etContactNoTextWatcher, etEmailTextWatcher;
//    TextWatcher etAddressTextWatcher, etNameTextWatcher;
    TextView errorContactNo, errorEmail;
    String waranntyRequest;
    ImageButton homeBtn;
    String dealerCode = null;
    DealerUser dealerUserMock;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_deive);

        Utils.changeStatusBarColor(NewDeiveActivity.this, getWindow());
        type = getIntent().getStringExtra("type");
        warrantyString = getIntent().getStringExtra("warrantyString");
        previous_activity = getIntent().getStringExtra("previous_activity");
        waranntyRequest = getIntent().getStringExtra("waranntyRequest");

        warranty = gson.fromJson(warrantyString, Warranty.class);

        init();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDeviceOnline(NewDeiveActivity.this)) {
                    if (!TextUtils.isEmpty(etCustomerName.getText()) && !TextUtils.isEmpty(etCustomerAddress.getText()) && !TextUtils.isEmpty(etCustomerContactNo.getText()) && !TextUtils.isEmpty(etCustomerEmail.getText())) {

                        if (!TextUtils.isEmpty(etCustomerEmail.getText()) && !TextUtils.isEmpty(etCustomerContactNo.getText())) {

                            if (etCustomerEmail.getText().toString().matches(Config.Instance.emailPattern) && etCustomerContactNo.getText().toString().length() == 10) {
                                /**
                                 * If email and contact no are correct  navigate to device info activity
                                 */
                                progressDialog = showProgressDialog(NewDeiveActivity.this);
                                progressDialog.show();
                                btnConfirm.setEnabled(false);
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
                                errorEmail.setVisibility(View.VISIBLE);
                                errorEmail.setText(getString(R.string.wrong_email_pattern));
                                etCustomerEmail.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                            }

                            if (!TextUtils.isEmpty(etCustomerContactNo.getText())) {
                                errorContactNo.setVisibility(View.VISIBLE);
                                errorContactNo.setText(getString(R.string.invalid_contact_no));
                                etCustomerContactNo.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
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
                } else {
                    Utils.showAlertWithoutTitleDialog(NewDeiveActivity.this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
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

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigateWithoutHistory(NewDeiveActivity.this, MainActivity.class);
            }
        });

    }

    /**
     * Navigate to device info page with string values
     */
    private void navigateToDeviceInfoActivity() {
        warranty.setCustomerName(etCustomerName.getText().toString());
        warranty.setContactNo(etCustomerContactNo.getText().toString());
        warranty.setAddress(etCustomerAddress.getText().toString());
        warranty.setEmail(etCustomerEmail.getText().toString());


        if (warranty.getActivationStatus().equals("Enable") || warranty.getActivationStatus().equals("Enable with Date")) {

            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            Gson gson = new Gson();

            String dealerString = sharedPref.getString("loggedInUser", "0");
            dealerUserMock = gson.fromJson(dealerString, DealerUser.class);

            warranty.setDealerCode(dealer.getDealerCode());
            warranty.setDealerUserName(dealerUserMock.getUsername());
        }


        if (type.equals("new device")) {
            DateTime dateTime = new DateTime();
            warranty.setActivationStatus("Enable with Date");
            warranty.setActivationDate(null);
//            warranty.setActivationDate(dateTime.toString("yyyy-MM-dd"));
        }

        new UpdateWarrantyAsync().execute();


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
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setVisibility(View.VISIBLE);

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Gson gson = new Gson();

        String dealerString = sharedPref.getString("loggedInUser", "0");
        dealerUserMock = gson.fromJson(dealerString, DealerUser.class);

        if (type.equals("new device")) {
            warranty.setActivationStatus("Enable with Date");
            dealerCode = dealerUserMock.getDealerCode();
        } else if (type.equals("activated device")) {
            warranty.setActivationStatus("Enable with Date");
            if (warranty.getDealerCode() != null) {
                dealerCode = warranty.getDealerCode();
            } else {
                dealerCode = dealerUserMock.getDealerCode();
            }
        } else {
            dealerCode = warranty.getDealerCode();
        }

        new GetDealerAsync(NewDeiveActivity.this, dealerCode, new AsyncListner() {
            @Override
            public void onSuccess(Context context, JSONObject jsonObject) {

                try {
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        String objectOne = jsonObject.getString("object");
                        Gson gson = new Gson();
                        dealer = gson.fromJson(objectOne, Dealer.class);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (dealer != null) {
                    tvDealerName.setText(dealer.getDealerName());
                    tvCity.setText(dealer.getCity());
                    tvDistric.setText(dealer.getDistrict());
                }
            }

            @Override
            public void onError(Context context, String error) {

            }
        }).execute();

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

//        etNameTextWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                etCustomerName.removeTextChangedListener(etNameTextWatcher);
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                etCustomerName.setText(strnew);
//                etCustomerName.setSelection(etCustomerName.length());
//                etCustomerName.addTextChangedListener(etNameTextWatcher);
//
//
//            }
//        };
//
//        etAddressTextWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                String str = s.toString();
//                etCustomerAddress.removeTextChangedListener(this);
////                str = WordUtils.capitalize(str);
//                str = WordUtils.capitalize(str);
//                etCustomerAddress.setText(str);
//                etCustomerAddress.setSelection(etCustomerAddress.length());
//                etCustomerAddress.addTextChangedListener(etAddressTextWatcher);
//            }
//        };

        etCustomerContactNo.addTextChangedListener(etContactNoTextWatcher);
        etCustomerEmail.addTextChangedListener(etEmailTextWatcher);
    }


    /**
     * Set values to text fields
     */
    private void setValues() {

        tvBrand.setText(warranty.getBrand());
        tvModel.setText(warranty.getModel());
        tvIMEI.setText(warranty.getImei());


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

    private class UpdateWarrantyAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            UserService.getInstance().updateWarranty(NewDeiveActivity.this, warranty, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    try {
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");
                        if (success) {
                            new RequestWarrantyAsync().execute();
                        } else {
                            if (message.contains("No warranty template found for brand :")) {
                                Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        new RequestWarrantyAsync().execute();
                                    }
                                });
                            } else {
                                progressDialog.dismiss();

                                Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        btnConfirm.setEnabled(true);
                                    }
                                });
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        btnConfirm.setEnabled(true);

                    }
                }

                @Override
                public void onError(Context context, String error) {
                    progressDialog.dismiss();
                    Utils.showAlertWithoutTitleDialog(context, error, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            btnConfirm.setEnabled(true);

                        }
                    });
                }
            });

            return null;
        }
    }

    private class RequestWarrantyAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().getWarrantyFromIMEI(NewDeiveActivity.this, gson.fromJson(waranntyRequest, WarrantyRequest.class), new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    progressDialog.dismiss();
                    String objectOne;

                    try {
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        if (success) {
                            objectOne = jsonObject.getString("objectOne");
                            Gson gson = new Gson();
                            Warranty warranty = gson.fromJson(objectOne, Warranty.class);

                            Intent intent = new Intent(NewDeiveActivity.this, DeivceInfoActivity.class);
                            intent.putExtra("warrantyString", gson.toJson(warranty));
                            intent.putExtra("type", type);
                            intent.putExtra("previous_activity", "new_device_activity");
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        btnConfirm.setEnabled(true);
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
                            btnConfirm.setEnabled(true);
                        }
                    });
                }
            });

            return null;
        }
    }
}
