package com.info.charith.smartwarrantyapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.info.charith.smartwarrantyapp.Activities.MainActivity;
import com.info.charith.smartwarrantyapp.BuildConfig;
import com.info.charith.smartwarrantyapp.R;

public class AboutFragment extends Fragment {
    TextView tvAppVersion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, container, false);

        init(root);


        return root;
    }

    private void init(View root) {
        tvAppVersion = root.findViewById(R.id.tvAppVersion);
        tvAppVersion.setText("App Version " + BuildConfig.VERSION_NAME + "." + String.valueOf(BuildConfig.VERSION_CODE));

    }



}
