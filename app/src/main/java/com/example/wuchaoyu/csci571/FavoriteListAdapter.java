package com.example.wuchaoyu.csci571;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wuchaoyu on 11/26/17.
 */

public class FavoriteListAdapter extends BaseAdapter {
    private ArrayList<String> favoriteStockSymbols;
    private ArrayList<String> favoriteStockPrices;
    private ArrayList<String> favoriteStockChanges;
    private ArrayList<String> favoriteStockPercentChanges;
    private String[] result;
    private String sortOptions;

    private static LayoutInflater inflater;

    public FavoriteListAdapter(Context context, ArrayList<String> favoriteListSymbols, ArrayList<String> favoriteListPrices, ArrayList<String> favoriteListChange, ArrayList<String> favoriteListPercentChange) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        favoriteStockSymbols = favoriteListSymbols;
        favoriteStockPrices = favoriteListPrices;
        favoriteStockChanges = favoriteListChange;
        favoriteStockPercentChanges = favoriteListPercentChange;
        result = new String[favoriteStockSymbols.size()];
        for(int i=0;i<result.length;i++){
            result[i]=favoriteStockSymbols.get(i)+","+favoriteStockPrices.get(i)+","+favoriteStockChanges.get(i)+","+favoriteStockPercentChanges.get(i);
        }
//        Log.d("FavorResult",result.length+"");
//        String[] test = result[0].split(",");
//        Log.d("FavorResult",test.length+"");
    }

    @Override
    public int getCount() {
        return favoriteStockSymbols.size();
    }

    @Override
    public Object getItem(int i) {
       return favoriteStockSymbols.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.favorite_list, viewGroup, false);
        TextView favoriteSymbol=(TextView) view.findViewById(R.id.favoriteSymbol);
        TextView favoritePrice=(TextView) view.findViewById(R.id.favoritePrice);
        TextView favoriteChange=(TextView) view.findViewById(R.id.favoriteChange);
        String[] result = this.result[i].split(",");
        Log.d("resultLength",result.length+"");
        if(result.length==4) {
            favoriteSymbol.setText(result[0]);
            favoritePrice.setText(result[1]);
            favoriteChange.setText(result[2]+" "+"("+result[3]+"%)");
        }
//        favoriteSymbol.setText(favoriteStockSymbols.get(i));
//        favoritePrice.setText(favoriteStockPrices.get(i));
//        favoriteChange.setText(favoriteStockChanges.get(i)+" "+"("+favoriteStockPercentChanges.get(i)+"%)");
        favoriteSymbol.setTextColor(Color.BLACK);
        favoritePrice.setTextColor(Color.BLACK);
        if(!favoriteStockChanges.get(i).equals("")) {
            if ((Float.parseFloat(favoriteStockChanges.get(i))) < 0) {
                favoriteChange.setTextColor(Color.RED);
            } else {
                favoriteChange.setTextColor(Color.GREEN);
            }
        }
        return view;
    }
    public void deleteData (int i) {
        favoriteStockSymbols.remove(i);
        favoriteStockPrices.remove(i);
        favoriteStockChanges.remove(i);
        favoriteStockPercentChanges.remove(i);
        notifyDataSetChanged();
    }
    public void refresh(){

    }
    public void sortResult(String sortOption){

    }
}
