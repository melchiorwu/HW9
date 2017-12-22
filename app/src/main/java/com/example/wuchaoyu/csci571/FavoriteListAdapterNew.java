package com.example.wuchaoyu.csci571;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by wuchaoyu on 11/30/17.
 */

public class FavoriteListAdapterNew extends ArrayAdapter {
    ArrayList<String> favoriteStocks;
    ArrayList<String> defaultOrder;
    ArrayList<String> symbols;

    public FavoriteListAdapterNew(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        favoriteStocks = objects;
        defaultOrder = new ArrayList<>();
        for(int i=0;i<objects.size();i++){
            defaultOrder.add(objects.get(i));
        }
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.favorite_list,null);
        TextView favoriteSymbol=(TextView) view.findViewById(R.id.favoriteSymbol);
        TextView favoritePrice=(TextView) view.findViewById(R.id.favoritePrice);
        TextView favoriteChange=(TextView) view.findViewById(R.id.favoriteChange);
        Log.d("HopeInNew",favoriteStocks.get(i));
        String[] result = this.favoriteStocks.get(i).split(",");
        Log.d("resultLength",result.length+"");
        if(result.length==4) {
            favoriteSymbol.setText(result[0]);
            favoritePrice.setText(result[1]);
            favoriteChange.setText(result[2]+" "+"("+result[3]+"%)");
            favoriteSymbol.setTextColor(Color.BLACK);
            favoritePrice.setTextColor(Color.BLACK);
            if ((Float.parseFloat(result[2])) < 0) {
                favoriteChange.setTextColor(Color.RED);
            } else {
                favoriteChange.setTextColor(Color.GREEN);
            }
        }
//        favoriteSymbol.setText(favoriteStockSymbols.get(i));
//        favoritePrice.setText(favoriteStockPrices.get(i));
//        favoriteChange.setText(favoriteStockChanges.get(i)+" "+"("+favoriteStockPercentChanges.get(i)+"%)");
        return view;
    }

    public ArrayList<String> returnSortSymbol(){
        symbols=new ArrayList<String>();
        for(int i=0;i<favoriteStocks.size();i++){
            symbols.add(favoriteStocks.get(i).split(",")[0]);
        }
        return symbols;
    }
    public void sort(String option, String order){
        if(option.equals("Symbol")&&order.equals("Ascending")){
            Collections.sort(favoriteStocks);
            notifyDataSetChanged();
        }
        if(option.equals("Symbol")&&order.equals("Descending")){
            Collections.sort(favoriteStocks);
            Collections.reverse(favoriteStocks);
            notifyDataSetChanged();
        }
        if(option.equals("Price")&&order.equals("Ascending")){
            Collections.sort(favoriteStocks, new Comparator<String>() {
                @Override
                public int compare(String x1, String x2) {
                    if((Float.parseFloat(x1.split(",")[1])-Float.parseFloat(x2.split(",")[1]))<0)
                        return -1;
                    else
                        return 1;
                }
            });
            notifyDataSetChanged();
        }
        if(option.equals("Price")&&order.equals("Descending")){
            Collections.sort(favoriteStocks, new Comparator<String>() {
                @Override
                public int compare(String x1, String x2) {
                    if((Float.parseFloat(x1.split(",")[1])-Float.parseFloat(x2.split(",")[1]))>0)
                        return -1;
                    else
                        return 1;
                }
            });
            notifyDataSetChanged();
        }
        if(option.equals("Change")&&order.equals("Ascending")){
            Collections.sort(favoriteStocks, new Comparator<String>() {
                @Override
                public int compare(String x1, String x2) {
                    if((Float.parseFloat(x1.split(",")[2])-Float.parseFloat(x2.split(",")[2]))<0)
                        return -1;
                    else
                        return 1;
                }
            });
            notifyDataSetChanged();
        }
        if(option.equals("Change")&&order.equals("Descending")){
            Collections.sort(favoriteStocks, new Comparator<String>() {
                @Override
                public int compare(String x1, String x2) {
                    if((Float.parseFloat(x1.split(",")[2])-Float.parseFloat(x2.split(",")[2]))>0)
                        return -1;
                    else
                        return 1;
                }
            });
            notifyDataSetChanged();
        }
        if(option.equals("Change(%)")&&order.equals("Ascending")){
            Collections.sort(favoriteStocks, new Comparator<String>() {
                @Override
                public int compare(String x1, String x2) {
                    if((Float.parseFloat(x1.split(",")[3])-Float.parseFloat(x2.split(",")[3]))<0)
                        return -1;
                    else
                        return 1;
                }
            });
            notifyDataSetChanged();
        }
        if(option.equals("Change(%)")&&order.equals("Descending")){
            Collections.sort(favoriteStocks, new Comparator<String>() {
                @Override
                public int compare(String x1, String x2) {
                    if((Float.parseFloat(x1.split(",")[3])-Float.parseFloat(x2.split(",")[3]))>0)
                        return -1;
                    else
                        return 1;
                }
            });
            notifyDataSetChanged();
        }
        if(option.equals("Default")){
            Log.d("Default","Order");
            for(int i=0;i<defaultOrder.size();i++){
                Log.d("DefaultOrder",defaultOrder.get(i));
                favoriteStocks.set(i,defaultOrder.get(i));
            }
            notifyDataSetChanged();
        }
    }
}
