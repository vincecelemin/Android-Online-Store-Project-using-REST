package com.example.user.concept;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class UpdateProfile extends AppCompatActivity {

    private static final String TAG = UpdateProfile.class.getSimpleName();
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailAddressInput;
    private EditText addressInput;
    private EditText contactNumberInput;
    private EditText passwordInput;

    private Button cancelButton;
    private Button updateButton;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_NAME_KEY = "profile_name";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        initResources();
        initEvents();
    }

    private void initEvents() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, getIntent());
                finish();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        if(passwordInput.getText().toString().isEmpty()) {
            Toast.makeText(
                    getApplicationContext(),
                    "Enter your password to proceed",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.updateProfileInformation), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject responseObj = new JSONObject(response);
                    if(responseObj.get("status").equals("success")) {
                        if(firstNameInput.getText().toString().equals(getIntent().getStringExtra("first_name")) &&
                                lastNameInput.getText().toString().equals(getIntent().getStringExtra("last_name")) &&
                                addressInput.getText().toString().equals(getIntent().getStringExtra("address")) &&
                                emailAddressInput.getText().toString().equals(getIntent().getStringExtra("email")) &&
                                contactNumberInput.getText().toString().equals(getIntent().getStringExtra("contact_number"))) {
                            setResult(RESULT_CANCELED, getIntent());
                            finish();
                        } else {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("status", "success");

                            sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putString(PROFILE_NAME_KEY, firstNameInput.getText().toString() + " " + lastNameInput.getText().toString());
                            editor.commit();
                            setResult(Activity.RESULT_OK, returnIntent);
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
                parameters.put("first_name", firstNameInput.getText().toString());
                parameters.put("last_name", lastNameInput.getText().toString());
                parameters.put("email", emailAddressInput.getText().toString());
                parameters.put("address", addressInput.getText().toString());
                parameters.put("contact_number", contactNumberInput.getText().toString());
                parameters.put("password", passwordInput.getText().toString());

                parameters.put("profile_id", String.valueOf(getIntent().getIntExtra("profile_id", 0)));
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }

    private void initResources() {
        getSupportActionBar().setTitle("Update your profile");

        firstNameInput = (EditText) findViewById(R.id.accountFirstNameInput);
        lastNameInput = (EditText) findViewById(R.id.accountLastNameInput);
        emailAddressInput = (EditText) findViewById(R.id.accountEmailInput);
        addressInput = (EditText) findViewById(R.id.accountAddressInput);
        contactNumberInput = (EditText) findViewById(R.id.contactNumberInput);
        passwordInput = (EditText) findViewById(R.id.accountPasswordInput);

        cancelButton = (Button) findViewById(R.id.updateProfileCancelBtn);
        updateButton = (Button) findViewById(R.id.updateProfileBtn);

        firstNameInput.setText(getIntent().getStringExtra("first_name"));
        lastNameInput.setText(getIntent().getStringExtra("last_name"));
        emailAddressInput.setText(getIntent().getStringExtra("email"));
        addressInput.setText(getIntent().getStringExtra("address"));
    }
}
