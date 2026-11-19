package letshangllc.onroute.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.directions.route.Segment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import letshangllc.onroute.BuildConfig;
import letshangllc.onroute.Direction;
import letshangllc.onroute.R;
import letshangllc.onroute.UserLocation;

public class Activity_Maps extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private static final String TAG = "MAPS_ACTIVITY";
    private static final LatLng FALLBACK_LAT_LNG = new LatLng(37.7749, -122.4194);

    private GoogleMap mMap;
    private UserLocation userLocation;

    private PlacesClient placesClient;

    private ArrayList<Polyline> polylines;
    private ArrayList<Direction> directions;
    private ArrayList<LatLng> waypoints;

    protected LatLng start;
    protected LatLng end;

    private Toolbar toolbar;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean fine = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                boolean coarse = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
                if (fine || coarse) {
                    enableMyLocationIfReady();
                } else {
                    Toast.makeText(this, R.string.toast_location_permission, Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Intent> createRouteLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    return;
                }
                Intent data = result.getData();
                String[] placeIdsResult = data.getStringArrayExtra(getString(R.string.PlacesListIntent));
                if (placeIdsResult == null) {
                    return;
                }

                ArrayList<String> placeIdsList = new ArrayList<>();
                for (String id : placeIdsResult) {
                    if (id != null && !id.isEmpty()) {
                        placeIdsList.add(id);
                    }
                }
                if (placeIdsList.isEmpty()) {
                    return;
                }

                fetchPlacesAndRoute(placeIdsList);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_maps);

        applyWindowInsets();
        setUpToolbar();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }
        placesClient = Places.createClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        polylines = new ArrayList<>();
        directions = new ArrayList<>();

        requestLocationPermissionIfNeeded();
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("On Route");
    }

    private void requestLocationPermissionIfNeeded() {
        boolean fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        if (!fine && !coarse) {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void fetchPlacesAndRoute(List<String> placeIds) {
        waypoints = new ArrayList<>();
        fetchPlaceAtIndex(placeIds, 0);
    }

    private void fetchPlaceAtIndex(List<String> placeIds, int index) {
        if (index >= placeIds.size()) {
            startRouting();
            return;
        }

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeIds.get(index), fields).build();
        placesClient.fetchPlace(request)
                .addOnSuccessListener(response -> {
                    Place place = response.getPlace();
                    if (place.getLatLng() != null) {
                        waypoints.add(place.getLatLng());
                    }
                    fetchPlaceAtIndex(placeIds, index + 1);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Place query did not complete", e);
                    Toast.makeText(this, R.string.toast_routing_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void startRouting() {
        if (waypoints == null || waypoints.size() < 2) {
            Toast.makeText(this, R.string.toast_routing_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e(TAG, "GOT TO CREATING ROUTE");
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(Activity_Maps.this)
                .waypoints(waypoints)
                .key(BuildConfig.MAPS_API_KEY)
                .build();
        routing.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_directions) {
            Intent intent = new Intent(Activity_Maps.this, Activity_DirectionsList.class);
            intent.putParcelableArrayListExtra(getString(R.string.DirectionsIntent), directions);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_route) {
            Intent intent2 = new Intent(Activity_Maps.this, Activity_CreateRoute.class);
            createRouteLauncher.launch(intent2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        userLocation = new UserLocation(this, googleMap);
        Location currentLocation = userLocation.getLocation();
        LatLng target = FALLBACK_LAT_LNG;
        if (currentLocation != null) {
            target = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 14f));
        enableMyLocationIfReady();
    }

    private void enableMyLocationIfReady() {
        if (mMap == null) {
            return;
        }
        boolean fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        if (fine || coarse) {
            mMap.setMyLocationEnabled(true);
            if (userLocation != null) {
                Location currentLocation = userLocation.getLocation();
                if (currentLocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            14f
                    ));
                }
            }
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.e(TAG, "Routing failure", e);
        Toast.makeText(this, R.string.toast_routing_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int shortestRouteIndex) {
        start = waypoints.get(0);
        end = waypoints.get(waypoints.size() - 1);

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);

        mMap.moveCamera(center);
        mMap.moveCamera(zoom);

        if (polylines != null) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        polylines = new ArrayList<>();
        directions = new ArrayList<>();

        Log.e(TAG, "Number of routes: " + routes.size());

        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(this, R.color.primary));
            polyOptions.width(13);
            polyOptions.addAll(route.getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            List<Segment> segments = route.getSegments();
            if (segments == null || segments.isEmpty()) {
                continue;
            }
            Segment previousSegment = segments.get(0);
            directions.add(new Direction(i, previousSegment.getInstruction(), previousSegment.getDistance()));
            for (int r = 1; r < segments.size(); r++) {
                Segment segment = segments.get(r);
                directions.add(new Direction(
                        i,
                        segment.getInstruction(),
                        segment.getDistance() - previousSegment.getDistance()
                ));
                previousSegment = segment;
            }
            directions.add(new Direction(i, getString(R.string.arrived_at_marked_location), 0));
        }

        for (LatLng waypoint : waypoints) {
            mMap.addMarker(new MarkerOptions().position(waypoint));
        }
    }

    @Override
    public void onRoutingCancelled() {
    }
}
