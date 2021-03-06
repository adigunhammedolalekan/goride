package com.goride.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.goride.FirstPage;
import com.goride.HomePage.HomePage;
import com.goride.base.BaseActivity;
import com.goride.util.L;
;import goride.com.goride.R;

/**
 * Created by Lekan Adigun on 12/13/2017.
 */

public class Splash extends BaseActivity {

    private Handler handler = new Handler();

    private void launch(final Class<?> c) {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Splash.this, c);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, 1000);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_splash);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null) {
            L.fine("Current User ==> " + user.getUid() + " " + user.getDisplayName());
            launch(HomePage.class);
        }else {
            launch(FirstPage.class);
        }
    }
}
