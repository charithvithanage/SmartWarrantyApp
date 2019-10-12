package com.info.charith.smartwarrantyapp.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.Adapters.DealershipAdapter;
import com.info.charith.smartwarrantyapp.Entities.Dealer;
import com.info.charith.smartwarrantyapp.Entities.DealerRequest;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.DealerService;
import com.info.charith.smartwarrantyapp.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.info.charith.smartwarrantyapp.Utils.isUserNICValid;

public class DealerSearchActivity extends AppCompatActivity {

    Button btnSearch;
    EditText etDealerNIC;
    DealerRequest dealerRequest;
    private static final String TAG = "SmartWarrantyApp";
    Gson gson = new Gson();
    List<Dealer> dealers = new ArrayList<>();
    TextView loginButton;
    TextWatcher userNICTextWatcher;
    TextView errorNIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_search);

        init();
    }

    private void init() {

        dealerRequest = new DealerRequest();
        btnSearch = findViewById(R.id.btnRegister);
        etDealerNIC = findViewById(R.id.dealerNIC);
        loginButton = findViewById(R.id.signIn);
        errorNIC = findViewById(R.id.errorUserNICLable);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(etDealerNIC.getText())) {

                    String str = etDealerNIC.getText().toString();
                    if (str.length() == 13) {

                        if (!str.matches("[0-9]+")) {
                            errorNIC.setVisibility(View.VISIBLE);
                            errorNIC.setText(getString(R.string.invalid_user_nic));
                            etDealerNIC.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
//                            Utils.showAlertWithoutTitleDialog(DealerSearchActivity.this, getString(R.string.invalid_user_nic), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    int pos = etDealerNIC.getText().length();
//                                    etDealerNIC.requestFocus(pos);
//                                }
//                            });
                        }else {
                            dealerRequest.setNic(etDealerNIC.getText().toString().toUpperCase());

                            /**
                             * Enter dealer's NIC
                             * And get dealer's dealerships from the server
                             * User can select dealer ship and go to the sign up page
                             */
                            new GetDealerAsync().execute();
                        }

                    } else {
                        if (str.length() == 10) {
                            String lastCharacter = str.substring(str.length() - 1);
                            if (!lastCharacter.toLowerCase().equals("v")) {

                                if (!lastCharacter.toLowerCase().equals("x")) {
                                    errorNIC.setVisibility(View.VISIBLE);
                                    errorNIC.setText(getString(R.string.invalid_user_nic));
                                    etDealerNIC.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                                }else {
                                    dealerRequest.setNic(etDealerNIC.getText().toString().toUpperCase());

                                    /**
                                     * Enter dealer's NIC
                                     * And get dealer's dealerships from the server
                                     * User can select dealer ship and go to the sign up page
                                     */
                                    new GetDealerAsync().execute();
                                }
//                                Utils.showAlertWithoutTitleDialog(DealerSearchActivity.this, getString(R.string.invalid_user_nic), new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        int pos = etDealerNIC.getText().length();
//                                        etDealerNIC.requestFocus(pos);
//                                    }
//                                });

                            }else {
                                dealerRequest.setNic(etDealerNIC.getText().toString().toUpperCase());

                                /**
                                 * Enter dealer's NIC
                                 * And get dealer's dealerships from the server
                                 * User can select dealer ship and go to the sign up page
                                 */
                                new GetDealerAsync().execute();
                            }
                        } else {

                            errorNIC.setVisibility(View.VISIBLE);
                            errorNIC.setText(getString(R.string.invalid_user_nic));
                            etDealerNIC.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
//                            Utils.showAlertWithoutTitleDialog(DealerSearchActivity.this, getString(R.string.invalid_user_nic), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    int pos = etDealerNIC.getText().length();
//                                    etDealerNIC.requestFocus(pos);
//                                }
//                            });
                        }
                    }

                }else {
                    if (!isUserNICValid(etDealerNIC.getText().toString())) {
                        errorNIC.setVisibility(View.VISIBLE);
                        errorNIC.setText(getString(R.string.invalid_user_nic));
                        etDealerNIC.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                    }
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigateWithoutHistory(DealerSearchActivity.this, LoginActivity.class);
            }
        });

        userNICTextWatcher = new TextWatcher() {
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

        etDealerNIC.addTextChangedListener(userNICTextWatcher);


    }

    private void setEditTextBGNormal() {
        etDealerNIC.setBackground(getResources().getDrawable(R.drawable.edt_bg_normal));
        errorNIC.setVisibility(View.GONE);
    }

    private class GetDealerAsync extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DealerSearchActivity.this);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().getDealersFromNIC(DealerSearchActivity.this, dealerRequest, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {

                    Log.d(TAG, jsonObject.toString());

                    progressDialog.dismiss();
                    try {

                        String objectString = jsonObject.getString("object");
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        if (success) {
                            JSONArray jsonArray = new JSONArray(objectString);

                            if (jsonArray.length() > 0) {
                                dealers = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Dealer dealer = gson.fromJson(jsonArray.getString(i), Dealer.class);
                                    dealers.add(dealer);
                                }

                                final Dialog dialog = new Dialog(DealerSearchActivity.this);
                                dialog.setContentView(R.layout.dealership_dialog_layout);
                                dialog.setCancelable(false);

                                RecyclerView recyclerView = dialog.findViewById(R.id.recycleView);
                                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                                LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getApplicationContext());
                                MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                                final DealershipAdapter adapter = new DealershipAdapter(dealers, DealerSearchActivity.this);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(MyLayoutManager);

                                adapter.setMyClickListener(new DealershipAdapter.MyClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {

                                        dialog.dismiss();

                                        Dealer dealer = dealers.get(position);

                                        Intent intent = new Intent(DealerSearchActivity.this, SignUpActivity.class);
                                        intent.putExtra("dealerString", gson.toJson(dealer));
                                        startActivity(intent);
                                    }
                                });

                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            } else {
                                Utils.showAlertWithoutTitleDialog(context, getString(R.string.no_dealer_for_given_nic_string), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
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


                    } catch (Exception e) {
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
                        }
                    });
                }
            });

            return null;
        }
    }

}
