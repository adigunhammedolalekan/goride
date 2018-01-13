package com.goride.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import goride.com.goride.R;

/**
 * Created by root on 11/12/17.
 */

public class Util {

    public static String textOf(EditText editText) {
        return editText.getText().toString().trim();
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception e) {
            //if there is NPE
        }
    }
    public static Bitmap getMarker(Context context, String location, String type) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_marker, null);

        if (location.trim().length() > 14) {
            location = location.substring(0, 14) + "...";
        }

        TextView textView = view.findViewById(R.id.tv_location_marker_layout);
        textView.setText(location);
        TextView typeTextView = view.findViewById(R.id.tv_time_marker_layout);
        typeTextView.setText(type);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));

        return bitmap;
    }
}
