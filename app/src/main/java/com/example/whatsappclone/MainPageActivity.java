package com.example.whatsappclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.whatsappclone.adapters.ChatAdapter;
import com.example.whatsappclone.model.Chat;
import com.example.whatsappclone.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {

    private static final String TAG = "MainPageActivity";

    Button mContactButton;
    ArrayList<Chat> mChatList;
    RecyclerView mRecyclerView;
    ChatAdapter mChatAdapter;

    ArrayList<User> mContacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        mRecyclerView = findViewById(R.id.chat_recycle_view);
        mChatList = new ArrayList<>();

        mContactButton = findViewById(R.id.contact_button);


        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),ContactListActivity.class));

            }
        });

        getUsersChatList();

        initRecycleView();

        //getContactList();


    }

    private void getUsersChatList() {
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");

        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot:dataSnapshot.getChildren()){

//
                        //

                        Chat singleChat = new Chat(childSnapshot.getKey(),"");
                        boolean exists = false;

                        for(Chat chatIterator: mChatList){

                            if(chatIterator.getChatId().equals(singleChat.getChatId()))
                                exists = true;
                        }

                        if(exists)
                            continue;

                        mChatList.add(singleChat);

                        initRecycleView();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getContactList() {


        Cursor usersContactInfo = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

        while (usersContactInfo.moveToNext()) {

            String userName = usersContactInfo.getString(usersContactInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String userPhoneNumber = usersContactInfo.getString(usersContactInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            userPhoneNumber = userPhoneNumber.replace(" ", "");
            userPhoneNumber = userPhoneNumber.replace("-", "");
            userPhoneNumber = userPhoneNumber.replace("(", "");
            userPhoneNumber = userPhoneNumber.replace(")", "");

            User singleContact = new User("",userName, userPhoneNumber);
//
//            mContacts.add(singleContact);
            //getUserDetails(singleContact);
//            Log.d(TAG, "getContactList: "+ userName);
            Log.d(TAG, "getContactList: "+userName);
        }


    }


    private void getUserDetails(User user) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = databaseReference.orderByChild("PhoneNumber").equalTo(user.getUserPhoneNumber());

        Log.d(TAG, "getUserDetails: user "+ user.getUserName());


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String userName="";
                    String userPhoneNumber ="";

                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("Name").getValue()!=null)
                            userName = childSnapshot.child("Name").getValue().toString();

                        if(childSnapshot.child("PhoneNumber").getValue()!=null)
                            userPhoneNumber = childSnapshot.child("PhoneNumber").getValue().toString();
                        Log.d(TAG, "onDataChange: "+ userPhoneNumber);

                        User singleUser = new User(childSnapshot.getKey(),userName, userPhoneNumber);

                        Log.d(TAG, "user id: "+ childSnapshot.getKey());

                        if(userName.equals(userPhoneNumber)){
                            for(User contactIterator:mContacts){
                                if(contactIterator.getUserPhoneNumber().equals(singleUser.getUserPhoneNumber())){
                                    singleUser.setUserName(contactIterator.getUserName());
                                }
                            }
                        }

//                        mUsers.add(singleUser);
//                        initRecycleView();
                        return;

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserName(Chat chatId){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");

        Query query = databaseReference.orderByChild("chat").equalTo(chatId.getChatId());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_put) {

            FirebaseAuth.getInstance().signOut();


        }
        return true;
    }

    private void initRecycleView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mChatAdapter = new ChatAdapter(mChatList);
        mRecyclerView.setAdapter(mChatAdapter);
    }
}
