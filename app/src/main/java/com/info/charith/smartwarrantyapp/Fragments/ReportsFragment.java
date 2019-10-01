package com.info.charith.smartwarrantyapp.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.info.charith.smartwarrantyapp.Entities.ActivityReport;
import com.info.charith.smartwarrantyapp.Entities.SummaryReport;
import com.info.charith.smartwarrantyapp.R;

import org.joda.time.DateTime;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ReportsFragment extends Fragment {

    Button selectByDateBtn, selectByClientBtn;
    DateTime fromDate, toDate;
    LinearLayout fromDateLayout, toDateLayout;
    TextView tvFromDate, tvToDate;
    private ActivityReportsAdapter activityReportsAdapter;
    SummaryReportsAdapter summaryReportsAdapter;
    List<ActivityReport> activityReports = new ArrayList<>();
    List<SummaryReport> summaryReports = new ArrayList<>();
    ListView activityReportListView, summaryReportListView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reports, container, false);

        DateTime today = new DateTime();

        activityReports.add(new ActivityReport(today, "charith", "samsung", "s6", "12345667"));
        activityReports.add(new ActivityReport(today.plusDays(1), "charith", "samsung", "s6", "12345667"));
        activityReports.add(new ActivityReport(today.plusDays(2), "charith", "samsung", "s6", "12345667"));
        activityReports.add(new ActivityReport(today.plusDays(3), "charith", "samsung", "s6", "12345667"));
        activityReports.add(new ActivityReport(today.plusDays(4), "charith", "samsung", "s6", "12345667"));

        summaryReports.add(new SummaryReport(today, "samsung", "s6", 3));
        summaryReports.add(new SummaryReport(today.plusDays(2), "samsung", "s6", 3));
        summaryReports.add(new SummaryReport(today.plusDays(3), "samsung", "s6", 3));
        summaryReports.add(new SummaryReport(today.plusDays(5), "samsung", "s6", 3));

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

        activityReportListView = root.findViewById(R.id.activityReportsListView);
        summaryReportListView = root.findViewById(R.id.summaryReportsListView);

        fromDate = new DateTime();
        toDate = fromDate.plusDays(1);

        activityReportsAdapter = new ActivityReportsAdapter(getActivity(), activityReports);
        activityReportListView.setAdapter(activityReportsAdapter);

        summaryReportsAdapter = new SummaryReportsAdapter(getActivity(), summaryReports);
        summaryReportListView.setAdapter(summaryReportsAdapter);

    }

    //Show from date dialog
    public void displayFromDateDialog() {
        showDateTimePicker();

        fromDate=new DateTime(date.getTimeInMillis());
        tvFromDate.setText(fromDate.toString("dd MMM YYYY"));

        if (activityReportsAdapter != null) {
            activityReportsAdapter.filterByFromDate(beginOfDay(fromDate), endOfDay(toDate));
        }

        if (summaryReportsAdapter != null) {
            summaryReportsAdapter.filterByFromDate(beginOfDay(fromDate), endOfDay(toDate));
        }

    }

    //Show to date dialog
    public void displayToDateDialog() {

        toDate = new DateTime(date.getTimeInMillis());

        if (toDate.getMillis() > fromDate.getMillis()) {
            tvToDate.setText(toDate.toString("dd MMM YYYY"));
            if (activityReportsAdapter != null) {
                activityReportsAdapter.filterByToDate(beginOfDay(fromDate), endOfDay(toDate));
            }
            if (summaryReportsAdapter != null) {
                summaryReportsAdapter.filterByToDate(beginOfDay(fromDate), endOfDay(toDate));
            }

        }
    }

    Calendar date;

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
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
    }

    private void selectSummaryReports() {
        selectByClientBtn.setTextColor(getResources().getColor(R.color.charcoal));
        selectByDateBtn.setTextColor(getResources().getColor(R.color.platinum));
        selectByDateBtn.setBackground(getResources().getDrawable(R.drawable.deselect_btn_bg));
        selectByClientBtn.setBackground(getResources().getDrawable(R.drawable.select_btn_bg));

        summaryReportListView.setVisibility(View.VISIBLE);
        activityReportListView.setVisibility(View.GONE);
    }

    //return time as 12.00 am time of given date
    public static DateTime beginOfDay(DateTime date) {
        return date.withTimeAtStartOfDay();
    }

    //return time as 11.59 am time of given date
    public static DateTime endOfDay(DateTime date) {
        return date.plusDays(1).minusMinutes(1);
    }
}
