package letshangllc.onroute.Activities;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.directions.route.Segment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.common.api.ResultCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import letshangllc.onroute.Direction;
import letshangllc.onroute.R;
import letshangllc.onroute.UserLocation;

public class Activity_Maps extends AppCompatActivity implements OnMapReadyCallback, RoutingListener{ //, GoogleApiClient.OnConnectionFailedListener

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private int CREATE_ROUTE_REQUEST = 1;
    private ArrayList<Place> places;
    private UserLocation userLocation;

    private GoogleApiClient googleApiClient;

    private ArrayList<Polyline> polylines;
    private ArrayList<Direction> directions;

    private ArrayList<LatLng> waypoints;

    protected LatLng start;
    protected LatLng end;

    private String TAG = "MAPS_ACTIVITY";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        this.setUpToolbar();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used via callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        polylines = new ArrayList<>();
        places = new ArrayList<>();
        directions = new ArrayList<>();

                /* todo Add connection callbacks */
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();

    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        this.setTitle("On Route"); /* Set the title for toolbar */
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ROUTE_REQUEST) {
            if (resultCode == RESULT_OK) {

                String[] placeIdsResult = data.getStringArrayExtra(getResources().getString(R.string.PlacesListIntent));
                /* todo redo placesIds */
                ArrayList<String> placeIdsList = new ArrayList();
                for(int i =0; i<7; i++){
                    if (placeIdsResult[i]!= null && !placeIdsResult[i].isEmpty()){
                        placeIdsList.add(placeIdsResult[i]);
                    }
                }
                String[] placeIds = placeIdsList.toArray(new String[placeIdsList.size()]);
                PendingResult<PlaceBuffer> placeResult1 = Places.GeoDataApi.getPlaceById(googleApiClient, placeIds);

                placeResult1.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer placesBuffer) {
                        if (!placesBuffer.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(TAG, "Place query did not complete. Error: " + placesBuffer.getStatus().toString());
                            placesBuffer.release();
                            return;
                        }
                        waypoints = new ArrayList<LatLng>();
                        // Get the Place object from the buffer.
                        //final Place place = placesBuffer.get(0);
                        Iterator iterator = placesBuffer.iterator();
                        while (iterator.hasNext()) {
                            Place place = (Place) iterator.next();
                            waypoints.add(place.getLatLng());

                        }

                        placesBuffer.release();
                        Log.e(TAG, "GOT TO CREATING ROUTE");
                        Routing routing = new Routing.Builder()
                                .travelMode(Routing.TravelMode.DRIVING)
                                .withListener(Activity_Maps.this)
                                .waypoints(waypoints)
                                .build();

                        routing.execute();
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_directions:
                Intent intent = new Intent(Activity_Maps.this, Activity_DirectionsList.class);
                intent.putParcelableArrayListExtra(getResources().getString(R.string.DirectionsIntent), directions);
                startActivity(intent);
                break;

            case R.id.action_route:
                /* Start the place picker */
                Intent intent2 = new Intent(Activity_Maps.this, Activity_CreateRoute.class);
                startActivityForResult(intent2, CREATE_ROUTE_REQUEST);
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        userLocation = new UserLocation(this);
        Location currentLocation = userLocation.getLocation();


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 14f));

        mMap.setMyLocationEnabled(true);
    }



    @Override
    public void onRoutingFailure() {
        //progressDialog.dismiss();
        Toast.makeText(this,"Something went wrong, Try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int shortestRouteIndex) {
        //progressDialog.dismiss();
        start = waypoints.get(0);
        end = waypoints.get(waypoints.size()-1);

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

        mMap.moveCamera(center);
        mMap.moveCamera(zoom);

        polylines = new ArrayList<>();
        for (int i = 0; i <routes.get(0).getSegments().size(); i++) {

            Log.i("DIRECTION: ", routes.get(0).getSegments().get(i).getInstruction());
        }
        Log.e("routes: ", "ROutes: " + routes.size());
        //add route(s) to the map.
        for (int i = 0; i <routes.size(); i++) {

            //todo In case of more than 5 alternative routes
            //int colorIndex = i % colors.length;

            Route route = routes.get(i);

            PolylineOptions polyOptions = new PolylineOptions();
            //polyOptions.color(getResources().getColor(colors[colorIndex]));
            polyOptions.color(getResources().getColor(R.color.black));
            polyOptions.width(10 + i * 3);



            polyOptions.addAll(route.getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            /* loop throught all the segments in the route to get the directions */
            List<Segment> segments = route.getSegments();
            Segment segment;
            Segment previousSegment = segments.get(0);
            directions.add(new Direction(i, previousSegment.getInstruction(), previousSegment.getDistance()));
            for (int r = 1; r < segments.size(); r++){
                segment = segments.get(r);
                directions.add(new Direction(i, segment.getInstruction(), segment.getDistance()-previousSegment.getDistance()));
                previousSegment = segment;
            }
            directions.add(new Direction(i, "You arrived at your marked location. \n", 0));

            //Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

        for(int i = 0; i<waypoints.size(); i++){

            MarkerOptions options = new MarkerOptions();
            options.position(waypoints.get(i));
            /* todo add color to marker */

            mMap.addMarker(options);
        }

    }

    @Override
    public void onRoutingCancelled() {

    }
}
