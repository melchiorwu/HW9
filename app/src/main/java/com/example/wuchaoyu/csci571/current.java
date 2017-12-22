package com.example.wuchaoyu.csci571;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import com.facebook.FacebookSdk;

import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class current extends android.support.v4.app.Fragment {
    public String symbol;
    private String[] infoDetails = {"","","","","","","","",""};
    private ArrayList<String> dates = new ArrayList<String>();
    private ListView stockDetailsTable;
    private stockDetailsAdapter mAdapter;
    private WebView variousChartWebView;
    private Button change;
    private String indicator;
    private Button fbShare;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private String selectedIndicator;
    private ProgressBar tableProBar;
    private Button addFavorite;
    private String priceChange;
    private String percentChange;
    private TextView error;

    public current() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StockInfoContent parentActivity = (StockInfoContent) getActivity();
        this.symbol = parentActivity.symbol;
        setRetainInstance(true);
//        Log.d("Current symbol is",symbol);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentRootView = inflater.inflate(R.layout.fragment_current, container, false);
        variousChartWebView = (WebView) fragmentRootView.findViewById(R.id.variousChart);
        stockDetailsTable = (ListView) fragmentRootView.findViewById(R.id.stockDetails);
        Spinner spinner = (Spinner)fragmentRootView.findViewById(R.id.indicators);
        tableProBar = (ProgressBar)fragmentRootView.findViewById(R.id.tableProBar);
        addFavorite = (Button)fragmentRootView.findViewById(R.id.addFavorite);
        error = (TextView)fragmentRootView.findViewById(R.id.tableError);

        fbShare = (Button)fragmentRootView.findViewById(R.id.fbShare);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager,shareCallBack);

        if (symbol != null && !symbol.equals("")) {
            TreeSet<String> favorites = new TreeSet<String>(getActivity().getSharedPreferences("default", Context.MODE_PRIVATE)
                    .getStringSet("favorite", new HashSet<String>()));
            if (favorites.contains(symbol)) {
                addFavorite.setBackgroundResource(R.drawable.filled);
            }
        }

        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("add","added");
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("default", Context.MODE_PRIVATE);
                TreeSet<String> favorites = new TreeSet<>(sharedPreferences
                        .getStringSet("favorite", new TreeSet<String>()));
                String position = sharedPreferences.getString("position", new String());
                JSONObject test = null;
                try {
                    Log.d("add","added");
                    if(position.length()<2){
                        Log.d("iter","null");
                        test = new JSONObject();
                    }else {
                        Log.d("iter","Nnull");
                        test = new JSONObject(position);
                        Log.d("iter",test.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (favorites.contains(symbol)) {
                    favorites.remove(symbol);
                    test.remove(symbol);
                    addFavorite.setBackgroundResource(R.drawable.empty);
                } else {
                    favorites.add(symbol);
                    try {
                        test.put(symbol,symbol);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addFavorite.setBackgroundResource(R.drawable.filled);
                }
//                editor.clear();
                editor.putStringSet("favorite", favorites);
                editor.putString("position",test.toString());
                editor.apply();
            }
        });

        fbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/fb?stockSymbol=aapl&chartType="+indicator;
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                StringRequest requestImage = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("URL IMG",response);
                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                            .setContentUrl(Uri.parse(response))
                                            .build();
                                    shareDialog.show(linkContent);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(requestImage);
            }
        });

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] indicators = getResources().getStringArray(R.array.indicators);
                indicator = indicators[i];
                if(indicator.equals(selectedIndicator)){
                    change.setEnabled(false);
                }
                else{
                    change.setEnabled(true);
                }
                Log.d("Indicator",indicator);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        change = (Button)fragmentRootView.findViewById(R.id.change);
        change.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("Click","Click");
                loadVariousChart(indicator);
                change.setEnabled(false);
                selectedIndicator=indicator;
            }
        });
        return fragmentRootView;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getStockInfo();
    }

    private void getStockInfo(){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        final java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
        String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/priceS?stockSymbol="+symbol;
        JsonObjectRequest requestPrice = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("stockInfo",response.toString());
                try {
                    infoDetails[0] = response.getJSONObject("Meta Data").getString("2. Symbol");
                    JSONObject timeSeries = response.getJSONObject("Time Series (Daily)");
                    Iterator iterator = timeSeries.keys();
                    while(iterator.hasNext()){
                        String key = (String)iterator.next();
                        dates.add(key);
                    }
//                    Log.d("dates",dates.toString());
                    infoDetails[1] = df.format(Double.parseDouble(timeSeries.getJSONObject(dates.get(1)).getString("4. close")));
                    double change = Double.parseDouble(timeSeries.getJSONObject(dates.get(0)).getString("4. close"))-Double.parseDouble(timeSeries.getJSONObject(dates.get(1)).getString("4. close"));
                    double changePercent = (change/Double.parseDouble(timeSeries.getJSONObject(dates.get(1)).getString("4. close")))*100;
                    priceChange = df.format(change);
                    percentChange = df.format(changePercent);
                    if(change<0){
                        infoDetails[8]="0";
                    }
                    else{
                        infoDetails[8]="1";
                    }
                    infoDetails[2] = df.format(change)+" ("+df.format(changePercent)+"%) ";
                    infoDetails[3] = response.getJSONObject("Meta Data").getString("3. Last Refreshed")+" 16:00:00 EDT";
                    infoDetails[4] = df.format(Double.parseDouble(timeSeries.getJSONObject(dates.get(0)).getString("1. open")));
                    infoDetails[5] = df.format(Double.parseDouble(timeSeries.getJSONObject(dates.get(0)).getString("4. close")));
                    infoDetails[6] = df.format(Double.parseDouble(timeSeries.getJSONObject(dates.get(0)).getString("3. low")))+" - "+df.format(Double.parseDouble(timeSeries.getJSONObject(dates.get(0)).getString("2. high")));
                    infoDetails[7] = timeSeries.getJSONObject(dates.get(0)).getString("5. volume");
                    Log.d("change",infoDetails[2]);
                    Log.d("refresh",response.getJSONObject("Meta Data").get("3. Last Refreshed")+"16:00:00 EDT");
                    mAdapter = new stockDetailsAdapter(getContext(), infoDetails);
                    stockDetailsTable.setAdapter(mAdapter);
                    tableProBar.setEnabled(false);
                    tableProBar.setVisibility(getView().GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("TableError","Error");
                    error.setText("Fail to load data");
                    error.setTextColor(Color.BLACK);
                    error.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                    tableProBar.setVisibility(getView().GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError errors) {
                error.setText("Fail to load data");
                error.setTextColor(Color.BLACK);
                error.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                tableProBar.setVisibility(getView().GONE);
            }
        });
        requestQueue.add(requestPrice);
    }

    private void loadVariousChart(String indicator) {
        if(indicator.equals("Price")) {
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/hw9.html?symbol=" + symbol+"&type=price";
            System.out.println(url);
            WebSettings webSettings = variousChartWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            variousChartWebView.loadUrl(url);
        } else if(indicator.equals("SMA")){
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/hw9.html?symbol=" + symbol+"&type=SMA";
            System.out.println(url);
            WebSettings webSettings = variousChartWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            variousChartWebView.loadUrl(url);
        } else if(indicator.equals("EMA")){
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/hw9.html?symbol=" + symbol+"&type=EMA";
            System.out.println(url);
            WebSettings webSettings = variousChartWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            variousChartWebView.loadUrl(url);

        } else if(indicator.equals("BBANDS")){
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/hw9.html?symbol=" + symbol+"&type=BBANDS";
            System.out.println(url);
            WebSettings webSettings = variousChartWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            variousChartWebView.loadUrl(url);
        } else if(indicator.equals("RSI")){
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/hw9.html?symbol=" + symbol+"&type=RSI";
            System.out.println(url);
            WebSettings webSettings = variousChartWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            variousChartWebView.loadUrl(url);
        } else if(indicator.equals("ADX")){
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/hw9.html?symbol=" + symbol+"&type=ADX";
            System.out.println(url);
            WebSettings webSettings = variousChartWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            variousChartWebView.loadUrl(url);
        } else if(indicator.equals("STOCH")){
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/hw9.html?symbol=" + symbol+"&type=STOCH";
            System.out.println(url);
            WebSettings webSettings = variousChartWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            variousChartWebView.loadUrl(url);
        } else if(indicator.equals("CCI")){
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/hw9.html?symbol=" + symbol+"&type=CCI";
            System.out.println(url);
            WebSettings webSettings = variousChartWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            variousChartWebView.loadUrl(url);
        } else{
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/hw9.html?symbol=" + symbol+"&type=MACD";
            System.out.println(url);
            WebSettings webSettings = variousChartWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            variousChartWebView.loadUrl(url);
        }
    }
    public FacebookCallback<Sharer.Result> shareCallBack = new FacebookCallback<Sharer.Result>() {

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("Succeed","Fb");
            Toast.makeText(getActivity(), "Facebook Post Successful", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancel() {
            Log.d("failed","Fb");
            Toast.makeText(getActivity(), "Post Cancelled", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onError(FacebookException error) {
            Log.d("ERROR","Fb");
            Toast.makeText(getActivity(), "Failed To Post", Toast.LENGTH_SHORT).show();
        }
    };

}
