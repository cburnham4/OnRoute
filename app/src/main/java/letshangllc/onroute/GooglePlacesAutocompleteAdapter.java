package letshangllc.onroute;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Autocomplete adapter backed by the Places SDK for Android.
 * Based on the prior Geo Data API adapter used with Google-Directions-Android samples.
 */
public class GooglePlacesAutocompleteAdapter
        extends ArrayAdapter<GooglePlacesAutocompleteAdapter.PlaceAutocomplete>
        implements Filterable {

    private static final String TAG = "PlaceAutocomplete";

    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();
    private final PlacesClient placesClient;
    private final AutocompleteSessionToken sessionToken;

    public GooglePlacesAutocompleteAdapter(Context context, int resource, PlacesClient placesClient) {
        super(context, resource);
        this.placesClient = placesClient;
        this.sessionToken = AutocompleteSessionToken.newInstance();
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Nullable
    @Override
    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    ArrayList<PlaceAutocomplete> list = getAutocomplete(constraint);
                    if (list != null) {
                        results.values = list;
                        results.count = list.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    //noinspection unchecked
                    mResultList = (ArrayList<PlaceAutocomplete>) results.values;
                    notifyDataSetChanged();
                } else {
                    mResultList = new ArrayList<>();
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    @Nullable
    private ArrayList<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {
        Log.i(TAG, "Starting autocomplete query for: " + constraint);

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setQuery(constraint.toString())
                .build();

        try {
            FindAutocompletePredictionsResponse response = Tasks.await(
                    placesClient.findAutocompletePredictions(request),
                    60,
                    TimeUnit.SECONDS
            );

            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
            Log.i(TAG, "Query completed. Received " + predictions.size() + " predictions.");

            ArrayList<PlaceAutocomplete> resultList = new ArrayList<>(predictions.size());
            for (AutocompletePrediction prediction : predictions) {
                resultList.add(new PlaceAutocomplete(
                        prediction.getPlaceId(),
                        prediction.getFullText(null).toString()
                ));
            }
            return resultList;
        } catch (Exception e) {
            Log.e(TAG, "Error getting autocomplete prediction API call", e);
            Toast.makeText(getContext(), "Error contacting Google Places: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /** Holder for Places Autocomplete API results. */
    public static class PlaceAutocomplete {
        public final CharSequence placeId;
        public final CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @NonNull
        @Override
        public String toString() {
            return description.toString();
        }
    }
}
