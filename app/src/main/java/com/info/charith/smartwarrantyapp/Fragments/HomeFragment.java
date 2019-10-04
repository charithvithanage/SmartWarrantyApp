package com.info.charith.smartwarrantyapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.info.charith.smartwarrantyapp.Adapters.BrandAdapter;
import com.info.charith.smartwarrantyapp.Entities.Brand;
import com.info.charith.smartwarrantyapp.Entities.Dealer;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Activities.ScannerActivity;
import com.info.charith.smartwarrantyapp.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;

    List<Brand> brands;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

       init(root);

        return root;
    }

    private void init(View root) {
        brands=new ArrayList<>();
        recyclerView=root.findViewById(R.id.recycleView);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Gson gson=new Gson();

        String dealerString=sharedPref.getString("userDealer","0");
        Dealer dealer=gson.fromJson(dealerString,Dealer.class);

        JSONArray jsonArray=new JSONArray(dealer.getEnableBrands());

        brands.add(new Brand("Tellko", Utils.GetImage(getActivity(),"Tellko".toLowerCase())));
        brands.add(new Brand("Xiaomi", Utils.GetImage(getActivity(),"Xiaomi".toLowerCase())));
        brands.add(new Brand("Samsung", Utils.GetImage(getActivity(),"Samsung".toLowerCase())));
        brands.add(new Brand("Huawei", Utils.GetImage(getActivity(),"Huawei".toLowerCase())));

//        for (int i=0;i<jsonArray.length();i++){
//
//            try {
//
//
//                Brand brand=new Brand(jsonArray.getString(i), Utils.GetImage(getActivity(),jsonArray.getString(i).toLowerCase()));
//
//
////                if(brand.getName().toLowerCase().startsWith("t")){
////                    brands.add(0,brand);
////
////                }else if(brand.getName().toLowerCase().startsWith("x")){
////                    brands.add(1,brand);
////
////                }else if(brand.getName().toLowerCase().startsWith("s")){
////                    brands.add(2,brand);
////
////                }else if(brand.getName().toLowerCase().startsWith("h")){
////                    brands.add(3,brand);
////
////                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }


        final BrandAdapter adapter = new BrandAdapter(brands, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(MyLayoutManager);

        adapter.setMyClickListener(new BrandAdapter.MyClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Brand selectedBrand=brands.get(position);
                Intent intent=new Intent(getActivity(), ScannerActivity.class);
                intent.putExtra("selected_brand",selectedBrand.getName());
                startActivity(intent);
            }
        });
    }


}