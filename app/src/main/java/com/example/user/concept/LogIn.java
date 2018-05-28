package com.example.user.concept;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LogIn extends AppCompatActivity {

    private Button newUserBtn;
    private Button logInBtn;

    private EditText emailInput;
    private EditText passwordInput;

    private CheckBox rememberCheckBox;

    private static final String TAG = LogIn.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String PROFILE_NAME_KEY = "profile_name", USER_LOGGED_KEY = "userLogged", PROFILE_ID_KEY = "profile_id", FRAGMENT_WINDOW_KEY = "fragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initResources();
        initEvents();
    }

    private void initEvents() {
        newUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LogIn.this, SignUpCustomer.class), 1);
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateUser();
            }
        });
    }

    private void authenticateUser() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, getString(R.string.authenticateCustomerURL), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObject = new JSONObject(response);
                    String status = responseObject.getString("status");
                    if (status.equals("success")) {
                        JSONObject account_data = new JSONObject(responseObject.getString("profile_data"));
                        editor = sharedPreferences.edit();

                        editor.putString(PROFILE_NAME_KEY, account_data.getString("first_name") + " " + account_data.getString("last_name"));
                        editor.putInt(PROFILE_ID_KEY, Integer.parseInt(account_data.getString("profile_id")));
                        editor.putInt(FRAGMENT_WINDOW_KEY, 0);

                        if (rememberCheckBox.isChecked()) {
                            editor.putBoolean(USER_LOGGED_KEY, true);
                        }
                        editor.commit();

                        Toast.makeText(
                                LogIn.this,
                                "Welcome " + sharedPreferences.getString(PROFILE_NAME_KEY, ""),
                                Toast.LENGTH_LONG
                        ).show();

                        startActivityForResult(new Intent(LogIn.this, ProductView.class), 2);
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
                parameters.put("email", emailInput.getText().toString().trim());
                parameters.put("password", passwordInput.getText().toString().trim());
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(strRequest);
    }

    private void initResources() {
        newUserBtn = (Button) findViewById(R.id.newUserBtn);
        logInBtn = (Button) findViewById(R.id.logInBtn);

        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        rememberCheckBox = (CheckBox) findViewById(R.id.rememberMeCheck);

        sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(PROFILE_ID_KEY) && sharedPreferences.getBoolean(USER_LOGGED_KEY, false)) {
            editor = sharedPreferences.edit();
            editor.putInt(FRAGMENT_WINDOW_KEY, 0);
            editor.commit();
            startActivityForResult(new Intent(LogIn.this, ProductView.class), 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    editor = sharedPreferences.edit();
                    editor.putInt(FRAGMENT_WINDOW_KEY, 0);
                    editor.commit();
                    startActivityForResult(new Intent(LogIn.this, ProductView.class), 2);
                    break;
            }
        } else if (requestCode == 2) {
            switch (resultCode) {
                case RESULT_CANCELED:
                    passwordInput.setText("");
                    emailInput.setText("");
                    editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    break;
            }
        }
    }
}
