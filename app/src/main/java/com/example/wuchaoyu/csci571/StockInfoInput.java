package com.example.wuchaoyu.csci571;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextMenu;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.view.MenuItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Switch;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Timer;
import java.util.TreeSet;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.CompoundButton;
import java.util.TimerTask;

public class StockInfoInput extends AppCompatActivity {
    AutoCompleteTextView textView;
    ProgressBar autoProBar;
    ProgressBar loadFavorite;
    ListView favoriteList;
    private ArrayList<String> favoriteListSymbols;
    private ArrayList<String> favoriteListPrices;
    private ArrayList<String> favoriteListChanges;
    private ArrayList<String> favoriteListPercentChanges;
    private ArrayList<String> dates = new ArrayList<String>();
    private ArrayList<String> favoriteStocks;
    private Spinner sortOptions;
    private Spinner upOrDown;
    ArrayList<String> optionsSort;
    ArrayList<String> order;
    FavoriteListAdapter mAdapter;
    FavoriteListAdapterNew nAdapter;
    private String option;
    private String upDown;
    Button refresh;
    Switch autoRefresh;
    Timer timer;
    Timer lala;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stock_info_input);
        optionsSort = new ArrayList<>(Arrays.asList("Sort By","Default","Symbol","Price","Change","Change(%)"));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        sortOptions = (Spinner)findViewById(R.id.sortOption);
        upOrDown = (Spinner)findViewById(R.id.upOrDown);
        autoProBar = (ProgressBar)findViewById(R.id.autoProBar);
        loadFavorite = (ProgressBar)findViewById(R.id.loadFavorite);
        autoProBar.setVisibility(View.GONE);
        refresh =(Button)findViewById(R.id.refresh);
        autoRefresh = (Switch)findViewById(R.id.auto);

        autoRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Log.d("switch","true");
                    loadFavorite.setVisibility(View.VISIBLE);
                    timer = new Timer();
                    lala = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {@Override public void run() {refreshFavoriteStocksList();}}, 0, 5000);
//                    lala.scheduleAtFixedRate(new TimerTask() {@Override public void run() {loadFavorite.setVisibility(View.GONE);}}, 0, 2000);
                }
                else{
                    Log.d("switch","false");
                    timer.cancel();
//                    lala.cancel();
                }
            }
        });

        order = new ArrayList<>(Arrays.asList("Order","Ascending","Descending"));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_options,optionsSort){
            @Override
            public boolean isEnabled(int position){
                if(position == 0||optionsSort.get(position).equals(option))
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0||optionsSort.get(position).equals(option)) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        final ArrayAdapter<String> spinnerArray = new ArrayAdapter<String>(this,R.layout.spinner_order,order){
            @Override
            public boolean isEnabled(int position){
                if(position == 0||order.get(position).equals(upDown))
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if(position==0||order.get(position).equals(upDown)) {
                    // Set the disable item text color
                    textView.setTextColor(Color.GRAY);
                }
                else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_options);
        sortOptions.setAdapter(spinnerArrayAdapter);
        spinnerArray.setDropDownViewResource(R.layout.spinner_order);
        upOrDown.setAdapter(spinnerArray);



        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                refreshFavoriteStocksList();
            }
        });

        sortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] indicators = getResources().getStringArray(R.array.sortOptions);
                option = indicators[i];
                if(nAdapter!=null) {
                    nAdapter.sort(option, upDown);
                }
                Log.d("Option",option);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        upOrDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] indicators = getResources().getStringArray(R.array.upOrDown);
                upDown = indicators[i];
                if(nAdapter!=null) {
                    nAdapter.sort(option, upDown);
                }
                Log.d("Order",upDown);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        autoComplete();
    }

    @Override
    protected void onResume() {
        super.onResume();
        favoriteListInit();
        refreshFavoriteStocksList();
        sortOptions.setSelection(0);
        upOrDown.setSelection(0);
    }
    private void favoriteListInit(){
        favoriteList = (ListView)findViewById(R.id.favoriteList);
        TreeSet<String> favoriteItems = new TreeSet<>(getSharedPreferences("default", MODE_PRIVATE)
                .getStringSet("favorite", new TreeSet<String>()));
        String position = new String(getSharedPreferences("default",MODE_PRIVATE).getString("position",new String()));
        JSONObject test = null;
        try {
            test = new JSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Iterator<String> iterator = favoriteItems.iterator();
        favoriteListSymbols = new ArrayList<String>();
        favoriteListPrices = new ArrayList<String>();
        favoriteListChanges = new ArrayList<String>();
        favoriteListPercentChanges = new ArrayList<String>();
        favoriteStocks = new ArrayList<String>();
        if(test!=null) {
            Iterator testIter = test.keys();
            while (testIter.hasNext()) {
                String symbol = (String) testIter.next();
//                Log.d("this is JSON", key);
                favoriteListSymbols.add(symbol);
            }
        }

//        while (iterator.hasNext()) {
//            String symbol = iterator.next();
//            favoriteListSymbols.add(symbol);
//            Log.d("stocks",symbol);
//        }
        for (int i = 0; i < favoriteListSymbols.size(); i++) {
            favoriteListPrices.add("");
            favoriteListChanges.add("");
            favoriteListPercentChanges.add("");
            favoriteStocks.add("");
        }
        favoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getQuote(favoriteListSymbols.get(position));
            }
        });
        registerForContextMenu(favoriteList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Remove from Favorites?");
        menu.add(0, v.getId(), 0, "Yes");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "No");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getTitle()=="Yes"){
            Toast.makeText(getApplicationContext(),"Selected Yes",Toast.LENGTH_LONG).show();
            SharedPreferences sharedPreferences = this.getSharedPreferences("default", Context.MODE_PRIVATE);
            TreeSet<String> favorites = new TreeSet<>(sharedPreferences
                    .getStringSet("favorite", new TreeSet<String>()));
            String positions = sharedPreferences.getString("position", new String());
            JSONObject test = null;
            try {
                test = new JSONObject(positions);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.d("PositiooIndex",info.position+"");
            favoriteListSymbols = nAdapter.returnSortSymbol();
            Log.d("Position",favoriteListSymbols.get(info.position));
            favorites.remove(favoriteListSymbols.get(info.position));
            test.remove(favoriteListSymbols.get(info.position));
            editor.putString("position",test.toString());
            editor.putStringSet("favorite", favorites);
            editor.apply();
            favoriteListSymbols.remove(info.position);
            nAdapter.remove(nAdapter.getItem(info.position));
            nAdapter.notifyDataSetChanged();
            favoriteList.invalidateViews();
        }
        else{
            Toast.makeText(getApplicationContext(),"Selected No",Toast.LENGTH_LONG).show();
        }
        return true;
    }


    public void refreshFavoriteStocksList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadFavorite.setVisibility(View.VISIBLE);
            }
        });
        for (int i = 0; i < favoriteListSymbols.size(); i++) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/priceS?stockSymbol="+favoriteListSymbols.get(i);
