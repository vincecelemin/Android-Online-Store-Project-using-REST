package com.example.user.concept;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class CancelOrderDialog extends AppCompatDialogFragment {
    private static final String TAG = CancelOrderDialog.class.getSimpleName();
    private String productName;
    private Context mCtx;
    private int deliveryItemId;
    private boolean isDeleted;
    private Activity activity;


    private TextView alertBody;

    private CancelOrderDialogListener listener;

    public void setValues(String productName, int deliveryItemId, Context mCtx) {
        this.productName = productName;
        this.deliveryItemId = deliveryItemId;
        this.mCtx = mCtx;
        this.isDeleted = false;
        this.activity = getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.cancel_order_alert_dialog, null);


        alertBody = (TextView) view.findViewById(R.id.cancelOrderBody);
        alertBody.setText("Are you sure you want to cancel " + productName);

        builder.setView(view)
                .setTitle("Cancel " + productName + "?")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.cancelOrder), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, response.toString());

                                try {
                                    JSONObject responseObj = new JSONObject(response);
                                    if (responseObj.getString("status").equals("success")) {
                                        Toast.makeText(
                                                mCtx,
                                                productName + " has been cancelled.",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        isDeleted = true;
                                    } else {
                                        Toast.makeText(
                                                mCtx,
                                                "Error cancelling " + productName,
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }

//                                    listener.applyTexts(isDeleted, getActivity());
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
                                parameters.put("delivery_item_id", String.valueOf(deliveryItemId));
                                return parameters;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(strRequest);

                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        try {
            listener = (CancelOrderDialogListener) getActivity().getApplicationContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getApplicationContext().toString() + " must implement CancelOrderDialogListener");
        }
    }

    public interface CancelOrderDialogListener {
        void applyTexts(boolean isDeleted, Activity activity);
    }
}
