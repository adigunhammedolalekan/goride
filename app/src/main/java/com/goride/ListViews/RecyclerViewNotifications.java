package com.goride.ListViews;

import java.util.ArrayList;
import java.util.List;

import goride.com.goride.R;


/**
 * Created by User on 5/19/2017.
 */

public class RecyclerViewNotifications {

    private int imageItem;
    private String notificationTitle;
    private String notificationDetails;
    private String notificationTime;


    public int getImageItem() {
        return imageItem;
    }

    public void setImageItem(int imageItem) {
        this.imageItem = imageItem;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDetails() {

        return notificationDetails;
    }

    public void setNotificationDetails(String notificationDetails) {

        this.notificationDetails = notificationDetails;
    }

    public String getNotificationTime() {

        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {

        this.notificationTime = notificationTime;
    }


    public static List<RecyclerViewNotifications> getObjectList() {

        List<RecyclerViewNotifications> dataList = new ArrayList<>();

        int[] images = getImages();


        for (int i = 0; i < getImages().length; i++) {
            RecyclerViewNotifications recyclerViewNotifications = new RecyclerViewNotifications();

            //natureModel.setImageID(images[i]);
            recyclerViewNotifications.setImageItem(images[i]);
            recyclerViewNotifications.setNotificationTitle("PROMOTION");
            recyclerViewNotifications.setNotificationDetails("\"Get 50% discount from...");
            recyclerViewNotifications.setNotificationTime("30 Sep");
            dataList.add(recyclerViewNotifications);

        }


        return dataList;
    }


    public static int[] getImages() {

        int[] images = {

                R.drawable.goride_logo,
                R.drawable.goride_logo,
                R.drawable.goride_logo,



        };

        return images;
    }
}
