package com.info.charith.smartwarrantyapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.info.charith.smartwarrantyapp.Entities.Brand;
import com.info.charith.smartwarrantyapp.R;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Brand> list;
    Context context;
    private static MyClickListener myClickListener;


    public BrandAdapter(List<Brand> Data, Context context) {
        list = Data;
        this.context = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.brand_layout, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (list.size() != 0) {
            Brand brand = list.get(position);
            if (brand != null) {
                ((MyViewHolder) holder).imgView.setImageDrawable(brand.getImageResource());
            }
        }


    }

    public void setMyClickListener(MyClickListener myClickListener) {
        BrandAdapter.myClickListener = myClickListener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface MyClickListener {
        void onItemClick(View v, int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgView;


        public MyViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            imgView = v.findViewById(R.id.imgView);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}