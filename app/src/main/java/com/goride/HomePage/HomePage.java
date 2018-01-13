package com.goride.HomePage;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.goride.FragmentNotifications;
import com.goride.FreeRides.FreeRides;
import com.goride.GoRideApplication;
import com.goride.InviteFriends.FragmentInviteFriends;
import com.goride.MainFragment;
import com.goride.NearByDrivers.SearchingNearbyDrivers;
import com.goride.base.BaseActivity;
import com.goride.core.LocationTask;
import com.goride.ui.activities.PlaceAutoCompleteActivity;
import com.goride.util.L;

import butterknife.BindView;
import butterknife.OnClick;
import goride.com.goride.R;

public class HomePage extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>,
        LocationTask.IOnLocationReady {

    GoogleMap mMap;
    SupportMapFragment sMapFragment;
    android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();

    /*
    * Setup
    * */
    private Location mCurrentLocation, mDestinationLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationManager mLocationManager;
    private Marker mCurrentLocationMarker;
    private String addressString, destinationAddress = "";
    private LatLng mDestinationLatLng;

    public static final int LOCATION_UPDATE_INTERVAL = 10000;
    public static final int FASTEST_LOCATION_UPDATE_INTERVAL = LOCATION_UPDATE_INTERVAL / 2;
    public static final int PERMISSION_CODE = 101;
    public static final int REQUEST_CHECK_SETTINGS = 102;
    public static final int REQUEST_SET_PICK_UP = 2;
    public static final int REQUEST_SET_DESTINATION = 4;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.tv_current_location_home_page)
    TextView currentLocationTextView;
    @BindView(R.id.tv_destination_name_homepage)
    TextView destinationTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sMapFragment = SupportMapFragment.newInstance();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (!sMapFragment.isAdded())
            sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
        else
            sFm.beginTransaction().show(sMapFragment).commit();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();

        sMapFragment.getMapAsync(this);

        initLocationManager();
        requestPermission();
    }
    void requestPermission() {

        if(PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            buildGoogleApiClient();
            buildLocationRequest();
            buildLocationSettingsRequest();
            if(!isLocationEnabled())
                requestLocationSettings();
            return;
        }

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            buildLocationRequest();
            buildLocationSettingsRequest();
            if(!isLocationEnabled())
                requestLocationSettings();
        }else {
            //
        }
    }

    private void initLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.iv_drawer_toggle)
    public void onDrawerToggleClick() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (sMapFragment.isAdded())
            sFm.beginTransaction().hide(sMapFragment).commit();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_free_rides) {

            Intent intent = new Intent(this, FreeRides.class);
            startActivity(intent);

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_invite_Friends) {

            Intent intent = new Intent(this, FragmentInviteFriends.class);
            startActivity(intent);

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_drive_with_GoRide) {

        }else if (id == R.id.nav_notification) {
            Intent intent = new Intent(this, FragmentNotifications.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    public void promoCode(View v) {

        // Intent intent = new Intent(this, PlayVideo.class);
        //  startActivity(intent);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_promo_code);
        dialog.show();

    }

    public void notesToDriver(View v) {
        // Intent intent = new Intent(this, PlayVideo.class);
        //  startActivity(intent);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_notes_to_driver);
        dialog.show();

    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        initLocation(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }catch (SecurityException e) {
            //SecurityExecption::
            L.WTF(e);
        }
        initLocation(true);
    }

    private void initLocation(boolean shouldFetchAddress) {
        L.fine("Init Location!");
        if(mCurrentLocation != null) {

            L.fine("Current Location " + mCurrentLocation.toString());
            if(mCurrentLocationMarker != null)
                mCurrentLocationMarker.remove();

            mCurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(
                    new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).title(addressString).snippet(addressString));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 15f));

            if(shouldFetchAddress)
                GoRideApplication.getApplication()
                        .getExecutorService().execute(new LocationTask(this, this, mCurrentLocation));
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        L.fine("Connection Failed " + connectionResult.getErrorMessage());
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

        Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(HomePage.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    //
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:

                        break;
                }
                break;
            case REQUEST_SET_PICK_UP:
                switch (resultCode) {
                    case RESULT_OK:
                        Place place = PlaceAutocomplete.getPlace(this, data);
                        addressString = place.getAddress().toString();
                        mCurrentLocation = new Location("");
                        mCurrentLocation.setLatitude(place.getLatLng().latitude);
                        mCurrentLocation.setLongitude(place.getLatLng().longitude);
                        initLocation(false);
                        currentLocationTextView.setText(addressString);
                        break;
                }
                break;
            case REQUEST_SET_DESTINATION:
                switch (resultCode) {
                    case RESULT_OK:
                        Place destination = PlaceAutocomplete.getPlace(this, data);
                        destinationAddress = destination.getAddress().toString();
                        initDestination(destination);
                        break;
                    case RESULT_CANCELED:
                        break;
                }
        }
    }

    private void initDestination(Place destination) {

        mDestinationLatLng = destination.getLatLng();
        mDestinationLocation = new Location("");
        mDestinationLocation.setLongitude(mDestinationLatLng.longitude);
        mDestinationLocation.setLatitude(mDestinationLatLng.latitude);

        destinationAddress = destination.getAddress().toString();
        destinationTextView.setText(destinationAddress);

    }
    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    L.fine(status.toString());
                }
            });
        }catch (SecurityException e) {}

    }
    private synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        mGoogleApiClient.connect();
    }

    private synchronized void buildLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private synchronized void buildLocationSettingsRequest() {

        mLocationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .build();
    }
    private synchronized void requestLocationSettings() {

        PendingResult<LocationSettingsResult> pendingResult =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest);

        pendingResult.setResultCallback(this);
    }
    boolean isLocationEnabled() {
        if(mLocationManager == null)
            return false;

        return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                || mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onReady(Address address) {

        String text = address.getAddressLine(0);
        currentLocationTextView.setText(text);

        if(mCurrentLocationMarker != null) {
            mCurrentLocationMarker.setTitle(text);
            mCurrentLocationMarker.setSnippet(text);
        }
    }

    @Override
    public void onError(Throwable throwable) {

        L.WTF(throwable);
    }
    @OnClick(R.id.card_set_pick_up_location) public void onSetPickupLocationClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_SET_PICK_UP);
        }catch (Exception e) {
            L.WTF(e);
        }
    }
    @OnClick(R.id.btn_book_ride_now) public void onBookRideClick() {

        if (mCurrentLocation == null) {
            snack("Please wait...");
            return;
        }

        if(mDestinationLatLng == null) {
            showDialog("Error", "You have to set destination first");
            return;
        }

        Intent intent = new Intent(this, SearchingNearbyDrivers.class);
        Bundle extras = new Bundle();
        Location location = new Location("");
        location.setLatitude(mDestinationLatLng.latitude);
        location.setLongitude(mDestinationLatLng.longitude);
        extras.putParcelable(SearchingNearbyDrivers.EXTRA_LOCATION, mCurrentLocation);
        extras.putParcelable(SearchingNearbyDrivers.EXTRA_DESTINATION_LOCATION, location);
        extras.putString("key_pickup_address", addressString);
        extras.putString("key_destination_address", destinationAddress);
        intent.putExtras(extras);
        startActivity(intent);
    }
    @OnClick(R.id.card_enter_destination_location) public void onSetDestinationClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_SET_DESTINATION);
        }catch (Exception e) {
            L.WTF(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
