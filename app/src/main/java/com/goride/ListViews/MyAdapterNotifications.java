package com.goride.ListViews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import goride.com.goride.R;


/**
 * Created by User on 5/19/2017.
 */

public class MyAdapterNotifications extends RecyclerView.Adapter<MyAdapterNotifications.MyViewHolder>{

    private List<RecyclerViewNotifications> objectList;
    private LayoutInflater inflater;

    public MyAdapterNotifications(Context context, List<RecyclerViewNotifications> objectList){

        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_notifications, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        RecyclerViewNotifications current = objectList.get(position);
        holder.setData(current, position);

    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private int position;
        private ImageView imageItem;
        private TextView notificationTitle;
        private TextView notificationDetails;
        private TextView notificationTime;


        private RecyclerViewNotifications currentObject;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageItem = (ImageView) itemView.findViewById(R.id.notificationIcon);
            notificationTitle = (TextView) itemView.findViewById(R.id.notificationTitle);
            notificationDetails = (TextView) itemView.findViewById(R.id.notificationDetails);
            notificationTime = (TextView) itemView.findViewById(R.id.notificationTime);
        }

        public void setData(RecyclerViewNotifications currentObject, int position) {

            this.imageItem.setImageResource(currentObject.getImageItem());
            this.notificationTitle.setText(currentObject.getNotificationTitle());
            this.notificationDetails.setText(currentObject.getNotificationDetails());
            this.notificationTime.setText(currentObject.getNotificationTime());
            this.position = position;
            this.currentObject = currentObject;


        }
    }
}
