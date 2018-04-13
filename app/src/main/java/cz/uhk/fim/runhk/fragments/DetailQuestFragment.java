package cz.uhk.fim.runhk.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.Quest;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailQuestFragment extends Fragment {

    public DetailQuestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_quest, container, false);


    }

}
