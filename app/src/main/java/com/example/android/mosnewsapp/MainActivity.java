package com.example.android.mosnewsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<List<News>> {


    private static final int NEWS_LOADER_ID = 1;

    private static final String API_INITIAL_QUERY = "https://content.guardianapis.com/search?";

    private static LoaderManager loaderManager;

    private static NewsAdapter newsAdapter;

    TextView messageTextView;

    ProgressBar progressBar;

    SwipeRefreshLayout swipeRefreshLayout;

    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.main_screen_title));

        messageTextView = (TextView) findViewById(R.id.message_textView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerView();
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            messageTextView.setText(getString(R.string.message_fetching));

            initializeLoaderAndAdapter();

        } else {
            messageTextView.setText(getString(R.string.message_no_internet));
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String searchQuery = sharedPreferences.getString(getString(R.string
                .settings_search_query_key), getString(R.string.settings_search_query_default));

        String orderBy = sharedPreferences.getString(getString(R.string
                .settings_order_by_list_key), getString(R.string.settings_order_by_list_default));

        Uri baseIri = Uri.parse(API_INITIAL_QUERY);
        Uri.Builder uriBuilder = baseIri.buildUpon();

        uriBuilder.appendQueryParameter("q", searchQuery);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "test");
        Log.v("MainActivity", "Uri: " + uriBuilder);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsItems) {
        if (newsItems != null && !newsItems.isEmpty()) {
            newsAdapter.addAll(newsItems);
            progressBar.setVisibility(View.GONE);
            messageTextView.setText("");

        } else {
            messageTextView.setText(getString(R.string.message_no_articles));
        }
        Log.v("MainActivity", "Loader completed operation!");
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clearAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.menu_refresh) {
            refreshRecyclerView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initializeLoaderAndAdapter() {
        loaderManager = getLoaderManager();

        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void refreshRecyclerView() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        Log.v("MainActivity", "networkInfo: " + networkInfo);

        if (networkInfo != null && networkInfo.isConnected()) {
            messageTextView.setText(getString(R.string.message_refreshing));
            progressBar.setVisibility(View.VISIBLE);

            if (newsAdapter != null) {

                newsAdapter.clearAll();
            }
            if (loaderManager != null) {
                loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
                swipeRefreshLayout.setRefreshing(false);
            } else {
                initializeLoaderAndAdapter();
                swipeRefreshLayout.setRefreshing(false);
            }

        } else {

            if (newsAdapter != null) {
                newsAdapter.clearAll();
            }

            messageTextView.setText(getString(R.string.message_no_internet));
            swipeRefreshLayout.setRefreshing(false);
        }

    }
}