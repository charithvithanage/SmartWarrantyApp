package tellko.smarthub.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.zxing.Result;
import tellko.smarthub.CustomViews.CustomZXingScannerView;
import tellko.smarthub.Entities.Product;
import tellko.smarthub.Entities.Warranty;
import tellko.smarthub.Entities.WarrantyRequest;
import tellko.smarthub.Interfaces.AsyncListner;
import tellko.smarthub.R;
import tellko.smarthub.Services.DealerService;
import tellko.smarthub.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static tellko.smarthub.Utils.isDeviceOnline;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    WarrantyRequest warrantyRequest;
    private static final String TAG = "SmartWarrantyApp";

    Button manualBtn;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    ViewGroup contentFrame;
    String selectedBrand;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_scanner);

        Utils.changeStatusBarColor(ScannerActivity.this, getWindow());

        selectedBrand = getIntent().getStringExtra("selected_brand");

        init();

        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCamera();
                final Dialog dialog = new Dialog(ScannerActivity.this);
                dialog.setContentView(R.layout.imei_dialog_layout);
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        startCamera();
                    }
                });

                final EditText imei = dialog.findViewById(R.id.imei);
                final TextView errorLable = dialog.findViewById(R.id.errorIMEILable);
                Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
                TextWatcher imieWatcher;

                imieWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        errorLable.setVisibility(View.GONE);

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                imei.addTextChangedListener(imieWatcher);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(isDeviceOnline(ScannerActivity.this)){
                            if (imei.getText().length() == 15) {
                                dialog.dismiss();

                                if (!TextUtils.isEmpty(imei.getText())) {
                                    warrantyRequest.setImei(imei.getText().toString());
                                    new GetProductAsync(selectedBrand).execute();
                                }
                            } else {
                                errorLable.setText("( Enter correct IMEI )");
                                errorLable.setVisibility(View.VISIBLE);
                            }
                        }else {
                            Utils.showAlertWithoutTitleDialog(ScannerActivity.this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }



                    }
                });
                dialog.show();
            }
        });
    }


    private void init() {
        warrantyRequest = new WarrantyRequest();
        manualBtn = findViewById(R.id.manual);
        contentFrame = findViewById(R.id.content_frame);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant

                return;
            } else {
                mScannerView = new ZXingScannerView(this) {

                    @Override
                    protected IViewFinder createViewFinderView(Context context) {
                        return new CustomZXingScannerView(context);
                    }

                };

                contentFrame.addView(mScannerView);
            }
        } else {
            mScannerView = new ZXingScannerView(this) {

                @Override
                protected IViewFinder createViewFinderView(Context context) {
                    return new CustomZXingScannerView(context);
                }

            };

            contentFrame.addView(mScannerView);
        }

    }

    private String getDeviceType(Warranty warranty) {

        String type;

        if (warranty.getActivationStatus().equals("Enable")) {

            type = "new device";

        } else if (warranty.getActivationStatus().equals("Disable")) {
            type = "disabled device";
        } else if (warranty.getActivationStatus().equals("Enable with Date")) {
            if (warranty.getCustomerName() != null && warranty.getEmail() != null && warranty.getContactNo() != null && warranty.getAddress() != null) {
                if (!warranty.getCustomerName().equals("") && !warranty.getEmail().equals("") && !warranty.getContactNo().equals("") && !warranty.getAddress().equals("")) {
                    type = "sold device";
                } else {
                    type = "activated device";
                }
            } else {
                type = "activated device";
            }
        } else {
            type = "new device";

        }

        return type;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do the
                    mScannerView = new ZXingScannerView(this) {

                        @Override
                        protected IViewFinder createViewFinderView(Context context) {
                            return new CustomZXingScannerView(context);
                        }

                    };

                    contentFrame.addView(mScannerView);

                } else {

                    // permission denied. Disable the
                    // functionality that depends on this permission.
                    // permission was granted, yay! do the
                    Utils.showAlertWithoutTitleDialog(ScannerActivity.this, getString(R.string.no_permission_to_camera), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                }
                return;
            }

        }
    }


    @Override
    public void handleResult(Result rawResult) {

        String barCodeString = rawResult.getText();

        warrantyRequest.setImei(barCodeString);

        new GetProductAsync(selectedBrand).execute();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScannerActivity.this);
            }
        }, 2000);
    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCamera();
    }

    private class RequestWarrantyAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().getWarrantyFromIMEI(ScannerActivity.this, warrantyRequest, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Log.d(TAG, jsonObject.toString());
                    progressDialog.dismiss();

                    String objectOne = null;
                    String objectTwo = null;

                    try {
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        if (success) {
                            objectOne = jsonObject.getString("objectOne");
                            objectTwo = jsonObject.getString("objectTwo");
                            Gson gson = new Gson();
                            Warranty warranty = gson.fromJson(objectOne, Warranty.class);

                            String deviceType = getDeviceType(warranty);


                            if (warranty.getBrand().equals(selectedBrand)) {
                                if (deviceType.equals("new device")) {
                                    Intent intent = new Intent(ScannerActivity.this, NewDeiveActivity.class);
                                    intent.putExtra("waranntyRequest", gson.toJson(warrantyRequest));
                                    intent.putExtra("warrantyString", gson.toJson(warranty));
                                    intent.putExtra("dealerString", objectTwo);
                                    intent.putExtra("type", deviceType);
                                    intent.putExtra("previous_activity", "scan_activity");
                                    startActivity(intent);
                                } else if (deviceType.equals("sold device")) {
                                    Intent intent = new Intent(ScannerActivity.this, DeivceInfoActivity.class);
                                    intent.putExtra("type", deviceType);
                                    intent.putExtra("warrantyString", gson.toJson(warranty));
                                    intent.putExtra("dealerString", objectTwo);
                                    intent.putExtra("previous_activity", "scan_activity");
                                    startActivity(intent);
                                } else if (deviceType.equals("activated device")) {
                                    Intent intent = new Intent(ScannerActivity.this, MessageActivity.class);
                                    intent.putExtra("waranntyRequest", gson.toJson(warrantyRequest));
                                    intent.putExtra("warrantyString", gson.toJson(warranty));
                                    intent.putExtra("dealerString", objectTwo);
                                    intent.putExtra("type", deviceType);
                                    intent.putExtra("previous_activity", "scan_activity");
                                    startActivity(intent);
                                } else if (deviceType.equals("disabled device")) {
                                    Intent intent = new Intent(ScannerActivity.this, MessageActivity.class);
                                    intent.putExtra("type", "disabled device");
                                    intent.putExtra("previous_activity", "scan_activity");
                                    startActivity(intent);
                                }
                            } else {
                                Utils.showAlertWithoutTitleDialog(context, getString(R.string.wrong_imei), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();

                                    }
                                });
                            }


                        } else {

                            if (message.equals("Invalid country") || message.equals("No warranty entry found for given entry")) {
                                Intent intent = new Intent(ScannerActivity.this, MessageActivity.class);
                                intent.putExtra("type", "unauthorized device");
                                intent.putExtra("previous_activity", "scan_activity");
                                startActivity(intent);
                            } else if (message.equals("Unauthorized Device, Please contact Smartwarranty Team")) {
                                Intent intent = new Intent(ScannerActivity.this, MessageActivity.class);
                                intent.putExtra("type", "disabled device");
                                intent.putExtra("previous_activity", "scan_activity");
                                startActivity(intent);
                            } else if (message.equals("Please scan correct brand")) {
                                Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                });
                            }else if (message.equals("Disabled model")) {
                                Utils.showAlertWithoutTitleDialog(context, "Inactive Model", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                });
                            } else {
                                Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startCamera();
                                    }
                                });
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        startCamera();
                    }


                }

                @Override
                public void onError(Context context, String error) {
                    progressDialog.dismiss();

                    Utils.showAlertWithoutTitleDialog(context, getString(R.string.server_error), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startCamera();
                        }
                    });
                }
            });

            return null;
        }
    }

    private class RequestExternalApiWarrantyAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().getExternalApiWarrantyFromIMEI(ScannerActivity.this, warrantyRequest, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Log.d(TAG, jsonObject.toString());
                    progressDialog.dismiss();

                    String objectOne = null;
                    String objectTwo = null;

                    try {
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        if (success) {
                            objectOne = jsonObject.getString("objectOne");
                            objectTwo = jsonObject.getString("objectTwo");
                            Gson gson = new Gson();
                            Warranty warranty = gson.fromJson(objectOne, Warranty.class);

                            String deviceType = getDeviceType(warranty);


                            if (warranty.getBrand().equals(selectedBrand)) {
                                if (deviceType.equals("new device")) {
                                    Intent intent = new Intent(ScannerActivity.this, NewDeiveActivity.class);
                                    intent.putExtra("waranntyRequest", gson.toJson(warrantyRequest));
                                    intent.putExtra("warrantyString", gson.toJson(warranty));
                                    intent.putExtra("dealerString", objectTwo);
                                    intent.putExtra("type", deviceType);
                                    intent.putExtra("previous_activity", "scan_activity");
                                    startActivity(intent);
                                } else if (deviceType.equals("sold device")) {
                                    Intent intent = new Intent(ScannerActivity.this, DeivceInfoActivity.class);
                                    intent.putExtra("type", deviceType);
                                    intent.putExtra("warrantyString", gson.toJson(warranty));
                                    intent.putExtra("dealerString", objectTwo);
                                    intent.putExtra("previous_activity", "scan_activity");
                                    startActivity(intent);
                                } else if (deviceType.equals("activated device")) {
                                    Intent intent = new Intent(ScannerActivity.this, MessageActivity.class);
                                    intent.putExtra("waranntyRequest", gson.toJson(warrantyRequest));
                                    intent.putExtra("warrantyString", gson.toJson(warranty));
                                    intent.putExtra("dealerString", objectTwo);
                                    intent.putExtra("type", deviceType);
                                    intent.putExtra("previous_activity", "scan_activity");
                                    startActivity(intent);
                                } else if (deviceType.equals("disabled device")) {
                                    Intent intent = new Intent(ScannerActivity.this, MessageActivity.class);
                                    intent.putExtra("type", "disabled device");
                                    intent.putExtra("previous_activity", "scan_activity");
                                    startActivity(intent);
                                }
                            } else {
                                Utils.showAlertWithoutTitleDialog(context, getString(R.string.wrong_imei), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();

                                    }
                                });
                            }


                        } else {

                            if (message.equals("Invalid country") || message.equals("No warranty entry found for given entry")) {
                                Intent intent = new Intent(ScannerActivity.this, MessageActivity.class);
                                intent.putExtra("type", "unauthorized device");
                                intent.putExtra("previous_activity", "scan_activity");
                                startActivity(intent);
                            } else if (message.equals("Unauthorized Device, Please contact Smartwarranty Team")) {
                                Intent intent = new Intent(ScannerActivity.this, MessageActivity.class);
                                intent.putExtra("type", "disabled device");
                                intent.putExtra("previous_activity", "scan_activity");
                                startActivity(intent);
                            } else if (message.equals("Please scan correct brand")) {
                                Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                });
                            }else if (message.equals("Disabled model")) {
                                Utils.showAlertWithoutTitleDialog(context, "Inactive Model", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                });
                            } else {
                                Utils.showAlertWithoutTitleDialog(context, message, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startCamera();
                                    }
                                });
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        startCamera();
                    }


                }

                @Override
                public void onError(Context context, String error) {
                    progressDialog.dismiss();

                    Utils.showAlertWithoutTitleDialog(context, getString(R.string.server_error), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startCamera();
                        }
                    });
                }
            });

            return null;
        }
    }


    private class GetProductAsync extends AsyncTask<Void, Void, Void> {

        String productName;


        public GetProductAsync(String productName) {
            this.productName = productName;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ScannerActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.show();

            stopCamera();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().getProduct(ScannerActivity.this, productName, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    Log.d(TAG, jsonObject.toString());

                    String object = null;

                    try {
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        object = jsonObject.getString("object");
                        Gson gson = new Gson();
                        Product product = gson.fromJson(object, Product.class);

                        if (product.isBrandStatus()) {
                            warrantyRequest.setProduct(product);

                            if(product.getApi().equals("internal")){
                                new RequestWarrantyAsync().execute();
                            }else {
                                new RequestExternalApiWarrantyAsync().execute();
                            }

                        } else {
                            Utils.showAlertWithoutTitleDialog(context, "Brand is not active", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        startCamera();
                    }


                }

                @Override
                public void onError(Context context, String error) {
                    progressDialog.dismiss();

                    Utils.showAlertWithoutTitleDialog(context, error, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startCamera();
                        }
                    });
                }
            });

            return null;
        }
    }

    private void startCamera() {
        if (mScannerView != null) {
            mScannerView.setResultHandler(ScannerActivity.this);
            mScannerView.startCamera();
        }
    }

    private void stopCamera() {
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

}
