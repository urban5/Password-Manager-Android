package com.example.passwordmanager3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import android.view.MenuItem;

public class editPassword extends AppCompatActivity {

    private String website, username, pass_enc, date;
    private String update_url = "http://192.168.1.194/password_manager/gesla.php";
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit password");

        TextView titleView = findViewById(R.id.tvWebsite);
        titleView.setText(getIntent().getStringExtra("website"));

        TextView usernameView = findViewById(R.id.tvUsername);
        usernameView.setText(getIntent().getStringExtra("username"));

        TextView pass_encView = findViewById(R.id.tvPassword);
        pass_encView.setText(getIntent().getStringExtra("pass_enc"));

        TextView dateView = findViewById(R.id.tvDate);
        dateView.setText(getIntent().getStringExtra("date"));

        Intent intent1 = getIntent();
        token = intent1.getStringExtra("token");
        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();

        pass_encView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open dialog/popup to update password
                updatePassword();

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void updatePassword() {
        // show dialog/popup to get new password from user
        final EditText input = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter new password:");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPass = input.getText().toString();
                // update password with PUT request
                String url = "http://192.168.1.194/password_manager/gesla.php";
                JSONObject jsonBody = new JSONObject();
                try {
                    website = getIntent().getStringExtra("website");
                    jsonBody.put("website", website);
                    jsonBody.put("geslo", newPass);
                    jsonBody.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Toast.makeText(getApplicationContext(), jsonBody.toString(), Toast.LENGTH_LONG).show();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, "http://192.168.1.194/password_manager/gesla.php", jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.length() != 0) {
                                    Log.e("Response", response.toString());
                                    // code to handle successful response
                                } else {
                                    Log.e("Response", "No response received.");
                                }
                                Intent intent2 = new Intent(editPassword.this, testActivity.class);
                                startActivity(intent2);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response Error", error.toString());
                        // code to handle error response

                        finish();

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}