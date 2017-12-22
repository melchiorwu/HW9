package com.example.wuchaoyu.csci571;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wuchaoyu on 11/25/17.
 */

public class stockNewsAdapter extends BaseAdapter {
    private ArrayList<String> mStockNewsTitles;
    private ArrayList<String> mStockNewsPublishers;
    private ArrayList<String> mStockNewsDates;
    private static LayoutInflater inflater;

    public stockNewsAdapter(Context context) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mStockNewsTitles = new ArrayList<>();
        this.mStockNewsPublishers = new ArrayList<>();
        this.mStockNewsDates = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mStockNewsTitles.size();
    }

    @Override
    public Object getItem(int i) {
        return mStockNewsTitles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.news, viewGroup, false);
        TextView stockNewsCellTitle = (TextView) view.findViewById(R.id.stockNewsCellTitle);
        TextView stockNewsCellPublisher = (TextView) view.findViewById(R.id.stockNewsCellPublisher);
        TextView stockNewsCellDate = (TextView) view.findViewById(R.id.stockNewsCellDate);
        stockNewsCellTitle.setText(mStockNewsTitles.get(i));
        stockNewsCellPublisher.setText(mStockNewsPublishers.get(i));
        stockNewsCellDate.setText(mStockNewsDates.get(i));
        return view;
    }
    public void refreshData (ArrayList<String> newTitles,
                             ArrayList<String> newPublishers,
                             ArrayList<String> newDates) {
        this.mStockNewsTitles = newTitles;
        this.mStockNewsPublishers = newPublishers;
        this.mStockNewsDates = newDates;
        notifyDataSetChanged();
    }
}
