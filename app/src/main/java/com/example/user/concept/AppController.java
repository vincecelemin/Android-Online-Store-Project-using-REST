package com.example.user.concept;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Power on 3/17/2018.
 */

public class AppController extends Application implements UpdateCartItemQuantityDialog.UpdateCartDialogListener, CancelOrderDialog.CancelOrderDialogListener {
    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppController mInstance;


    private SharedPreferences sharedPreferences;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String FRAGMENT_WINDOW_KEY = "fragment";
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue =
                    Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    public void applyTexts(final String newQuantity, boolean isEmptied, final int cartItemId, final Activity activity) {
        if(isEmptied) {
            StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.deleteFromCart), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response.toString());
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        if (responseObj.getString("status").equals("success")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Item has been removed from your cart",
                                    Toast.LENGTH_LONG
                            ).show();

                            sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putInt(FRAGMENT_WINDOW_KEY, 1);
                            editor.commit();

                            activity.startActivity(activity.getIntent());
                            activity.finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("cart_id", String.valueOf(cartItemId));
                    return parameters;
                }
            };
            AppController.getInstance().addToRequestQueue(strRequest);
        } else {
            StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.updateCartQuantity), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response.toString());
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        if (responseObj.getString("status").equals("success")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Your cart has been updated",
                                    Toast.LENGTH_LONG
                            ).show();

                            sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putInt(FRAGMENT_WINDOW_KEY, 1);
                            editor.commit();

                            activity.startActivity(activity.getIntent());
                            activity.finish();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    responseObj.getString("message"),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("cart_id", String.valueOf(cartItemId));
                    parameters.put("new_quantity", newQuantity);
                    return parameters;
                }
            };
            AppController.getInstance().addToRequestQueue(strRequest);
        }
    }

    @Override
    public void applyTexts(boolean isDeleted, Activity activity) {
        activity.finish();
        activity.startActivity(activity.getIntent());
    }
}

