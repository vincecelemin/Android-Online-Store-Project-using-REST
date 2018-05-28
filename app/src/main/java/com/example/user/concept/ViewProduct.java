package com.example.user.concept;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewProduct extends AppCompatActivity {

    private static final String TAG = ViewProduct.class.getSimpleName();
    private TextView productName;
    private TextView productSeller;
    private TextView productDescription;
    private TextView productPrice;
    private TextView productQuantity;

    private EditText inputQuantity;

    private Button addToWishlistBtn;
    private Button addToCartBtn;

    private int productStock;

    private SharedPreferences sharedPreferences;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        initResources();
        initEvents();
    }

    private void initEvents() {
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputQuantity.getText().toString().trim().isEmpty()) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Enter quantity",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                if(Integer.valueOf(inputQuantity.getText().toString().trim()) > productStock ||
                        Integer.valueOf(inputQuantity.getText().toString().trim()) == 0) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Enter a valid quantity",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                addItemToCart();
            }
        });
    }

    private void addItemToCart() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.addItemToCart), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);
                    if(responseObj.getString("status").equals("success")) {
                        Toast.makeText(
                                getApplicationContext(),
                                productName.getText().toString() + " is added to your cart",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                        startActivity(new Intent(ViewProduct.this, ProductView.class));
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
                parameters.put("product_id", String.valueOf(getIntent().getIntExtra("product_id", 0)));
                parameters.put("profile_id", String.valueOf(sharedPreferences.getInt(PROFILE_ID_KEY, 0)));
                parameters.put("quantity", String.valueOf(inputQuantity.getText().toString().trim()));
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }

    private void initResources() {
        productStock = 0;
        productName = (TextView) findViewById(R.id.productName);
        productPrice = (TextView) findViewById(R.id.productPrice);
        productSeller = (TextView) findViewById(R.id.productSeller);
        productDescription = (TextView) findViewById(R.id.productDescription);
        productQuantity = (TextView) findViewById(R.id.productQuantity);
        inputQuantity = (EditText) findViewById(R.id.inputQuantity);

        addToCartBtn = (Button) findViewById(R.id.addToCartBtn);
        addToWishlistBtn = (Button) findViewById(R.id.addToWishlistBtn);

        sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);

        setProductInformation();
    }

    private void setProductInformation() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getProductInformation), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);
                    if(responseObj.getString("status").equals("success")) {
                        JSONObject productData = new JSONObject(responseObj.getString("product_data"));

                        productName.setText(productData.getString("name"));
                        productDescription.setText(productData.getString("description"));
                        productPrice.setText("P " + productData.getString("price"));
                        productSeller.setText("sold by " + productData.getString("shop_name"));
                        productQuantity.setText(productData.getString("stock") + " available");

                        productStock = productData.getInt("stock");
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Error retrieving product",
                                Toast.LENGTH_LONG
                        ).show();

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
                parameters.put("product_id", String.valueOf(getIntent().getIntExtra("product_id", 0)));
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
