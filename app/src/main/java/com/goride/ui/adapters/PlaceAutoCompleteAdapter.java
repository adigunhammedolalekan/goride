package com.goride.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.goride.util.L;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import goride.com.goride.R;

/**
 * Created by root on 11/17/17.
 */

public class PlaceAutoCompleteAdapter extends
        RecyclerView.Adapter<PlaceAutoCompleteAdapter.PlaceAutoCompleteViewHolder> implements Filterable {

    public PlaceAutoCompleteAdapter() {}

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<AutocompletePrediction> autocompletePredictions = new ArrayList<>();
    private IPlaceClickListener placeClickListener;

    public PlaceAutoCompleteAdapter(Context context, GoogleApiClient client) {
        mContext = context;
        mGoogleApiClient = client;
        if(mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setPlaceClickListener(IPlaceClickListener placeClickListener) {
        this.placeClickListener = placeClickListener;
    }

    @Override
    public PlaceAutoCompleteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null)
            mLayoutInflater = LayoutInflater.from(parent.getContext());

        return new PlaceAutoCompleteViewHolder(mLayoutInflater.inflate(R.layout.layout_place, parent, false));
    }

    @Override
    public void onBindViewHolder(PlaceAutoCompleteViewHolder holder, int position) {

        final AutocompletePrediction autocompletePrediction = autocompletePredictions.get(position);
        holder.namePrimary.setText(autocompletePrediction.getPrimaryText(null));
        holder.nameSecondary.setText(autocompletePrediction.getSecondaryText(null));

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(placeClickListener != null)
                    placeClickListener.onClick(autocompletePrediction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return autocompletePredictions.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults filterResults = new FilterResults();
                List<AutocompletePrediction> autocompletePredictions = new ArrayList<>();

                if(charSequence != null) {
                    autocompletePredictions = getAutocompletePredictions(charSequence.toString());
                }
                filterResults.values = autocompletePredictions;
                filterResults.count = autocompletePredictions == null ?
                        0 : autocompletePredictions.size();

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                if(filterResults != null && filterResults.count > 0) {
                    autocompletePredictions = (List<AutocompletePrediction>) filterResults.values;
                    notifyDataSetChanged();
                }else {

                }
            }
        };
    }

    class PlaceAutoCompleteViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_place_name_primary)
        TextView namePrimary;
        @BindView(R.id.tv_place_name_secondary)
        TextView nameSecondary;
        @BindView(R.id.layout_place_root)
        RelativeLayout root;

        public PlaceAutoCompleteViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
    private List<AutocompletePrediction> getAutocompletePredictions(String input) {

        if(!mGoogleApiClient.isConnected())
            return new ArrayList<>();

        PendingResult<AutocompletePredictionBuffer>
                predictionPendingResult = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, input, null, null);

        AutocompletePredictionBuffer autocompletePredictions = predictionPendingResult.await(60, TimeUnit.SECONDS);
        Status status = autocompletePredictions.getStatus();
        if(!status.isSuccess()) {
            L.fine("Error fetching data from api " + status.toString());
            autocompletePredictions.release();
            return new ArrayList<>();
        }
        return DataBufferUtils.freezeAndClose(autocompletePredictions);
    }
    public interface IPlaceClickListener {
        void onClick(AutocompletePrediction autocompletePrediction);
    }
}
