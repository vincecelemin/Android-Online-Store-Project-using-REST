package com.example.user.concept;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class UpdateCartItemQuantityDialog extends AppCompatDialogFragment {

    private static final String TAG = UpdateCartItemQuantityDialog.class.getSimpleName();
    private int productId;
    private int cartId;
    private String availableStock;
    private String currentQuantity;
    private TextView availableQuantity;
    private EditText currentQuantityInput;

    private UpdateCartDialogListener listener;


    public void setData(int productId, String currentQuantity, int cartId) {
        this.productId = productId;
        this.cartId = cartId;
        this.currentQuantity = currentQuantity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.modify_cart_quantity_dialog, null);

        builder.setView(view)
            .setTitle("enter a valid quantity")
            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            })
            .setPositiveButton("update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    validateQuantityInput();
                }
            });

        currentQuantityInput = view.findViewById(R.id.dialogCurrentQuantity);
        currentQuantityInput.setText(currentQuantity);
        availableQuantity = view.findViewById(R.id.dialogAvailableStock);

        getAvailableStock();
        return builder.create();
    }

    private void validateQuantityInput() {
        if(currentQuantityInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Enter a quantity",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if(Integer.parseInt(availableStock) < Integer.parseInt(currentQuantityInput.getText().toString().trim())) {
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Enter a valid quantity",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String quantityInput = currentQuantityInput.getText().toString().trim();
        boolean isEmptied = false;
        if(Integer.parseInt(currentQuantityInput.getText().toString().trim()) == 0) {
            isEmptied = true;
        }

        listener.applyTexts(quantityInput, isEmptied, cartId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (UpdateCartDialogListener) getActivity().getApplicationContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getApplicationContext().toString() + " must implement UpdateDialogListener");
        }
    }

    private void getAvailableStock() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getProductInformation), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);
                    if(responseObj.getString("status").equals("success")) {
                        JSONObject productData = new JSONObject(responseObj.getString("product_data"));

                        availableStock = productData.getString("stock");

                        availableQuantity.setText(availableStock + " available");
                    } else {
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Error retrieving product",
                                Toast.LENGTH_LONG
                        ).show();
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
                parameters.put("product_id", String.valueOf(productId));
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }

    public interface UpdateCartDialogListener {
        void applyTexts(String newQuantity, boolean isEmptied, int cartId);
    }
}
