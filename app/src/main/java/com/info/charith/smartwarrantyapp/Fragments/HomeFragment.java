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

        new GetDealerAsync(getActivity(), dealerUserMock.getDealerCode(), new AsyncListner() {
            @Override

            public void onSuccess(Context context, JSONObject jsonObject) {
                String objectOne = null;

                try {
                    boolean success = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");

                    if (success) {
                        objectOne = jsonObject.getString("object");
                        Gson gson = new Gson();
                        dealer = gson.fromJson(objectOne, Dealer.class);

                        SharedPreferences sharedPref = context.getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("userDealer", gson.toJson(dealer));
                        editor.commit();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArray = new JSONArray(dealer.getEnableBrands());

//                    jsonArray = sortBrands(new JSONArray(dealer.getEnableBrands()));
                atomicInteger = new AtomicInteger(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        new GetProductAsync(jsonArray.getString(i)).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Context context, String error) {
//                jsonArray = sortBrands(new JSONArray(dealer.getEnableBrands()));
                jsonArray = new JSONArray(dealer.getEnableBrands());
                atomicInteger = new AtomicInteger(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        new GetProductAsync(jsonArray.getString(i)).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute();

        brands = new ArrayList<>();
        recyclerView = root.findViewById(R.id.recycleView);

        MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


    }

    private JSONArray sortBrands(JSONArray brands) {

        JSONArray tempBrands = new JSONArray();

        for (int i = 0; i < brands.length(); i++) {
//            try {
//                if (brands.getString(i).startsWith("T")) {
//                    tempBrands.put(0, brands.getString(i));
//                } else if (brands.getString(i).startsWith("X")) {
//                    tempBrands.put(1, brands.get(i));
//                } else if (brands.getString(i).startsWith("S")) {
//                    tempBrands.put(2, brands.get(i));
//                } else if (brands.getString(i).startsWith("H")) {
//                    tempBrands.put(3, brands.get(i));
//                } else {
//                    if (i > 2) {
//                        tempBrands.put(i, brands.get(i));
//                    } else {
//                        tempBrands.put((i + 3), brands.get(i));
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            try {
                if (brands.getString(i).startsWith("T")) {
                    tempBrands.put(0, brands.getString(i));
                } else if (brands.getString(i).startsWith("X")) {
                    tempBrands.put(1, brands.get(i));
                } else {
                    tempBrands.put(brands.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tempBrands;

    }

    private class GetProductAsync extends AsyncTask<Void, Void, Void> {

        String productName;

        public GetProductAsync(String productName) {
            this.productName = productName;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            DealerService.getInstance().getProduct(getActivity(), productName, new AsyncListner() {
                @Override
                public void onSuccess(Context context, JSONObject jsonObject) {

                    String object = null;

                    try {
                        object = jsonObject.getString("object");
                        Gson gson = new Gson();
                        Product product = gson.fromJson(object, Product.class);

                        brands.add(product);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (atomicInteger.decrementAndGet() == 0) {
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

                @Override
                public void onError(Context context, String error) {

                }
            });

            return null;
        }
    }

//    private class GetDealerAsync extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            jsonArray=new JSONArray();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            DealerService.getInstance().getDealerFromDealerCode(getActivity(), dealerUserMock.getDealerCode(), new AsyncListner() {
//                @Override
//                public void onSuccess(Context context, JSONObject jsonObject) {
//                    String objectOne = null;
//
//                    try {
//                        boolean success = jsonObject.getBoolean("success");
//                        String message = jsonObject.getString("message");
//
//                        if (success) {
//                            objectOne = jsonObject.getString("object");
//                            Gson gson = new Gson();
//                            dealer = gson.fromJson(objectOne, Dealer.class);
//
//                            SharedPreferences sharedPref = context.getSharedPreferences(
//                                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPref.edit();
//                            editor.putString("userDealer", gson.toJson(dealer));
//                            editor.commit();
//                        }
//
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    jsonArray = new JSONArray(dealer.getEnableBrands());
//
////                    jsonArray = sortBrands(new JSONArray(dealer.getEnableBrands()));
//                    atomicInteger = new AtomicInteger(jsonArray.length());
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        try {
//                            new GetProductAsync(jsonArray.getString(i)).execute();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onError(Context context, String error) {
////                    jsonArray = sortBrands(new JSONArray(dealer.getEnableBrands()));
//                    jsonArray = new JSONArray(dealer.getEnableBrands());
//                    atomicInteger = new AtomicInteger(jsonArray.length());
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        try {
//                            new GetProductAsync(jsonArray.getString(i)).execute();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//
//            return null;
//        }
//    }


}