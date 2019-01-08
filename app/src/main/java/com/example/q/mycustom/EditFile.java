package com.example.q.mycustom;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;

public class EditFile extends Activity {
    private Socket mSocket;
    // link socket to "http://socrip4.kaist.ac.kr:1880"
    {
        try{
            mSocket = IO.socket("http://socrip4.kaist.ac.kr:1880");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //Emitting events
    private void attemptSend() {
        JSONObject message = new JSONObject();
        try {
            message.put("id", "3");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mSocket.emit("new message", message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_file);

        // get fileName to Edit
        Bundle bundle = getIntent().getExtras();
        String fileName = bundle.getString("name");

        // bring layout
        TextView editFileName = findViewById(R.id.editFileName);
        EditText editFileContents = findViewById(R.id.editFileContents);
        editFileName.setText(fileName);

        // Get Random Username
        Random random = new Random();
        int userNumber = random.nextInt(20000) + 1;
        String username = "User-" + String.valueOf(userNumber);

        // socket connection
        mSocket.connect();
//        mSocket.emit('newViewer', ())


    }
}
