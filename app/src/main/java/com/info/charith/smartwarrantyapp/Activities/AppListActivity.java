package com.info.charith.smartwarrantyapp.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.info.charith.smartwarrantyapp.Adapters.AppAdapter;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Utils;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {
    List<String> appList;
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
        appList.add("Smart Warranty");
    }

}
