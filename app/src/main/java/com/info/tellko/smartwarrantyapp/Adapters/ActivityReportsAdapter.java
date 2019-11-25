package com.info.tellko.smartwarrantyapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.info.tellko.smartwarrantyapp.Entities.Warranty;
import com.info.tellko.smartwarrantyapp.R;

import java.util.List;

import static com.info.tellko.smartwarrantyapp.Utils.getActivationTime;

public class ActivityReportsAdapter extends ArrayAdapter<Warranty> {
    List<Warranty> filteredActivityReports;
    List<Warranty> eventList;
    Context context;

    public ActivityReportsAdapter(Context context, List<Warranty> eventList) {
        super(context, 0, eventList);
        this.eventList = eventList;
        this.filteredActivityReports = eventList;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Warranty eventObj = getItem(position);
        ViewHolder holder = null;
        // Check if an existing view is being reused, otherwise inflate the view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.activity_reports_layout, null);
        holder = new ViewHolder();
        holder.imei = convertView.findViewById(R.id.tvImei);
        holder.brand = convertView.findViewById(R.id.tvBrand);
        holder.model = convertView.findViewById(R.id.tvModel);
        holder.user = convertView.findViewById(R.id.tvUser);
        holder.date = convertView.findViewById(R.id.tvDate);

        convertView.setTag(holder);
        convertView.setTag(R.id.tvImei, holder.imei);
        convertView.setTag(R.id.tvBrand, holder.brand);
        convertView.setTag(R.id.tvModel, holder.model);
        convertView.setTag(R.id.tvUser, holder.user);
        convertView.setTag(R.id.tvDate, holder.date);

        holder.imei.setText(eventObj.getImei());
        holder.brand.setText(eventObj.getBrand());
        holder.model.setText(eventObj.getModel());
        holder.user.setText(eventObj.getDealerUserName());
        holder.date.setText(eventObj.getWarrantyActivatedDate()+"  "+getActivationTime(eventObj.getActivationTime()));


        return convertView;
    }

    static class ViewHolder {
        protected TextView imei;
        protected TextView brand;
        protected TextView model;
        protected TextView user;
        protected TextView date;

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
    public Warranty getItem(int position) {
        return filteredActivityReports.get(position);
    }

}
