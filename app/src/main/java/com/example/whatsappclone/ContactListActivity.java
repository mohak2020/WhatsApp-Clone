package com.example.whatsappclone;

import android.Manifest;
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
import android.widget.Toast;

import com.example.whatsappclone.adapters.UserListAdapter;
import com.example.whatsappclone.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    private static final String TAG = "ContactListActivity";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    ArrayList<User> mUsers;
    ArrayList<User> mContacts;
    RecyclerView mRecyclerView;
    UserListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Log.d(TAG, "onCreate: ");


        mUsers = new ArrayList<>();
        mContacts = new ArrayList<>();
        mRecyclerView = findViewById(R.id.contact_recycle_view);

        getContactList();
        initRecycleView();

    }

    private void getContactList() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }


        Cursor usersContactInfo = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

        while (usersContactInfo.moveToNext()) {

            String userName = usersContactInfo.getString(usersContactInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String userPhoneNumber = usersContactInfo.getString(usersContactInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            userPhoneNumber = userPhoneNumber.replace(" ", "");
            userPhoneNumber = userPhoneNumber.replace("-", "");
            userPhoneNumber = userPhoneNumber.replace("(", "");
            userPhoneNumber = userPhoneNumber.replace(")", "");

            User singleContact = new User(userName, userPhoneNumber);

            mContacts.add(singleContact);
            getUserDetails(singleContact);
            Log.d(TAG, "getContactList: "+ userName);
        }


    }

    private void getUserDetails(User user) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
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

                        User singleUser = new User(userName, userPhoneNumber);

                        Log.d(TAG, "onDataChange: "+ userName);

                        if(userName.equals(userPhoneNumber)){
                            for(User contactIterator:mContacts){
                                if(contactIterator.getUserPhoneNumber().equals(singleUser.getUserPhoneNumber())){
                                    singleUser.setUserName(contactIterator.getUserName());
                                }
                            }
                        }

                        mUsers.add(singleUser);
                        initRecycleView();
                        return;

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContactList();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initRecycleView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
         mAdapter = new UserListAdapter(mUsers);
        mRecyclerView.setAdapter(mAdapter);
    }


}
