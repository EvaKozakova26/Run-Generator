package cz.uhk.fim.runhk.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.activities.DetailSectionActivity;
import cz.uhk.fim.runhk.database.DatabaseHelper;
import cz.uhk.fim.runhk.model.LocationModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChallengeLocationFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ChallengeLocationFragment.class.getSimpleName();

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";

    private onLocationUpdateInterface onLocationUpdateInterface;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest mLocationRequestHighAccuracy;

    private LocationCallback mLocationCallback;

    private Location mCurrentLocation;

    private Boolean mRequestingLocationUpdates;

    private SettingsClient client;

    private LocationSettingsRequest mLocationSettingsRequest;

    Chronometer chronometer;
    TextView textViewDistance;

    double lat;
    double lon;

    private DatabaseHelper databaseHelper;
    private List<Double> listLaTLon;
    private LocationModel locationModel;
    private List<LocationModel> distancePointsList;

    double distance;

    public ChallengeLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        view.findViewById(R.id.btnPlay).setOnClickListener(this);
        view.findViewById(R.id.btnStop).setOnClickListener(this);
        view.findViewById(R.id.btnSave).setOnClickListener(this);
        chronometer = view.findViewById(R.id.chronometer);
        textViewDistance = view.findViewById(R.id.textViewDistance);
        databaseHelper = new DatabaseHelper();
        listLaTLon = new ArrayList<>();
        mRequestingLocationUpdates = false;
        distancePointsList = new ArrayList<>();

        getLastKnownLocation();

        mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(4000);

        // Update values using data stored in the Bundle. - //TODO to check if needed
        updateValuesFromBundle(savedInstanceState);

        client = LocationServices.getSettingsClient(getContext());

        createLocationCallback();
        buildLocationSettingsRequest();
        Log.i(TAG, "Vola se onCreateView");

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                mRequestingLocationUpdates = true;
                if (mRequestingLocationUpdates && checkPermissions()) {
                    System.out.println(mRequestingLocationUpdates + "clickedGo");
                    startLocationUpdates();
                    Toast.makeText(getContext(), "Start location updates", Toast.LENGTH_SHORT).show();
                } else if (!checkPermissions()) {
                    requestPermissions();
                }
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();

                updateLocation();
                break;
            case R.id.btnStop:
                Toast.makeText(getContext(), "Stop location updates", Toast.LENGTH_SHORT).show();
                chronometer.stop();
                stopLocationUpdates();
                break;
            case R.id.btnSave:
                saveChallenge(distance);
            default:
        }
    }

    public interface onLocationUpdateInterface {
        void onLocationUpdate(Location currentLocation);
    }

    public void setOnLocationUpdateInterface(ChallengeLocationFragment.onLocationUpdateInterface onLocationUpdateInterface) {
        this.onLocationUpdateInterface = onLocationUpdateInterface;
        Log.i(TAG, "vola setOnLocationUpdateInterface");
    }

    //overeni, ze se interface nasetoval
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onLocationUpdateInterface = (onLocationUpdateInterface) context;
            Log.i(TAG, "vola se onAttach");
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLocationUpdateInterface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            System.out.println(mRequestingLocationUpdates);
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
        Log.i(TAG, "Vola se onResume");
        Log.i(TAG, "jak je v onResume interface" + onLocationUpdateInterface);
        updateLocation();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "vola se onActiviyResult");
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateLocation();
                        break;
                }
                break;
        }
    }

    /**
     * Stores activity data in the Bundle.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "vola se onSaveInstanceState");
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "vola se onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                //TODO zjistit, o co kráčí
             /*   showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });*/
            }
        }
    }

    private void createLocationCallback() {
        Log.i(TAG, "vola se createLoacationCallback");
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();

                updateLocation();
            }
        };
    }

    private void updateLocation() { //TODO - proc je nekdy interface Null ???
        Log.i(TAG, "vola se updateLocation");
        Log.i(TAG, "mCurrentLocatio je" + mCurrentLocation);
        Log.i(TAG, "interface je " + onLocationUpdateInterface);
        if (mCurrentLocation != null && onLocationUpdateInterface != null) {
            locationModel = new LocationModel(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            distancePointsList.add(locationModel);

            onLocationUpdateInterface.onLocationUpdate(mCurrentLocation);
            System.out.println(mRequestingLocationUpdates + "je na jaké hodnotě?");
        } else {
            return;
        }
    }

    public void updateDistance(double distance) {
        this.distance = distance;
        textViewDistance.setText(" ");
        textViewDistance.setText(String.format("%.2f", distance));
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        Log.i(TAG, "vola se checkPermission");
        int permissionState = ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        Log.i(TAG, "vola se requestPermission");
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        Log.i(TAG, "vola se stopLocationUpdates");
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                        System.out.println(mRequestingLocationUpdates + "mel by byt false?");
                    }
                });
    }


    /**
     * Shows a {@link android.support.design.widget.Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                getView().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void buildLocationSettingsRequest() {
        Log.i(TAG, "Vola se buildLocatioRequestSettings");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequestHighAccuracy);
        mLocationSettingsRequest = builder.build();
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "vola se updateValuesFromBundle");
        if (savedInstanceState != null) {
            //TODO create buttons enabled/disabled
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
                System.out.println("tady se to nahodou nenastavuej" + mRequestingLocationUpdates);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            /*// Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }*/
            updateLocation();
        }
    }


    private void startLocationUpdates() {
        Log.i(TAG, "vola se startLocationUpdates");
        // Begin by checking if the device has the necessary location settings.
        client.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions();
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(mLocationRequestHighAccuracy,
                                mLocationCallback, Looper.myLooper());

                        updateLocation();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        updateLocation();
                    }
                });

    }

    private void saveChallenge(double distance) {
        databaseHelper.saveQuest(distance, distancePointsList);
        Intent intent = new Intent(getActivity(), DetailSectionActivity.class);
        intent.putExtra("distance", distance);
        getActivity().finish();
        startActivity(intent);
    }

    //TODO nastavit lastKnownLocation při startu aktivity (mapa)
    @SuppressLint("StaticFieldLeak")
    private void getLastKnownLocation() {
        new AsyncTask<Void, Void, List<Double>>() {

            @SuppressLint("MissingPermission")
            @Override
            protected List<Double> doInBackground(Void... voids) {

                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location == null) {
                                    lat = 50;
                                    lon = 50;
                                } else {

                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                }
                                listLaTLon.add(lat);
                                listLaTLon.add(lon);
                            }
                        });
                return listLaTLon;
            }
        }.execute();
    }
}

