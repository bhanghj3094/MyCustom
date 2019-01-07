package com.example.q.mycustom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ImagePopup extends FragmentActivity implements View.OnClickListener {
    private Context mContext;
    String urlUpload = "http://143.248.140.106:1880/api/post/image";
    Bitmap bitmap;
    ProgressDialog progressDialog;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("wrong", "onto onCreate of ImagePopup");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepopup);
        mContext = this;

        // Information with intent
        Bundle extras = getIntent().getExtras();
        final String imgPath = extras.getString("filepath");
        final String imgName = extras.getString("filename");
        // Parse imgPath to only directory
        String[] parseDirectory = imgPath.split("/");
        final String directory = TextUtils.join("/", Arrays.copyOfRange(parseDirectory, 0, parseDirectory.length - 1));
        Log.d("wrong", "directory: " + directory);
        Log.d("wrong", "image name: " + imgName);

        // image fullscreen view
        ImageView iv = findViewById(R.id.imageView);
        Glide.with(mContext).load(imgPath).into(iv);

        // button for return
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);

        // button for sharing
        Button buttonShare = (Button) findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("wrong", "onClick of setOnClickListener");
                shareImage(); //공유 이미지 함수를 호출 합니다.
            }

            private void shareImage() {
                Log.d("wrong", "begin shareImage");

                File file = new File(directory, imgName); // 파일 경로 설정 + imgName 은 파일 이름
                Uri uri = FileProvider.getUriForFile(mContext, "com.example.q.mycustom.provider", file);

                Log.d("wrong","file");
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpg"); // set jpg type
                intent.putExtra(Intent.EXTRA_STREAM, uri); // put img w/ uri
                startActivity(Intent.createChooser(intent, "Choose")); // bring up sharing activity
            }
        });

        // ===================================================================================== //
        /* ============================ POST IMAGE to Server =================================== */
        // ===================================================================================== //
        Button buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("errT", "start upload");
                progressDialog = new ProgressDialog(ImagePopup.this);
                progressDialog.setTitle("Uploading");
                progressDialog.setMessage("Please wait..");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("errT", "Response here!");
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("errT", "eeerrrroorrrr");
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "error: " + volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }){ // adding parameter to send
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Log.d("errT", "into the getParams()");
                        Map<String, String> parameters = new HashMap<String, String>();

                        // make image bitmap
                        Uri uri = Uri.fromFile(new File(imgPath));
                        try{ InputStream inputStream = getContentResolver().openInputStream(uri);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                        } catch (FileNotFoundException e) { e.printStackTrace(); }

                        // change image into String with Base64 and send
                        String imageData = imageToString(bitmap);
                        parameters.put("image", imageData);
                        parameters.put("name", imgName);

                        progressDialog.dismiss();
                        return parameters;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(ImagePopup.this);
                requestQueue.add(stringRequest);
                Log.d("errT", "end of onClick buttonUpload");
            }
        });

        Log.d("wrong", "successful in ImagePopup onCreate");
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonBack:
                finish();
        }
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}







