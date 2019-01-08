package com.example.q.mycustom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.util.Random;

public class SearchFile extends Activity {
    String fileName;
    int CALL_EDIT_FROM_SEARCH = 1;
    private Socket mSocket;
    // link socket to "http://socrip4.kaist.ac.kr:1880"
    {
        try{
            mSocket = IO.socket("http://socrip4.kaist.ac.kr:1880");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    TextView fileNameView, fileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_file);

        // get fileName
        Bundle bundle = getIntent().getExtras();
        fileName = bundle.getString("name");

        // set views
        fileNameView = findViewById(R.id.search_filename);
        fileTextView = findViewById(R.id.search_filecontents);
        fileNameView.setText(fileName);

        // Get Random Username
        Random random = new Random();
        int userNumber = random.nextInt(20000) + 1;
        final String username = "User-" + String.valueOf(userNumber);

        // socket connection
        mSocket.connect();

        /* ================ User Alert ===================== */
        // ================================================= //
        // alert newViewer and bring file
        mSocket.emit("newViewer", username, fileName);
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
        // listen for newEditor
        mSocket.on("newEditor", new Emitter.Listener() {
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
        }); // ============================================= //


        /* ================= File Update =================== */
        // ================================================= //
        // listen for file Update
        mSocket.on("fileUpdated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSocket.emit("updateFile", fileName);
                    }
                });
            }
        });
        Log.d("sooo","onCreate");
        // listen for fileView
        mSocket.on("fileView", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Log.d("sooo", "Emitter listened and called");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("hello", "into run()");
                            JSONArray jsonArray = (JSONArray) args[0];
                            Log.d("hello", jsonArray.toString());
                            JSONObject File = (JSONObject) jsonArray.get(0);
                            Log.d("hello", File.toString());
                            String fileContents = File.getString("fileText");
                            Log.d("hello", fileContents);
                            fileTextView.setText(fileContents);
                            Log.d("hello", "end");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: cannot bring File", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }); // ============================================= //

        // ERROR dealing with socket: cannot EDIT
        mSocket.on("Error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), SearchFile.class);
                        Toast.makeText(getApplicationContext(), "Oops.. Already Edit in Process", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                });
            }
        });


        // button for EDIT text
        Button EditFileButton = findViewById(R.id.EditFileButton);
        EditFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("edit", username);
                try { // wait for the incoming Error call in case of error.
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace(); }
                Intent intent = new Intent(getApplicationContext(), EditFile.class);
                intent.putExtra("name", fileName);
                intent.putExtra("userName", username);
                startActivity(intent);
            }
        });
    }

}
