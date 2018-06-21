package cz.uhk.fim.runhk.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.uhk.fim.runhk.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailChallengeFragment extends Fragment {

    public DetailChallengeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_challenge, container, false);

        double distance = getArguments().getDouble("distance", 0);

        TextView textViewDistance = view.findViewById(R.id.textViewDetailDistance);
        textViewDistance.setText(String.valueOf(distance));
        // Inflate the layout for this fragment
        return view;


    }

}
