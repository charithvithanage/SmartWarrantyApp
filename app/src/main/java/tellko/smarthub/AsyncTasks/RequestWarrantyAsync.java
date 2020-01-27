package tellko.smarthub.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import tellko.smarthub.Entities.WarrantyRequest;
import tellko.smarthub.Interfaces.AsyncListner;
import tellko.smarthub.Services.DealerService;

public class RequestWarrantyAsync extends AsyncTask<Void, Void, Void> {
    Context context;
    WarrantyRequest warrantyRequest;
    AsyncListner listner;

    public RequestWarrantyAsync(Context context, WarrantyRequest warrantyRequest, AsyncListner listner) {
        this.context = context;
        this.warrantyRequest = warrantyRequest;
        this.listner = listner;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        DealerService.getInstance().getWarrantyFromIMEI(context, warrantyRequest, new AsyncListner() {
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

