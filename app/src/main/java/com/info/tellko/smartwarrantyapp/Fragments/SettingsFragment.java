package com.info.tellko.smartwarrantyapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.info.tellko.smartwarrantyapp.Activities.ChangePasswordActivity;
import com.info.tellko.smartwarrantyapp.R;

public class SettingsFragment extends Fragment {

    LinearLayout btnChangePassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        init(root);


        return root;
    }


    private void init(View view) {
        btnChangePassword = view.findViewById(R.id.change_password);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }


}
