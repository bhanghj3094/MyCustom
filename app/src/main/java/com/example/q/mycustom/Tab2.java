package com.example.q.mycustom;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Tab2 extends Fragment {
    View rootView;
    String imgname;
    GridView gridview;
    private CallbackManager callbackManager;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            gridview = rootView.findViewById(R.id.gridview);
            final ImageAdapter ia = new ImageAdapter(rootView.getContext());
            gridview.setAdapter(ia);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    ia.callImageViewer(position);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab2, container, false);

        GridView gridview = rootView.findViewById(R.id.gridview);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            final ImageAdapter ia = new ImageAdapter(rootView.getContext());
            gridview.setAdapter(ia);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    ia.callImageViewer(position);
                }
            });
        }


        //이미지 버튼 구현
        ImageButton cloudButton = rootView.findViewById(R.id.cloudButton1);
        final ImageButton imageDB_Button = rootView.findViewById(R.id.imageDB_Button);
        cloudButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 누름 유지
                if (view.isSelected() == false) {
                    view.setSelected(true);
                } else {
                    view.setSelected(false);
                }

                //숨김 버튼 다시 나타내는 애니메이션
                Button facebookbutton = rootView.findViewById(R.id.login_button1);
                facebookbutton.setVisibility(facebookbutton.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                imageDB_Button.setVisibility(imageDB_Button.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);


                fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
                fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
                anim();
            }
        });
        imageDB_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ServerImages.class);
                startActivity(intent);
            }
        });

        //facebook callback
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.login_button1);
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
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr", error.toString());
            }
        });



        // Inflate the layout for this fragment
        return rootView;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private String imgData = null;
        private String geoData = null;
        private ArrayList<String> thumbsDataList;
        private ArrayList<String> thumbsIDList;

        public ImageAdapter(Context c) {
            mContext = c;
            thumbsDataList = new ArrayList<String>();
            thumbsIDList = new ArrayList<String>();
            getThumbInfo(thumbsIDList, thumbsDataList);
        }

        public final void callImageViewer(int selectedIndex) {
            Intent i = new Intent(mContext, ImagePopup.class);
            String imgPath = getImageInfo(imgData, geoData, thumbsIDList.get(selectedIndex));

            i.putExtra("filename", imgname);
            i.putExtra("filepath", imgPath);
            Toast.makeText(getActivity(), "imagepath : " + imgPath + "\nimage name : " + imgname + "!", Toast.LENGTH_SHORT).show();
            startActivityForResult(i, 1);
        }

        public int getCount() {
            return thumbsIDList.size(); //mThumbIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("eTest", "Tab2 getView");
            ImageView imageView;
            if (convertView == null) { // not recycled
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(500, 500));
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(4, 4, 4, 4);
            } else {
                imageView = (ImageView) convertView;
            }

            Glide.with(mContext).load(thumbsDataList.get(position)).into(imageView);
            return imageView;
        }

        private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas) {
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME};

            Cursor imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);

            if (imageCursor != null && imageCursor.moveToLast()) {
                String thumbsID;
                String thumbsImageID;
                String thumbsData;

                int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
                int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int num = 0;
                do {
                    thumbsID = imageCursor.getString(thumbsIDCol);
                    thumbsData = imageCursor.getString(thumbsDataCol);
                    thumbsImageID = imageCursor.getString(thumbsImageIDCol);
                    num++;
                    if (thumbsImageID != null) {
                        thumbsIDs.add(thumbsID);
                        thumbsDatas.add(thumbsData);
                    }
                } while (imageCursor.moveToPrevious());
            }
            return;
        }

        private String getImageInfo(String ImageData, String Location, String thumbID) {
            String imageDataPath = null;

            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};
            Cursor imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, "_ID='" + thumbID + "'", null, null);

            if (imageCursor != null && imageCursor.moveToFirst()) {
                if (imageCursor.getCount() > 0) {
                    int imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    int imageName = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    imageDataPath = imageCursor.getString(imgData);
                    imgname = imageCursor.getString(imageName);
                }
            }
            return imageDataPath;
        }
    }
    public void anim() {
        Button facebookbutton = rootView.findViewById(R.id.login_button1);
        ImageButton imageDB_Button = rootView.findViewById(R.id.imageDB_Button);
        if (isFabOpen) {
            facebookbutton.startAnimation(fab_close);
            imageDB_Button.startAnimation(fab_close);
            isFabOpen = false;

        } else {
            facebookbutton.startAnimation(fab_open);
            imageDB_Button.startAnimation(fab_open);
            isFabOpen = true;
        }
    }

    //facebook callback
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
