package com.example.q.mycustom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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


import static com.facebook.FacebookSdk.getApplicationContext;


public class MakeFile extends Activity {
    private EditText filename;
    String sendfilename;
    private EditText file_contents;
    String sendfilecontents;
    ProgressDialog progressDialog;
    String urlUpload = "http://143.248.140.106:1880/api/make/file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("wrong", "onto onCreate of ImagePopup");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_file);




        //////////////////////////////////////////////////////////////////////////////////////////////////
        //Input file name
        //////////////////////////////////////////////////////////////////////////////////////////////////
        filename = (EditText) findViewById(R.id.filename);
        filename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                sendfilename = filename.getText().toString();
            }
        });


        //////////////////////////////////////////////////////////////////////////////////////////////////
        //Input file Contents
        //////////////////////////////////////////////////////////////////////////////////////////////////
        file_contents = (EditText) findViewById(R.id.file_contents);
        file_contents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                sendfilecontents = file_contents.getText().toString();
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////////
        //Make File in Server
        //////////////////////////////////////////////////////////////////////////////////////////////////
        final ImageButton make_file = (ImageButton) findViewById(R.id.make_file);
        make_file.setBackgroundResource(R.drawable.make_file);
        make_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(MakeFile.this);
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
                }){ // adding parameter to send
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();

                        parameters.put("name", sendfilename);
                        parameters.put("fileText", sendfilecontents);


                        progressDialog.dismiss();
                        return parameters;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(MakeFile.this);
                requestQueue.add(stringRequest);
            }
        });




    }
}
