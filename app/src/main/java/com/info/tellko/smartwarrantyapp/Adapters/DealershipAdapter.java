package com.info.tellko.smartwarrantyapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.info.tellko.smartwarrantyapp.Entities.Dealer;
import com.info.tellko.smartwarrantyapp.R;

import java.util.List;

public class DealershipAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Dealer> list;
    Context context;
    private static MyClickListener myClickListener;


    public DealershipAdapter(List<Dealer> Data, Context context) {
        list = Data;
        this.context = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dealership_layout, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (list.size() != 0) {
            Dealer dealer = list.get(position);
            if (dealer != null) {
                ((MyViewHolder) holder).dealerName.setText(dealer.getDealerName());
                ((MyViewHolder) holder).dealerCity.setText(dealer.getCity());
            }
        }


    }

    public void setMyClickListener(MyClickListener myClickListener) {
        DealershipAdapter.myClickListener = myClickListener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface MyClickListener {
        void onItemClick(View v, int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView dealerName;
        TextView dealerCity;

        public MyViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            dealerName = v.findViewById(R.id.dealerName);
            dealerCity = v.findViewById(R.id.tvCity);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}