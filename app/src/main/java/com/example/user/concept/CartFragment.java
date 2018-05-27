package com.example.user.concept;

import android.content.Context;
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

public class CartFragment extends Fragment {
    private static final String TAG = ShopFragment.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";

    private CartItem cartItem;
    private RecyclerView cartRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart_view, null);

        initResources(rootView);
        return rootView;
    }

    private void initResources(View rootView) {
        sharedPreferences = getActivity().getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
        cartRecyclerView = (RecyclerView) rootView.findViewById(R.id.cartRecyclerView);

        setCartView();
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

                        if(cartList.length() == 0) {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Your cart is empty",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        for (int i = 0; i < cartList.length(); i++) {
                            JSONObject cartObject = new JSONObject(cartList.get(i).toString());
                            cartItem = new CartItem();
                            cartItem.setCartItemId(cartObject.getInt("cart_id"));
                            cartItem.setCartProductName(cartObject.getString("name"));
                            cartItem.setCartProductSeller(cartObject.getString("shop_name"));
                            cartItem.setCartProductPrice(cartObject.getDouble("price") * cartObject.getInt("quantity"));
                            cartItem.setCartQuantity(cartObject.getInt("quantity"));
                            cartItem.setCartProductId(cartObject.getInt("product_id"));

                            cartItemArrayList.add(cartItem);
                        }

                        cartRecyclerView.setHasFixedSize(true);
                        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

                        CartListItemAdapter cartListItemAdapter = new CartListItemAdapter(cartItemArrayList,
                                getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
                        cartRecyclerView.setAdapter(cartListItemAdapter);
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
