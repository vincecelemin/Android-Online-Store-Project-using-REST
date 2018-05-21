package com.example.user.concept;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieStore;
import java.util.HashMap;
import java.util.Map;

public class SignUpCustomer extends AppCompatActivity {

    private Button cancelBtn;
    private Button signUpBtn;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText addressInput;
    private char gender;

    private static final String TAG = SignUpCustomer.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_ID_KEY = "profile_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_customer);

        initResources();
        initEvents();
    }

    private void initEvents() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pastIntent = new Intent();
                setResult(RESULT_CANCELED, pastIntent);
                finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAccount();
            }
        });
    }

    private void registerAccount() {
        if(!passwordInput.getText().toString().trim().equals(confirmPasswordInput.getText().toString().trim())) {
            Toast.makeText(
                    getApplicationContext(),
                    "Passwords do not Match",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.registerCustomerAccountURL), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObject = new JSONObject(response);
                    String status = responseObject.getString("status");
                    if(status.equals("success")){
                        sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);

                        editor = sharedPreferences.edit();
                        editor.putInt(PROFILE_ID_KEY, responseObject.getInt("customer_id"));
                        editor.commit();

                        Toast.makeText(
                                getApplicationContext(),
                                "Registration Successful",
                                Toast.LENGTH_LONG
                        ).show();

                        Intent backToHomeIntent = new Intent();
                        setResult(RESULT_OK, backToHomeIntent);
                        finish();
                    } else {
                        String message = responseObject.getString("message");
                        Toast.makeText(
                                getApplicationContext(),
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(getApplicationContext(),
//                        response,
//                        Toast.LENGTH_LONG).show();
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
                parameters.put("first_name", firstNameInput.getText().toString().trim());
                parameters.put("last_name", lastNameInput.getText().toString().trim());
                parameters.put("address", addressInput.getText().toString().trim());
                parameters.put("gender", String.valueOf(gender));
                parameters.put("email", emailInput.getText().toString().trim());
                parameters.put("password", passwordInput.getText().toString().trim());
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }

    private void initResources() {
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);

        firstNameInput = (EditText) findViewById(R.id.firstNameInput);
        lastNameInput = (EditText) findViewById(R.id.lastNameInput);
        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        confirmPasswordInput = (EditText) findViewById(R.id.confirmPasswordInput);
        addressInput = (EditText) findViewById(R.id.addressInput);
        gender = 'M';

        getSupportActionBar().setTitle("Sign Up to Concept");
    }

    public void changeGenderValue(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.maleRadioButton:
                if (checked)
                    gender = 'M';
                    break;
            case R.id.femaleRadioButton:
                if (checked)
                    gender = 'F';
                    break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
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
        Intent pastIntent = new Intent();
        setResult(RESULT_CANCELED, pastIntent);
        finish();
    }
}
