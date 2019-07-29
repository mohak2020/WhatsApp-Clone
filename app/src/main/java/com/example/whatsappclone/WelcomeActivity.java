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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsappclone.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static final int RC_SIGN_IN = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference mDatabaseReference;

    ArrayList<User> mContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: ");

        mContacts = new ArrayList<>();

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                Log.d(TAG, "onAuthStateChanged: ");

                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user!=null) {
                    //Log.d(TAG, "onAuthStateChanged: "+user.getEmail());
                    setContentView(R.layout.activity_main);

                    Log.d(TAG, "onAuthStateChanged: "+user.getPhoneNumber());

                    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());

                    getContactList();

                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Map<String,Object> userMap = new HashMap<>();


                            for(User contactIterator:mContacts){
                                if(contactIterator.getUserPhoneNumber().equals(user.getPhoneNumber())){
                                   // singleUser.setUserName(contactIterator.getUserName());
                                    userMap.put("Name",contactIterator.getUserName());
                                    Log.d(TAG, "onDataChange: "+contactIterator.getUserName());
                                }
                            }

                            userMap.put("PhoneNumber",user.getPhoneNumber());

                            mDatabaseReference.updateChildren(userMap);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Button launchButton = (Button)findViewById(R.id.launch_app);


                    launchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {



                            Log.d(TAG, "onClick: "+ user.getUid());
                            Intent intent = new Intent(WelcomeActivity.this, ChatActivity.class);
                            startActivity(intent);
                        }
                    });

                    Button signoutButton =(Button)findViewById(R.id.sign_out);

                    signoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AuthUI.getInstance().signOut(getApplicationContext());
                        }
                    });

                    mFirebaseAuth = FirebaseAuth.getInstance();

                    Toast.makeText(getApplicationContext(), "You're now signed in. Welcome to News App.", Toast.LENGTH_SHORT).show();


                }else {


                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(

                                            new AuthUI.IdpConfig.PhoneBuilder().build()
                                    ))
                                    .build(),
                            RC_SIGN_IN);







                }


            }
        };


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

            User singleContact = new User("",userName, userPhoneNumber);

            mContacts.add(singleContact);
            //getUserDetails(singleContact);
            Log.d(TAG, "getContactList: "+ userName);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                // Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                //Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }


}












//    EditText mPhoneNumber, mCodeEntered;
//    Button  mVerificationButton, mSignInButton;
//    String mCodeSent;
//
//    FirebaseAuth mAuth;

//PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


// onCreate

//        mPhoneNumber = findViewById(R.id.editTextPhone);
//        mCodeEntered = findViewById(R.id.editTextCode);
//
//        mVerificationButton = findViewById(R.id.buttonGetVerificationCode);
//        mSignInButton = findViewById(R.id.buttonSignIn);
//
//        mAuth = FirebaseAuth.getInstance();
//
//        userIsLoggedIn();
//
//        mVerificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            sendVerificationCode();
//
//            }
//
//        });
//
//        mSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                verifySignInCode();
//            }
//        });





//Methods


//    private void userIsLoggedIn() {
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if(user!=null){
//            startActivity(new Intent(WelcomeActivity.this,ChatActivity.class));
//            finish();
//            return;
//        }
//
//
//
//    }
//
//    private void verifySignInCode(){
//        String codeEntered = mCodeEntered.getText().toString();
//        try {
//
//            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mCodeSent, codeEntered);
//            signInWithPhoneAuthCredential(credential);
//        } catch (Exception e) {
//            Log.i("exception",e.toString());
//            Toast.makeText(WelcomeActivity.this,"Invalid credentials",Toast.LENGTH_LONG).show();
//        }
//
//    }
//
////    private void updateProfile(){
////        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
////
////        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
////                .setDisplayName()
////                .build();
////
////        user.updateProfile(profileUpdates)
////                .addOnCompleteListener(new OnCompleteListener<Void>() {
////                    @Override
////                    public void onComplete(@NonNull Task<Void> task) {
////                        if (task.isSuccessful()) {
////                            Log.d(TAG, "User profile updated.");
////                        }
////                    }
////                });
////    }
//
//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//
//                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                            final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
//
//                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                                        Map<String,Object> userMap = new HashMap<>();
//
//
//
//                                        userMap.put("Name",user.getPhoneNumber());
//                                        userMap.put("PhoneNumber",user.getPhoneNumber());
//
//                                        mDatabaseReference.updateChildren(userMap);
//
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            userIsLoggedIn();
//
////                            FirebaseUser user = task.getResult().getUser();
////
////                            if(user!=null){
////                                startActivity(new Intent(WelcomeActivity.this,ChatActivity.class));
////                                finish();
////                                return;
////                            }
//                            // ...
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
//                        }
//                    }
//                });
//    }
//
//
//
//    private void sendVerificationCode() {
//
//       String phoneNumber = mPhoneNumber.getText().toString();
//        //String phoneNumber = "+966543511355";
//
//        if(phoneNumber.isEmpty()){
//            mPhoneNumber.setError("Phone number is required");
//            mPhoneNumber.requestFocus();
//            return;
//        }
//
//        if(phoneNumber.length()<10){
//            mPhoneNumber.setError("Please enter a valid phone");
//            mPhoneNumber.requestFocus();
//            return;
//        }
//
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
//    }
//
//    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
//
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//
//        }
//
//        @Override
//        public void onVerificationFailed(FirebaseException e) {
//
//        }
//
//        @Override
//        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//
//            mCodeSent = s;
//
//            Log.d(TAG, "onCodeSent: "+mCodeSent);
//        }
//    }