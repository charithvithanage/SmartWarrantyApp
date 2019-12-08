package tellko.smarthub.AsyncTasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONObject;

import tellko.smarthub.Activities.AppListActivity;
import tellko.smarthub.Activities.LoginActivity;
import tellko.smarthub.Entities.DealerUser;
import tellko.smarthub.Interfaces.AsyncListner;
import tellko.smarthub.R;
import tellko.smarthub.Services.DealerService;
import tellko.smarthub.Utils;

public class LogoutAsync extends AsyncTask<Void, Void, Void> {

    ProgressDialog progressDialog;
    Context context;
    DealerUser dealerUserMock;
    AsyncListner listner;

    public LogoutAsync(Context context, DealerUser dealerUser, AsyncListner listner) {
        this.context = context;
        this.dealerUserMock = dealerUser;
        this.listner = listner;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.waiting));
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        DealerService.getInstance().logout(context, dealerUserMock.getUsername(), new AsyncListner() {
            @Override
            public void onSuccess(Context context, JSONObject jsonObject) {

                progressDialog.dismiss();
                listner.onSuccess(context, jsonObject);
            }

            @Override
            public void onError(Context context, String error) {
                progressDialog.dismiss();
                listner.onError(context, error);
//                    Utils.navigateWithoutHistory(context, LoginActivity.class);
            }
        });

        return null;
    }
}
