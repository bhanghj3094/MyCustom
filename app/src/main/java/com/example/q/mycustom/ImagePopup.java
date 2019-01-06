package com.example.q.mycustom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;




import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;


import java.io.File;
import java.util.Arrays;

public class ImagePopup extends FragmentActivity implements View.OnClickListener {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("wrong", "onto onCreate of ImagePopup");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepopup);
        mContext = this;

        // Information with intent
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        final String imgPath = extras.getString("filepath");
        final String imgName = extras.getString("filename");

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
                String[] parseDirectory = imgPath.split("/");
                String directory = TextUtils.join("/", Arrays.copyOfRange(parseDirectory, 0, parseDirectory.length - 1));
                Log.d("wrong", "directory: " + directory);
                Log.d("wrong", "image name: " + imgName);

                File file = new File(directory, imgName); // 파일 경로 설정 + imgName 은 파일 이름
                Uri uri = FileProvider.getUriForFile(mContext, "com.example.q.mycustom.provider", file);

                Log.d("wrong","file");
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpg"); // set jpg type
                intent.putExtra(Intent.EXTRA_STREAM, uri); // put img w/ uri
                startActivity(Intent.createChooser(intent, "Choose")); // bring up sharing activity
            }
        });

        /* PUSH IMAGE to Database -------------------------------------------------------------- */
        // ===================================================================================== //
        




        Log.d("wrong", "successful in ImagePopup onCreate");
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonBack:
                finish();
        }
    }


}
