package com.example.q.mycustom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class ContactPopup extends Activity {
    Context mContext;
    ProgressDialog progressDialog;
    String urlUpload = "http://143.248.140.106:1880/api/add/contact";
    String contactName, contactNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("wrong", "onto onCreate of ImagePopup");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactpopup);
        mContext = this;

        // Information with intent
        Bundle extras = getIntent().getExtras();
        contactName = extras.getString("name");
        contactNumber = extras.getString("number");
        contactNumber.replaceAll("-","");

        // String Full Screen View
        TextView name = findViewById(R.id.nameText);
        name.setText(contactName);
        TextView number = findViewById(R.id.numberText);
        number.setText(contactNumber);
        ImageButton buttonContactUpload = findViewById(R.id.buttonContactUpload);
        buttonContactUpload.setBackgroundResource(R.drawable.upload);
        buttonContactUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ContactPopup.this);
                progressDialog.setTitle("Uploading");
                progressDialog.setMessage("Please wait..");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "error: " + volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) { // adding parameter to send
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();

                        parameters.put("name", contactName);
                        parameters.put("number", contactNumber);

                        progressDialog.dismiss();
                        return parameters;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(ContactPopup.this);
                requestQueue.add(stringRequest);
            }
        });

        ImageButton call = findViewById(R.id.call);
        call.setBackgroundResource(R.drawable.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calla = new Intent();
                calla.setAction(Intent.ACTION_DIAL);
                calla.setData(Uri.parse("tel:" + contactNumber));
                startActivity(calla);

            }

        });

        ImageButton message = findViewById(R.id.message);
        message.setBackgroundResource(R.drawable.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calla = new Intent();
                calla.setAction(Intent.ACTION_SENDTO);
                calla.setData(Uri.parse("sms:" + contactNumber));
                startActivity(calla);

            }

        });
    }


}
