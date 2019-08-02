package com.example.whatsappclone.adapters;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Chat;
import com.example.whatsappclone.model.Contacts;
import com.example.whatsappclone.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private static final String TAG = "UserListAdapter";

    ArrayList<User> mUsers;

    public UserListAdapter(ArrayList<User> users) {

        this.mUsers = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.userNameView.setText(mUsers.get(i).getUserName());

        viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String chatKey = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

                Log.d(TAG, "onClick chat key: "+ chatKey);

                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(chatKey).setValue(mUsers.get(i).getUserName());



                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(mUsers.get(i).getUserId()).child("contactList");
                databaseReference.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        String chatKey = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
//
//                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(chatKey).setValue(mUsers.get(i).getUserName());

                        for(DataSnapshot contacts:dataSnapshot.getChildren()){

                            Contacts contactUser = contacts.getValue(Contacts.class);
                            String receiveName = contactUser.getUserName();
                            String receiverNumber = contactUser.getUserPhoneNumber();


                            Log.d(TAG, "onDataChange: "+receiveName);
                            Log.d(TAG, "onDataChange: "+ FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                            Log.d(TAG, "onDataChange: "+ receiverNumber);
                           // FirebaseDatabase.getInstance().getReference().child("user").child(mUsers.get(i).getUserId()).child("chat").child(chatKey).setValue(receiveName);

                            if(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().equals(receiverNumber))
                            FirebaseDatabase.getInstance().getReference().child("user").child(mUsers.get(i).getUserId()).child("chat").child(chatKey).setValue(receiveName);

                        }
//                        String receiveName = dataSnapshot.getRef().getKey();
//                        Log.d(TAG, "onDataChange: "+receiveName);
//                        FirebaseDatabase.getInstance().getReference().child("user").child(mUsers.get(i).getUserId()).child("chat").child(chatKey).setValue(receiveName);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
               // String receiverName = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("contactList");


                Log.d(TAG, "onClick: "+ mUsers.get(i).getUserPhoneNumber());
                Log.d(TAG, "onClick: "+ FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());


            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView userNameView;
        FrameLayout mLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameView = itemView.findViewById(R.id.user_name_view);
            mLayout = itemView.findViewById(R.id.user_layout);
        }
    }
}
