package com.example.user.concept;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class CartFragment extends Fragment {
    private static final String TAG = ShopFragment.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";
    private static final String SHIP_NAME_KEY  = "ship_name", SHIP_ADDRESS_KEY = "ship_address", SHIP_CONTACT_KEY = "ship_contact";
    private static final String FRAGMENT_WINDOW_KEY = "fragment";
    private static final String TOTAL_AMOUNT_KEY = "total_amount";

    private CartItem cartItem;
    private RecyclerView cartRecyclerView;
    private TextView totalItemCount;
    private TextView totalProductCost;
    private TextView emptyCartView;
    private Button checkoutBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart_view, null);

        initResources(rootView);
        initEvents();
        return rootView;
    }

    private void initEvents() {
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assignShipDetailsToPref();
                Intent intent = new Intent(getActivity(), CheckOut.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void initResources(View rootView) {
        sharedPreferences = getActivity().getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
        cartRecyclerView = (RecyclerView) rootView.findViewById(R.id.cartRecyclerView);
        totalProductCost = (TextView) rootView.findViewById(R.id.totalProductCost);
        totalItemCount = (TextView) rootView.findViewById(R.id.totalItemCount);
        emptyCartView = (TextView) rootView.findViewById(R.id.emptyCartView);
        checkoutBtn = (Button) rootView.findViewById(R.id.checkoutBtn);

        setCartView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Your orders have been processed successfully",
                            Toast.LENGTH_SHORT
                    ).show();


                    editor = sharedPreferences.edit();
                    editor.putInt(FRAGMENT_WINDOW_KEY, 1);
                    editor.commit();

                    getActivity().startActivity(getActivity().getIntent());
                    getActivity().finish();
                    break;
            }
        }
    }

    private void setCartView() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getCartItemsInformation), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        JSONArray cartList = responseObj.getJSONArray("cart_list");
                        List<CartItem> cartItemArrayList = new ArrayList<>();

                        double totalAmount = 0.00;
                        int totalItems = 0;
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

                            totalItems += cartObject.getInt("quantity");
                            totalAmount += cartItem.getCartProductPrice();

                            cartItemArrayList.add(cartItem);
                        }

                        cartRecyclerView.setHasFixedSize(true);
                        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

                        CartListItemAdapter cartListItemAdapter = new CartListItemAdapter(cartItemArrayList,
                                getActivity().getApplicationContext(), getActivity().getSupportFragmentManager(), getActivity());
                        cartRecyclerView.setAdapter(cartListItemAdapter);

                        totalItemCount.setText("Items: " + String.valueOf(totalItems));
                        totalProductCost.setText("Total Amount: P " + String.format("%.2f", totalAmount));

                        editor = sharedPreferences.edit();
                        editor.putFloat(TOTAL_AMOUNT_KEY, (float) totalAmount);
                        editor.commit();

                        if(cartItemArrayList.size() > 0) {
                            totalProductCost.setVisibility(View.VISIBLE);
                            totalItemCount.setVisibility(View.VISIBLE);
                            checkoutBtn.setVisibility(View.VISIBLE);
                        } else {
                            emptyCartView.setVisibility(View.VISIBLE);
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
                Toast.makeText(getActivity().getApplicationContext(),
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

    private void assignShipDetailsToPref() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getProfileInformation), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        JSONObject profileData = new JSONObject(responseObj.getString("profile_data"));

                        String first_name = profileData.getString("first_name");
                        String last_name = profileData.getString("last_name");

                        editor = sharedPreferences.edit();
                        editor.putString(SHIP_NAME_KEY, first_name + " " + last_name);
                        editor.putString(SHIP_ADDRESS_KEY, profileData.getString("address"));
                        editor.putString(SHIP_CONTACT_KEY, profileData.getString("contact_number"));
                        editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
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
