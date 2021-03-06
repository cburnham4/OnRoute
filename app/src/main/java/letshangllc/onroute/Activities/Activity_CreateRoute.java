package letshangllc.onroute.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.io.IOException;

import letshangllc.onroute.GooglePlacesAutocompleteAdapter;
import letshangllc.onroute.R;
import letshangllc.onroute.UserLocation;

/**
 * Created by Carl on 12/22/2015.
 */
public class Activity_CreateRoute extends AppCompatActivity {
    private static final String TAG = "CREATE_ROUTE";
    private GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;

    /*todo add place picker as option */

    /* Todo save current locations */
    private GoogleApiClient googleApiClient;

    private Toolbar toolbar;

    /* todo try putting places in static class */
    private String[] placeIds;

    /*Todo Dynamically create the new views */
    private CardView waypoint1CardView;
    private CardView waypoint2CardView;
    private CardView waypoint3CardView;
    private CardView waypoint4CardView;
    private CardView waypoint5CardView;

    private AutoCompleteTextView startingAutoComplete;
    private AutoCompleteTextView waypoint1AutoComplete;
    private AutoCompleteTextView waypoint2AutoComplete;
    private AutoCompleteTextView waypoint3AutoComplete;
    private AutoCompleteTextView waypoint4AutoComplete;
    private AutoCompleteTextView waypoint5AutoComplete;
    private AutoCompleteTextView destinationAutoComplete;

    private Button btn_createRoute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        /* todo Add connection callbacks */
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();

        googlePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(this,
                android.R.layout.simple_list_item_1, googleApiClient, null);

        /*Create an array for the 7 options of places to drive to */
        placeIds = new String[7];

        this.setUpToolbar();
        this.findViews();
        this.setAdapters();
        this.setListeners();

        /* Create Route only if start and destination locations have been selected */
        btn_createRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startId = placeIds[0];
                String destinationId = placeIds[6];

                if (startId == null || destinationId == null || startId.isEmpty() || destinationId.isEmpty()) {
                    Toast.makeText(Activity_CreateRoute.this, "Please choose a start point and destination", Toast.LENGTH_SHORT).show();
                } else {
                    setActivityResult();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_routes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                /* Add in waypoint if it is not already added */
                if (waypoint1CardView.getVisibility() == View.GONE) {
                    waypoint1CardView.setVisibility(View.VISIBLE);
                } else if (waypoint2CardView.getVisibility() == View.GONE) {
                    waypoint2CardView.setVisibility(View.VISIBLE);

                } else if (waypoint3CardView.getVisibility() == View.GONE) {
                    waypoint3CardView.setVisibility(View.VISIBLE);

                } else if (waypoint4CardView.getVisibility() == View.GONE) {
                    waypoint4CardView.setVisibility(View.VISIBLE);

                } else if (waypoint5CardView.getVisibility() == View.GONE) {
                    waypoint5CardView.setVisibility(View.VISIBLE);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        this.setTitle("Create Route"); /* Set the title for toolbar */
    }

    private void setActivityResult() {
        /* Create a intent to return as result */
        Intent intent = new Intent();
        intent.putExtra(getResources().getString(R.string.PlacesListIntent), placeIds);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void findViews() {
        btn_createRoute = (Button) findViewById(R.id.btn_createRoute);

        waypoint1CardView = (CardView) findViewById(R.id.cardview_waypoint1);
        waypoint2CardView = (CardView) findViewById(R.id.cardview_waypoint2);
        waypoint3CardView = (CardView) findViewById(R.id.cardview_waypoint3);
        waypoint4CardView = (CardView) findViewById(R.id.cardview_waypoint4);
        waypoint5CardView = (CardView) findViewById(R.id.cardview_waypoint5);

        /* Find waypoints as childs of cardview so the cardview visibility can be set */
        startingAutoComplete = (AutoCompleteTextView) findViewById(R.id.auto_start);
        waypoint1AutoComplete = (AutoCompleteTextView) waypoint1CardView.getChildAt(0);
        waypoint2AutoComplete = (AutoCompleteTextView) waypoint2CardView.getChildAt(0);
        waypoint3AutoComplete = (AutoCompleteTextView) waypoint3CardView.getChildAt(0);
        waypoint4AutoComplete = (AutoCompleteTextView) waypoint4CardView.getChildAt(0);
        waypoint5AutoComplete = (AutoCompleteTextView) waypoint5CardView.getChildAt(0);
        destinationAutoComplete = (AutoCompleteTextView) findViewById(R.id.auto_destination);

        /* Set the starting position to the User's current location */
        UserLocation userLocation = new UserLocation(this.getApplicationContext());
        try {
            Address address = userLocation.getUserPlace();
            startingAutoComplete.setText(address.getAddressLine(0)+", "+address.getLocality()+", "+ address.getAdminArea());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* If the permission is not granted then complete the method */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        /* todo get placeid from long lat */
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(googleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                /* set the first likely place to the starting point (ie. most likely place) */
                placeIds[0] = likelyPlaces.get(0).getPlace().getId();
                likelyPlaces.release();
            }
        });

    }

    private void setAdapters(){
        startingAutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        waypoint1AutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        waypoint2AutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        waypoint3AutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        waypoint4AutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        waypoint5AutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        destinationAutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
    }

    private void setListeners(){
        startingAutoComplete.setOnItemClickListener(new onPlaceClickListener(0));
        waypoint1AutoComplete.setOnItemClickListener(new onPlaceClickListener(1));
        waypoint2AutoComplete.setOnItemClickListener(new onPlaceClickListener(2));
        waypoint3AutoComplete.setOnItemClickListener(new onPlaceClickListener(3));
        waypoint4AutoComplete.setOnItemClickListener(new onPlaceClickListener(4));
        waypoint5AutoComplete.setOnItemClickListener(new onPlaceClickListener(5));
        destinationAutoComplete.setOnItemClickListener(new onPlaceClickListener(6));
    }

    /* todo collapse auto picker when place is selected */

    public class onPlaceClickListener implements AdapterView.OnItemClickListener{
        private int routeNum;

        /* Pass in the route number to assign each listener to specific view */
        public onPlaceClickListener(int routeNum){
            this.routeNum = routeNum;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /* Get the place selected and add it to placeIds array */
            final GooglePlacesAutocompleteAdapter.PlaceAutocomplete item = googlePlacesAutocompleteAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);
            Log.i(TAG, "Autocomplete item ID: " + placeId);

            placeIds[routeNum] = placeId;

            Places.GeoDataApi.getPlaceById(googleApiClient, placeIds);

        };

    }
}
