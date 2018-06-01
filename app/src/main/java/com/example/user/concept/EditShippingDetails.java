package com.example.user.concept;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditShippingDetails extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String ACCOUNT_PREFERENCE = "accountPref";
    private static final String SHIP_NAME_KEY = "ship_name", SHIP_ADDRESS_KEY = "ship_address", SHIP_CONTACT_KEY = "ship_contact";

    private EditText deliveryPerson;
    private EditText deliveryAddress;
    private EditText deliveryContact;
    private Button updateDeliveryInfoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shipping_details);

        initResources();
        initEvents();
    }

    private void initResources() {
        getSupportActionBar().setTitle("Edit shipping details");
        sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);

        deliveryPerson = (EditText) findViewById(R.id.deliveryPersonInput);
        deliveryAddress = (EditText) findViewById(R.id.deliveryAddressInput);
        deliveryContact = (EditText) findViewById(R.id.deliveryContactInput);
        updateDeliveryInfoBtn = (Button) findViewById(R.id.updateDeliveryInfoBtn);

        deliveryPerson.setText(sharedPreferences.getString(SHIP_NAME_KEY, ""));
        deliveryAddress.setText(sharedPreferences.getString(SHIP_ADDRESS_KEY, ""));
        deliveryContact.setText(sharedPreferences.getString(SHIP_CONTACT_KEY, ""));
    }

    private void initEvents() {
        updateDeliveryInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deliveryPerson.getText().toString().trim().isEmpty()
                        || deliveryAddress.getText().toString().trim().isEmpty()
                        || deliveryContact.getText().toString().trim().isEmpty()) {
                    Toast.makeText(EditShippingDetails.this, "Incomplete Fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (deliveryPerson.getText().toString().equals(sharedPreferences.getString(SHIP_NAME_KEY, ""))
                        && deliveryAddress.getText().toString().equals(sharedPreferences.getString(SHIP_ADDRESS_KEY, ""))
                        && deliveryContact.getText().toString().equals(sharedPreferences.getString(SHIP_CONTACT_KEY, ""))) {
                    setResult(Activity.RESULT_CANCELED, getIntent());
                    finish();
                } else {
                    editor = sharedPreferences.edit();
                    editor.putString(SHIP_NAME_KEY, deliveryPerson.getText().toString());
                    editor.putString(SHIP_ADDRESS_KEY, deliveryAddress.getText().toString());
                    editor.putString(SHIP_CONTACT_KEY, deliveryContact.getText().toString());
                    editor.commit();

                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                }
            }
        });
    }
}
