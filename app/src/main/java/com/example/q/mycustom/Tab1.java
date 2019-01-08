package com.example.q.mycustom;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Tab1 extends Fragment {
    View rootView;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private EditText editSearch;
    private ArrayList<phonenum_item> list;
    private ArrayList<phonenum_item> arrayList;
    private ArrayList<phonenum_item> data;
    private CallbackManager callbackManager;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this.getContext());
        rootView = inflater.inflate(R.layout.tab1, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_DENIED) {
            try {
                data = getContactList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mRecyclerView = rootView.findViewById(R.id.phonenum);
            mLayoutManager = new LinearLayoutManager(getActivity());
            final MyAdapter myAdapter = new MyAdapter(data);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(myAdapter);

            ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    // do it
                    myAdapter.callContactViewer(position);
                }
            });

            //검색 기능 구현
            editSearch = (EditText) rootView.findViewById(R.id.editSearch);
            mRecyclerView = rootView.findViewById(R.id.phonenum);
            arrayList = new ArrayList<phonenum_item>();
            list = new ArrayList<phonenum_item>();
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

        }

        //facebook callback
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
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
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));



        //이미지 버튼 구현
        ImageButton cloudButton = rootView.findViewById(R.id.cloudButton);
        final ImageButton servercontactButton = rootView.findViewById(R.id.servercontact);
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
                Button facebookbutton = rootView.findViewById(R.id.login_button);
                facebookbutton.setVisibility(facebookbutton.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                servercontactButton.setVisibility(servercontactButton.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);


                fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
                fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
                anim();

//                if (facebookbutton.getVisibility() == View.GONE) {
//                    facebookbutton.setVisibility(View.VISIBLE);
//                } else {
//                    facebookbutton.setVisibility(View.GONE);
//                }
            }
        });
        //show server contact 이미지 버튼
        servercontactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getActivity(), servercontact.class);
                startActivity(it);
            }
        });

        //add to a contact 버튼
        ImageButton addcontact = rootView.findViewById(R.id.addcontact);
        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addContact = new Intent(ContactsContract.Intents.Insert.ACTION);
                addContact.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                startActivity(addContact);
            }
        });


        return rootView;

    }


    @Override
    public void onResume() {
        Log.d("cheeck", "on resume tab1");
        super.onResume();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_DENIED) {
            mRecyclerView = rootView.findViewById(R.id.phonenum);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), 1));
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            ArrayList<phonenum_item> data = new ArrayList<>();
            try {
                data = getContactList();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MyAdapter myAdapter = new MyAdapter(data);
            mRecyclerView.setAdapter(myAdapter);

        }
    }

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
        data = list;
        MyAdapter mySearchAdapter = new MyAdapter(data);
        mRecyclerView.setAdapter(mySearchAdapter);
    }

    private ArrayList<phonenum_item> getContactList() throws JSONException {
        ArrayList<phonenum_item> data = new ArrayList<>();
        int icon = R.drawable.user;


        Cursor cursor = getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID
        }, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

//        ri photo_uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,cursor.getInt(2));
//        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContext().getContentResolver().photo_uri);

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
        return data;
    }


    private String contactsPhone(String id) {
        String result = null;

        if ((id == null) || (id.trim().equals(""))) {
            return null;
        }

        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{
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

    public void anim() {
        Button facebookbutton = rootView.findViewById(R.id.login_button);
        ImageButton servercontactButton = rootView.findViewById(R.id.servercontact);
        if (isFabOpen) {
            facebookbutton.startAnimation(fab_close);
            servercontactButton.startAnimation(fab_close);
            isFabOpen = false;

        } else {
            facebookbutton.startAnimation(fab_open);
            servercontactButton.startAnimation(fab_open);
            isFabOpen = true;
        }
    }


    //facebook callback
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            TextView name;
            TextView phonenum;

            MyViewHolder(View view) {
                super(view);
                icon = view.findViewById(R.id.imageView);
                name = view.findViewById(R.id.textView1);
                phonenum = view.findViewById(R.id.textView2);
            }
        }

        public final void callContactViewer(int selectedIndex) {
            Intent i = new Intent(getContext(), ContactPopup.class);

            i.putExtra("name", data.get(selectedIndex).getName());
            i.putExtra("number", data.get(selectedIndex).getPhonenum());
            startActivityForResult(i, 1);
        }

        private ArrayList<phonenum_item> data;

        MyAdapter(ArrayList<phonenum_item> data) {
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.phonenum_item, parent, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MyViewHolder myViewHolder = (MyViewHolder) holder;

            myViewHolder.icon.setImageResource(data.get(position).getIcon());
            myViewHolder.name.setText(data.get(position).getName());
            myViewHolder.phonenum.setText(data.get(position).getPhonenum());
        }

        @Override
        public int getItemCount() {
            Integer i = 0;
            try {
                return data.size();
            } catch (NullPointerException e) {
                return 0;
            }
        }

    }


}
