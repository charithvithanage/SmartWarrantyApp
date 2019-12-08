package tellko.smarthub.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import tellko.smarthub.Interfaces.AsyncListner;
import tellko.smarthub.Services.DealerService;

import org.json.JSONObject;

public class GetProductsAsync extends AsyncTask<Void, Void, Void> {

    String dealerCode;
    Gson gson = new Gson();
    Context context;
    AsyncListner listner;

    public GetProductsAsync(Context context, String dealerCode, AsyncListner listner) {
        this.dealerCode = dealerCode;
        this.context = context;
        this.listner = listner;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        DealerService.getInstance().getProductList(context, dealerCode, new AsyncListner() {
            @Override
            public void onSuccess(Context context, JSONObject jsonObject) {
                listner.onSuccess(context, jsonObject);
            }

            @Override
            public void onError(Context context, String error) {
                listner.onError(context, error);
            }
        });
        return null;
    }
}
