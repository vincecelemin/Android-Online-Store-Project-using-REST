package com.example.user.concept;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CartListItemAdapter extends RecyclerView.Adapter<CartListItemAdapter.CartItemHolder>  {

    private static final String TAG = CartListItemAdapter.class.getSimpleName();
    private List<CartItem> cartItemList;
    private FragmentManager fragmentManager;
    private Context mCtx;
    private Activity activity;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String FRAGMENT_WINDOW_KEY = "fragment";

    public CartListItemAdapter(List<CartItem> cartItemList, Context mCtx, FragmentManager fragmentManager, Activity activity) {
        this.cartItemList = cartItemList;
        this.mCtx = mCtx;
        this.fragmentManager = fragmentManager;
        this.activity = activity;

        sharedPreferences = mCtx.getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
    }

    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.from(parent.getContext()).inflate(R.layout.cart_list_item_view, parent, false);
        return new CartItemHolder(view);
    }

    @Override
    public void onBindViewHolder(CartListItemAdapter.CartItemHolder holder, int position) {
        final CartItem cartItem = cartItemList.get(position);

        holder.cartProductName.setText(cartItem.getCartProductName());
        holder.cartProductSeller.setText(cartItem.getCartProductSeller());
        holder.cartProductQuantity.setText(String.valueOf(cartItem.getCartQuantity()) + " item/s");
        holder.cartProductPrice.setText("P " + String.valueOf(cartItem.getCartProductPrice()));

        holder.removeFromCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFromCart(cartItem);
            }
        });

        holder.modifyQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditQuantityDialog(String.valueOf(cartItem.getCartQuantity()), cartItem.getCartProductId(), cartItem.getCartItemId());
            }
        });

        Picasso.get()
                .load("http://10.0.2.2/WebFramFinalProj/public/storage/product_images/" + cartItem.getCartImageLocation())
                .resize(100, 100)
                .into(holder.cartProductImage);
    }

    private void openEditQuantityDialog(String productQuantity, int cartProductId, int cartId) {
        UpdateCartItemQuantityDialog updateDialog = new UpdateCartItemQuantityDialog();
        updateDialog.setData(cartProductId, productQuantity, cartId, activity);
        updateDialog.show(fragmentManager, "update quantity");
    }

    private void deleteFromCart(final CartItem cartItem) {
        StringRequest strRequest = new StringRequest(Request.Method.POST, mCtx.getString(R.string.deleteFromCart), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        Toast.makeText(
                                mCtx,
                                cartItem.getCartProductName() + " has been removed from your cart",
                                Toast.LENGTH_LONG
                        ).show();

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
                Toast.makeText(mCtx,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("cart_id", String.valueOf(cartItem.getCartItemId()));
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView cartProductName, cartProductSeller, cartProductPrice, cartProductQuantity;
        Button removeFromCartBtn, modifyQuantityBtn;
        ImageView cartProductImage;

        public CartItemHolder(View itemView) {
            super(itemView);

            cartProductName = (TextView) itemView.findViewById(R.id.productName);
            cartProductSeller = (TextView) itemView.findViewById(R.id.productSeller);
            cartProductPrice = (TextView) itemView.findViewById(R.id.productPrice);
            cartProductQuantity = (TextView) itemView.findViewById(R.id.productQuantity);

            removeFromCartBtn = (Button) itemView.findViewById(R.id.removeFromCartBtn);
            modifyQuantityBtn = (Button) itemView.findViewById(R.id.addMoreToCartBtn);

            cartProductImage = (ImageView) itemView.findViewById(R.id.cartProductImage);
        }
    }
}
