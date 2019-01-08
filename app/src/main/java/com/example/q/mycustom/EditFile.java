package com.example.q.mycustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class EditFile extends AppCompatActivity {
    private Socket mSocket;
    // link socket to "http://socrip4.kaist.ac.kr:1880"
    {
        try{
            mSocket = IO.socket("http://socrip4.kaist.ac.kr:1880");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_file);

        // get fileName to Edit
        Bundle bundle = getIntent().getExtras();
        fileName = bundle.getString("name");
        String userName = bundle.getString("userName");  // userName from SearchFile

        // bring layout
        TextView editFileName = findViewById(R.id.editFileName);
        final EditText editFileContents = findViewById(R.id.editFileContents);
        editFileName.setText(fileName);

        // socket connection
        mSocket.connect();

        /* ================ User Alert ===================== */
        // ================================================= //
        // alert newEditor and bring file
        mSocket.emit("newEditor", userName, fileName);
        // listen for newUser
        mSocket.on("newUser", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String msg = (String) args[0];
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        // listen for fileView since new editor
        mSocket.on("fileView", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray = (JSONArray) args[0];
                            JSONObject File = (JSONObject) jsonArray.get(0);
                            String fileContents = File.getString("fileText");
                            editFileContents.setText(fileContents);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: cannot bring File", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }); // ============================================= //


        ImageButton sendFileButton = findViewById(R.id.sendFileButton);
        sendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("save", fileName, editFileContents.getText());
                finish();
            }
        });

    }
}
