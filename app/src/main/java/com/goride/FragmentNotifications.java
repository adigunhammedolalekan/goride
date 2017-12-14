package com.goride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.goride.ListViews.MyAdapterNotifications;
import com.goride.ListViews.RecyclerViewNotifications;

import goride.com.goride.R;

public class FragmentNotifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_notifications);

        RecyclerView notificationRecyclerView = (RecyclerView) findViewById(R.id.notifications_recycler_view);
        notificationRecyclerView.setFocusable(false);

        MyAdapterNotifications myAdapter = new MyAdapterNotifications(this, RecyclerViewNotifications.getObjectList());
        notificationRecyclerView.setAdapter(myAdapter);


        LinearLayoutManager notificationLinearLayoutManager = new LinearLayoutManager(this);
        notificationLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notificationRecyclerView.setLayoutManager(notificationLinearLayoutManager);



          /*
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanCount(2);
        showCaseRecyclerView.setLayoutManager(gridLayoutManager);

        */

        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }
}
