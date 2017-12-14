package com.goride;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.goride.BookingAccepted.BookingAccepted;
import com.goride.FreeRides.FreeRides;
import com.goride.HomePage.HomePage;
import com.goride.InviteFriends.FragmentInviteFriends;
import com.goride.RateDriver.RateDriver;
import com.goride.base.BaseActivity;
import com.goride.entities.User;
import com.goride.login.LoginActivity;
import com.goride.util.L;
import com.goride.util.Util;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import goride.com.goride.R;


public class FirstPage extends BaseActivity {


    public void toLogin(View view) {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void toSignUp(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        LinearLayout loginLinearLayout = (LinearLayout) findViewById(R.id.login_linear_layout);
        loginLinearLayout.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {

       super.onBackPressed();
    }
}

