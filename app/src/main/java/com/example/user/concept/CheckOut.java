package com.example.user.concept;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class CheckOut extends AppCompatActivity {
    private static final String TAG = CheckOut.class.getSimpleName();
    private TextView deliveryPersonText;
    private TextView deliveryAddressText;
    private TextView deliveryContactText;
    private RecyclerView cartRecyclerView;
    private Button editDeliveryDetailsBtn;
    private Button proceedToPaymentBtn;

    private SharedPreferences sharedPreferences;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";
    private static final String SHIP_NAME_KEY  = "ship_name", SHIP_ADDRESS_KEY = "ship_address", SHIP_CONTACT_KEY = "ship_contact";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        initResources();
        initEvents();
    }

    private void initResources() {
        getSupportActionBar().setTitle("Review your order");
        deliveryPersonText = (TextView) findViewById(R.id.deliveryPersonText);
        deliveryAddressText = (TextView) findViewById(R.id.deliveryAddressText);
        deliveryContactText = (TextView) findViewById(R.id.deliveryContactText);
        editDeliveryDetailsBtn = (Button) findViewById(R.id.editDeliveryDetailsBtn);
        proceedToPaymentBtn = (Button) findViewById(R.id.proceedToPaymentBtn);
        cartRecyclerView = (RecyclerView) findViewById(R.id.cartRecyclerView);

        sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
        deliveryPersonText.setText(sharedPreferences.getString(SHIP_NAME_KEY, ""));
        deliveryAddressText.setText(sharedPreferences.getString(SHIP_ADDRESS_KEY, ""));
        deliveryContactText.setText(sharedPreferences.getString(SHIP_CONTACT_KEY, ""));
        setOrdersView();
    }

    private void initEvents() {
        editDeliveryDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), EditShippingDetails.class), 0);
            }
        });

        proceedToPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), PaymentSelection.class), 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            switch (resultCode) {
                case RESULT_OK:
                    deliveryPersonText.setText(sharedPreferences.getString(SHIP_NAME_KEY, ""));
                    deliveryAddressText.setText(sharedPreferences.getString(SHIP_ADDRESS_KEY, ""));
                    deliveryContactText.setText(sharedPreferences.getString(SHIP_CONTACT_KEY, ""));
                    break;
            }
        } else if(requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        }
    }

    private void setOrdersView() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getCartItemsInformation), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        JSONArray cartList = responseObj.getJSONArray("cart_list");
                        CartItem cartItem;
                        List<CartItem> cartItemArrayList = new ArrayList<>();
                        double totalCartAmount = 0.0;
                        for (int i = 0; i < cartList.length(); i++) {
                            JSONObject cartObject = new JSONObject(cartList.get(i).toString());
                            cartItem = new CartItem();
                            cartItem.setCartItemId(cartObject.getInt("cart_id"));
                            cartItem.setCartProductName(cartObject.getString("name"));
                            cartItem.setCartProductSeller(cartObject.getString("shop_name"));
                            cartItem.setCartProductPrice(cartObject.getDouble("price") * cartObject.getInt("quantity"));
                            cartItem.setCartQuantity(cartObject.getInt("quantity"));
                            cartItem.setCartProductId(cartObject.getInt("product_id"));
                            cartItem.setCartImageLocation(cartObject.getString("image_location"));
                            cartItemArrayList.add(cartItem);
                            totalCartAmount += cartItem.getCartProductPrice();
                        }

                        cartRecyclerView.setHasFixedSize(true);
                        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        CheckoutCartListAdapter checkoutAdapter = new CheckoutCartListAdapter(cartItemArrayList,
                                getApplicationContext());
                        cartRecyclerView.setAdapter(checkoutAdapter);
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
