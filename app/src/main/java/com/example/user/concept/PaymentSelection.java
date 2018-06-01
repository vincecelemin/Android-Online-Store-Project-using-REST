package com.example.user.concept;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentSelection extends AppCompatActivity {

    private static final String TAG = PaymentSelection.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";
    private static final String SHIP_NAME_KEY = "ship_name", SHIP_ADDRESS_KEY = "ship_address", SHIP_CONTACT_KEY = "ship_contact";
    private static final String TOTAL_AMOUNT_KEY = "total_amount";
    private static final String CURRENT_BALACE_KEY = "current_balance";

    private LinearLayout payWithLoad;
    private LinearLayout payWithCOD;
    private TextView currentBalanceTextView;
    private Button placeOrderBtn;

    private int paymentType;
    private double currentBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_selection);

        initResources();
        initEvents();
    }

    private void initResources() {
        getSupportActionBar().setTitle("Choose a payment method");
        sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
        paymentType = 1;

        payWithLoad = (LinearLayout) findViewById(R.id.payWithLoad);
        payWithCOD = (LinearLayout) findViewById(R.id.payWithCOD);
        placeOrderBtn = (Button) findViewById(R.id.placeOrderBtn);
        currentBalanceTextView = (TextView) findViewById(R.id.currentBalance);

        currentBalance = 0.00;
        setCurrentBalance();
        payWithCOD.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_shape));
    }

    private void initEvents() {
        payWithLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getFloat(TOTAL_AMOUNT_KEY, 0.0f) > sharedPreferences.getFloat(CURRENT_BALACE_KEY, 0.0f)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "You do not have enough balance",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    payWithCOD.setBackgroundColor(getResources().getColor(R.color.fefefe));
                    payWithLoad.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_shape));
                    paymentType = 0;
                }
            }
        });

        payWithCOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payWithLoad.setBackgroundColor(getResources().getColor(R.color.fefefe));
                payWithCOD.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_shape));
                paymentType = 1;
            }
        });

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processOrders();
            }
        });
    }

    private void processOrders() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.finalizeCustomerOrder), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        setResult(RESULT_OK);
                        finish();
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
                parameters.put("profile_id", String.valueOf(sharedPreferences.getInt(PROFILE_ID_KEY, 0)));
                parameters.put("delivery_person", String.valueOf(sharedPreferences.getString(SHIP_NAME_KEY, "")));
                parameters.put("delivery_address", String.valueOf(sharedPreferences.getString(SHIP_ADDRESS_KEY, "")));
                parameters.put("delivery_contact", String.valueOf(sharedPreferences.getString(SHIP_CONTACT_KEY, "")));
                parameters.put("payment_type", String.valueOf(paymentType));
                parameters.put("total_amount", String.valueOf(sharedPreferences.getFloat(TOTAL_AMOUNT_KEY, 0.0f)));
                parameters.put("current_bal", String.valueOf(sharedPreferences.getFloat(CURRENT_BALACE_KEY, 0.0f)));
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }

    private void setCurrentBalance() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getCurrentBalance), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        currentBalance = responseObj.getDouble("balance");

                        editor = sharedPreferences.edit();
                        editor.putFloat(CURRENT_BALACE_KEY, (float) responseObj.getDouble("balance"));
                        editor.commit();

                        currentBalanceTextView.setText(String.format("P %.2f", currentBalance));
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
                parameters.put("profile_id", String.valueOf(sharedPreferences.getInt(PROFILE_ID_KEY, 0)));
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }
}
