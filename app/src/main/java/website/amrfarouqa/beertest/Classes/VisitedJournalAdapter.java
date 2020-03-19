package website.amrfarouqa.beertest.Classes;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import website.amrfarouqa.beertest.R;

public class VisitedJournalAdapter extends RecyclerView.Adapter<VisitedJournalAdapter.MyViewHolder> {

    private List<VisitedJournalClass> dataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView VisitedBreweryName;

        public MyViewHolder(View view) {
            super(view);

            VisitedBreweryName = (TextView) view.findViewById(R.id.visitedBreweryNameRowField);

        }
    }


    public VisitedJournalAdapter(List<VisitedJournalClass> dataList) {
        this.dataList = dataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visited_journal_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        VisitedJournalClass VisitedJournalClass = dataList.get(position);
        holder.VisitedBreweryName.setText("--> " + VisitedJournalClass.getVisitedBreweryName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}