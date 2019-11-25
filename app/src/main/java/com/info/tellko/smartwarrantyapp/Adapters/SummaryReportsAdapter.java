package com.info.tellko.smartwarrantyapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.info.tellko.smartwarrantyapp.Entities.SummaryReport;
import com.info.tellko.smartwarrantyapp.R;

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
        SummaryReportsAdapter.ViewHolder holder = null;
        // Check if an existing view is being reused, otherwise inflate the view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.summary_reports_layout, null);
        holder = new SummaryReportsAdapter.ViewHolder();
        holder.brand = convertView.findViewById(R.id.brand);
        holder.qty = convertView.findViewById(R.id.tvQty);
        holder.brand = convertView.findViewById(R.id.tvBrand);
        holder.model = convertView.findViewById(R.id.tvModel);

        convertView.setTag(holder);
        convertView.setTag(R.id.tvImei, holder.qty);
        convertView.setTag(R.id.tvBrand, holder.brand);
        convertView.setTag(R.id.tvModel, holder.model);

        holder.qty.setText(String.valueOf(eventObj.getQty()));
        holder.brand.setText(eventObj.getBrandName());
        holder.model.setText(eventObj.getModel());
        return convertView;
    }




    static class ViewHolder {
        protected TextView qty;
        protected TextView brand;
        protected TextView model;

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


