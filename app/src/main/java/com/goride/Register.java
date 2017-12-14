package com.goride;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.goride.HomePage.HomePage;
import com.goride.base.BaseActivity;
import com.goride.entities.User;
import com.goride.util.L;
import com.goride.util.Util;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import goride.com.goride.R;

public class Register extends BaseActivity {

    @BindView(R.id.edt_full_name_register)
    EditText fullNameEditText;
    @BindView(R.id.edt_phone_number_register)
    EditText phoneNumberEditText;
    @BindView(R.id.edt_password_register)
    EditText passwordEditText;
    @BindView(R.id.edt_confirm_password_register)
    EditText confirmPasswordEditText;
    @BindView(R.id.edt_code_register)
    EditText codeEditText;

    @BindView(R.id.otp_layout_register)
    CardView otpLayout;
    @BindView(R.id.layout_main_register)
    CardView mainLayout;

    private String mVerificationID = "";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mChangedCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseUser newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        mChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                codeEditText.setText(phoneAuthCredential.getSmsCode());
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                showDialog("Network Error", "Failed to send code. Please retry");

                L.WTF(e);

                FirebaseAuth.getInstance().signInAnonymously()
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Intent intent = new Intent(Register.this, HomePage.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });
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
    @OnClick(R.id.btn_sign_up_register) public void onSignUpClick() {

        String phone = Util.textOf(phoneNumberEditText);
        if(phone.length() < 9 || phone.length() > 16) {
            showDialog("Error", "Invalid phone number.");
            return;
        }
        if(Util.textOf(fullNameEditText).isEmpty()) {
            showDialog("Error", "Enter your full name to continue");
            return;
        }
        String password = Util.textOf(passwordEditText);
        String cPassword = Util.textOf(confirmPasswordEditText);
        if(password.isEmpty() && cPassword.isEmpty()) {
            showDialog("Error", "Set a password.");
            return;
        }
        if(!password.equalsIgnoreCase(cPassword)) {
            showDialog("Error", "Password doesn't match!");
            return;
        }

        progressDialog.show();
        FirebaseDatabase.getInstance()
        .getReference("users")
        .child(phone).addListenerForSingleValueEvent(valueEventListener);
    }
    void signIn(PhoneAuthCredential authCredential) {
        progressDialog.show();
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.cancel();
                        if(task.isSuccessful()) {
                            newUser = task.getResult().getUser();
                            createUser();
                        }else {
                            showDialog("Error", "Invalid verification code. Please retry");

                            L.WTF(task.getException());
                        }
                    }
                });
    }
    private void codeSent() {

        progressDialog.cancel();

        mainLayout.setVisibility(View.GONE);
        otpLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_resend_code) public void onResendCodeClick() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(Util.textOf(phoneNumberEditText), 60, TimeUnit.SECONDS,
                this, mChangedCallbacks, resendingToken);
    }

    @OnClick(R.id.btn_submit_otp_register) public void onSubmitOtp() {

        Util.hideKeyboard(this);
        String otp = Util.textOf(codeEditText);
        if(otp.isEmpty()) {
            snack("Enter code!");
            return;
        }
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationID, otp);
        signIn(phoneAuthCredential);
    }

    private void createUser() {

        User user = new User(Util.textOf(fullNameEditText), Util.textOf(phoneNumberEditText), Util.textOf(passwordEditText));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users");
        databaseReference.child(Util.textOf(phoneNumberEditText))
                .setValue(user);

        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        progressDialog.cancel();

    }
    /*
    * ValueEventListener used to check for phone number existence
    * */
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            User user = dataSnapshot.getValue(User.class);
            if(user != null) {
                /*
                * Phone number already exists.
                * */
                progressDialog.cancel();
                showDialog("Error", "Phone number " + Util.textOf(phoneNumberEditText) + " already in use.");
            }else {
                PhoneAuthProvider.getInstance()
                        .verifyPhoneNumber(Util.textOf(phoneNumberEditText), 60, TimeUnit.SECONDS, Register.this, mChangedCallbacks);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
