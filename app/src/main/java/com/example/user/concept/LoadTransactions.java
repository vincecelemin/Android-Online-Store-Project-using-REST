package com.example.user.concept;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadTransactions extends AppCompatActivity {

    private static final String TAG = LoadTransactions.class.getSimpleName();
    private RecyclerView transactionsRecyclerView;
    private TextView noTransactionView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_transactions);

        initResources();
    }

    private void initResources() {
        transactionsRecyclerView = (RecyclerView) findViewById(R.id.transactionsRecyclerView);
        noTransactionView = (TextView) findViewById(R.id.noRecordView);
        sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
        getSupportActionBar().setTitle("Load Transactions");

        getTopupTransactions();
    }

    private void getTopupTransactions() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getLoadTransactions), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        JSONArray transactionsArray = new JSONArray(responseObj.getString("transaction_list"));
                        List<Transaction> transactionList = new ArrayList<>();
                        for (int i = 0; i < transactionsArray.length(); i++) {
                            JSONObject transactionObject = new JSONObject(transactionsArray.getString(i));
                            Transaction transaction = new Transaction();
                            transaction.setDate(transactionObject.getString("transaction_date").substring(0, 10));
                            transaction.setAmount((float) transactionObject.getDouble("amount"));
                            transaction.setType(transactionObject.getInt("type"));

                            transactionList.add(transaction);
                        }

                        if (transactionList.isEmpty()) {
                            noTransactionView.setVisibility(View.VISIBLE);
                        } else {
                            transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            TransactionItemListAdapter transactionItemListAdapter = new TransactionItemListAdapter(transactionList, getApplicationContext());
                            transactionsRecyclerView.setAdapter(transactionItemListAdapter);
                        }
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
