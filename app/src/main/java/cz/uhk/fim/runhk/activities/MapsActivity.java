package cz.uhk.fim.runhk.activities;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.fragments.QuestFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, QuestFragment.onLocationUpdateInterface {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    double lat = 0;
    double lon = 0;

    double prevLat;
    double prevLng;

    LatLng myLocation;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        QuestFragment questFragment1 = (QuestFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentQuest);
        questFragment1.setOnLocationUpdateInterface(this);

        if (findViewById(R.id.fragmentQuest) != null) {
            QuestFragment questFragment = new QuestFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentQuest, questFragment) // kam to chci a co
                    .commit();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     *
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLocation();
        // Add a marker to your position and move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12.5f));
        mMap.addMarker(new MarkerOptions().position(myLocation).title("You are here"));

    }

    private void updateLocation() {
        myLocation = new LatLng(lat, lon);
    }

    @Override
    public void onLocationUpdate(Location currentLocation) {
        if (currentLocation == null) {
            return;
        } else {
            prevLat = lat;
            prevLng = lon;

            lat = currentLocation.getLatitude();
            lon = currentLocation.getLongitude();
            myLocation = new LatLng(lat, lon);
            ///  mMap.addMarker(new MarkerOptions().position(myLocation).title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

            if (prevLat != 0) {
                mMap.addPolyline(new PolylineOptions().clickable(false).add(
                        new LatLng(prevLat, prevLng),
                        new LatLng(lat, lon)
                ));
            }

        }

    }
}
