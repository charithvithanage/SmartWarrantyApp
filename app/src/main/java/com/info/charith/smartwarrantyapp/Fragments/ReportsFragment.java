package com.info.charith.smartwarrantyapp.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.Activities.DeivceInfoActivity;
import com.info.charith.smartwarrantyapp.Adapters.ActivityReportsAdapter;
import com.info.charith.smartwarrantyapp.Adapters.SummaryReportsAdapter;
import com.info.charith.smartwarrantyapp.Entities.SummaryReport;
import com.info.charith.smartwarrantyapp.Entities.Warranty;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.DealerService;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportsFragment extends Fragment {

    Button selectByDateBtn, selectByClientBtn;
    DateTime fromDate, toDate;
    LinearLayout fromDateLayout, toDateLayout;
    TextView tvFromDate, tvToDate;
    private ActivityReportsAdapter activityReportsAdapter;
    SummaryReportsAdapter summaryReportsAdapter;
    List<Warranty> activityReports = new ArrayList<>();
    List<SummaryReport> summaryReports = new ArrayList<>();
    ListView activityReportListView, summaryReportListView;
    ProgressDialog progressDialog;
    TextView tvTotal;
    String activationReportsSize, summaryReportsSize = null;
    boolean showActivationLayout = true;
    TextView emptyActivityLable, emptySummartLable;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reports, container, false);

        init(root);


        selectByDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActivityReports();
            }
        });
        selectByClientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSummaryReports();

            }
        });

        fromDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterFromDateLayout();
            }
        });
        toDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterToDateLayout();
            }
        });

        selectActivityReports();

        return root;
    }

    private void init(View root) {

        selectByDateBtn = root.findViewById(R.id.selectByDateBtn);
        selectByClientBtn = root.findViewById(R.id.selectByClientBtn);
        fromDateLayout = root.findViewById(R.id.fromDateLayout);
        toDateLayout = root.findViewById(R.id.toDateLayout);
        tvFromDate = root.findViewById(R.id.tvFromDate);
        tvToDate = root.findViewById(R.id.tvToDate);
        tvTotal = root.findViewById(R.id.tvTotal);
        emptyActivityLable = root.findViewById(R.id.emptyActivityLable);
        emptySummartLable = root.findViewById(R.id.emptySummaryLable);

        activityReportListView = root.findViewById(R.id.activityReportsListView);
        summaryReportListView = root.findViewById(R.id.summaryReportsListView);


        activityReportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Warranty warranty = activityReportsAdapter.getItem(position);
                Gson gson = new Gson();
                Intent intent = new Intent(getActivity(), DeivceInfoActivity.class);
                intent.putExtra("type", "Warranty Details");
                intent.putExtra("warrantyString", gson.toJson(warranty));
                intent.putExtra("previous_activity", "activation_list_activity");
                getActivity().startActivity(intent);

            }
        });

        fromDate = new DateTime();
