package cz.uhk.fim.runhk.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.fragments.ChallengeLocationFragment;
import cz.uhk.fim.runhk.model.PolyLineData;
import cz.uhk.fim.runhk.service.AsyncResponse;
import cz.uhk.fim.runhk.service.ElevationService;
import cz.uhk.fim.runhk.service.RouteDataProvider;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ChallengeLocationFragment.onLocationUpdateInterface, AsyncResponse {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    double lat = 0;
    double lon = 0;
    double prevLat;
    double prevLng;

    private double avgDistance;
    private long avgTime;
    private double avgElevation;
    private int avgCalories;
    private int playerWeight;


    private List<Double> elevations = new ArrayList<>();
    private List<LatLng> distancePoints;
    private PolyLineData currentPolylineData;

    private double elevationGain;

    private RouteDataProvider routeDataProvider;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ElevationService elevationService;

    LatLng myLocation;
    private double distance = 0;
    private PopupWindow popupWindowLoad;


    ChallengeLocationFragment challengeLocationFragment;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ChallengeLocationFragment challengeLocationFragment1 = (ChallengeLocationFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentQuest);
        challengeLocationFragment1.setOnLocationUpdateInterface(this);

        routeDataProvider = new RouteDataProvider();
        elevationService = new ElevationService();
        elevationService.delegate = this;

        Intent intent = getIntent();
        avgDistance = intent.getDoubleExtra("distance", 0);
        avgTime = intent.getLongExtra("time", 0);
        avgElevation = intent.getDoubleExtra("elevation", 0);
        avgCalories = intent.getIntExtra("calories", 0);
        playerWeight = intent.getIntExtra("weight", 0);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();

        if (findViewById(R.id.fragmentQuest) != null) {
            challengeLocationFragment = new ChallengeLocationFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentQuest, challengeLocationFragment) // kam to chci a co
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


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
                try {
                    createRoute(latLng);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                currentPolylineData = (PolyLineData) polyline.getTag();
                elevations.clear();
                onPolylineLoadClick();
                getElevationFromRoute(currentPolylineData.getPolyLinePoints(), 1);
            }
        });

    }

    private void getElevationFromRoute(List<LatLng> distancePoints, int index) {
        distancePoints = getSortedDistancePoints(distancePoints);
        this.distancePoints = distancePoints;
        for (LatLng point : distancePoints) {
            //spusti async task
            elevationService.getElevation(point.latitude, point.longitude);
        }
    }

    private List<LatLng> getSortedDistancePoints(List<LatLng> distancePoints) {
        List<LatLng> sortedDistancePoints = new ArrayList<>();
        for (int i = 0; i < distancePoints.size() - 1; i += 2) {
            sortedDistancePoints.add(distancePoints.get(i));
        }
        return sortedDistancePoints;
    }

    private void onButtonShowPopupWindowClick(PolyLineData polyLineData) {
        popupWindowLoad.dismiss();

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);


        // create the popup window
        int width = 800;
        int height = 600;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        double minutes = polyLineData.getTime();
        double decimals = minutes % 1;
        int seconds = (int) (decimals * 60);

        String textDistanceDifference;
        int distanceDifference = (int) ((polyLineData.getDistance() - avgDistance));
        if (distanceDifference >= 0) {
            textDistanceDifference = " +" + distanceDifference;
        } else {
            textDistanceDifference = String.valueOf(distanceDifference);
        }

        String textElevationDifference;
        int elevationDifference = (int) ((polyLineData.getElevationGain() - avgElevation));
        if (elevationDifference >= 0) {
            textElevationDifference = " +" + elevationDifference;
        } else {
            textElevationDifference = String.valueOf(elevationDifference);
        }

        String textCaloriesDifference;
        int caloriesDifference = ((polyLineData.getCalories() - avgCalories));
        if (caloriesDifference >= 0) {
            textCaloriesDifference = " +" + caloriesDifference;
        } else {
            textCaloriesDifference = String.valueOf(caloriesDifference);
        }

        TextView popupText = popupView.findViewById(R.id.popupText);
        popupText.setText(polyLineData.getDistance() / 1000.0 + " km" + " (" + textDistanceDifference + ")" + "\n"
                + polyLineData.getElevationGain() + " elevation gain" + " (" + textElevationDifference + ")" + "\n"
                + (int) minutes + ":" + seconds + " minutes" + "\n"
                + polyLineData.getCalories() + " (" + textCaloriesDifference + ")" + " kcals");

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(mapFragment.getView(), Gravity.CENTER, 0, -200);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private void createRoute(LatLng latLng) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(myLocation.latitude, myLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        List<Address> addresses1 = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String address1 = addresses1.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        DirectionsResult directionsResult = routeDataProvider.createDirectionResult(address, address, address1);

        addLine(directionsResult);
    }

    private void addLine(DirectionsResult results) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        PolyLineData polyLineData = new PolyLineData();
        //TODO dynamicky na n pocet legs
        long expecteDistance = results.routes[0].legs[0].distance.inMeters + results.routes[0].legs[1].distance.inMeters;
        double expectedDuration = routeDataProvider.getExpectedDuration(avgTime, expecteDistance, avgDistance);
        polyLineData.setDistance(expecteDistance);
        polyLineData.setTime(expectedDuration);
        polyLineData.setCalories(0);
        polyLineData.setElevationGain(0);
        polyLineData.setPolyLinePoints(decodedPath);
        Polyline polyline = mMap.addPolyline(new PolylineOptions().color(Color.BLUE).clickable(true).addAll(decodedPath));
        polyline.setTag(polyLineData);
    }

    private void onPolylineLoadClick() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_load, null);
        int width = 800;
        int height = 600;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindowLoad = popupWindow;
        popupWindow.showAtLocation(mapFragment.getView(), Gravity.CENTER, 0, -150);
    }

    private void updateLocation() {
        myLocation = new LatLng(lat, lon);
    }

    @Override
    public void processFinish(Double output) {
        elevations.add(output);
        // count elevationGain
        if (elevations.size() == distancePoints.size()) {
            System.out.println("pocitam elevation Gain");
            for (int i = 0; i < elevations.size() - 1; i++) {
                if (elevations.get(i + 1) > elevations.get(i)) {
                    elevationGain = elevationGain + (elevations.get(i + 1) - elevations.get(i));
                }
            }
            currentPolylineData.setElevationGain((int) elevationGain);
            currentPolylineData.setCalories(routeDataProvider.getExpectedCaloriesBurn(playerWeight, currentPolylineData.getDistance(), currentPolylineData.getTime() * 60000, (int) elevationGain));
            onButtonShowPopupWindowClick(currentPolylineData);

        }
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

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

            if (prevLat != 0) {
                mMap.addPolyline(new PolylineOptions()
                        .clickable(false)
                        .jointType(JointType.ROUND)
                        .color(Color.BLUE)
                        .startCap(new RoundCap())
                        .endCap(new RoundCap())
                        .add(
                        new LatLng(prevLat, prevLng),
                        new LatLng(lat, lon)
                ));


                float[] distance2 = new float[2];
                Location.distanceBetween(prevLat, prevLng, lat, lon, distance2);
                double currentDistance = distance2[0];

                distance = distance + currentDistance;
                challengeLocationFragment.updateDistance(distance);
            }
        }
    }

    @SuppressLint({"StaticFieldLeak", "MissingPermission"})
    private void getLastKnownLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(MapsActivity.this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            myLocation = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14.5f));
                            mMap.addMarker(new MarkerOptions().position(myLocation).title("You are here"));

                        } else {
                            System.out.println("ouh");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit? You will have to start your run again")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MapsActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}
