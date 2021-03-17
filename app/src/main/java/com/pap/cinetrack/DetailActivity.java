package com.pap.cinetrack;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleTExtView, detailTextView,LeptomeriesTextView,novideo;
    private String detailStringLept="";
    private String detailStringDesc="";
    private YouTubePlayerView youTubePlayerView;
    private String videoId;
    private int token=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        imageView = findViewById(R.id.imageView);
        titleTExtView = findViewById(R.id.mellontikes_kykl);
        novideo= findViewById(R.id.novideo);


        detailTextView = findViewById(R.id.detailTextView);
        LeptomeriesTextView = findViewById(R.id.Leptomeries);
        titleTExtView.setText(getIntent().getStringExtra("title"));
        Picasso.get().load(getIntent().getStringExtra("image")).into(imageView);

        Content content = new Content();
        content.execute();

    }

    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            detailTextView.setText(detailStringDesc);
            LeptomeriesTextView.setText(detailStringLept);
            if(token!=3){
                novideo.setVisibility(View.VISIBLE);
                youTubePlayerView.release();
            }

        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            Document doc=null;
            Elements data=null;
            try {
                String detailUrl = getIntent().getStringExtra("detailUrl");
                System.out.println(detailUrl);
                videoId= FetchMovieId();
                if(videoId!=null && detailUrl.contains("imdb")){
                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                            youTubePlayer.cueVideo(videoId, 0);
                        }
                    });
                }

                    if(detailUrl.contains("cinemametropol"))
                        token=1;
                    else if(detailUrl.contains("cinepallas"))
                        token=2;
                    else if(detailUrl.contains("imdb"))
                        token=3;

                switch (token){
                    case 1:
                        detailStringLept="Cinema: METROPOL\n\n";
                        doc = Jsoup.connect(detailUrl).get();
                        System.out.println("MPIKA");
                        //pairnw tiws perigrafes

                        data = doc.select("div.col-sm-6.col-md-8").select("p"); //pairnw poses paragrafous exei
                        int size = data.size();
                        String[] temp = new String[size];

                        for (int i = 0; i < size; i++) {
                            temp[i] = data.eq(i).text()+"\n"; //edw ta vazw katheta gia na ta emfanizw se grammes
                            if(i==size-1)
                                temp[i] = data.eq(i).text();
                        }

                        for (int i = 0; i < size; i++) {
                            detailStringLept += temp[i] + "\n"; //edw ta ennwnw
                        }


                        data = doc.select("p.movie__describe").eq(0); //pairnw tin perigrafi apo edw
                        if (!data.isEmpty()) {
                            detailStringDesc += "\n" + "Συντομη Περιγραφη:\n\n";

                            detailStringDesc += data.text();
                            detailStringDesc += "\n\n" + "Υπόθεση:\n\n";

                            data = doc.select("p.movie__describe").eq(1); //pairnw tin ypothesi apo edw
                            detailStringDesc += data.text();
                        }
                        else{
                            detailStringDesc="\n\n"+"Empty";
                        }
                        break;
                    case 2:
                        doc = Jsoup.connect(detailUrl).get();

                        detailStringLept="Cinema: ΠΑΛΛΑΣ\n";
                        data = doc.select("div.item-page").select("p"); //pairnw poses paragrafous exei
                        temp = new String[data.size()];

                        for (int i = 0; i < data.size(); i++) {
                            temp[i] = data.eq(i).text(); //edw ta vazw katheta gia na ta emfanizw se grammes
                        }
                        boolean flag=false;
                        for (int i = 0; i < data.size(); i++) {
                            if(temp[i].contains("Υπόθεση")){
                                flag=true;
                            }
                            if(flag){
                                if(temp[i].contains("Υπόθεση")){
                                    detailStringDesc+=temp[i] + "\n\n";
                                }
                                else{
                                    detailStringDesc+=temp[i] + "\n";
                                }

                            }
                            else
                                detailStringLept += temp[i] + "\n";//edw ta ennwnw
                        }

                        detailStringDesc=detailStringDesc.substring(0,detailStringDesc.length()-15);
                        break;
                    case 3:
                        doc = Jsoup.connect(detailUrl).get();
                        data = doc.select("div.plot_summary");

                        detailStringDesc="Υπόθεση\n\n";
                        detailStringDesc+=data.select("div.summary_text").text()+"\n\n";
                        data = doc.select("div.credit_summary_item");
                        for(int i =0;i<data.size();i++){
                            String temp1=data.eq(i).text()+"\n";
                            if(temp1.contains("more credit ")){
                                temp1=temp1.substring(0,temp1.length()-18);
                            }
                            else if(temp1.contains("more credits ")){
                                temp1=temp1.substring(0,temp1.length()-19);
                            }
                            detailStringLept+=temp1+"\n";

                        }
                        detailStringLept=detailStringLept.substring(0,detailStringLept.length()-26);


//                    Details
                        detailStringDesc+="Λεπτομέριες\n\n";
                        data= doc.select("#titleDetails").select("div.txt-block");
                        size =data.size();
                        temp = new String[data.size()];

                        for(int i =0;i<size;i++){
                            temp[i]=data.eq(i).text();
                            if(temp[i].contains("See more"))
                                temp[i]=temp[i].substring(0,temp[i].length()-10);
                            else if(temp[i].contains("Show more on IMDbPro"))
                                temp[i]=null;
                            else if(temp[i].contains("Runtime"))
                                break;
                        }
                        for(int i =0;i<temp.length;i++){
                            if(temp[i]!=null)
                                detailStringDesc += temp[i] + "\n";
                        }
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String FetchMovieId(){

        if(titleTExtView==null) {
            novideo.setVisibility(View.VISIBLE);
            youTubePlayerView.release();
            return null;
        }
            try {
                videoId=null;
                String titleforsearch = "" + titleTExtView.getText();//tolink na ftiajw
                if (titleforsearch.contains(" ")) {
                    titleforsearch = titleforsearch.replace(" ", "+"); //svinei ta kena
                    titleforsearch = titleforsearch.replace("(", "");
                    titleforsearch = titleforsearch.replace(")", "");
                }

                String keyword = titleforsearch + "+official+trailer";
                String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&q=" + keyword + "&key=AIzaSyCqyv8Pk35hnlW0ifpKEVSxDfA_8uPQ5kM"; //vriskw to ID toy video

                Document doc = Jsoup.connect(url).userAgent("Mozilla")
                        .ignoreContentType(true)
                        .get();

                String getJson[] = doc.text().split(":");

                for (int i = 0; i < getJson.length; i++) {
                    if (getJson[i].contains("videoId")) {
                        videoId = getJson[i + 1]; //na vrw to video id
                        break;
                    }
                }

                String v[] = videoId.split("\""); //to spaw se " "
                videoId = v[1]; //pairnw monoo to ID giati itan "ViSXtpv6XbU" },snippet

                videoId = videoId.replace("\"", "");
                System.out.println("Video ID = " + videoId);
            }catch (IOException e) {
                e.printStackTrace();
            }

       return videoId;
    }



}
