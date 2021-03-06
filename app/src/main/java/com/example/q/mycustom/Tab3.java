package com.example.q.mycustom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Tab3 extends Fragment {
    private CallbackManager callbackManager;
    View rootView;
    private EditText editSearch;
    ProgressDialog progressDialog;
    String text;
    String urlUpload = "http://143.248.140.106:1880/api/search/file/%s", url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab3, container, false);
        FacebookSdk.sdkInitialize(this.getContext());
        //facebook callback
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result", object.toString());
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() { }
            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr", error.toString());
            }
        });
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (!isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }

        //Input file name
        editSearch = (EditText) rootView.findViewById(R.id.SearchBar);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다. search 메소드를 호출한다.
                text = editSearch.getText().toString();
                Log.d("abcd", "-------edittext: " + text + "-------------");
            }
        });

        //Search file
        final ImageButton search_file = rootView.findViewById(R.id.search_file);
        search_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = String.format(urlUpload, text);
                searchFileDB();
            }
        });

        //make File
        ImageButton make_file = (ImageButton) rootView.findViewById(R.id.make_file);
        make_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent(getActivity(), MakeFile.class);
                startActivity(view);
            }
        });

        ImageButton merge_file = rootView.findViewById(R.id.merge_file);
        merge_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MergeFile.class);
                startActivity(intent);
            }
        });

        ImageView imageIcon = rootView.findViewById(R.id.imageIcon);
        imageIcon.setAlpha(127);

        return rootView;
    }

    //facebook callback
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void searchFileDB() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Searching");
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        Log.d("abcd", "onClick!");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("abcd", "onResponse!");
                progressDialog.dismiss();
                try {
                    Log.d("abcd","in try!");
                    Log.d("abcd", String.valueOf(response.length()));
                    Log.d("abcd", response.toString());
                    JSONObject File = response.getJSONObject(0);
                    Log.d("abcd", "got response");
                    String name = File.getString("name");
                    String fileText = File.getString("fileText");
                    Log.d("abcd", name);
                    Log.d("abcd", fileText);

                    // found file and move to file View
                    Intent view = new Intent(getActivity(), SearchFile.class);
                    view.putExtra("name", name);
                    Toast.makeText(getApplicationContext(), "Found file!", Toast.LENGTH_LONG).show();
                    startActivity(view);
                } catch (JSONException e) { e.printStackTrace(); }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "error: " + volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }
}
