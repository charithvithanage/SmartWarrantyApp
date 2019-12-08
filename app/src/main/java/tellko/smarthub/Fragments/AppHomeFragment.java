package tellko.smarthub.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tellko.smarthub.Activities.MainActivity;
import tellko.smarthub.Adapters.AppAdapter;
import tellko.smarthub.Entities.AppObject;
import tellko.smarthub.R;
import tellko.smarthub.Utils;

import static tellko.smarthub.Utils.convertImageToBase64;
import static tellko.smarthub.Utils.navigateToAnotherActivity;

public class AppHomeFragment extends Fragment {
    List<AppObject> appList;
    AppAdapter adapter;
    LinearLayoutManager MyLayoutManager;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_app_home, container, false);

        init(root);

        return root;
    }

    private void init(View root) {

        appList = new ArrayList<>();
        recyclerView = root.findViewById(R.id.recycleView);

        MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        setupAppList();

        adapter = new AppAdapter(appList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(MyLayoutManager);

        adapter.setMyClickListener(new AppAdapter.MyClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                AppObject appObject=appList.get(position);

                if(appObject.getAppName().equals("Smart Warranty")){
                    navigateToAnotherActivity(getActivity(), MainActivity.class);
                }

            }
        });
    }

    private void setupAppList() {
        appList = new ArrayList<>();
        appList.add(new AppObject("Smart Warranty",R.mipmap.test_sw));
        appList.add(new AppObject("Smart Communication",R.mipmap.test_sc));
    }
}