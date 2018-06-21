package cz.uhk.fim.runhk.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.uhk.fim.runhk.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChallengeListFragment extends Fragment implements View.OnClickListener {

    private OnItemSelectedInterface onItemSelectedInterface;


    public ChallengeListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge_list, container, false);

        view.findViewById(R.id.txtRandom).setOnClickListener(this);

        return view;
    }

    public void setOnItemSelectedInterface(OnItemSelectedInterface onItemSelectedInterface) {
        this.onItemSelectedInterface = onItemSelectedInterface;
    }

    @Override
    public void onClick(View v) {
        onItemSelectedInterface.onItemSelected(v);
    }

    public interface OnItemSelectedInterface {
        void onItemSelected(View view);
    }

}
