package com.example.wuchaoyu.csci571;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wuchaoyu on 11/22/17.
 */

public class stockDetailsAdapter extends BaseAdapter {
    private String[] mStockDetailTitles = {
            "Stock Symbol",
            "Last Price",
            "Change",
            "Timestamp",
            "open",
            "close",
            "Day's Range",
            "Volume"
    };
    private String[] mStockDetailContents;
    private static LayoutInflater inflater;

    public stockDetailsAdapter(Context context, String[] stockDetailContents){
        this.mStockDetailContents = stockDetailContents;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mStockDetailTitles.length;
    }

    @Override
    public Object getItem(int i) {
        return mStockDetailContents[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.table, viewGroup, false);
        TextView stockDetailCellTitle = (TextView) view.findViewById(R.id.stockDetailCellTitle);
        TextView stockDetailCellContent= (TextView) view.findViewById(R.id.stockDetailCellContent);
        ImageView stockDetailCellImageView = (ImageView) view.findViewById(R.id.arrow);;
        if(i==2){
            if(mStockDetailContents[8]=="0") {
                stockDetailCellImageView.setImageResource(R.drawable.down);
            }
            else{
                stockDetailCellImageView.setImageResource(R.drawable.up);
            }
        }
        stockDetailCellTitle.setText(mStockDetailTitles[i]);
        stockDetailCellContent.setText(mStockDetailContents[i]);
        stockDetailCellTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,19);
        stockDetailCellContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,19);
        return view;
    }

    public void refreshData (String[] newContentList) {
        this.mStockDetailContents = newContentList;
        notifyDataSetChanged();
    }
}
