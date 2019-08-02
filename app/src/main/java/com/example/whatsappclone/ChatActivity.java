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

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

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

                        Chat singleChat = new Chat(childSnapshot.getKey().toString(),childSnapshot.getValue().toString());

                        Log.d(TAG, "onDataChange: "+childSnapshot.getKey().toString());
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
