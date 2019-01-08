package com.example.q.mycustom;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerImages extends AppCompatActivity {
    Context context = this;
    GridView gridview;
    String urlGetImages = "http://143.248.140.106:1880/api/show/images";
    ArrayList<String> imageNames = new ArrayList<String>();
    ArrayList<byte[]> imageObjs = new ArrayList<byte[]>();

    @Override
    public void onResume()
    {
        super.onResume();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            getImagesDB();
            final ImageAdapter ia = new ImageAdapter(context, imageObjs);
            gridview.setAdapter(ia);
//            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                public void onItemClick(AdapterView<?> parent, View v,
//                                        int position, long id) {
//                    ia.callImageViewer(position);
//                }
//            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("eTest", "----------------ServerImages.class onCreate------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverimage);
        gridview = findViewById(R.id.gridview_server);

        getImagesDB();
        Log.d("eTest", "ServerImages onCreate end");
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<byte[]> imageArray;
        ImageView imageView;

        public ImageAdapter(Context c, ArrayList<byte[]> o) {
            mContext = c;
            imageArray = o;
        }

        public final void callImageViewer(int selectedIndex) {
//            Intent i = new Intent(mContext, ImagePopup.class);
//            startActivityForResult(i,1);
        }

        @Override
        public int getCount() {
            Log.d("eTest", "getCount: " + String.valueOf(imageArray.size()));
            return imageArray.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("eTest", "Adapter getView function started");
            if (convertView == null) { // not recycled
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(500,500));
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(4, 4, 4, 4);
            } else {
                imageView = (ImageView) convertView;
            }
            Glide.with(mContext).load(imageArray.get(position)).into(imageView);
            return imageView;
        }

    }

    // Get image Informations from DB
    public void getImagesDB() {
        Log.d("eTest", "start getImagesDB");

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetImages, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("eTest", String.valueOf(response.length()));
                try {
                    for (int i=0; i<response.length(); i++) {
                        // get Json object and data
                        JSONObject Person = response.getJSONObject(i);
                        String name = Person.getString("name");
                        String bsImage = Person.getString("imageBase64");

                        byte[] decodedString = Base64.decode(bsImage, Base64.DEFAULT);

                        imageNames.add(name);
                        imageObjs.add(decodedString);
                        Log.d("eTest", name);
                        Log.d("eTest", decodedString.toString());
                }
                    // 비동기적 코드이기 때문에 여기 있어야 한다!!!!!!
                    final ImageAdapter ia = new ImageAdapter(context, imageObjs);
                    gridview.setAdapter(ia);
                } catch (JSONException e) { e.printStackTrace(); }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
        Log.d("eTest", "end getImagesDB..");
    }

}



