package com.example.whatsappclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.whatsappclone.adapters.ChatAdapter;
import com.example.whatsappclone.adapters.MessageAdapter;
import com.example.whatsappclone.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    ArrayList<Message> mMessageList;
    RecyclerView mRecyclerView;
    MessageAdapter mMessageAdapter;

    private RecyclerView.LayoutManager mChatLayoutManager;

    String mChatId;

    DatabaseReference mReadMessagesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mRecyclerView = findViewById(R.id.message_recycle_view);
        mMessageList = new ArrayList<>();


        mChatId = getIntent().getExtras().getString("chatID");

        mReadMessagesDB = FirebaseDatabase.getInstance().getReference().child("chat").child(mChatId);

        Button sendButton = findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();

            }


        });

        readChatMessages();
        initRecycleView();



        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if ( bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.scrollToPosition(mMessageList.size()-1);
                        }
                    }, 100);
                }
            }
        });


    }

    private void sendMessage(){

        DatabaseReference mWriteMessagesDB = FirebaseDatabase.getInstance().getReference().child("chat").child(mChatId).push();

        EditText messageEditText = findViewById(R.id.message_edit);

        if(!messageEditText.getText().toString().isEmpty()) {

            Map newMessageMap = new HashMap<>();

            newMessageMap.put("message", messageEditText.getText().toString());
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

            mWriteMessagesDB.updateChildren(newMessageMap);


        }

        messageEditText.setText(null);

    }

    private void readChatMessages(){

        mReadMessagesDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String creator = "";
                    String message = "";

                    if(dataSnapshot.child("creator").getValue().toString()!=null)
                        creator = dataSnapshot.child("creator").getValue().toString();

                    if(dataSnapshot.child("message").getValue().toString()!=null)
                        message = dataSnapshot.child("message").getValue().toString();


                    Message singleMessage = new Message(dataSnapshot.getKey(),creator,message);

                    mMessageList.add(singleMessage);
                    mChatLayoutManager.scrollToPosition(mMessageList.size()-1);
                    mMessageAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    private void initRecycleView(){
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        ((LinearLayoutManager) mChatLayoutManager).setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mMessageAdapter = new MessageAdapter(mMessageList);
        mRecyclerView.setAdapter(mMessageAdapter);

    }
}
