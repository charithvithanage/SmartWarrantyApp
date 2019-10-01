package com.info.charith.smartwarrantyapp.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.info.charith.smartwarrantyapp.Entities.ActivityReport;
import com.info.charith.smartwarrantyapp.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ActivityReportsAdapter extends ArrayAdapter<ActivityReport> {
    List<ActivityReport> filteredActivityReports;
    List<ActivityReport> eventList;
    Context context;

    public ActivityReportsAdapter(Context context, List<ActivityReport> eventList) {
        super(context, 0, eventList);
        this.eventList = eventList;
        this.filteredActivityReports = eventList;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ActivityReport eventObj = getItem(position);
        ViewHolder holder = null;
        // Check if an existing view is being reused, otherwise inflate the view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.activity_reports_layout, null);
        holder = new ViewHolder();
        holder.imei = convertView.findViewById(R.id.tvIMEI);

        convertView.setTag(holder);
        convertView.setTag(R.id.tvIMEI, holder.imei);

        holder.imei.setText(eventObj.getImei());

        return convertView;
    }



    public void filterByFromDate(DateTime fromDate, DateTime toDate) {

        filteredActivityReports = new ArrayList<ActivityReport>();
        if (this.eventList.size() != 0 || this.eventList != null) {
            for (ActivityReport wp : this.eventList) {
                if (wp.getDate().isAfter(fromDate.getMillis()) && wp.getDate().isBefore(toDate.getMillis())) {
                    filteredActivityReports.add(wp);
                } else if (wp.getDate().isEqual(fromDate.getMillis()) && wp.getDate().isBefore(toDate.getMillis())) {
                    filteredActivityReports.add(wp);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void filterByToDate(DateTime fromDate, DateTime toDate) {

        filteredActivityReports = new ArrayList<ActivityReport>();
        if (this.eventList.size() != 0 || this.eventList != null) {
            for (ActivityReport wp : this.eventList) {
                if (wp.getDate().isAfter(fromDate.getMillis()) && wp.getDate().isBefore(toDate.getMillis())) {
                    filteredActivityReports.add(wp);
                } else if (wp.getDate().isEqual(fromDate.getMillis()) && wp.getDate().isBefore(toDate.getMillis())) {
                    filteredActivityReports.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        protected TextView imei;

    }

    @Override
    public int getCount() {
        int size;
        if (filteredActivityReports != null) {
            size = filteredActivityReports.size();
        } else {
            size = 0;
        }
        return size;
    }

    @Nullable
    @Override
    public ActivityReport getItem(int position) {
        return filteredActivityReports.get(position);
    }

}
