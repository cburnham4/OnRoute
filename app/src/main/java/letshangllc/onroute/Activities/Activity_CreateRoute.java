package letshangllc.onroute.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import letshangllc.onroute.GooglePlacesAutocompleteAdapter;
import letshangllc.onroute.R;

/**
 * Created by Carl on 12/22/2015.
 */
public class Activity_CreateRoute extends AppCompatActivity{
    private static final String TAG = "CREATE_ROUTE";
    private GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;

    private GoogleApiClient googleApiClient;

    private Toolbar toolbar;

    /* todo try putting places in static class */
    private Place[] places;

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

    private int numWaypoints;

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
        places = new Place[7];

        this.setUpToolbar();
        this.findLayouts();
        this.setAdapters();


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
        switch (id){
            case R.id.action_add:
                Toast.makeText(this, "HERE: " + waypoint1AutoComplete.getVisibility() , Toast.LENGTH_SHORT).show();
                if(waypoint1CardView.getVisibility() == View.GONE){
                    waypoint1CardView.setVisibility(View.VISIBLE);
                }else if(waypoint2CardView.getVisibility() == View.GONE){
                    waypoint2CardView.setVisibility(View.VISIBLE);

                }else if(waypoint3CardView.getVisibility() == View.GONE){
                    waypoint3CardView.setVisibility(View.VISIBLE);

                }else if(waypoint4CardView.getVisibility() == View.GONE){
                    waypoint4CardView.setVisibility(View.VISIBLE);

                }else if(waypoint5CardView.getVisibility() == View.GONE){
                    waypoint5CardView.setVisibility(View.VISIBLE);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        this.setTitle("Create Route"); /* Set the title for toolbar */
    }

    private void findLayouts(){
        waypoint1CardView = (CardView) findViewById(R.id.cardview_waypoint1);
        waypoint2CardView = (CardView) findViewById(R.id.cardview_waypoint2);
        waypoint3CardView = (CardView) findViewById(R.id.cardview_waypoint3);
        waypoint4CardView = (CardView) findViewById(R.id.cardview_waypoint4);
        waypoint5CardView = (CardView) findViewById(R.id.cardview_waypoint5);

        startingAutoComplete = (AutoCompleteTextView) findViewById(R.id.auto_start);
        waypoint1AutoComplete = (AutoCompleteTextView) waypoint1CardView.getChildAt(0);
        waypoint2AutoComplete = (AutoCompleteTextView) waypoint2CardView.getChildAt(0);
        waypoint3AutoComplete = (AutoCompleteTextView) waypoint3CardView.getChildAt(0);
        waypoint4AutoComplete = (AutoCompleteTextView) waypoint4CardView.getChildAt(0);
        waypoint5AutoComplete = (AutoCompleteTextView) waypoint5CardView.getChildAt(0);
        destinationAutoComplete = (AutoCompleteTextView) findViewById(R.id.auto_destination);
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

    public class onPlaceClickListener implements AdapterView.OnItemClickListener{
        private int routeNum;

        /* Pass in the route number to assign each listener to specific view */
        public onPlaceClickListener(int routeNum){
            this.routeNum = routeNum;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final GooglePlacesAutocompleteAdapter.PlaceAutocomplete item = googlePlacesAutocompleteAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);
            Log.i("View ", view.toString());

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(PlaceBuffer placesBuffer) {
                    if (!placesBuffer.getStatus().isSuccess()) {
                        // Request did not complete successfully
                        Log.e(TAG, "Place query did not complete. Error: " + placesBuffer.getStatus().toString());
                        placesBuffer.release();
                        return;
                    }
                    // Get the Place object from the buffer.
                    final Place place = placesBuffer.get(0);
                    places[routeNum] = place;
                }
            });
        };

    }
}
