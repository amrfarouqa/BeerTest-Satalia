package website.amrfarouqa.beertest.Classes;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import website.amrfarouqa.beertest.R;

public class TripJournalAdapter extends RecyclerView.Adapter<TripJournalAdapter.MyViewHolder> {

    private List<TripJournalClass> dataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView breweryName, fromText, toText, DistanceText;

        public MyViewHolder(View view) {
            super(view);

            breweryName = (TextView) view.findViewById(R.id.visitedBreweryNameRowField);
            fromText = (TextView) view.findViewById(R.id.fromText);
            toText = (TextView) view.findViewById(R.id.toText);
            DistanceText = (TextView) view.findViewById(R.id.DistanceText);
        }
    }


    public TripJournalAdapter(List<TripJournalClass> dataList) {
        this.dataList = dataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_journal_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TripJournalClass tripJournalClass = dataList.get(position);
        holder.breweryName.setText(tripJournalClass.getBreweryName());
        holder.fromText.setText(("From: " +tripJournalClass.getFrom()));
        holder.toText.setText("To: " + tripJournalClass.getTo());
        holder.DistanceText.setText(String.valueOf(tripJournalClass.getDistance()));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}