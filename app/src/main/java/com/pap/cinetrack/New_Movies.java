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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class New_Movies extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Calendar c = Calendar.getInstance();
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>(); //all
    private ProgressBar progressBar;
    private int year=0,month= 0;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_movies);
        textView = findViewById(R.id.LOADINGGGGGG);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ParseAdapter(parseItems, this);

        recyclerView.setAdapter(adapter);

        loadItems(); //fortwnei tin selida

        GetInternetInfo(); //dedomena internet

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


    private void loadItems() {
        Content content = new Content();
        content.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_item, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        MenuItem c = menu.findItem(R.id.coming_soon);
        c.setVisible(false);
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

    public TextView getTextView() {
        return textView;
    }

    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            year=c.get(Calendar.YEAR);
            month=c.get(Calendar.MONTH)+1; //jan=0 dec==11
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(New_Movies.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.INVISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(New_Movies.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged(); //ta emfanizei
        }


        @Override
        protected Void doInBackground(Void... voids) {
            FetchMovies();
            return null;
        }
    }

    private void FetchMovies(){
        try {
            int yearexp=c.get(Calendar.YEAR)+1;
            while(year!=yearexp) {
                if(year==yearexp-1 && month==12) {
                    TextView t = getTextView();
                    Color c = new Color();
                    t.setTextColor(c.WHITE);
                    t.setVisibility(View.INVISIBLE);
                }
                String link;
                if(month>=10) {
                    link = "https://www.imdb.com/movies-coming-soon/";
                    link += year + "-" + month;
                }
                else{
                    link = "https://www.imdb.com/movies-coming-soon/";
                    link += year + "-0" + month;
                }
                System.out.println("LINK = " + link);
                Document doc=null;
                while(true) {
                    doc = Jsoup.connect(link).get();
                    if(!doc.isBlock()){
                        break;
                    }
                }

                int nextmontht = month + 1;
                month++;
                if (nextmontht == 13) {
                    month = 1;
                    year++;
                }
                Elements data = doc.select("div.list_item.odd"); //odd
                if(data.isEmpty()){
                    continue;
                }
                else {
                    int size = data.size();
                    for (int i = 0; i < size; i++) {
                        String imgUrl = data.select("div.image")
                                .select("img")
                                .eq(i)
                                .attr("src");

                        String title = data.select("td.overview-top")
                                .select("h4").select("a")
                                .eq(i)
                                .attr("title");

                        String detailUrl = data.select("div.image")
                                .select("a")
                                .eq(i)
                                .attr("href");

                        detailUrl = "https://www.imdb.com" + detailUrl;
                        parseItems.add(new ParseItem(imgUrl, title, detailUrl));
                        Log.d("items", "img: " + imgUrl + " . title: " + title + "detail: " + detailUrl);
                    }

                }
                data = doc.select("div.list_item.even"); //even
                if(data.isEmpty()) {
                    continue;
                }
                else {
                    int size=data.size();
                    for (int i = 0; i < size; i++) {
                        String imgUrl = data.select("div.image")
                                .select("img")
                                .eq(i)
                                .attr("src");

                        String title = data.select("td.overview-top")
                                .select("h4").select("a")
                                .eq(i)
                                .attr("title");

                        String detailUrl = data.select("div.image")
                                .select("a")
                                .eq(i)
                                .attr("href");
                        detailUrl="https://www.imdb.com"+detailUrl;

                        parseItems.add(new ParseItem(imgUrl, title, detailUrl));
                        Log.d("items", "img: " + imgUrl + " . title: " + title + "detail: " + detailUrl);
                    }
                }
                }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
