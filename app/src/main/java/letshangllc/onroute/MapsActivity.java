package letshangllc.onroute;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{ //, GoogleApiClient.OnConnectionFailedListener

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* todo Add connection callbacks later */
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();







    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UserLocation userLocation = new UserLocation(this);
        Location currentLocation = userLocation.getLocation();


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 10f));

        mMap.setMyLocationEnabled(true);
    }
    public void onSearch(View view){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try{
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }catch (Exception e){

        }
    }

//    public void onSearch(View view){
//        Log.i("CLICKED ", "YIPPEE");
//        EditText et_search = (EditText) findViewById(R.id.et_search);
//        String search = et_search.getText().toString();
//
//        if(search != null && !search.isEmpty()){
//            Geocoder geocoder = new Geocoder(this);
//            try {
//                List<Address> addresses = geocoder.getFromLocationName(search, 1);
//                Address address = addresses.get(0);
//                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//
//                mMap.addMarker(new MarkerOptions().position(latLng).title(search));
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
