package cz.uhk.fim.runhk.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.database.DatabaseHelper;
import cz.uhk.fim.runhk.fragments.ChallengeLocationFragment;
import cz.uhk.fim.runhk.model.PolyLineData;
import cz.uhk.fim.runhk.service.AsyncResponse;
import cz.uhk.fim.runhk.service.ElevationService;
import cz.uhk.fim.runhk.service.RouteDataProvider;
import cz.uhk.fim.runhk.service.helper.utils.PolylineUtils;
import cz.uhk.fim.runhk.service.helper.utils.StringLabelUtils;

public class GeneratedMapActivity extends FragmentActivity implements OnMapReadyCallback, ChallengeLocationFragment.onLocationUpdateInterface, AsyncResponse {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private RouteDataProvider routeDataProvider;

    double lat = 0;
    double lon = 0;
    double prevLat;
    double prevLng;
    private double distance;
    private double avgDistance;
    private long avgTime;
    private double avgElevation;
    private int avgCalories;
    private int playerWeight;
    private List<Double> elevations;
    private List<LatLng> distancePoints;
    private PolyLineData currentPolylineData;

    private double elevationGain;
    private int polyLineIndex;
    private List<Polyline> polylines;

    private String address;
    private String address3;
    private String address4;
    private String address5;
    private LatLng myLocation;
    private PopupWindow popupWindowLoad;

    private ChallengeLocationFragment challengeLocationFragment;
    private ElevationService elevationService;
    DatabaseHelper databaseHelper;

    private PolyLineData currentPolyLineData1;
    private PolyLineData currentPolyLineData2;
    private PolyLineData currentPolyLineData3;

    private FusedLocationProviderClient fusedLocationProviderClient;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ChallengeLocationFragment challengeLocationFragment1 = (ChallengeLocationFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentQuest);
        challengeLocationFragment1.setOnLocationUpdateInterface(this);

        Intent intent = getIntent();
        avgDistance = intent.getDoubleExtra("distance", 0);
        avgTime = intent.getLongExtra("time", 0);
        avgElevation = intent.getDoubleExtra("elevation", 0);
        avgCalories = intent.getIntExtra("calories", 0);
        playerWeight = intent.getIntExtra("weight", 0);

        polylines = new ArrayList<>();

        elevations = new ArrayList<>();
        elevationService = new ElevationService();
        distancePoints = new ArrayList<>();
        elevationService.delegate = this;
        databaseHelper = new DatabaseHelper();
        routeDataProvider = new RouteDataProvider();


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
     * <p>
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //TODO - current location při spusteni aktivity
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker to your position and move the camera
        //     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12.5f));
        //     mMap.addMarker(new MarkerOptions().position(myLocation).title("You are here"));

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                currentPolylineData = (PolyLineData) polyline.getTag();
                polyLineIndex = currentPolylineData.getIndex();
                System.out.println("current index " + polyLineIndex);
                elevations.clear();
                onPolylineLoadClick();

