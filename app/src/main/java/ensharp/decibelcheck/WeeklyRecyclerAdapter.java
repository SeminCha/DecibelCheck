package ensharp.decibelcheck;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class WeeklyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<WeeklyInfoAdapterItem> itemList;

    private OnItemClickListener listener;


    public static class YearViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLayoutYear;
        public TextView mYearTxt;

        public YearViewHolder(View v) {
            super(v);
            mYearTxt = (TextView) v.findViewById(R.id.yearTxt);
            mLayoutYear = (LinearLayout) v.findViewById(R.id.layout_year);
        }
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mSimpleWeekTxt;
        public TextView mDetaileWeekTxt;
        private OnViewHolderClickListener listener;

        public DataViewHolder(View v, OnViewHolderClickListener listener) {
            super(v);
            mSimpleWeekTxt = (TextView) v.findViewById(R.id.simpleweekTxt);
            mDetaileWeekTxt = (TextView) v.findViewById(R.id.detailweekTxt);
            v.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener != null)
                listener.onViewHolderClick(getPosition());
        }

        private interface OnViewHolderClickListener {
            void onViewHolderClick(int position);
        }
    }

    public WeeklyRecyclerAdapter(ArrayList<WeeklyData> dataset) {
        itemList = initItemList(dataset);
    }

    private ArrayList<WeeklyInfoAdapterItem> initItemList(ArrayList<WeeklyData> dataset) {
        ArrayList<WeeklyInfoAdapterItem> result = new ArrayList<>();

        String yearType = "";

        for (WeeklyData data : dataset) {
            if (yearType != data.getYear()) {
                result.add(new WeeklyYearItem(data.getYear()));
                yearType = data.getYear();
            }
            result.add(data);
        }
        return result;
    }


    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == WeeklyInfoAdapterItem.TYPE_YEAR)
            return new YearViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_year, parent, false));
        else
            return new DataViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_week, parent, false),
                    new DataViewHolder.OnViewHolderClickListener() {
                        @Override
                        public void onViewHolderClick(int position) {
                            if (listener != null)
                                listener.onItemClick(position);
                        }
                    }
            );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //코스 이름
        if (holder instanceof YearViewHolder) {
            YearViewHolder tHolder = (YearViewHolder) holder;
            tHolder.mYearTxt.setText((itemList.get(position)).getYearToString());
        }
        //관광명소 이름 및 인증 여부
        else if (holder instanceof DataViewHolder) {
            DataViewHolder dHolder = (DataViewHolder) holder;
            //관광명소 이름
            dHolder.mSimpleWeekTxt.setText(
                    ((WeeklyData) itemList.get(position))
                            .getSimpleWeek());
            dHolder.mDetaileWeekTxt.setText(
                    ((WeeklyData) itemList.get(position))
                            .getDetailWeek());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public WeeklyData getItem(int position) {
        return (WeeklyData) itemList.get(position);
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}