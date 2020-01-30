package cz.uhk.fim.runhk.fragments;


import android.content.Context;
import android.location.Location;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.LocationModel;
import cz.uhk.fim.runhk.service.AsyncResponse;
import cz.uhk.fim.runhk.service.ElevationService;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailChallengeFragment extends Fragment implements OnMapReadyCallback, AsyncResponse {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    private ArrayList<LocationModel> pointsList;

    private List<Double> elevationList;

    public DetailChallengeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_challenge, container, false);

        elevationList = new ArrayList<>();

        double distance = getArguments().getDouble("distance", 0);
        pointsList = getArguments().getParcelableArrayList("points");
        int exps = getArguments().getInt("exps");
        int calories = getArguments().getInt("calories");
        int elevation = getArguments().getInt("elevation");
        String time = getArguments().getString("time");

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(this);

        TextView textViewDistance = view.findViewById(R.id.textViewDetailDistance);
        textViewDistance.setText(String.format("%.2f", distance / 1000) + " km");

        TextView textViewExps = view.findViewById(R.id.textViewDetailExps);
        textViewExps.setText(String.valueOf(exps) + " points");

        TextView textViewTime = view.findViewById(R.id.textViewDetailTime);
        textViewTime.setText(time);

        TextView textViewCalories = view.findViewById(R.id.textViewDetailCalories);
        textViewCalories.setText(String.valueOf(calories) + " kcals");

        TextView textViewElevation = view.findViewById(R.id.textViewDetailElevation);
        textViewElevation.setText(String.valueOf(elevation) + " m");

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getRoute();
    }

    private void getRoute() {
        double currentLat;
        double currentLng;
        double prevLat = pointsList.get(0).getLatitude();
        double prevLng = pointsList.get(0).getLongitude();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(prevLat, prevLng), 12.5f));

        for (LocationModel location : pointsList) {
            currentLat = location.getLatitude();
            currentLng = location.getLongitude();

            mMap.addPolyline(new PolylineOptions().clickable(false).jointType(JointType.ROUND).add(
                    new LatLng(prevLat, prevLng),
                    new LatLng(currentLat, currentLng)
            ));

            prevLat = currentLat;
            prevLng = currentLng;

        }
    }

    @Override
    public void processFinish(Double output) {
        elevationList.add(output);
    }
}
