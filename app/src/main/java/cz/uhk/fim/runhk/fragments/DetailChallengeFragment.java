package cz.uhk.fim.runhk.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.LocationModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailChallengeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    ArrayList<LocationModel> pointsList;

    public DetailChallengeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_challenge, container, false);

        double distance = getArguments().getDouble("distance", 0);
        pointsList = getArguments().getParcelableArrayList("points");
        int exps = getArguments().getInt("exps");

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(this);

        TextView textViewDistance = view.findViewById(R.id.textViewDetailDistance);
        textViewDistance.setText(String.format("%.2f", distance) + " meters");

        TextView textViewExps = view.findViewById(R.id.textViewDetailExps);
        textViewExps.setText(String.valueOf(exps) + " points");

        TextView textViewTime = view.findViewById(R.id.textViewDetailTime);
        textViewTime.setText("");

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double currentLat;
        double currentLng;
        double prevLat = pointsList.get(0).getLatitude();
        double prevLng = pointsList.get(0).getLongitude();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(prevLat, prevLng), 12.5f));

        for (LocationModel location : pointsList) {
            currentLat = location.getLatitude();
            currentLng = location.getLongitude();

            mMap.addPolyline(new PolylineOptions().clickable(false).add(
                    new LatLng(prevLat, prevLng),
                    new LatLng(currentLat, currentLng)
            ));

            prevLat = currentLat;
            prevLng = currentLng;

        }
    }
}
