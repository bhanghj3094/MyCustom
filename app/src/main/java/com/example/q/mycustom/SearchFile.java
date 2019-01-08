package com.example.q.mycustom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SearchFile extends Activity {
    String fileName, fileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_file);

        // get fileName and fileText
        Bundle bundle = getIntent().getExtras();
        fileName = bundle.getString("name");
        fileText = bundle.getString("fileText");

        // set views
        TextView fileNameView = (TextView) findViewById(R.id.search_filename);
        TextView fileTextView = (TextView) findViewById(R.id.search_filecontents);

        // put strings into textviews
        fileNameView.setText(fileName);
        fileTextView.setText(fileText);

        // button for EDIT text
//        ImageButton editFileButton = findViewById(R.id.editFileButton);
//        editFileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), EditFile.class);
//                intent.putExtra("name", fileName);
//                startActivity(intent);
//            }
//        });

        // button for EDIT text
        Button EditFileButton = findViewById(R.id.EditFileButton);
        EditFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditFile.class);
                intent.putExtra("name", fileName);
                startActivity(intent);
            }
        });
    }

}