//        fromDate = fromDate.minusDays(1);
        toDate = new DateTime();

        tvFromDate.setText(toDate.toString("dd MMM YYYY"));
        tvToDate.setText(toDate.toString("dd MMM YYYY"));

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.waiting));

        showActivationLayout = true;

        new GetActivityReportsAsync().execute();
        new GetSummaryReportsAsync().execute();

    }

    //Show from date dialog
    public void displayFromDateDialog() {

        final Calendar date;

        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                fromDate = new DateTime(date.getTimeInMillis());
                tvFromDate.setText(fromDate.toString("dd MMM YYYY"));
                new GetSummaryReportsAsync().execute();
                new GetActivityReportsAsync().execute();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();


    }

    //Show to date dialog
    public void displayToDateDialog() {
        final Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                toDate = new DateTime(date.getTimeInMillis());
                tvToDate.setText(toDate.toString("dd MMM YYYY"));
                new GetSummaryReportsAsync().execute();
                new GetActivityReportsAsync().execute();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();


    }

    private void showFilterFromDateLayout() {
        displayFromDateDialog();
    }

    private void showFilterToDateLayout() {
        displayToDateDialog();
    }

    private void selectActivityReports() {
        selectByDateBtn.setTextColor(getResources().getColor(R.color.charcoal));
        selectByClientBtn.setTextColor(getResources().getColor(R.color.platinum));
        selectByDateBtn.setBackground(getResources().getDrawable(R.drawable.select_btn_bg));
        selectByClientBtn.setBackground(getResources().getDrawable(R.drawable.deselect_btn_bg));

        activityReportListView.setVisibility(View.VISIBLE);
        summaryReportListView.setVisibility(View.GONE);
        emptySummartLable.setVisibility(View.GONE);
        showActivationLayout = true;
        tvTotal.setText(activationReportsSize);
        if (activityReports.size() == 0) {
            emptyActivityLable.setVisibility(View.VISIBLE);
        } else {
            emptyActivityLable.setVisibility(View.GONE);
        }



    }

    private void selectSummaryReports() {
        selectByClientBtn.setTextColor(getResources().getColor(R.color.charcoal));
        selectByDateBtn.setTextColor(getResources().getColor(R.color.platinum));
        selectByDateBtn.setBackground(getResources().getDrawable(R.drawable.deselect_btn_bg));
        selectByClientBtn.setBackground(getResources().getDrawable(R.drawable.select_btn_bg));
        showActivationLayout = false;
        summaryReportListView.setVisibility(View.VISIBLE);
        activityReportListView.setVisibility(View.GONE);
        emptyActivityLable.setVisibility(View.GONE);
        tvTotal.setText(summaryReportsSize);
        if (summaryReports.size() == 0) {
            emptySummartLable.setVisibility(View.VISIBLE);
        } else {
            emptySummartLable.setVisibility(View.GONE);
        }
    }

    private class GetActivityReportsAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().getActivityReports(getActivity(), fromDate.toString("yyyy-MM-dd"), toDate.toString("yyyy-MM-dd"), new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    activityReports = new ArrayList<>();

                    try {

                        String objectString = jsonObject.getString("object");
                        boolean success = jsonObject.getBoolean("success");
                        Gson gson = new Gson();

                        if (success) {
                            JSONArray jsonArray = new JSONArray(objectString);

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Warranty activityReport = gson.fromJson(jsonArray.getString(i), Warranty.class);
                                    activityReports.add(activityReport);
                                }
                            }
                        }

                        activationReportsSize = String.valueOf(activityReports.size());

                        if (showActivationLayout) {
                            tvTotal.setText(activationReportsSize);
                            if (activityReports.size() == 0) {
                                emptyActivityLable.setVisibility(View.VISIBLE);
                            }else {
                                emptyActivityLable.setVisibility(View.GONE);

                            }
                        }

                        activityReportsAdapter = new ActivityReportsAdapter(getActivity(), activityReports);
                        activityReportListView.setAdapter(activityReportsAdapter);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onError(Context context, String error) {
                    progressDialog.dismiss();
                }
            });

            return null;
        }
    }

    private class GetSummaryReportsAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().getSummaryReports(getActivity(), fromDate.toString("yyyy-MM-dd"), toDate.toString("yyyy-MM-dd"), new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {
                    summaryReports = new ArrayList<>();

                    try {

                        String objectString = jsonObject.getString("object");
                        boolean success = jsonObject.getBoolean("success");
                        Gson gson = new Gson();

                        if (success) {
                            JSONArray jsonArray = new JSONArray(objectString);

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    SummaryReport summaryReport = gson.fromJson(jsonArray.getString(i), SummaryReport.class);
                                    summaryReports.add(summaryReport);
                                }
                            }
                        }

                        summaryReportsSize = String.valueOf(summaryReports.size());


                        if (!showActivationLayout) {
                            tvTotal.setText(summaryReportsSize);
                            if (summaryReports.size() == 0) {
                                emptySummartLable.setVisibility(View.VISIBLE);
                            }else {
                                emptySummartLable.setVisibility(View.GONE);
                            }
                        }

                        summaryReportsAdapter = new SummaryReportsAdapter(getActivity(), summaryReports);
                        summaryReportListView.setAdapter(summaryReportsAdapter);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(Context context, String error) {

                }
            });

            return null;
        }
    }

}
