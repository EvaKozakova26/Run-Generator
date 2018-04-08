package cz.uhk.fim.runhk;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestListFragment extends Fragment implements View.OnClickListener {

    private OnItemSelectedInterface onItemSelectedInterface;


    public QuestListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quest_list, container, false);

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
