package com.info.tellko.smartwarrantyapp.Activities;

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
import com.info.tellko.smartwarrantyapp.AsyncTasks.GetDealerAsync;
import com.info.tellko.smartwarrantyapp.AsyncTasks.GetProductsAsync;
import com.info.tellko.smartwarrantyapp.Config;
import com.info.tellko.smartwarrantyapp.Entities.Dealer;
import com.info.tellko.smartwarrantyapp.Entities.DealerUser;
import com.info.tellko.smartwarrantyapp.Entities.DealerUserMock;
import com.info.tellko.smartwarrantyapp.Entities.Product;
import com.info.tellko.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.tellko.smartwarrantyapp.R;
import com.info.tellko.smartwarrantyapp.Services.UserService;
import com.info.tellko.smartwarrantyapp.Utils;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.info.tellko.smartwarrantyapp.Utils.dateTimeToString;
import static com.info.tellko.smartwarrantyapp.Utils.endOfDay;
import static com.info.tellko.smartwarrantyapp.Utils.isDeviceOnline;
import static com.info.tellko.smartwarrantyapp.Utils.navigateWithoutHistory;
import static com.info.tellko.smartwarrantyapp.Utils.showProgressDialog;
import static com.info.tellko.smartwarrantyapp.Utils.sortProductList;

public class DealerInfoActivity extends AppCompatActivity {
    private static final String TAG = "SmartWarrantyApp";

    Button btnConfirm;
    String dealerUserMockString;
    String dealerString;
    Dealer dealer;
    Gson gson = new Gson();
    TextView tvDealerCode, tvDealerName, tvAddress, tvCity, tvDistric, tvOwnerName, tvNIC, tvContactNo, tvEmail, tvDealerStatus;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_info);

        /**
         * Change status bar color programmatically
         */
        Utils.changeStatusBarColor(DealerInfoActivity.this, getWindow());

        dealerUserMockString = getIntent().getStringExtra("dealerUserMockString");
        dealerString = getIntent().getStringExtra("dealerString");
        dealer = gson.fromJson(dealerString, Dealer.class);
        DealerUserMock dealerUserMock = gson.fromJson(dealerUserMockString, DealerUserMock.class);
        dealerUserMock.setUserStatus(true);
        dealerUserMockString = gson.toJson(dealerUserMock);

        init();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDeviceOnline(DealerInfoActivity.this)) {

                    /**
                     * If dealer's details are correct
                     * User can register by clicking confirm button
                     */
                    progressDialog = showProgressDialog(DealerInfoActivity.this);
                    progressDialog.show();
                    btnConfirm.setEnabled(false);
                    new RegisterAsync().execute();
                } else {
                    Utils.showAlertWithoutTitleDialog(DealerInfoActivity.this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
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
     *
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


        @Override
        protected Void doInBackground(Void... voids) {

            UserService.getInstance().registerUser(DealerInfoActivity.this, dealerUserMockString, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Log.d(TAG, jsonObject.toString());


                    String loggedInUser;
                    String accessToken;
                    String refreshToken;
                    String userDealer;

                    try {


                        boolean success = jsonObject.getBoolean("success");

                        if (success) {

                            DateTime dateTime = new DateTime();
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
                            editor.putString("logoutTime", dateTimeToString(endOfDay(dateTime)));

                            editor.commit();


                            gson = new Gson();
                            DealerUser dealerUserMock = gson.fromJson(loggedInUser, DealerUser.class);
                            dealer = gson.fromJson(userDealer, Dealer.class);

                            new GetDealerAsync(DealerInfoActivity.this, dealerUserMock.getDealerCode(), new AsyncListner() {
                                @Override

                                public void onSuccess(Context context, JSONObject jsonObject) {
                                    String objectOne = null;

                                    try {
                                        boolean success = jsonObject.getBoolean("success");
                                        String message = jsonObject.getString("message");

                                        if (success) {
                                            objectOne = jsonObject.getString("object");

                                            dealer = gson.fromJson(objectOne, Dealer.class);

                                            SharedPreferences sharedPref = context.getSharedPreferences(
                                                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("userDealer", gson.toJson(dealer));
                                            editor.commit();

                                            new GetProductsAsync(DealerInfoActivity.this, dealer.getDealerCode(), new AsyncListner() {
                                                @Override
                                                public void onSuccess(Context context, JSONObject jsonObject) {
                                                    String object = null;
                                                    progressDialog.dismiss();

                                                    try {
                                                        object = jsonObject.getString("object");

                                                        JSONArray jsonArray = new JSONArray(object);
                                                        List<Product> brands = new ArrayList<>();


                                                        if (jsonArray.length() > 0) {
                                                            brands = new ArrayList<>();

                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                Product dealer = gson.fromJson(jsonArray.getString(i), Product.class);
                                                                brands.add(dealer);
                                                            }
                                                        }


                                                        Config.Instance.setEnabledBrands(sortProductList(brands));

                                                        JSONArray sortedJsonArray = new JSONArray();

                                                        for (int i = 0; i < Config.Instance.getEnabledBrands().size(); i++) {
                                                            Product dealer = Config.Instance.getEnabledBrands().get(i);
                                                            sortedJsonArray.put(gson.toJson(dealer));
                                                        }

                                                        SharedPreferences sharedPref = context.getSharedPreferences(
                                                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPref.edit();
                                                        editor.putString("enabledBrands", sortedJsonArray.toString());
                                                        editor.commit();

                                                        Utils.navigateWithoutHistory(context, MainActivity.class);


                                                    } catch (JSONException e) {
                                                        Utils.showAlertWithoutTitleDialog(context, getString(R.string.login_again), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                navigateWithoutHistory(DealerInfoActivity.this, LoginActivity.class);
                                                            }
                                                        });
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onError(Context context, String error) {
                                                    progressDialog.dismiss();
                                                    Utils.showAlertWithoutTitleDialog(context, getString(R.string.login_again), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            navigateWithoutHistory(DealerInfoActivity.this, LoginActivity.class);
                                                        }
                                                    });

                                                }
                                            }).execute();


                                        } else {
                                            progressDialog.dismiss();
                                            Utils.showAlertWithoutTitleDialog(context, getString(R.string.login_again), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    navigateWithoutHistory(DealerInfoActivity.this, LoginActivity.class);
                                                }
                                            });
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        progressDialog.dismiss();
                                        Utils.showAlertWithoutTitleDialog(context, getString(R.string.login_again), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                navigateWithoutHistory(DealerInfoActivity.this, LoginActivity.class);
                                            }
                                        });
                                    }


                                }

                                @Override
                                public void onError(Context context, String error) {
                                    progressDialog.dismiss();
                                    Utils.showAlertWithoutTitleDialog(context, getString(R.string.login_again), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            navigateWithoutHistory(DealerInfoActivity.this, LoginActivity.class);
                                        }
                                    });
                                }
                            }).execute();


                        } else {
                            progressDialog.dismiss();
                            String message = jsonObject.getString("message");

                            Utils.showAlertWithoutTitleDialog(context, message + getString(R.string.login_again), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    navigateWithoutHistory(DealerInfoActivity.this, LoginActivity.class);
                                }
                            });
                        }


                    } catch (JSONException e) {

                        progressDialog.dismiss();

                        Utils.showAlertWithoutTitleDialog(context, e.toString(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                navigateWithoutHistory(DealerInfoActivity.this, LoginActivity.class);

                            }
                        });
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