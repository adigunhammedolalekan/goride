package com.goride.NearByDrivers;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.goride.BookingAccepted.BookingAccepted;
import com.goride.base.BaseActivity;
import com.goride.entities.Ride;
import com.goride.util.L;

import org.json.JSONException;

import butterknife.BindView;
import goride.com.goride.R;

public class SearchingNearbyDrivers extends BaseActivity {

    public static int RADIUS = 1;
    public static final String EXTRA_LOCATION = "current_location";
    public static final String EXTRA_DESTINATION_LOCATION = "destination_location";

    private GeoQuery geoQuery;
    private String destinationAddress = "", pickupAddress = "";
    private Location destinationLocation;
    private Location pickupLocation;

    @BindView(R.id.loading_layout_nearby_drivers_activity)
    LinearLayout loadingLayout;

    private String mDriverID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_nearby_drivers);

        Intent intent = getIntent();
        if(intent == null) {
            finish();
            return;
        }
        loadingLayout.setVisibility(View.VISIBLE);

        Bundle extras = intent.getExtras();

        pickupLocation = extras.getParcelable(EXTRA_LOCATION);
        destinationLocation = extras.getParcelable(EXTRA_DESTINATION_LOCATION);
        pickupAddress = extras.getString("key_pickup_address");
        destinationAddress = extras.getString("key_destination_address");

        if (pickupLocation != null)
            L.fine("Pick up ==> " + pickupLocation);
        findClosestDriver();
    }
    void findClosestDriver() {

        L.fine("Finding driver---");
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance().getReference().child("online_drivers");
        GeoFire geoFire = new GeoFire(databaseReference);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.getLatitude(), pickupLocation.getLongitude()), RADIUS);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation geoLocation) {

                L.fine("Key ==> " + s + ", Location ==> " + geoLocation.toString());
                if(mDriverID.isEmpty()) {
                    mDriverID = s;
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference rideRequestReference = FirebaseDatabase.getInstance().getReference("ride_requests");
                    if (user != null) {
                        Ride ride = new Ride(mDriverID, user.getUid(), pickupLocation.getLatitude(), pickupLocation.getLongitude(),
                                destinationLocation.getLatitude(), destinationLocation.getLongitude(), pickupAddress, destinationAddress, "newRide");
                        rideRequestReference.child(user.getUid()).setValue(ride);
                        notifyDriver(ride);
                        startWaitingForDriversResponse();
                    }
                }
            }

            @Override
            public void onKeyExited(String s) {

            }

            @Override
            public void onKeyMoved(String s, GeoLocation geoLocation) {

            }

            @Override
            public void onGeoQueryReady() {

                if(mDriverID.isEmpty()) {
                    RADIUS++;
                    findClosestDriver();

                    L.fine("Radius ==> " + RADIUS);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError databaseError) {

                L.WTF(databaseError.toException());
            }
        });
    }
    public void testFoundDriver(View v) {

    }

    public void testDriverNotFound(View v) {

    }
    private void notifyDriver(Ride ride) {
        FirebaseDatabase.getInstance().getReference("drivers")
                .child(mDriverID)
                .child("rideRequest")
                .setValue(ride);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void startWaitingForDriversResponse() {

        if(mDriverID != null && !mDriverID.isEmpty()) {

            FirebaseDatabase.getInstance().getReference("drivers")
                    .child(mDriverID)
                    .child("rideRequest")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot != null) {
                                L.fine("New Payload ==> " + dataSnapshot.toString());
                                Ride ride = dataSnapshot.getValue(Ride.class);
                                if(ride != null) {
                                    processRideStatus(ride);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            L.fine("Database Error " + databaseError);
                        }
                    });

        }

    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    private void processRideStatus(Ride ride) {

        String status = ride.getStatus().trim();
        L.fine("New ride status ==> " + status);
        switch (status) {
            case "accepted":
                bookingAccepted(ride);
                break;
            case "rejected":
                break;
            default:
                L.fine("Unknown state " + status);
        }

    }
    private void bookingAccepted(Ride ride) {

        Intent intent = new Intent(this, BookingAccepted.class);
        intent.putExtra("driver_id", mDriverID);
        try {
            intent.putExtra("ride", ride.toJson());
        }catch (JSONException ignored) {/*should never happen. */}

        startActivity(intent);
    }
}
