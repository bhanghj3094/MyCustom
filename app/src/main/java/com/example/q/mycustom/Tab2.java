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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Tab2 extends Fragment {
    View rootView;
    String imgname = null;
    GridView gridview;

    @Override
    public void onResume()
    {
        super.onResume();
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
            startActivityForResult(i,1);
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
            ImageView imageView;
            if (convertView == null) { // not recycled
                Log.d("wrong", "convertView == null");
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(500,500));
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(4, 4, 4, 4);
            } else {
                imageView = (ImageView) convertView;
            }

            Glide.with(mContext).load(thumbsDataList.get(position)).into(imageView);
            return imageView;
        }

        private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas){
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME};

            Cursor imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);

            if (imageCursor != null && imageCursor.moveToLast()){
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
                    if (thumbsImageID != null){
                        thumbsIDs.add(thumbsID);
                        thumbsDatas.add(thumbsData);
                    }
                } while (imageCursor.moveToPrevious());
            }
            return;
        }

        private String getImageInfo(String ImageData, String Location, String thumbID){
            String imageDataPath = null;

            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};
            Cursor imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, "_ID='"+ thumbID +"'", null, null);

            if (imageCursor != null && imageCursor.moveToFirst()){
                if (imageCursor.getCount() > 0){
                    int imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    int imageName = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    imageDataPath = imageCursor.getString(imgData);
                    imgname = imageCursor.getString(imageName);
                }
            }
            return imageDataPath;
        }
    }
}
