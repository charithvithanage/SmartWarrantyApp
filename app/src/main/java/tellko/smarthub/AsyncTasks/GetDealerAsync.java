package tellko.smarthub.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import tellko.smarthub.Interfaces.AsyncListner;
import tellko.smarthub.Services.DealerService;

public class GetDealerAsync extends AsyncTask<Void, Void, Void> {

    Context context;
    AsyncListner listner;
    String dealerCode;

    public GetDealerAsync(Context context,  String dealerCode,AsyncListner listner) {
        this.context = context;
        this.listner = listner;
        this.dealerCode = dealerCode;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        DealerService.getInstance().getDealerFromDealerCode(context, dealerCode, listner);

        return null;
    }
}

