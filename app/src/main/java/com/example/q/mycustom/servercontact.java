package com.example.q.mycustom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class servercontact extends AppCompatActivity {
    Context context = this;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private EditText editSearch;
    private ArrayList<phonenum_item> list = new ArrayList<phonenum_item>();
    private ArrayList<phonenum_item> arrayList = new ArrayList<phonenum_item>();

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    //서버 관련
    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON = "contacts";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";

    private TextView mTextViewResult;
    ArrayList<phonenum_item> data = new ArrayList<phonenum_item>();
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servercontact);

        mRecyclerView = findViewById(R.id.phonenum1);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //서버에서 데이터 가져오기
        GetData task = new GetData();
        task.execute("http://143.248.140.106:1880/api/show/contacts");


        //검색 기능 구현
        editSearch = (EditText) findViewById(R.id.editSearch1);
        mRecyclerView = findViewById(R.id.phonenum1);
        arrayList.addAll(data);
        list.addAll(data);


        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);

            }
        });

        ImageButton uploadContact = findViewById(R.id.add_contact1);
        uploadContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("eTest", "onClick!");
                Intent intent = new Intent(context, AddContactServer.class);
                Log.d("eTest", "Intent");
                startActivity(intent);
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(servercontact.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null) {

                mTextViewResult.setText(errorString);
            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult() {
        try {
            int icon = R.drawable.user;
            JSONArray jsonArray = new JSONArray(mJsonString);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                phonenum_item phone_item = new phonenum_item(icon, item.getString("name"), item.getString("number"));

                data.add(phone_item);
            }
            MyAdapter myAdapter = new MyAdapter(data);
            mRecyclerView.setAdapter(myAdapter);


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }



    // 검색 수행 방법
    public void search(String charText) {
        list.clear();

        if (charText.length() == 0) {
            list.addAll(arrayList);
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getName().toLowerCase().contains(charText)) {
                    list.add(arrayList.get(i));
                } else if (arrayList.get(i).getPhonenum().contains(charText)) {
                    list.add(arrayList.get(i));
                }
            }
        }
        MyAdapter mySearchAdapter = new MyAdapter(list);
        mRecyclerView.setAdapter(mySearchAdapter);
    }


    // add contact 버튼
    public void add_contact1(View view) {


    }

    // 서버에 저장된 연락처 가져오기
    private ArrayList<phonenum_item> getContactList() throws JSONException {
        ArrayList<phonenum_item> data = new ArrayList<>();
        int icon = R.drawable.user;

        Cursor cursor = managedQuery(ContactsContract.Contacts.CONTENT_URI, new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID
        }, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

        JSONArray jsonArray = new JSONArray();

        while (cursor.moveToNext()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("icon", icon);
                jsonObject.put("name", cursor.getString(1));
                jsonObject.put("phonenum", contactsPhone(cursor.getString(0)));

                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject dataJsonObject = jsonArray.getJSONObject(i);
            phonenum_item item = new phonenum_item(dataJsonObject.getInt("icon"), dataJsonObject.getString("name"), dataJsonObject.getString("phonenum"));
            data.add(item);
        }

//            try {
//                String id = cursor.getString(0);
//                String name = cursor.getString(1);
//                String phonenum = contactsPhone(id);
//                phonenum_item item = new phonenum_item(icon, name, phonenum);
//                data.add(item);
//            } catch (Exception e) {
//                System.out.println(e.toString());
//            }
//        }

        return data;
    }

    private String contactsPhone(String id) {
        String result = null;

        if ((id == null) || (id.trim().equals(""))) {
            return null;
        }

        Cursor cursor = managedQuery(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER
        }, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);

        while (cursor.moveToNext()) {
            try {
                result = cursor.getString(0);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        cursor.close();

        return result;
    }

//    @Override
//    public void onBackPressed() {
//        long tempTime = System.currentTimeMillis();
//        long intervalTime = tempTime - backPressedTime;
//
//        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
//            super.onBackPressed();
//        } else {
//            backPressedTime = tempTime;
//            Intent it = new Intent(this, MainActivity.class);
//            startActivity(it);
//            finish();
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(servercontact.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent2 = new Intent(servercontact.this, MainActivity.class);
            startActivity(intent2);
            finish();
        }
    }
}