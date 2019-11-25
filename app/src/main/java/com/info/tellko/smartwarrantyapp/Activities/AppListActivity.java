package com.info.tellko.smartwarrantyapp.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.info.tellko.smartwarrantyapp.Adapters.AppAdapter;
import com.info.tellko.smartwarrantyapp.Entities.AppObject;
import com.info.tellko.smartwarrantyapp.R;
import com.info.tellko.smartwarrantyapp.Utils;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {
    List<AppObject> appList;
    AppAdapter adapter;
    LinearLayoutManager MyLayoutManager;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        /**
         * Change status bar color programmatically
         */
        Utils.changeStatusBarColor(AppListActivity.this, getWindow());

        init();
    }

    private void init() {

        appList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycleView);

        MyLayoutManager = new LinearLayoutManager(this);
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        setupAppList();

        adapter = new AppAdapter(appList, AppListActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(MyLayoutManager);

        adapter.setMyClickListener(new AppAdapter.MyClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Utils.navigateWithoutHistory(AppListActivity.this, MainActivity.class);

            }
        });
    }

    private void setupAppList() {
        appList = new ArrayList<>();
        appList.add(new AppObject("Smart Warranty",R.mipmap.smart_warranty_logo_2));
        appList.add(new AppObject("Smart Communication",R.mipmap.smart_communication));
    }

}