//            Log.d("refreshFavorite",url);
            JsonObjectRequest requestPrice = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject timeSeries = null;
                    try {
                        timeSeries = response.getJSONObject("Time Series (Daily)");
                        Iterator iterator = timeSeries.keys();
                        while(iterator.hasNext()){
                            String key = (String)iterator.next();
                            dates.add(key);
                        }
                        double change = Double.parseDouble(timeSeries.getJSONObject(dates.get(0)).getString("4. close"))-Double.parseDouble(timeSeries.getJSONObject(dates.get(1)).getString("4. close"));
                        double changePercent = (change/Double.parseDouble(timeSeries.getJSONObject(dates.get(1)).getString("4. close")))*100;
                        int index = favoriteListSymbols.indexOf(response.getJSONObject("Meta Data").getString("2. Symbol"));
                        String price = df.format(Double.parseDouble(timeSeries.getJSONObject(dates.get(0)).getString("4. close")));
                        String changeStr = df.format(change);
                        String changePerStr = df.format(changePercent);
                        favoriteListPrices.set(index,price);
                        favoriteListChanges.set(index,changeStr);
                        favoriteListPercentChanges.set(index,changePerStr);
                        favoriteStocks.set(index,favoriteListSymbols.get(index)+","+price+","+changeStr+","+changePerStr);
                        Log.d("newHope",favoriteStocks.get(index));
                        nAdapter = new FavoriteListAdapterNew(getApplicationContext(),R.layout.favorite_list,favoriteStocks);
                        if(index==favoriteListSymbols.size()-1){
                            loadFavorite.setVisibility(View.GONE);
                        }
                        favoriteList.setAdapter(nAdapter);
//                        mAdapter = new FavoriteListAdapter(getApplicationContext(), favoriteListSymbols, favoriteListPrices, favoriteListChanges, favoriteListPercentChanges);
//                        favoriteList.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(requestPrice);
        }
    }

    public void getQuote(View V){
        Log.d("changePage","chaning");
        if(textView.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(),"Please enter a stock name or symbol",Toast.LENGTH_LONG).show();
        }else {
            String symbol = textView.getText().toString().split(" - ")[0];
            Intent intent = new Intent(StockInfoInput.this, StockInfoContent.class);
            intent.putExtra("Symbol", symbol);
            Log.d("Chosen Symbol", symbol);
            startActivity(intent);
        }
    }

    public void getQuote(String Symbol){
        Log.d("changePage","chaning");
        String symbol = Symbol;
        Intent intent = new Intent(StockInfoInput.this, StockInfoContent.class);
        intent.putExtra("Symbol", symbol);
        Log.d("Chosen Symbol",symbol);
        startActivity(intent);
    }

    public void clear(View V){
        textView.setText("");
        autoProBar.setVisibility(View.GONE);
    }


    private void autoComplete(){
        autoProBar.setVisibility(View.GONE);
        textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                autoProBar.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                autoProBar.setVisibility(View.VISIBLE);
                if(textView.getText().toString().equals("")){
                    autoProBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("Name",editable.toString());
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String url = "http://csci571hw8.us-east-2.elasticbeanstalk.com/autoComplete?stockSymbol="+editable.toString();
                JsonArrayRequest test = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        autoProBar.setVisibility(View.GONE);
                        ArrayList<String> results = new ArrayList<String>();
                        Log.d("test", response.toString());
                        int count = Math.min(response.length(),5);
                        for(int i=0;i<count;i++){
                            try {
                                JSONObject stock = response.getJSONObject(i);
                                String stockName = stock.getString("Symbol");
                                String stockCompany = stock.getString("Name");
                                String stockExchange = stock.getString("Exchange");
                                String result = stockName+" - "+stockCompany+" ("+stockExchange+")";
                                results.add(result);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("results", results.toString());
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line,results);
                        textView.setAdapter(adapter);
                        textView.setThreshold(0);
                        textView.setValidator(new AutoCompleteTextView.Validator() {
                            @Override
                            public boolean isValid (CharSequence text){
                                //some logic here returns true or false based on if the text is validated
                                if(text.toString().trim().isEmpty()){
                                    return false;
                                } else{
                                    return true;
                                }
                            }

                            @Override
                            public CharSequence fixText (CharSequence invalidText){
                                Toast.makeText(getApplicationContext(),"Please enter a stock name or symbol",Toast.LENGTH_LONG).show();
                                return "This is what I want";
                            }
                        });

                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(test);

            }
        });
    }
}
