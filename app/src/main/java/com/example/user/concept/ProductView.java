package com.example.user.concept;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class ProductView extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView navigation;
    private int fragmentWindow, currentWindow;

    private SharedPreferences sharedPreferences;
    private static final String ACCOUNT_PREFERENCE = "accountPref", USER_LOGGED_KEY = "userLogged";
    private static final String FRAGMENT_WINDOW_KEY = "fragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        sharedPreferences = getSharedPreferences(ACCOUNT_PREFERENCE, Context.MODE_PRIVATE);

        loadFragment(new ShopFragment());
        currentWindow = 0;

        if(sharedPreferences.contains(FRAGMENT_WINDOW_KEY)) {
            fragmentWindow = sharedPreferences.getInt(FRAGMENT_WINDOW_KEY, 0);
            loadFragmentFromKey();
        }
    }

    private void loadFragmentFromKey() {
        Fragment fragment = null;

        switch (fragmentWindow) {
            case 0:
                navigation.setSelectedItemId(R.id.navigation_shop);
                getSupportActionBar().setTitle("Shop");
                fragment = new ShopFragment();
                break;

            case 1:
                navigation.setSelectedItemId(R.id.navigation_cart);
                getSupportActionBar().setTitle("Cart");
                fragment = new CartFragment();
                break;

            case 2:
                navigation.setSelectedItemId(R.id.navigation_account);
                getSupportActionBar().setTitle("Account");
                fragment = new AccountFragment();
                break;
        }

        loadFragment(fragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
        if(!sharedPreferences.contains(USER_LOGGED_KEY)) {
            setResult(RESULT_CANCELED, getIntent());
            finish();
        } else {
            if(currentWindow == 0) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                navigation.setSelectedItemId(R.id.navigation_shop);
                loadFragment(new ShopFragment());
                currentWindow = 0;
            }
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_frame_layout, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
            return true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_shop:
                getSupportActionBar().setTitle("Shop");
                fragment = new ShopFragment();
                currentWindow = 0;
                break;

            case R.id.navigation_cart:
                getSupportActionBar().setTitle("Cart");
                fragment = new CartFragment();
                currentWindow = 1;
                break;

            case R.id.navigation_account:
                getSupportActionBar().setTitle("Account");
                fragment = new AccountFragment();
                currentWindow = 2;
                break;
        }

        return loadFragment(fragment);
    }
}
