package com.goride.FreeRides;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.goride.InviteFriends.FragmentInviteFriends;
import com.goride.RewardedAd;
import com.goride.base.BaseActivity;

import butterknife.OnClick;
import goride.com.goride.R;

public class FreeRides extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_rides);
    }

    @OnClick(R.id.btn_go_to_invite_friends) public void goToInviteFriends() {

        Intent intent = new Intent(this, FragmentInviteFriends.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
    }

    @OnClick(R.id.btn_go_to_rewarded_ad) public void goToRewarded() {
        Intent intent = new Intent(this, RewardedAd.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
    }
}
