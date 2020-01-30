package cz.uhk.fim.runhk.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.Run;

public class ChallengeViewAdapter extends RecyclerView.Adapter<ChallengeViewAdapter.FinishedQuestsViewHolder> {

    private List<Run> runList;

    private OnItemClickedInterface onItemClickedInterface;

    private int selected_position = 0;
    private boolean isLandscape;

    private FinishedQuestsViewHolder viewHolder;

    public ChallengeViewAdapter(List<Run> runList, boolean isLandscape) {
        this.runList = runList;
        this.isLandscape = isLandscape;
    }

    @NonNull
    @Override
    public FinishedQuestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_challenge, null);
        return new FinishedQuestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinishedQuestsViewHolder holder, int position) {
        Run run = runList.get(position);
        holder.setQuest(run);
        if (isLandscape) {
            holder.itemView.setBackgroundColor(selected_position == position ? Color.rgb(123, 241, 163) : Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return runList.size();
    }

    public void setOnItemClickedInterface(OnItemClickedInterface onItemClickedInterface) {
        this.onItemClickedInterface = onItemClickedInterface;
    }


    public class FinishedQuestsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewDate;
        private TextView textViewDistance;
        private TextView textViewExps;

        FinishedQuestsViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDistance = itemView.findViewById(R.id.textViewListDistance);
            textViewExps = itemView.findViewById(R.id.textViewListExps);
            itemView.setOnClickListener(this);
        }

        void setQuest(final Run run) {
            //TODO resolve warnings
            textViewDate.setText(run.getDate());
            double distance = run.getDistance() / 1000;
            String distanceString = String.format("%.2f", distance);
            textViewDistance.setText(distanceString + " km");
            textViewExps.setText(String.valueOf(run.getExps()) + " points");
        }


        @Override
        public void onClick(View view) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            // Updating old as well as new positions
            notifyItemChanged(selected_position);
            selected_position = getAdapterPosition();
            notifyItemChanged(selected_position);

            onItemClickedInterface.onButtonClicked(selected_position);
        }
    }
}
