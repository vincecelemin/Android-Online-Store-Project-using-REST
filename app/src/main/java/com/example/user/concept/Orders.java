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

public class Orders extends AppCompatActivity {

    private static final String TAG = Orders.class.getSimpleName();
    private TextView noRecordView;
    private RecyclerView ordersRecyclerView;


    private SharedPreferences sharedPreferences;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        initResources();
    }

    private void initResources() {
        sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
        getSupportActionBar().setTitle("Orders");
        noRecordView = (TextView) findViewById(R.id.noRecordView);
        ordersRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);

        setOrderList();
    }

    private void setOrderList() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getDeliveryItems), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        JSONArray ordersArray = new JSONArray(responseObj.getString("delivery_items"));
                        List<Order> orderList = new ArrayList<>();

                        for(int i = 0; i < ordersArray.length(); i ++) {
                            JSONObject orderObject = new JSONObject(ordersArray.getString(i));

                            Order order = new Order();
                            order.setProductName(orderObject.getString("name"));
                            order.setSellerName(orderObject.getString("shop_name"));
                            order.setQuantity(orderObject.getInt("quantity"));
                            order.setPrice((float) orderObject.getDouble("price"));
                            order.setImageLoc(orderObject.getString("image_location"));
                            order.setStatus(orderObject.getInt("status"));
                            order.setAddedDate(orderObject.getString("added"));
                            order.setArrivalDate(orderObject.getString("arrival_date"));
                            order.setContactPerson(orderObject.getString("contact_person"));
                            order.setDeliveryAddress(orderObject.getString("location"));
                            order.setContactNumber(orderObject.getString("contact_number"));

                            orderList.add(order);
                        }

                        if(orderList.isEmpty()) {
                            noRecordView.setVisibility(View.VISIBLE);
                        } else {
                            ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            OrderListAdapter orderListAdapter = new OrderListAdapter(orderList, getApplicationContext(), getSupportFragmentManager());
                            ordersRecyclerView.setAdapter(orderListAdapter);
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
