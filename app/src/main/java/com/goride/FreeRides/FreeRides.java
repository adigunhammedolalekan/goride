package com.goride.FreeRides;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.goride.InviteFriends.FragmentInviteFriends;
import com.goride.RewardedAd;

import goride.com.goride.R;

public class FreeRides extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_rides);
    }

    public void goToInviteFriends(View view) {

        Intent intent = new Intent(this, FragmentInviteFriends.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
    }

    public void goToRewardedAd(View view) {

        Intent intent = new Intent(this, RewardedAd.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
    }
}
