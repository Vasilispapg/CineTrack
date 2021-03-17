package com.pap.cinetrack;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class PaizetaiTwra extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>(); //all
    private ProgressBar progressBar;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        textView = findViewById(R.id.LOADINGGGGGG);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ParseAdapter(parseItems, this);

        recyclerView.setAdapter(adapter);

        loadItems(); //fortwnei tin selida

        GetInternetInfo(); //dedomena internet

        Refresh(); // leitoyrgia refresh


    }

    public TextView getTextView() {
        return textView;
    }

    private void GetInternetInfo(){
        ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = con.getActiveNetworkInfo(); //pairnei ta dedomena gia to internet
        if(networkinfo == null || !networkinfo.isConnected() || !networkinfo.isAvailable()) {

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.alert_network_connection);

            dialog.setCanceledOnTouchOutside(false);

            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

            Button btTryAgain = dialog.findViewById(R.id.retry);

            btTryAgain.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    recreate();
                }
            });

            dialog.show();
        }
    }

    private void Refresh(){
        swipeRefresh = findViewById(R.id.refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recreate();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void loadItems() {
        Content content = new Content();
        content.execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_item, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);

        // Get the search view and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); //Do not iconfy the widget; expand it by default

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase();
                ArrayList<ParseItem> newList = new ArrayList<>();

                for (ParseItem parseItem : parseItems) {
                    String title = parseItem.getTitle().toLowerCase();

                    // you can specify as many conditions as you like
                    if (title.contains(newText)) {
                        newList.add(parseItem);
                    }
                }
                // create method in adapter

                adapter.setFilter(newList);

                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);

        return true;

    }

    private class Content extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(PaizetaiTwra.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.INVISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(PaizetaiTwra.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged(); //ta emfanizei
            swipeRefresh.setRefreshing(false);

        }


        @Override
        protected Void doInBackground(Void... voids) {
            FetchMovies();
            return null;
        }
    }

    private void FetchMovies(){
        try {
            System.out.println("\n\nMetropol Cinema\n\n");

            //metropol cinema
            Document doc=null;
            String url = "https://www.cinemametropol.gr/";
            doc = Jsoup.connect(url).get();
            System.out.println(doc.isBlock());

            System.out.println("GAWM TO SPITI");

            Elements data = doc.select("div.col-sm-4");
            int size = data.size();
            for (int i = 0; i < size; i++) {
                String imgUrl = data.select("div.gallery-item")
                        .select("img")
                        .eq(i)
                        .attr("src");

                 String title = data.select("div.gallery-item")
                        .select("p")
                        .eq(i)
                        .text();
                if(title.contains("_")){
                    title=null;
                }
                String detailUrl = data.select("div.gallery-item")
                        .select("a")
                        .eq(i)
                        .attr("href");

                parseItems.add(new ParseItem(imgUrl, title, detailUrl));
                Log.d("items", "img: " + imgUrl + " . title: " + title + "detail: "+ detailUrl);
            }

            //palace cinema
            System.out.println("\n\nPallace Cinema\n\n");

            url = "https://www.cinepallas.gr/index.php/tainies/tainies-evdomadas";

            doc = Jsoup.connect(url).get();
            if(!doc.isBlock()) {
                data = doc.select("div.leading.clearfix");
                size = data.size();
                for (int i = 0; i < size; i++) {
                    String title = data.select("h2").eq(i).text();

                    String detailUrl = data.select("h2").select("a").eq(i).attr("href");

                    detailUrl = "https://www.cinepallas.gr" + detailUrl;

                    Document doc2 = Jsoup.connect(detailUrl).get();
                    Elements data2 = doc2.select("div.item-page");

                    String imgUrl = data2.select("p").select("img").attr("src");
                    imgUrl = "https://www.cinepallas.gr" + imgUrl;

                    parseItems.add(new ParseItem(imgUrl, title, detailUrl));
                    Log.d("items", "img: " + imgUrl + " . title: " + title + "detail: " + detailUrl);

                }
            }


            TextView text = getTextView();
            Color c = new Color();
            text.setTextColor(c.WHITE);
            text.setVisibility(View.INVISIBLE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
