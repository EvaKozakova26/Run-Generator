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
import cz.uhk.fim.runhk.model.Challenge;

public class ChallengeViewAdapter extends RecyclerView.Adapter<ChallengeViewAdapter.FinishedQuestsViewHolder> {

    private List<Challenge> challengeList;

    private OnItemClickedInterface onItemClickedInterface;

    int position;

    private int selected_position = 0;

    FinishedQuestsViewHolder viewHolder;

    public ChallengeViewAdapter(List<Challenge> challengeList) {
        this.challengeList = challengeList;
    }

    @NonNull
    @Override
    public FinishedQuestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_challenge, null);

        viewHolder = new FinishedQuestsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FinishedQuestsViewHolder holder, int position) {
        Challenge challenge = challengeList.get(position);
        holder.setQuest(challenge);
        holder.itemView.setBackgroundColor(selected_position == position ? Color.GREEN : Color.TRANSPARENT);

    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    public void setOnItemClickedInterface(OnItemClickedInterface onItemClickedInterface) {
        this.onItemClickedInterface = onItemClickedInterface;
    }


    public class FinishedQuestsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewDistance;
        private TextView textViewExps;


        public FinishedQuestsViewHolder(View itemView) {
            super(itemView);
            textViewDistance = itemView.findViewById(R.id.textViewFinishedDistance);
            textViewExps = itemView.findViewById(R.id.textViewFinishedExps);
            itemView.setOnClickListener(this);

        }

        public void setQuest(final Challenge challenge) {
         /*   textViewDistance.setText(String.valueOf(challenge.getDistance()));
            textViewExps.setText(String.valueOf(challenge.getExps()));*/

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
