package com.example.lucas.esapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lucas.esapp.Interface.RecycleViewOnClickListenerHack;
import com.example.lucas.esapp.Model.MarkedPlace;
import com.example.lucas.esapp.R;

import java.util.List;

/**
 * Created by Lucas on 07/08/2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.GameViewHolder> {
    private List<MarkedPlace> placesResult;
    private Context context;
    private RecycleViewOnClickListenerHack recycleViewOnCLickListenerHack;

    public SearchAdapter(Context context, List<MarkedPlace> places){
        this.placesResult = places;
        this.context = context;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item_card, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        MarkedPlace marker = placesResult.get(position);

        holder.namePlace.setText(marker.getName());
        holder.typePlace.setText(marker.getCategory());
        holder.evaluationPlace.setText(marker.getEvaluation() + " / 5.0");
        if(marker.getDistance() == -0.001){
            holder.distancePlace.setText("");
        } else
            holder.distancePlace.setText(marker.getDistance() + " km");
    }

    @Override
    public int getItemCount() {
        return placesResult.size();
    }


    public void update(List<MarkedPlace> list){
        this.placesResult = list;
        notifyDataSetChanged();
    }

    public void setRecycleViewOnCLickListenerHack (RecycleViewOnClickListenerHack r) {
        this.recycleViewOnCLickListenerHack = r;
    }



    public class GameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView namePlace;
        private TextView typePlace;
        private TextView evaluationPlace;
        private TextView distancePlace;

        public GameViewHolder(View itemView) {
            super(itemView);

            namePlace = (TextView) itemView.findViewById(R.id.placeName);
            typePlace = (TextView) itemView.findViewById(R.id.placeType);
            evaluationPlace = (TextView) itemView.findViewById(R.id.reputationText);
            distancePlace = (TextView) itemView.findViewById(R.id.distanceText);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (recycleViewOnCLickListenerHack != null) {
                recycleViewOnCLickListenerHack.onClickListener(v, getPosition());
            }
        }
    }
}
