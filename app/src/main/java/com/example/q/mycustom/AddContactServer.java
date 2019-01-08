package com.example.q.mycustom;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AddContactServer extends AppCompatActivity {
    ProgressDialog progressDialog;
    String urlUpload = "http://143.248.140.106:1880/api/add/contact";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("eTest", "AddContactServer onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontactserver);

        final EditText name = findViewById(R.id.addContactName);
        final EditText number = findViewById(R.id.addContactNumber);
        ImageButton buttonUpload = findViewById(R.id.addContactDBButton);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(AddContactServer.this);
                progressDialog.setTitle("Uploading");
                progressDialog.setMessage("Please wait..");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Contact Successfully saved in DB", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "error: " + volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }){ // adding parameter to send
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();

                        parameters.put("name", name.getText().toString());
                        parameters.put("number", number.getText().toString());

                        progressDialog.dismiss();
                        return parameters;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(AddContactServer.this);
                requestQueue.add(stringRequest);

                finish();
            }
        });


    }
}
