package com.example.passwordmanager3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText tietUsername, tietPassword;
    Button btnRegister;
    TextView tvLogin;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tietUsername = findViewById(R.id.username);
        tietPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress);
        btnRegister = (Button) findViewById(R.id.buttonSignUp);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password;
                username = String.valueOf(tietUsername.getText());
                password = String.valueOf(tietPassword.getText());

                if (!username.equals("") && !password.equals("")) { //if username and pass are both entered then send values
                    //Start ProgressBar first (Set visibility VISIBLE)
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[2];
                            field[0] = "username";
                            field[1] = "password";
                            //Creating array for data
                            String[] data = new String[2];
                            data[0] = username;
                            data[1] = password;
                            PutData putData = new PutData("http://192.168.1.194/password_manager/reg2.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if (result.equals("Registracija je uspešna. ")){
                                        Toast.makeText(getApplicationContext(), "You are now signed up, " + username, Toast.LENGTH_SHORT).show();
                                        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        finish();
                                    }
                                    else{
                                        //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(), "You are now signed up, " + username, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    //End ProgressBar (Set visibility to GONE)
                                    //Log.i("PutData", result);
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}