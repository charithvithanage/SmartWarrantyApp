package tellko.smarthub.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
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
import tellko.smarthub.Adapters.DealershipAdapter;
import tellko.smarthub.Entities.Dealer;
import tellko.smarthub.Entities.DealerRequest;
import tellko.smarthub.Interfaces.AsyncListner;
import tellko.smarthub.R;
import tellko.smarthub.Services.DealerService;
import tellko.smarthub.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static tellko.smarthub.Utils.isDeviceOnline;
import static tellko.smarthub.Utils.isUserNICValid;
import static tellko.smarthub.Utils.showAlertWithoutTitleDialog;
import static tellko.smarthub.Utils.showProgressDialog;

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
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_search);
        /**
         * Change status bar color programmatically
         */
        Utils.changeStatusBarColor(DealerSearchActivity.this, getWindow());

        init();
    }

    private void init() {

        dealerRequest = new DealerRequest();
        btnSearch = findViewById(R.id.btnRegister);
        etDealerNIC = findViewById(R.id.dealerNIC);
        loginButton = findViewById(R.id.signIn);
        errorNIC = findViewById(R.id.errorUserNICLable);

        etDealerNIC.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable et) {
                String s=et.toString();
                if(!s.equals(s.toUpperCase()))
                {
                    s=s.toUpperCase();
                    etDealerNIC.setText(s);
                    etDealerNIC.setSelection(etDealerNIC.length()); //fix reverse texting
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isDeviceOnline(DealerSearchActivity.this)){
                    if (isUserNICValid(etDealerNIC.getText().toString())) {

                        dealerRequest.setNic(etDealerNIC.getText().toString().toUpperCase());


                        /**
                         * Enter dealer's NIC
                         * And get dealer's dealerships from the server
                         * User can select dealer ship and go to the sign up page
                         */
                        progressDialog=showProgressDialog(DealerSearchActivity.this);
                        progressDialog.show();
                        btnSearch.setEnabled(false);

                        new GetDealerAsync().execute();

                    }else {
                        if (!isUserNICValid(etDealerNIC.getText().toString())) {
                            errorNIC.setVisibility(View.VISIBLE);
                            errorNIC.setText(getString(R.string.invalid_user_nic));
                            etDealerNIC.setBackground(getResources().getDrawable(R.drawable.error_edit_bg));
                        }
                    }
                }else {
                    Utils.showAlertWithoutTitleDialog(DealerSearchActivity.this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
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

    /**
     * Call to the dealer details from nic end point using AsyncTask
     */
    private class GetDealerAsync extends AsyncTask<Void, Void, Void> {

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

                                /**
                                 * If selected dealership is Active navigate to dealer user registration page
                                 */
                                adapter.setMyClickListener(new DealershipAdapter.MyClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {

                                        dialog.dismiss();

                                        Dealer dealer = dealers.get(position);

                                        if(dealer.isActive()){
                                            btnSearch.setEnabled(true);
                                            Intent intent = new Intent(DealerSearchActivity.this, SignUpActivity.class);
                                            intent.putExtra("dealerString", gson.toJson(dealer));
                                            startActivity(intent);
                                        }else {
                                            showAlertWithoutTitleDialog(DealerSearchActivity.this, getString(R.string.dealer_not_active), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    btnSearch.setEnabled(true);
                                                }
                                            });
                                        }


                                    }
                                });

                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        btnSearch.setEnabled(true);
                                   }
                                });
                                dialog.show();
                            } else {
                                Utils.showAlertWithoutTitleDialog(context, getString(R.string.no_dealer_for_given_nic_string), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        btnSearch.setEnabled(true);
                                    }
                                });
                            }


                        } else {
                            Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btnSearch.setEnabled(true);
                                    dialog.dismiss();
                                }
                            });
                        }


                    } catch (Exception e) {
                        btnSearch.setEnabled(true);

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
                            btnSearch.setEnabled(true);
                        }
                    });
                }
            });

            return null;
        }
    }

}
