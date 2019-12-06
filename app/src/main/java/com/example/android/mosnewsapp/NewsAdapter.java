package com.example.android.mosnewsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    public static final String LOG_TAG = NewsAdapter.class.getSimpleName();
    private List<News> mNews;
    private Context mContext;
    private String mURL;

    public NewsAdapter(Context context, List<News> news) {
        mContext = context;
        mNews = news;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View listView = inflater.inflate(R.layout.list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(mContext, listView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder viewHolder, int position) {
        News news = mNews.get(position);

        TextView newsTitleTextView = viewHolder.newsTitle;
        TextView newsSectionTextView = viewHolder.newsSection;
        TextView newsPublicationDateTextView = viewHolder.newsPublicationDate;
        TextView newsPublicationTimeTextView = viewHolder.newsPublicationTime;

        newsTitleTextView.setText(news.getTitle());
        newsSectionTextView.setText(news.getSectionName());
        newsPublicationDateTextView.setText(convertDateFormat(news.getPublicationDate()));
        newsPublicationTimeTextView.setText(convertTimeFormat(news.getPublicationDate()));
    }

    public String convertDateFormat(String input) {
        input = input.substring(0, input.length() - 1);
        String oldFormat = "yyyy-MM-dd'T'HH:mm:ss";
        String newFormat = "dd/MM/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(oldFormat);
        SimpleDateFormat outputFormat = new SimpleDateFormat(newFormat);
        Date date = null;
        String output = "";
        try {
            date = inputFormat.parse(input);
            output = outputFormat.format(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "DateTime parse exception: " + e);
        }
        return output;
    }

    public String convertTimeFormat(String input) {
        input = input.substring(0, input.length() - 1);
        String oldFormat = "yyyy-MM-dd'T'HH:mm:ss";
        String newFormat = "HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(oldFormat);
        SimpleDateFormat outputFormat = new SimpleDateFormat(newFormat);
        Date date = null;
        String output = "";
        try {
            date = inputFormat.parse(input);
            output = outputFormat.format(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "DateTime parse exception: " + e);
        }
        return output;
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    public void addAll(List<News> newsItemList) {
        mNews.clear();
        mNews.addAll(newsItemList);
        notifyDataSetChanged();
    }

    public void clearAll() {
        mNews.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView newsTitle;
        public TextView newsSection;
        public TextView newsPublicationDate;
        public TextView newsPublicationTime;
        private Context context;

        public ViewHolder(Context context, View itemView) {

            super(itemView);
            this.context = context;

            itemView.setOnClickListener(this);

            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            newsSection = (TextView) itemView.findViewById(R.id.news_section);
            newsPublicationDate = (TextView) itemView.findViewById(R.id.news_publication_date);
            newsPublicationTime = (TextView) itemView.findViewById(R.id.news_publication_time);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            News news = mNews.get(position);

            mURL = news.getWebURL();

            Uri newsURI = Uri.parse(mURL);
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsURI);
            context.startActivity(websiteIntent);
        }
    }


}
