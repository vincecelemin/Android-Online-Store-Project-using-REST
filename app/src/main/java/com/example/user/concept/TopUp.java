package com.example.user.concept;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class TopUp extends AppCompatActivity {

    private static final String TAG = TopUp.class.getSimpleName();
    private EditText topUpInput;
    private EditText accountPassword;

    private Button topUpAccountBtn;
    private TextView currentBalanceText;
    private String accountEmail;
    private double currentLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        initResources();
        initEvents();
    }

    private void initEvents() {
        topUpAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.addCustomerLoad), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject responseObj = new JSONObject(response);

                            if (responseObj.get("status").equals("success")) {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("status", "success");
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
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
                }, new Response.ErrorListener()

                {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })

                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("email", getIntent().getStringExtra("email"));
                        parameters.put("password", accountPassword.getText().toString().trim());
                        parameters.put("prev_balance", String.valueOf(currentLoad));
                        parameters.put("added_balance", topUpInput.getText().toString());
                        return parameters;
                    }
                };
                AppController.getInstance().

                        addToRequestQueue(strRequest);
            }
        });
    }

    private void initResources() {
        currentBalanceText = (TextView) findViewById(R.id.currentLoadTextView);
        topUpInput = (EditText) findViewById(R.id.topUpInput);
        accountPassword = (EditText) findViewById(R.id.accountPassword);
        topUpAccountBtn = (Button) findViewById(R.id.topUpAccountBtn);
        getSupportActionBar().setTitle("Top up your account");
        accountEmail = getIntent().getStringExtra("email");

        getCurrentBalance();
    }

    private void getCurrentBalance() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getCurrentBalance), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        currentBalanceText.setText("You currently have P " + responseObj.getString("balance"));
                        currentLoad = responseObj.getDouble("balance");
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
                parameters.put("profile_id", String.valueOf(getIntent().getIntExtra("profile_id", 0)));
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }
}
