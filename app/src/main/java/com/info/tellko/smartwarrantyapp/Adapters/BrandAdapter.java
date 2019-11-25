package com.info.tellko.smartwarrantyapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.info.tellko.smartwarrantyapp.Entities.Product;
import com.info.tellko.smartwarrantyapp.R;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Product> list;
    Context context;
    private static MyClickListener myClickListener;


    public BrandAdapter(List<Product> Data, Context context) {
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
            Product brand = list.get(position);
            if (brand != null) {
                if (brand.getImgBase64() != null) {
                    byte[] decodedString = Base64.decode(brand.getImgBase64(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ((MyViewHolder) holder).imgView.setImageBitmap(decodedByte);
                    ((MyViewHolder) holder).tv.setVisibility(View.GONE);
                    ((MyViewHolder) holder).imgView.setVisibility(View.VISIBLE);


                } else {
                    ((MyViewHolder) holder).imgView.setVisibility(View.GONE);
                    ((MyViewHolder) holder).tv.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).tv.setText(brand.getBrandName());

                }
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
        TextView tv;


        public MyViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            imgView = v.findViewById(R.id.imgView);
            tv = v.findViewById(R.id.tv);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}