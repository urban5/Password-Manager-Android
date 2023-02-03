package com.example.passwordmanager3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class addPassword extends AppCompatActivity {

    private EditText etWebsite, etUsername, etPass;
    private Button btnAdd;
    private String token;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add new entry");

        // get token from intent
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();

        // initialize views
        etWebsite = findViewById(R.id.etWebsite);
        etUsername = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPass);
        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create a JSON object with the data from the EditTexts
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("website", etWebsite.getText().toString());
                    jsonBody.put("username", etUsername.getText().toString());
                    jsonBody.put("pass_enc", etPass.getText().toString());
                    jsonBody.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // create the request
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://192.168.1.194/password_manager/gesla.php", jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.length() != 0) {
                                    Log.e("Response", response.toString());
                                    Toast.makeText(getApplicationContext(), "Password added successfully!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.toString());
                        Toast.makeText(getApplicationContext(), "New entry added.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })

                // add the request
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
                } ;
                // Access the RequestQueue through your singleton class.
                VolleySingleton.getInstance(addPassword.this).addToRequestQueue(jsonObjectRequest);
            }
        });
    }
}