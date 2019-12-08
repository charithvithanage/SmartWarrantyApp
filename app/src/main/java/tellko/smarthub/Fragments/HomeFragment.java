package tellko.smarthub.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import tellko.smarthub.Activities.ScannerActivity;
import tellko.smarthub.Adapters.BrandAdapter;
import tellko.smarthub.Config;
import tellko.smarthub.Entities.Dealer;
import tellko.smarthub.Entities.DealerUser;
import tellko.smarthub.Entities.Product;
import tellko.smarthub.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static tellko.smarthub.Utils.isDeviceOnline;
import static tellko.smarthub.Utils.showAlertWithoutTitleDialog;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;

    List<Product> brands;
    BrandAdapter adapter;
    JSONArray jsonArray;
    AtomicInteger atomicInteger;
    LinearLayoutManager MyLayoutManager;

    DealerUser dealerUserMock;

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
        dealerUserMock = gson.fromJson(loggedInUser, DealerUser.class);
        String dealerString = sharedPref.getString("userDealer", "0");
        dealer = gson.fromJson(dealerString, Dealer.class);

        jsonArray = new JSONArray();

        brands = new ArrayList<>();
        recyclerView = root.findViewById(R.id.recycleView);

        MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (Config.Instance.getEnabledBrands() != null) {
            brands = Config.Instance.getEnabledBrands();
        } else {
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

                if (isDeviceOnline(getActivity())) {
                    Product selectedBrand = brands.get(position);
                    Intent intent = new Intent(getActivity(), ScannerActivity.class);
                    intent.putExtra("selected_brand", selectedBrand.getBrandName());
                    startActivity(intent);
                } else {
                    showAlertWithoutTitleDialog(getActivity(), getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }


            }
        });
    }

}