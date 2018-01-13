package com.goride;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.goride.ListViews.MyAdapterNotifications;
import com.goride.ListViews.RecyclerViewNotifications;
import com.goride.base.BaseActivity;

import butterknife.BindView;
import goride.com.goride.R;

public class FragmentNotifications extends BaseActivity {

    @BindView(R.id.notifications_recycler_view)
    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_notifications);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Notifications");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView.setFocusable(false);


        LinearLayoutManager notificationLinearLayoutManager = new LinearLayoutManager(this);
        notificationLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(notificationLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        MyAdapterNotifications myAdapter = new MyAdapterNotifications(this, RecyclerViewNotifications.getObjectList());
        mRecyclerView.setAdapter(myAdapter);

    }
}
