package cz.uhk.fim.runhk.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.Quest;

public class QuestViewAdapter extends RecyclerView.Adapter<QuestViewAdapter.FinishedQuestsViewHolder> {

    private List<Quest> questList;

    private OnItemClickedInterface onItemClickedInterface;

    int position;

    public QuestViewAdapter(List<Quest> questList) {
        this.questList = questList;
    }

    @NonNull
    @Override
    public FinishedQuestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_quest, null);

        FinishedQuestsViewHolder viewHolder = new FinishedQuestsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FinishedQuestsViewHolder holder, int position) {
        Quest quest = questList.get(position);
        holder.setQuest(quest);

    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    public void setOnItemClickedInterface(OnItemClickedInterface onItemClickedInterface) {
        this.onItemClickedInterface = onItemClickedInterface;
    }

    public class FinishedQuestsViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDistance;
        private TextView textViewExps;
        private TextView textViewTime;
        private Button btnDetail;


        public FinishedQuestsViewHolder(View itemView) {
            super(itemView);
            textViewDistance = itemView.findViewById(R.id.textViewFinishedDistance);
            textViewExps = itemView.findViewById(R.id.textViewFinishedExps);
            textViewTime = itemView.findViewById(R.id.textViewFinishedTime);
            btnDetail = itemView.findViewById(R.id.btnGoToDetail);


        }

        public void setQuest(final Quest quest) {
            textViewDistance.setText(String.valueOf(quest.getDistance()));
            textViewExps.setText(String.valueOf(quest.getExps()));
            position = getAdapterPosition();

            btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickedInterface.onButtonClicked(getAdapterPosition());
                }
            });


        }

    }
}
