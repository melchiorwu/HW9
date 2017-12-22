package com.example.wuchaoyu.csci571;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class news extends android.support.v4.app.Fragment {
    public String symbol;
    private ListView stockNewsListView;
    private stockNewsAdapter mAdapter;

    private ArrayList<String> mStockNewsTitles;
    private ArrayList<String> mStockNewsPublishers;
    private ArrayList<String> mStockNewsDates;
    private ArrayList<String> mStockNewsUrls;
    private TextView errorNew;


    public news() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StockInfoContent parentActivity = (StockInfoContent) getActivity();
        this.symbol = parentActivity.symbol;

        this.mStockNewsTitles = new ArrayList<>();
        this.mStockNewsPublishers = new ArrayList<>();
        this.mStockNewsDates = new ArrayList<>();
        this.mStockNewsUrls = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View framentRootView = inflater.inflate(R.layout.fragment_news, container, false);

        this.stockNewsListView = (ListView) framentRootView.findViewById(R.id.news);
        mAdapter = new stockNewsAdapter(this.getContext());
        stockNewsListView.setAdapter(mAdapter);
        errorNew = (TextView)framentRootView.findViewById(R.id.newsError);
        stockNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mStockNewsUrls.get(position)));
                startActivity(browserIntent);
            }
        });
        return framentRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getStockNews();
    }
    private void getStockNews() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/news?stockSymbol="+symbol;
        JsonObjectRequest requestNews = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mStockNewsTitles.clear();
                mStockNewsPublishers.clear();
                mStockNewsDates.clear();
                mStockNewsUrls.clear();
                try {
                    JSONArray links = response.getJSONArray("links");
                    JSONArray titles = response.getJSONArray("titles");
                    JSONArray dates = response.getJSONArray("times");
                    JSONArray authors = response.getJSONArray("authors");
                    for(int i=0;i< links.length();i++){
                        mStockNewsTitles.add(titles.getString(i));
                        mStockNewsDates.add(dates.getString(i));
                        mStockNewsPublishers.add(authors.getString(i));
                        mStockNewsUrls.add(links.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.refreshData(mStockNewsTitles, mStockNewsPublishers, mStockNewsDates);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("newsError","error");
                errorNew.setText("Fail to load data");
                errorNew.setTextColor(Color.BLACK);
                errorNew.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            }
        });
        requestQueue.add(requestNews);
    }

}
