package tellko.smarthub.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import tellko.smarthub.Config;
import tellko.smarthub.Entities.Dealer;
import tellko.smarthub.Entities.DealerRequest;
import tellko.smarthub.Entities.WarrantyRequest;
import tellko.smarthub.Interfaces.AsyncListner;
import tellko.smarthub.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DealerService {
    private static final String TAG = "SmartWarrantyApp";

    Gson gson = new Gson();


    private static DealerService dealerService = new DealerService();

    public DealerService() {

    }

    public static DealerService getInstance() {
        return dealerService;
    }


    public void getDealersFromNIC(final Context context, DealerRequest dealerRequest, final AsyncListner callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String jsonString = gson.toJson(dealerRequest);
        Log.d(TAG, jsonString);
        Log.d(TAG, Config.get_dealers_url);

        JSONObject object = null;

        try {
            object = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.get_dealers_url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(context, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(context, error.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }

    public void getActivityReports(final Context context, final String fromDate, final String toDate, final AsyncListner callback) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String accessToken = sharedPref.getString("accessToken", null);

        RequestQueue queue = Volley.newRequestQueue(context);

        String uri = String.format(Config.get_activity_reports_url + "?from=%1$s&to=%2$s",
                fromDate,
                toDate);
        Log.d(TAG, uri);
        Log.d(TAG, accessToken);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(context, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(context, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + accessToken);


                return headers;
            }


        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }

    public void getSummaryReports(final Context context, final String fromDate, final String toDate, final AsyncListner callback) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String accessToken = sharedPref.getString("accessToken", null);
        Gson gson = new Gson();
        Dealer dealer = gson.fromJson(sharedPref.getString("userDealer", null), Dealer.class);

        RequestQueue queue = Volley.newRequestQueue(context);

        String uri = String.format(Config.get_summary_reports_url + dealer.getDealerCode() + "?from=%1$s&to=%2$s",
                fromDate,
                toDate);
        Log.d(TAG, uri);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(context, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(context, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + accessToken);


                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }


    public void getWarrantyFromIMEI(final Context context, WarrantyRequest warranty, final AsyncListner callback) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String accessToken = sharedPref.getString("accessToken", null);

        RequestQueue queue = Volley.newRequestQueue(context);

        String jsonString = gson.toJson(warranty);
        Log.d(TAG, jsonString);
        Log.d(TAG, Config.imei_url);
        Log.d(TAG, accessToken);

        JSONObject object = null;

        try {
            object = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.imei_url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(context, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(context, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + accessToken);

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }

    public void logout(final Context context, String username, final AsyncListner callback) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String accessToken = sharedPref.getString("accessToken", null);

        RequestQueue queue = Volley.newRequestQueue(context);

        Log.d(TAG, Config.logout_url + username);

        if (accessToken != null) {
            Log.d(TAG, accessToken);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.logout_url + username, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(context, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(context, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + accessToken);

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }

    public void fogotPassword(final Context context, String username, final AsyncListner callback) {

        RequestQueue queue = Volley.newRequestQueue(context);

        Log.d(TAG, Config.forgot_password_url + username);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.forgot_password_url + username, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(context, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(context, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }


    public void getProduct(final Context context, String productName, final AsyncListner callback) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String accessToken = sharedPref.getString("accessToken", null);

        RequestQueue queue = Volley.newRequestQueue(context);

        Log.d(TAG, Config.get_product_url + productName);
        Log.d(TAG, accessToken);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.get_product_url + productName, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(context, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(context, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + accessToken);

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }

    public void getProductList(final Context context, String dealerCode, final AsyncListner callback) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String accessToken = sharedPref.getString("accessToken", null);

        RequestQueue queue = Volley.newRequestQueue(context);

        Log.d(TAG, Config.get_products_url + dealerCode);
        Log.d(TAG, accessToken);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.get_products_url + dealerCode, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(context, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(context, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + accessToken);

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }

    public void getDealerFromDealerCode(final Context context, String dealerCode, final AsyncListner callback) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        RequestQueue queue = Volley.newRequestQueue(context);

        Log.d(TAG, Config.get_dealer_from_code_url + dealerCode);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.get_dealer_from_code_url + dealerCode, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(context, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(context, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }

    public void getExternalApiWarrantyFromIMEI(final Context context, WarrantyRequest warranty, final AsyncListner callback) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String accessToken = sharedPref.getString("accessToken", null);

        RequestQueue queue = Volley.newRequestQueue(context);

        String jsonString = gson.toJson(warranty);
        Log.d(TAG, jsonString);
        Log.d(TAG, Config.external_api_imei_url);
        Log.d(TAG, accessToken);

        JSONObject object = null;

        try {
            object = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.external_api_imei_url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(context, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(context, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + accessToken);

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }
}
