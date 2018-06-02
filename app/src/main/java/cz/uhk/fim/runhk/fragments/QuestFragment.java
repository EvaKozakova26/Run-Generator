package cz.uhk.fim.runhk.fragments;


import android.annotation.SuppressLint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.activities.MapsActivity;
import cz.uhk.fim.runhk.activities.PlayerProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestFragment extends Fragment {

    FusedLocationProviderClient fusedLocationProviderClient;

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

        listLaTLon = new ArrayList<>();
        listLaTLon.add(lat);
        listLaTLon.add(lon);

        new AsyncTask<Void, Void, List<Double>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<Double> doInBackground(Void... voids) {
                System.out.println("background madafakas");

                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                System.out.println("jsem tu madafakas ?");

                                if (location == null) {
                                    lat = 50;
                                    lon = 50;
                                } else {

                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                }

                                listLaTLon.add(lat);
                                listLaTLon.add(lon);
                                System.out.println("lat" + lat + "lissr " + listLaTLon.get(0));
                            }
                        });

                return listLaTLon;

            }

            @Override
            protected void onPostExecute(List<Double> doubles) {

            }
        }.execute();

        btnPlay = view.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaySelected(v);
            }
        });

        return view;
    }

    public void onPlaySelected(View view) {
        Toast.makeText(getContext(), lat + " " + lon, Toast.LENGTH_LONG).show();
    }



}
