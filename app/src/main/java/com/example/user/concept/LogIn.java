package com.example.user.concept;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogIn extends AppCompatActivity {

    private Button newUserBtn;

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
    }

    private void initResources() {
        newUserBtn = (Button) findViewById(R.id.newUserBtn);
    }
}
