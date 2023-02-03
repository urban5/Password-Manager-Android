package com.example.passwordmanager3;
import static com.example.passwordmanager3.R.layout.activity_list_view_gesla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class testActivity extends AppCompatActivity {
    private static String JSON_URL = "http://192.168.1.194/password_manager/gesla.php?token=";

    private ListView lv;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onResume() {
        super.onResume();

        if (!contactList.isEmpty()) {
            contactList.clear();
            GetData getData = new GetData();
            getData.execute();
            //Toast.makeText(getApplicationContext(), "onresume", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getSupportActionBar().setTitle("Saved credentials");

        contactList = new ArrayList<>();
        lv = findViewById(R.id.lvGesla);

        Intent intent1 = getIntent();
        String token = intent1.getStringExtra("token");
        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();

        JSON_URL += token;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(testActivity.this, editPassword.class);
                intent.putExtra("website", contactList.get(position).get("website"));
                intent.putExtra("username", contactList.get(position).get("username"));
                intent.putExtra("pass_enc", contactList.get(position).get("pass_enc"));
                intent.putExtra("date", contactList.get(position).get("date"));
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //get the text of textView5
                TextView textView5 = view.findViewById(R.id.textView5);
                String pass_enc = textView5.getText().toString();

                //copy the text to clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("password", pass_enc);
                clipboard.setPrimaryClip(clip);

                //show a toast message to indicate that the text is copied to clipboard
                Toast.makeText(testActivity.this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();

                return true;
            }
        });


        if (contactList.isEmpty()) {
            GetData getData = new GetData();
            getData.execute();
            //Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(testActivity.this, addPassword.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
    }

    public class GetData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // implement API in background and store the response in current variable
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    int data = isw.read();
                    while (data != -1) {
                        current += (char) data;
                        data = isw.read();
                        System.out.print(current);
                    }
                    // return the data to onPostExecute method
                    return current;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                // convert the String result to a JSON object
                JSONObject jsonObject = new JSONObject(s);
                // get the JSON array of "Friends"
                JSONArray jsonArray = jsonObject.getJSONArray("Friends");
                // loop through the JSON array
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    // create a HashMap for each "Friend" object
                    HashMap<String, String> contact = new HashMap<>();
                    // add the website, username, pass_enc, and date to the HashMap
                    contact.put("website", obj.getString("website"));
                    contact.put("username", obj.getString("username"));
                    contact.put("pass_enc", obj.getString("pass_enc"));
                    contact.put("date", obj.getString("date"));
                    JSONObject security = obj.getJSONObject("security");
                    //add security status to the HashMap
                    contact.put("secure",security.getBoolean("secure")?"secure":"insecure");
                    contactList.add(contact);
                }
                // create a SimpleAdapter to set the data for the ListView
                SimpleAdapter adapter = new SimpleAdapter(
                        testActivity.this, contactList,
                        activity_list_view_gesla, new String[]{"website", "username", "pass_enc", "date","secure"},
                        new int[]{R.id.textView, R.id.textView2, R.id.textView5, R.id.textView6,R.id.icon});
                // set the adapter for the ListView
                lv.setAdapter(adapter);
                // set the icon based on security status
                adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if (view.getId() == R.id.icon) {
                            ImageView imageView = (ImageView) view;
                            if(textRepresentation.equals("secure")) {
                                imageView.setImageResource(R.drawable.ic_secure);
                            } else {
                                imageView.setImageResource(R.drawable.ic_insecure);
                            }
                            return true;
                        }
                        return false;
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


