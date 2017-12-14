package com.goride.BookingAccepted;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.goride.base.BaseActivity;
import com.goride.entities.Driver;
import com.goride.entities.Ride;
import com.goride.util.L;

import org.json.JSONException;


import butterknife.BindView;
import butterknife.OnClick;
import goride.com.goride.R;

public class BookingAccepted extends BaseActivity implements OnMapReadyCallback {


    GoogleMap mMap;
    SupportMapFragment sMapFragment;
    FragmentManager sFm = getSupportFragmentManager();
    Ride currentRide;
    String driverID = "";
    Driver currentDriver;

    @BindView(R.id.tv_car_name_booking_accepted)
    TextView carNameTextView;
    @BindView(R.id.tv_pickup_address_booking_accepted)
    TextView pickupAddressTextView;
    @BindView(R.id.tv_destination_address_booking_accepted)
    TextView destinationAddressTextView;
    @BindView(R.id.tv_driver_name_booking_accepted)
    TextView driverNameTextView;
    @BindView(R.id.booking_accepted_linearlayout)
    LinearLayout bookingAcceptedLayout;
    @BindView(R.id.tv_plate_number_booking_accepted)
    TextView plateNumberTextView;
    @BindView(R.id.toolbar_booking_accepted)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sMapFragment = SupportMapFragment.newInstance();
        setContentView(R.layout.activity_booking_accepted);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            //What?
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (!sMapFragment.isAdded())
            sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
        else
            sFm.beginTransaction().show(sMapFragment).commit();

        Intent intent = getIntent();
        if(intent == null) {
            finish();return;
        }
        String rawRide = intent.getStringExtra("ride");
        driverID = intent.getStringExtra("driver_id");
        FirebaseDatabase.getInstance().getReference("drivers")
                .child(driverID).addListenerForSingleValueEvent(valueEventListener);

        try {
            currentRide = Ride.from(rawRide);
        }catch (JSONException e) {}

        render();

    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot != null) {
                currentDriver = dataSnapshot.getValue(Driver.class);
                setDriver();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            L.fine("Error from firebase " + databaseError);
        }
    };


    private void render() {
        bookingAcceptedLayout.setVisibility(View.VISIBLE);

        pickupAddressTextView.setText(currentRide.getPickupAddress());
        destinationAddressTextView.setText(currentRide.getDestinationAddress());

        if(currentDriver != null) {
            setDriver();
        }
    }

    private void setDriver() {
        driverNameTextView.setText(currentDriver.getName());
        carNameTextView.setText(currentDriver.getCarModel());
        plateNumberTextView.setText(currentDriver.getLicensePlateNumber());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(currentRide != null) {

            LatLng pickup = new LatLng(currentRide.getPickupLat(), currentRide.getPickupLon());
            LatLng destinationLatLng = new LatLng(currentRide.getDestinationLat(), currentRide.getDestinationLon());

            mMap.addMarker(new MarkerOptions().position(pickup).title(currentRide.getPickupAddress()));
            mMap.addMarker(new MarkerOptions().position(destinationLatLng).title(currentRide.getDestinationAddress()));

            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(pickup).include(destinationLatLng).build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 17));
        }
    }

    @OnClick(R.id.btn_send_message_booking_accepted) public void onSendMessageClick() {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra("sms_body", "Hi, " + currentDriver.getName());
        startActivity(intent);
    }
    @OnClick(R.id.btn_call_booking_accepted) public void onCallClick() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + currentDriver.getPhoneNumber()));

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

   public void onTrip(View view){

       LinearLayout bookingAcceptedLinearlayout = (LinearLayout) findViewById(R.id.booking_accepted_linearlayout);
       bookingAcceptedLinearlayout.setVisibility(View.INVISIBLE);

       LinearLayout onTripLinearlayout = (LinearLayout) findViewById(R.id.on_trip_linearlayout);
       onTripLinearlayout.setVisibility(View.VISIBLE);
   }
}
