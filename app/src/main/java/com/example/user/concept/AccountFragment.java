package com.example.user.concept;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {
    private TextView customerName;
    private TextView customerEmail;
    private TextView customerAddress;
    private TextView currentBalance;

    private Button accountEdit;
    private Button accountChangePassword;
    private Button logOutBtn;
    private Button topUpBtn;

    private static final String TAG = ShopFragment.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";
    private static final String FRAGMENT_WINDOW_KEY = "fragment";

    private String first_name, last_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_view, null);
        initResources(rootView);
        initEvents();
        return rootView;
    }


    private void initResources(View rootView) {
        customerName = (TextView) rootView.findViewById(R.id.accountCustomerName);
        customerEmail = (TextView) rootView.findViewById(R.id.accountCustomerEmail);
        customerAddress = (TextView) rootView.findViewById(R.id.accountCustomerAddress);
        currentBalance = (TextView) rootView.findViewById(R.id.currentBalance);

        accountEdit = (Button) rootView.findViewById(R.id.accountBtnUpdateProfile);
        accountChangePassword = (Button) rootView.findViewById(R.id.accountBtnChangePassword);
        logOutBtn = (Button) rootView.findViewById(R.id.logOutBtn);
        topUpBtn = (Button) rootView.findViewById(R.id.topUpAccountBtn);
        sharedPreferences = getActivity().getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);

        getProfileInformation();
        setCurrentBalance();
    }

    private void initEvents() {
        accountEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UpdateProfile.class);

                intent.putExtra("first_name", first_name);
                intent.putExtra("last_name", last_name);
                intent.putExtra("email", customerEmail.getText().toString());
                intent.putExtra("address", customerAddress.getText().toString());
                intent.putExtra("profile_id", sharedPreferences.getInt(PROFILE_ID_KEY, 0));

                startActivityForResult(intent, 0);
            }
        });

        accountChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePassword.class);

                intent.putExtra("email", customerEmail.getText().toString());

                startActivityForResult(intent, 1);
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setResult(RESULT_CANCELED, getActivity().getIntent());
                getActivity().finish();
            }
        });

        topUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent topUpIntent = new Intent(getActivity(), TopUp.class);

                topUpIntent.putExtra("email", customerEmail.getText().toString());
                topUpIntent.putExtra("profile_id", sharedPreferences.getInt(PROFILE_ID_KEY, 0));
                startActivityForResult(topUpIntent, 2);
            }
        });
    }

    private void setCurrentBalance() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getCurrentBalance), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        currentBalance.setText("P " + responseObj.getString("balance"));
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


    private void getProfileInformation() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.getProfileInformation), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if (responseObj.getString("status").equals("success")) {
                        JSONObject profileData = new JSONObject(responseObj.getString("profile_data"));

                        first_name = profileData.getString("first_name");
                        last_name = profileData.getString("last_name");

                        customerName.setText(first_name + " " + last_name);
                        customerEmail.setText(profileData.getString("email"));
                        customerAddress.setText(profileData.getString("address"));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            switch (resultCode) {
                case RESULT_OK:
                    if (data.getStringExtra("status").equals("success")) {
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Your profile has been updated",
                                Toast.LENGTH_SHORT
                        ).show();
                        editor = sharedPreferences.edit();
                        editor.putInt(FRAGMENT_WINDOW_KEY, 2);
                        editor.commit();

                        getActivity().startActivity(getActivity().getIntent());
                        getActivity().finish();
                    }
                    break;
            }
        } else if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    if (data.getStringExtra("status").equals("success")) {
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Your password has been updated",
                                Toast.LENGTH_SHORT
                        ).show();
                        editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();

                        getActivity().setResult(RESULT_CANCELED, getActivity().getIntent());
                        getActivity().finish();
                    }
                    break;
            }
        } else if (requestCode == 2) {
            switch (resultCode) {
                case RESULT_OK:
                    if (data.getStringExtra("status").equals("success")) {
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Your load has been updated",
                                Toast.LENGTH_SHORT
                        ).show();
                        editor = sharedPreferences.edit();
                        editor.putInt(FRAGMENT_WINDOW_KEY, 2);
                        editor.commit();

                        getActivity().startActivity(getActivity().getIntent());
                        getActivity().finish();
                    }
                    break;
            }
        }
    }
}
