package cz.uhk.fim.runhk.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.fim.runhk.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestFragment extends Fragment implements View.OnClickListener {

    OnButtonClickedInterface onButtonClickedInterface;

    FusedLocationProviderClient fusedLocationProviderClient;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    double lat;
    double lon;

    List<Double> listLaTLon;

    FloatingActionButton btnPlay;

    public QuestFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quest, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        view.findViewById(R.id.btnPlay).setOnClickListener(this);

        listLaTLon = new ArrayList<>();

        getLastKnownLocation();

        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // location updates
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        SettingsClient client = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

        return view;
    }


    public void setOnButtonClickedInterface(OnButtonClickedInterface onButtonClickedInterface1) {
        onButtonClickedInterface = onButtonClickedInterface1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                onButtonClickedInterface.onPlaySelected(v, lat, lon);
                break;
            case R.id.btnStop:
                onButtonClickedInterface.onStopSelected(v);
                break;
            default:
        }
    }

    public interface OnButtonClickedInterface {
        void onPlaySelected(View view, double lat, double lon);

        void onStopSelected(View view);
    }

    //overeni, ze se interface nasetoval
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onButtonClickedInterface = (OnButtonClickedInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnButtonClickInterface");
        }
    }

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

