package com.example.whatsappclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText mPhoneNumber, mCodeEntered;
    Button  mVerificationButton, mSignInButton;
    String mCodeSent;

    FirebaseAuth mAuth;

    //PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneNumber = findViewById(R.id.editTextPhone);
        mCodeEntered = findViewById(R.id.editTextCode);

        mVerificationButton = findViewById(R.id.buttonGetVerificationCode);
        mSignInButton = findViewById(R.id.buttonSignIn);

        mAuth = FirebaseAuth.getInstance();
        
        userIsLoggedIn();

        mVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            sendVerificationCode();

            }

        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySignInCode();
            }
        });

    }

    private void userIsLoggedIn() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            startActivity(new Intent(MainActivity.this,MainPageActivity.class));
            finish();
            return;
        }


        
    }

    private void verifySignInCode(){
        String codeEntered = mCodeEntered.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mCodeSent, codeEntered);
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            if(user!=null){
                                startActivity(new Intent(MainActivity.this,MainPageActivity.class));
                                finish();
                                return;
                            }
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }



    private void sendVerificationCode() {

       // String phoneNumber = mPhoneNumber.getText().toString();
        String phoneNumber = "+966543511355";

        if(phoneNumber.isEmpty()){
            mPhoneNumber.setError("Phone number is required");
            mPhoneNumber.requestFocus();
            return;
        }

        if(phoneNumber.length()<10){
            mPhoneNumber.setError("Please enter a valid phone");
            mPhoneNumber.requestFocus();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            mCodeSent = s;

            Log.d(TAG, "onCodeSent: "+mCodeSent);
        }
    };
}

