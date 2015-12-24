package letshangllc.onroute.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import letshangllc.onroute.GooglePlacesAutocompleteAdapter;
import letshangllc.onroute.R;

/**
 * Created by Carl on 12/22/2015.
 */
public class Activity_CreateRoute extends AppCompatActivity{
    private GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;

    private GoogleApiClient googleApiClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        /* todo Add connection callbacks later */
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();

        googlePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(this,
                R.layout.item_placepicker, googleApiClient, null);


    }
}
