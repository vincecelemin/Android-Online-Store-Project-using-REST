package com.example.user.concept;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {

    private EditText oldPasswordInput;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;

    private Button cancelBtn;
    private Button updatePasswordBtn;

    private TextView customerEmail;

    private static final String TAG = ShopFragment.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initResources();
        initEvents();
    }

    private void initEvents() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, getIntent());
                finish();
            }
        });

        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newPasswordInput.getText().toString().equals(confirmPasswordInput.getText().toString())){
                    updatePassword();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Passwords do not match",
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }
            }
        });
    }

    private void updatePassword() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.updateCustomerPassword), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if(responseObj.get("status").equals("success")) {
                        if(newPasswordInput.getText().toString().equals(oldPasswordInput.getText().toString())){
                            setResult(RESULT_CANCELED, getIntent());
                            finish();
                        } else {
                            setResult(RESULT_OK, getIntent());
                            finish();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                responseObj.getString("message"),
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
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("email", customerEmail.getText().toString());
                parameters.put("new_password", newPasswordInput.getText().toString());
                parameters.put("old_password", oldPasswordInput.getText().toString());

                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }

    private void initResources() {
        oldPasswordInput = (EditText) findViewById(R.id.accountOldPasswordInput);
        newPasswordInput = (EditText) findViewById(R.id.accountNewPasswordInput);
        confirmPasswordInput = (EditText) findViewById(R.id.accountConfirmPasswordInput);

        cancelBtn = (Button) findViewById(R.id.updateProfileCancelBtn);
        updatePasswordBtn = (Button) findViewById(R.id.updatePasswordBtn);

        customerEmail = (TextView) findViewById(R.id.customerEmail);

        sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
        customerEmail.setText(getIntent().getStringExtra("email"));
    }
}
