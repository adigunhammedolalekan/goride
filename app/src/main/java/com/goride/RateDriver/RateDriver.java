package com.goride.RateDriver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goride.HomePage.HomePage;

import goride.com.goride.R;

public class RateDriver extends Activity {

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_driver);


    }

    public void askRateDriverCar(View view) {


        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_rate_driver_car);
        dialog.show();

        Button button = (Button) dialog.findViewById(R.id.noBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RateDriver.this, HomePage.class);
                startActivity(intent);
                dialog.cancel();
                Toast toast = Toast.makeText(RateDriver.this, "Thanks for your feedback", Toast.LENGTH_SHORT);
                toast.show();
                toast.setGravity(Gravity.CENTER, 0, 0);


            }
        });
    }
}
