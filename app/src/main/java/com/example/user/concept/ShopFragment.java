package com.example.user.concept;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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

public class ShopFragment extends Fragment {

    private static final String TAG = ShopFragment.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";

    private ProductCardItem productCard;
    private RecyclerView productIndexRecyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop_view, null);
        initResources(rootView);

        return rootView;
    }

    private void initResources(View rootView) {
        sharedPreferences = getActivity().getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
        productIndexRecyclerView = (RecyclerView) rootView.findViewById(R.id.productRecyclerViewIndex);
        setProductView();
    }

    private void setProductView() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getProductViewIndexProducts), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        JSONArray productList = responseObj.getJSONArray("products");
                        List<ProductCardItem> productCardList = new ArrayList<>();

                        for (int i = 0; i < productList.length(); i++) {
                            JSONObject productItem = new JSONObject(productList.get(i).toString());
                            productCard = new ProductCardItem();

                            productCard.setName(productItem.getString("name"));
                            productCard.setSeller(productItem.getString("shop_name"));
                            productCard.setImageName(productItem.getString("image_location"));
                            productCard.setPrice(productItem.getDouble("price"));
                            productCard.setProductId(productItem.getInt("id"));
                            productCardList.add(productCard);
                        }

                        productIndexRecyclerView.setHasFixedSize(true);
                        productIndexRecyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));

                        ProductCardListAdapter productCardListAdapter = new ProductCardListAdapter(getActivity().getApplicationContext(), productCardList);
                        productIndexRecyclerView.setAdapter(productCardListAdapter);
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
