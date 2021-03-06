package tellko.smarthub.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tellko.smarthub.Activities.ChangePasswordActivity;
import tellko.smarthub.R;

public class SettingsFragment extends Fragment {

    private static final String TAG ="SmartWarrantyApp" ;
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
                intent.putExtra("activityName",getActivity().getClass().getSimpleName());
                Log.d(TAG,getActivity().getClass().getSimpleName() );
                startActivity(intent);
            }
        });
    }


}
