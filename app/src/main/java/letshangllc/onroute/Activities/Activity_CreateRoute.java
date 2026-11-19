package letshangllc.onroute.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import letshangllc.onroute.BuildConfig;
import letshangllc.onroute.GooglePlacesAutocompleteAdapter;
import letshangllc.onroute.R;
import letshangllc.onroute.UserLocation;

/**
 * Collects start, optional waypoints, and destination Place IDs for routing.
 */
public class Activity_CreateRoute extends AppCompatActivity {
    private static final String TAG = "CREATE_ROUTE";

    private GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;
    private PlacesClient placesClient;
    private Toolbar toolbar;

    private String[] placeIds;

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
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_create_route);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }
        placesClient = Places.createClient(this);

        googlePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(
                this,
                android.R.layout.simple_list_item_1,
                placesClient
        );

        placeIds = new String[7];

        setUpToolbar();
        findViews();
        setAdapters();
        setListeners();

        btn_createRoute.setOnClickListener(v -> {
            String startId = placeIds[0];
            String destinationId = placeIds[6];

            if (startId == null || destinationId == null || startId.isEmpty() || destinationId.isEmpty()) {
                Toast.makeText(
                        Activity_CreateRoute.this,
                        R.string.toast_need_start_destination,
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                setActivityResult();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_routes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Create Route");
    }

    private void setActivityResult() {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.PlacesListIntent), placeIds);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void findViews() {
        btn_createRoute = findViewById(R.id.btn_createRoute);

        waypoint1CardView = findViewById(R.id.cardview_waypoint1);
        waypoint2CardView = findViewById(R.id.cardview_waypoint2);
        waypoint3CardView = findViewById(R.id.cardview_waypoint3);
        waypoint4CardView = findViewById(R.id.cardview_waypoint4);
        waypoint5CardView = findViewById(R.id.cardview_waypoint5);

        startingAutoComplete = findViewById(R.id.auto_start);
        waypoint1AutoComplete = (AutoCompleteTextView) waypoint1CardView.getChildAt(0);
        waypoint2AutoComplete = (AutoCompleteTextView) waypoint2CardView.getChildAt(0);
        waypoint3AutoComplete = (AutoCompleteTextView) waypoint3CardView.getChildAt(0);
        waypoint4AutoComplete = (AutoCompleteTextView) waypoint4CardView.getChildAt(0);
        waypoint5AutoComplete = (AutoCompleteTextView) waypoint5CardView.getChildAt(0);
        destinationAutoComplete = findViewById(R.id.auto_destination);

        UserLocation userLocation = new UserLocation(getApplicationContext());
        try {
            Address address = userLocation.getUserPlace();
            if (address != null) {
                startingAutoComplete.setText(
                        address.getAddressLine(0) + ", "
                                + address.getLocality() + ", "
                                + address.getAdminArea()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        placesClient.findCurrentPlace(request)
                .addOnSuccessListener(response -> {
                    List<PlaceLikelihood> likelihoods = response.getPlaceLikelihoods();
                    if (likelihoods != null && !likelihoods.isEmpty()) {
                        placeIds[0] = likelihoods.get(0).getPlace().getId();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Find current place failed", e));
    }

    private void setAdapters() {
        startingAutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        waypoint1AutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        waypoint2AutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        waypoint3AutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        waypoint4AutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        waypoint5AutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
        destinationAutoComplete.setAdapter(googlePlacesAutocompleteAdapter);
    }

    private void setListeners() {
        startingAutoComplete.setOnItemClickListener(new OnPlaceClickListener(0));
        waypoint1AutoComplete.setOnItemClickListener(new OnPlaceClickListener(1));
        waypoint2AutoComplete.setOnItemClickListener(new OnPlaceClickListener(2));
        waypoint3AutoComplete.setOnItemClickListener(new OnPlaceClickListener(3));
        waypoint4AutoComplete.setOnItemClickListener(new OnPlaceClickListener(4));
        waypoint5AutoComplete.setOnItemClickListener(new OnPlaceClickListener(5));
        destinationAutoComplete.setOnItemClickListener(new OnPlaceClickListener(6));
    }

    public class OnPlaceClickListener implements AdapterView.OnItemClickListener {
        private final int routeNum;

        public OnPlaceClickListener(int routeNum) {
            this.routeNum = routeNum;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GooglePlacesAutocompleteAdapter.PlaceAutocomplete item =
                    googlePlacesAutocompleteAdapter.getItem(position);
            if (item == null) {
                return;
            }
            String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);
            Log.i(TAG, "Autocomplete item ID: " + placeId);
            placeIds[routeNum] = placeId;
        }
    }
}