                if (currentPolyLineData1 == null && currentPolylineData.getIndex() == 1) {
                    currentPolyLineData1 = currentPolylineData;
                    getElevationFromRoute(currentPolylineData.getPolyLinePoints(), 1);
                } else if (currentPolyLineData2 == null && currentPolylineData.getIndex() == 2) {
                    currentPolyLineData2 = currentPolylineData;
                    getElevationFromRoute(currentPolyLineData2.getPolyLinePoints(), 2);
                } else if (currentPolyLineData3 == null && currentPolylineData.getIndex() == 3) {
                    currentPolyLineData3 = currentPolylineData;
                    getElevationFromRoute(currentPolyLineData3.getPolyLinePoints(), 3);
                } else {
                    switch (polyLineIndex) {
                        case 1:
                            onButtonShowPopupWindowClick(currentPolyLineData1);
                            break;
                        case 2:
                            onButtonShowPopupWindowClick(currentPolyLineData2);
                            break;
                        case 3:
                            onButtonShowPopupWindowClick(currentPolyLineData3);
                            break;
                    }
                }
            }
        });



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

        String textDistanceDifference = StringLabelUtils.createDiffString((int) polyLineData.getDistance(), (int) avgDistance);
        String textElevationDifference = StringLabelUtils.createDiffString(polyLineData.getElevationGain(), (int) avgElevation);
        String textCaloriesDifference = StringLabelUtils.createDiffString(polyLineData.getCalories(), avgCalories);

        TextView popupText = popupView.findViewById(R.id.popupText);
        popupText.setText(polyLineData.getDistance() / 1000.0 + " km" + " (" + textDistanceDifference + ")" + "\n"
                + polyLineData.getElevationGain() + " elevation gain" + " (" + textElevationDifference + ")" + "\n"
                + (int) minutes + ":" + seconds + " minutes" + "\n"
                + polyLineData.getCalories() + " (" + textCaloriesDifference + ")" + " kcals");

        Button btnRunPopup = popupView.findViewById(R.id.btnRunPopup);
        btnRunPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int routeIndex = currentPolylineData.getIndex();
                int routeColor = PolylineUtils.getRouteColor(routeIndex);
                for (Polyline polyline : polylines) {
                    if (polyline.getColor() != routeColor) {
                        polyline.remove();
                    }
                }
                popupWindow.dismiss();
            }
        });

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

    @Override
    public void onLocationUpdate(Location currentLocation) {
        if (currentLocation != null) {
            prevLat = lat;
            prevLng = lon;

            lat = currentLocation.getLatitude();
            lon = currentLocation.getLongitude();
            myLocation = new LatLng(lat, lon);

            //mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

            if (prevLat != 0) {
                mMap.addPolyline(new PolylineOptions()
                        .clickable(false)
                        .jointType(JointType.ROUND)
                        .color(Color.BLACK)
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15f));

            }
        }
    }

    private void getAddressFromLocation() throws IOException {
        // ziskani adresy
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = geocoder.getFromLocation(myLocation.latitude, myLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        Random r = new Random();
        int randomHeading = r.nextInt(360);
        int randomHeading2 = r.nextInt(360);
        int randomHeading3 = r.nextInt(360);

        LatLng waypoint = SphericalUtil.computeOffset(myLocation, avgDistance / 2.5, randomHeading);
        LatLng waypoint2 = SphericalUtil.computeOffset(myLocation, avgDistance / 2, randomHeading2);
        LatLng waypoint3 = SphericalUtil.computeOffset(myLocation, avgDistance / 1.7, randomHeading3);

        List<Address> addresses3 = geocoder.getFromLocation(waypoint.latitude, waypoint.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        List<Address> addresses4 = geocoder.getFromLocation(waypoint2.latitude, waypoint2.longitude, 1);
        List<Address> addresses5 = geocoder.getFromLocation(waypoint3.latitude, waypoint3.longitude, 1);

        address3 = addresses3.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        address4 = addresses4.get(0).getAddressLine(0);
        address5 = addresses5.get(0).getAddressLine(0);
    }

    private void getRoute() {
        DirectionsResult directionsResult1 = routeDataProvider.createDirectionResult(address, address3, address);
        DirectionsResult directionsResult2 = routeDataProvider.createDirectionResult(address, address4, address);
        DirectionsResult directionsResult3 = routeDataProvider.createDirectionResult(address, address5, address);

        createRoute(directionsResult1, Color.BLUE, 1);
        createRoute(directionsResult2, Color.GREEN, 2);
        createRoute(directionsResult3, Color.YELLOW, 3);

    }


    private void addMarkers(DirectionsResult directionsResult, GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(directionsResult.routes[0].legs[0].startLocation.lat, directionsResult.routes[0].legs[0].startLocation.lng))
                .title(directionsResult.routes[0].legs[0].startAddress));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap, int color, int index) {
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
        polyLineData.setIndex(index);
        Polyline polyline = mMap.addPolyline(new PolylineOptions().color(color).clickable(true).addAll(decodedPath));
        polyline.setTag(polyLineData);
        polylines.add(polyline);

    }

    private void createRoute(DirectionsResult directionsResult, int color, int index) {
        if (directionsResult != null) {
            addMarkers(directionsResult, mMap);
            addPolyline(directionsResult, mMap, color, index);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(directionsResult.routes[0].legs[0].startLocation.lat,
                    directionsResult.routes[0].legs[0].startLocation.lng
            ), 12.5f));
        }
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit? Generated runs will be lost")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        GeneratedMapActivity.super.onBackPressed();
                    }
                }).create().show();
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
            switch (polyLineIndex) {
                case 1:
                    currentPolyLineData1.setElevationGain((int) elevationGain);
                    currentPolyLineData1.setCalories(routeDataProvider.getExpectedCaloriesBurn(playerWeight, currentPolyLineData1.getDistance(), currentPolyLineData1.getTime() * 60000, (int) elevationGain));
                    onButtonShowPopupWindowClick(currentPolyLineData1);
                    break;
                case 2:
                    currentPolyLineData2.setElevationGain((int) elevationGain);
                    currentPolyLineData2.setCalories(routeDataProvider.getExpectedCaloriesBurn(playerWeight, currentPolyLineData2.getDistance(), currentPolyLineData2.getTime() * 60000, (int) elevationGain));
                    onButtonShowPopupWindowClick(currentPolyLineData2);
                    break;
                case 3:
                    currentPolyLineData3.setElevationGain((int) elevationGain);
                    currentPolyLineData3.setCalories(routeDataProvider.getExpectedCaloriesBurn(playerWeight, currentPolyLineData3.getDistance(), currentPolyLineData3.getTime() * 60000, (int) elevationGain));
                    onButtonShowPopupWindowClick(currentPolyLineData3);
                    break;
                default:
                    System.out.println("nic");
            }

        }
    }

    @SuppressLint({"StaticFieldLeak", "MissingPermission"})
    private void getLastKnownLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(GeneratedMapActivity.this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            myLocation = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                            try {
                                getAddressFromLocation();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            getRoute();
                        } else {
                            System.out.println("ouh");
                        }
                    }
                });
    }
}
