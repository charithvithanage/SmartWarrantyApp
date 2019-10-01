package com.info.charith.smartwarrantyapp.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.info.charith.smartwarrantyapp.Entities.SummaryReport;
import com.info.charith.smartwarrantyapp.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class SummaryReportsAdapter extends ArrayAdapter<SummaryReport> {

    List<SummaryReport> filteredSummaryReports;
    List<SummaryReport> eventList;
    Context context;

    public SummaryReportsAdapter(Context context, List<SummaryReport> eventList) {
        super(context, 0, eventList);
        this.eventList = eventList;
        this.filteredSummaryReports = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SummaryReport eventObj = getItem(position);
        com.info.charith.smartwarrantyapp.Fragments.SummaryReportsAdapter.ViewHolder holder = null;
        // Check if an existing view is being reused, otherwise inflate the view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.summary_reports_layout, null);
        holder = new com.info.charith.smartwarrantyapp.Fragments.SummaryReportsAdapter.ViewHolder();
        holder.brand = convertView.findViewById(R.id.brand);

        convertView.setTag(holder);
        convertView.setTag(R.id.brand, holder.brand);

        holder.brand.setText(eventObj.getBrand());

        return convertView;
    }


    public void filterByFromDate(DateTime fromDate, DateTime toDate) {

        filteredSummaryReports = new ArrayList<SummaryReport>();
        if (this.eventList.size() != 0 || this.eventList != null) {
            for (SummaryReport wp : this.eventList) {
                if (wp.getDate().isAfter(fromDate.getMillis()) && wp.getDate().isBefore(toDate.getMillis())) {
                    filteredSummaryReports.add(wp);
                } else if (wp.getDate().isEqual(fromDate.getMillis()) && wp.getDate().isBefore(toDate.getMillis())) {
                    filteredSummaryReports.add(wp);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void filterByToDate(DateTime fromDate, DateTime toDate) {

        filteredSummaryReports = new ArrayList<SummaryReport>();
        if (this.eventList.size() != 0 || this.eventList != null) {
            for (SummaryReport wp : this.eventList) {
                if (wp.getDate().isAfter(fromDate.getMillis()) && wp.getDate().isBefore(toDate.getMillis())) {
                    filteredSummaryReports.add(wp);
                } else if (wp.getDate().isEqual(fromDate.getMillis()) && wp.getDate().isBefore(toDate.getMillis())) {
                    filteredSummaryReports.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        protected TextView brand;

    }

    @Override
    public int getCount() {
        int size;
        if (filteredSummaryReports != null) {
            size = filteredSummaryReports.size();
        } else {
            size = 0;
        }
        return size;
    }

    @Nullable
    @Override
    public SummaryReport getItem(int position) {
        return filteredSummaryReports.get(position);
    }

}


