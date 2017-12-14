package com.goride.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.goride.FirstPage;
import com.goride.HomePage.HomePage;
import com.goride.base.BaseActivity;
import com.goride.entities.User;
import com.goride.util.L;
import com.goride.util.Util;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import goride.com.goride.R;

/**
 * Created by root on 11/15/17.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edt_phone_number_login)
    EditText phoneNumberEditText;
    @BindView(R.id.edt_password_login)
    EditText passwordEditText;
    @BindView(R.id.code_edt_login)
    EditText codeEditText;

    @BindView(R.id.login_otp_layout)
    LinearLayout otpLayout;

    private User user;
    private ProgressDialog progressDialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mChangedCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseAuth firebaseAuth;
    private String mVerificationID = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait a seconds...");

        firebaseAuth = FirebaseAuth.getInstance();
        mChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                codeEditText.setText(phoneAuthCredential.getSmsCode());
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                progressDialog.cancel();
                L.fine("Verification Failed ==> " + e);
                if(e instanceof FirebaseAuthInvalidCredentialsException) {
                    String message = e.getMessage();
                    if(message == null || message.trim().isEmpty()) {
                        message = "Invalid login credentials. Please check and retry";
                    }
                    showDialog("Error", message);
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationID = s;
                resendingToken = forceResendingToken;
                codeSent();
            }
        };
    }
    @OnClick(R.id.btn_login) public void onLoginClick() {

        String phone = Util.textOf(phoneNumberEditText);
        String password = Util.textOf(passwordEditText);
        if(phone.length() > 16 || phone.length() < 9) {
            showDialog("Error", "Invalid phone number.");
            return;
        }
        if(password.isEmpty()) {
            showDialog("Error", "Enter your password");
            return;
        }
        signIn();
    }
    void signIn() {
        progressDialog.show();
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(Util.textOf(phoneNumberEditText))
                .addListenerForSingleValueEvent(valueEventListener);
    }
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot != null) {
                user = dataSnapshot.getValue(User.class);
                if(user != null) {

                    if(user.getPassword().equalsIgnoreCase(Util.textOf(passwordEditText))) {
                        sendOtp();
                    }else {
                        progressDialog.cancel();
                        showDialog("Error", "Invalid login credentials");
                    }
                }else {
                    progressDialog.cancel();
                    showDialog("Error", "Phone number " + Util.textOf(phoneNumberEditText) + " is not yet registered on our platform.");
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            snack("Error response from network. Please retry");
            progressDialog.cancel();
        }
    };
    private void sendOtp() {

        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(Util.textOf(phoneNumberEditText), 60,
                        TimeUnit.SECONDS, this, mChangedCallbacks);
    }
    private void codeSent() {

        progressDialog.cancel();
        findViewById(R.id.login_linear_layout)
                .setVisibility(View.GONE);

        otpLayout.setVisibility(View.VISIBLE);
    }
    private void signIn(AuthCredential credential) {

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.cancel();
                        if(task.isSuccessful()) {
                            Intent i = new Intent(LoginActivity.this, HomePage.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }else {
                            showDialog("Error", "Invalid verification code.");
                        }
                    }
                });
    }
    @OnClick(R.id.btn_submit_code_login) public void onSubmitOTP() {

        String code = Util.textOf(codeEditText);
        if(code.isEmpty()) {
            showDialog("Error", "Enter verification code.");
            return;
        }

        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationID, code);
        signIn(phoneAuthCredential);
    }
    @OnClick(R.id.btn_resend_code_login) public void onResendCodeClick() {

        progressDialog.show();
        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(Util.textOf(phoneNumberEditText), 60, TimeUnit.SECONDS, this, mChangedCallbacks, resendingToken);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
