package tellko.smarthub.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import tellko.smarthub.AsyncTasks.GetDealerAsync;
import tellko.smarthub.Entities.Dealer;
import tellko.smarthub.Entities.Warranty;
import tellko.smarthub.Interfaces.AsyncListner;
import tellko.smarthub.R;
import tellko.smarthub.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static tellko.smarthub.Utils.getActivationTime;

public class DeivceInfoActivity extends AppCompatActivity {
    private static final String TAG = "SmartWarrantyApp";

    String previous_activity;
    String type;
    Button btnSubmit;

    TextView tvBrand, tvModel, tvIMEI, tvDealerName, tvCity, tvDistric;
    TextView tvCustomerName, tvCustomerAddress, tvCustomerContactNo, tvCustomerEmail, tvBrandCategory, tvDealerContact;
    TextView tvFer, tvActivatedDate, tvAccessoryWStatus, tvDeviceWStatus, tvServiceWStatus;
    String warrantyString;
    Warranty warranty;
    Dealer dealer;

    Gson gson = new Gson();

    TextView titleView;
    ImageButton backBtn;
    ImageButton homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deivce_info);

        /**
         * Change status bar color programmatically
         */
        Utils.changeStatusBarColor(DeivceInfoActivity.this, getWindow());

        warrantyString = getIntent().getStringExtra("warrantyString");
        type = getIntent().getStringExtra("type");
        previous_activity = getIntent().getStringExtra("previous_activity");
        warranty = gson.fromJson(warrantyString, Warranty.class);


        init();

        if (!previous_activity.equals("activation_list_activity")) {
            if (!type.equals("sold device")) {
                backBtn.setVisibility(View.GONE);
            }
        }

        new GetDealerAsync(DeivceInfoActivity.this, warranty.getDealerCode(), new AsyncListner() {
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
                    tvDealerContact.setText(dealer.getDealerContact());
                }
            }

            @Override
            public void onError(Context context, String error) {

            }
        }).execute();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equals("sold device")) {
                    Utils.navigateWithoutHistory(DeivceInfoActivity.this, MainActivity.class);
                } else {

                    /**
                     * If activation status are Enable or Enable with Date
                     * Dealer's details should set to the warranty object manually
                     */

                    Intent intent = new Intent(DeivceInfoActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
//                    new UpdateWarrantyAsync().execute();
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
                Utils.navigateWithoutHistory(DeivceInfoActivity.this, AppListActivity.class);
            }
        });


    }


    private void init() {
        tvDealerContact = findViewById(R.id.dealerContact);
        titleView = findViewById(R.id.title_view);
        backBtn = findViewById(R.id.backBtn);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvBrand = findViewById(R.id.brand);
        tvModel = findViewById(R.id.model);
        tvIMEI = findViewById(R.id.imei);
        tvDealerName = findViewById(R.id.dealerName);
        tvCity = findViewById(R.id.city);
        tvDistric = findViewById(R.id.distric);
        tvBrandCategory = findViewById(R.id.brandCategory);
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
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setVisibility(View.VISIBLE);

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
            tvDealerContact.setVisibility(View.GONE);
            findViewById(R.id.dealerContactLable).setVisibility(View.GONE);
            findViewById(R.id.textView15).setVisibility(View.GONE);
            findViewById(R.id.textView17).setVisibility(View.GONE);
            findViewById(R.id.textView29).setVisibility(View.GONE);
            findViewById(R.id.textView13).setVisibility(View.GONE);
        }

    }

    /**
     * Set values to text fields
     */
    private void setValues() {
        tvBrand.setText(warranty.getBrand());
        tvModel.setText(warranty.getModel());
        tvIMEI.setText(warranty.getImei());
        tvBrandCategory.setText(warranty.getBrandCategory());

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
        tvActivatedDate.setText(warranty.getWarrantyActivatedDate() + "  " + getActivationTime(warranty.getActivationTime()));
        if (warranty.getAccessoryWarrantyStatus().equals("Defect After Purchase")) {
            tvAccessoryWStatus.setText("DAP");
        } else {
            tvAccessoryWStatus.setText(warranty.getAccessoryWarrantyStatus());

        }

        if (warranty.getDeviceWarrantyStatus().equals("Defect After Purchase")) {
            tvDeviceWStatus.setText("DAP");

        } else {
            tvDeviceWStatus.setText(warranty.getDeviceWarrantyStatus());

        }

        if (warranty.getServiceWarrantyStatus().equals("Defect After Purchase")) {
            tvServiceWStatus.setText("DAP");

        } else {
            tvServiceWStatus.setText(warranty.getServiceWarrantyStatus());

        }
    }

    @Override
    public void onBackPressed() {
        if (previous_activity.equals("activation_list_activity")) {
            super.onBackPressed();
        } else if (type.equals("sold device")) {
            super.onBackPressed();
        }
    }
}
