package com.example.wuchaoyu.csci571;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class historical extends android.support.v4.app.Fragment {
    public String symbol;
    private WebView historicalChartWebView;
    private ProgressBar progressBar;

    public historical() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StockInfoContent parentActivity = (StockInfoContent) getActivity();
        this.symbol = parentActivity.symbol;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentRootView = inflater.inflate(R.layout.fragment_historical, container, false);
        historicalChartWebView = (WebView) fragmentRootView.findViewById(R.id.historicalGraph);
        progressBar = (ProgressBar)fragmentRootView.findViewById(R.id.historicalBar);
        progressBar.setVisibility(View.VISIBLE);
        loadHistoricalChart();
        return fragmentRootView;
    }
    private void loadHistoricalChart() {
        String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/hw9Histoy.html?symbol="+symbol;
        System.out.println(url);
        WebSettings webSettings = historicalChartWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        try {
            historicalChartWebView.loadUrl(url);
        }
        catch(Exception e) {
            Log.d("historical","error");
        }
        historicalChartWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
