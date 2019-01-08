package com.example.q.mycustom;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class MakeFile extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("wrong", "onto onCreate of ImagePopup");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_file);

        //send
    }
}
