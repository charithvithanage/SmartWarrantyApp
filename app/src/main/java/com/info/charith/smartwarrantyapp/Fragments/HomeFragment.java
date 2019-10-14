package com.info.charith.smartwarrantyapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.Activities.MainActivity;
import com.info.charith.smartwarrantyapp.Activities.ScannerActivity;
import com.info.charith.smartwarrantyapp.Adapters.BrandAdapter;
import com.info.charith.smartwarrantyapp.AsyncTasks.GetDealerAsync;
import com.info.charith.smartwarrantyapp.Config;
import com.info.charith.smartwarrantyapp.Entities.Dealer;
import com.info.charith.smartwarrantyapp.Entities.DealerUserMock;
import com.info.charith.smartwarrantyapp.Entities.Product;
import com.info.charith.smartwarrantyapp.Interfaces.AsyncListner;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Services.DealerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;

    List<Product> brands;
    BrandAdapter adapter;
    JSONArray jsonArray;
    AtomicInteger atomicInteger;
    LinearLayoutManager MyLayoutManager;

    DealerUserMock dealerUserMock;

    Gson gson = new Gson();

    Dealer dealer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        init(root);

        return root;
    }

    private void init(View root) {

        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String loggedInUser = sharedPref.getString("loggedInUser", null);
        dealerUserMock = gson.fromJson(loggedInUser, DealerUserMock.class);
        String dealerString = sharedPref.getString("userDealer", "0");
        dealer = gson.fromJson(dealerString, Dealer.class);

        jsonArray = new JSONArray();

        brands = new ArrayList<>();
        recyclerView = root.findViewById(R.id.recycleView);

        MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if(Config.Instance.getEnabledBrands()!=null){
            brands=Config.Instance.getEnabledBrands();
        }else {
            String enabledBrandsString = sharedPref.getString("enabledBrands", "0");

            try {
                jsonArray = new JSONArray(enabledBrandsString);
                if (jsonArray.length() > 0) {
                    brands = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Product dealer = gson.fromJson(jsonArray.getString(i), Product.class);
                        brands.add(dealer);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new BrandAdapter(brands, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(MyLayoutManager);

        adapter.setMyClickListener(new BrandAdapter.MyClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Product selectedBrand = brands.get(position);
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                intent.putExtra("selected_brand", selectedBrand.getBrandName());
                startActivity(intent);
            }
        });
    }

}