package com.example.passwordmanager3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button btnUrbanSignUp;
    private Button btnLogin;
    TextInputEditText tietUsername;
    TextInputEditText tietPassword;
    ProgressBar progressBar;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tietUsername = findViewById(R.id.username);
        tietPassword = findViewById(R.id.password);

        progressBar = findViewById(R.id.progress);
        requestQueue = Volley.newRequestQueue(this);

        btnUrbanSignUp = (Button) findViewById(R.id.btnUrbanSignUp);
        btnUrbanSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterActivity();
            }
        });

        btnLogin = (Button) findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password;
                username = String.valueOf(tietUsername.getText());
                password = String.valueOf(tietPassword.getText());

                if (!username.equals("") && !password.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("master_username",username);
                        jsonObject.put("master_password",password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                    String URL = "http://192.168.1.194/password_manager/gesla.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String token = response;
                            progressBar.setVisibility(View.GONE);
                            //Toast.makeText(getApplicationContext(), "success  "+token, Toast.LENGTH_SHORT).show();
// use token in other activities
                            if(!token.isEmpty()) {
                                Intent intent = new Intent(getApplicationContext(), testActivity.class);
                                intent.putExtra("token", token);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        public byte[] getBody() {
                            return jsonObject.toString().getBytes();
                        }
                        @Override
                        public String getBodyContentType() {
                            return "application/json";
                        }
                    };
                    //stringRequest.setHeader("Content-Type", "application/json");
                    requestQueue.add(stringRequest);
                } else {
                    Toast.makeText(getApplicationContext(), "Enter both username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
