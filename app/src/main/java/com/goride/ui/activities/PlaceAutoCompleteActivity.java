package com.goride.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Places;
import com.goride.base.BaseActivity;
import com.goride.ui.adapters.PlaceAutoCompleteAdapter;
import com.goride.util.L;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.BindView;
import butterknife.OnClick;
import goride.com.goride.R;

/**
 * Created by root on 11/17/17.
 */

public class PlaceAutoCompleteActivity extends BaseActivity
        implements Filter.FilterListener, GoogleApiClient.ConnectionCallbacks{


    @BindView(R.id.edt_search_place_auto_complete)
    EditText searchEditText;
    @BindView(R.id.pw_place_auto_complete)
    ProgressWheel progressWheel;
    @BindView(R.id.rv_places_place_auto_complete)
    RecyclerView mRecyclerView;

    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_place_auto_complete);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();

        searchEditText.addTextChangedListener(textWatcher);

        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(placeAutoCompleteAdapter);

        placeAutoCompleteAdapter.setPlaceClickListener(placeClickListener);
    }

    @Override
    public void onFilterComplete(int i) {
        L.fine("Filter Completed ==> " + i);

        progressWheel.setVisibility(View.GONE);
    }
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            int len = charSequence.length();
            if (len == 0) {
                progressWheel.setVisibility(View.GONE);
                return;
            }
            if(len >= 3) {
                search(charSequence.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    void search(String key) {

        L.fine("toSearch ==> " + key);
        if (placeAutoCompleteAdapter != null) {
            ((Filterable) placeAutoCompleteAdapter).getFilter().filter(key, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        L.fine("Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        L.fine("Connection suspended ==> " +i);
    }
    @OnClick(R.id.iv_back_btn_place_auto_complete) public void onBackClick() {
        finish();
    }
    private PlaceAutoCompleteAdapter.IPlaceClickListener placeClickListener = new PlaceAutoCompleteAdapter.IPlaceClickListener() {
        @Override
        public void onClick(AutocompletePrediction autocompletePrediction) {

            if(autocompletePrediction != null) {
                Intent intent = getIntent();
                intent.putExtra("place_id", autocompletePrediction.getPlaceId());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }
}
