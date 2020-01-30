package cz.uhk.fim.runhk.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cz.uhk.fim.runhk.R;
import cz.uhk.fim.runhk.model.Player;


public class RankingsViewAdapter extends RecyclerView.Adapter<RankingsViewAdapter.RankingsViewHolder> {

    private List<Player> playersList;

    public RankingsViewAdapter(List<Player> playersList) {
        this.playersList = playersList;
    }

    @NonNull
    @Override
    public RankingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_player, null);
        return new RankingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingsViewHolder holder, int position) {
        Player player = playersList.get(position);
        holder.setPlayer(player);
    }

    @Override
    public int getItemCount() {
        return playersList.size();
    }


    class RankingsViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNickname;
        private TextView textViewLevel;
        private TextView textViewExps;


        RankingsViewHolder(View itemView) {
            super(itemView);
            textViewNickname = itemView.findViewById(R.id.textViewPlayerNicknameRank);
            textViewLevel = itemView.findViewById(R.id.textViewPlayerLevelRank);
            textViewExps = itemView.findViewById(R.id.textViewPlayerExpsRank);

        }

        void setPlayer(final Player player) {
            textViewNickname.setText(player.getNickname());
            textViewLevel.setText(String.valueOf(player.getLevel()));
            textViewExps.setText(String.valueOf(player.getExps()) + " experience points");

        }

    }
}
