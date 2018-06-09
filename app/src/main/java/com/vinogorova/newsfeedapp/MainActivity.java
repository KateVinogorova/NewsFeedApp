package com.vinogorova.newsfeedapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks <List<Article>>{

    private static final String LOG_TAG = MainActivity.class.getName();
    private final static String URL_QUERY = "https://content.guardianapis.com/search";

   //Constant value for the news loader ID.
    private final int  ARTICLE_LOADER_ID = 1;

    /** Adapter for the list of articles */
    private NewsAdapter adapter;

    private TextView emptyStateTextView;

    /**
     * Create settings menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyStateTextView = findViewById(R.id.empty_view);

        // Find a reference to the {@link ListView} in the activity_main
        ListView listView = findViewById(R.id.list);
        listView.setEmptyView(emptyStateTextView);

        // Create a new {@link ArrayAdapter} of news
        adapter = new NewsAdapter(MainActivity.this, 0, new ArrayList<Article>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        listView.setAdapter(adapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected article.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Article currentArticle = (Article) adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getUrl());

                // Create a new intent to view the earthquake URI
                Intent intent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(intent);
            }
        });

        //Checking the network connection via ConnectivityManager
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();

        if (network != null && network.isConnected()){
            LoaderManager manager = getLoaderManager();
            manager.initLoader(ARTICLE_LOADER_ID, null, this);
            Log.e(LOG_TAG, "initLoader invoked");
        }else {
            ProgressBar progressBar = findViewById(R.id.indeterminateBar);
            progressBar.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_connection);
        }

    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        Log.e(LOG_TAG, "onCreateLoader executing");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Get value of section settings
        String section = sharedPrefs.getString(getString(R.string.settings_section_key),
                getString(R.string.settings_sectioin_default));
        //Get value of date settings from EditText views From and To
        String dateFrom = sharedPrefs.getString(getString(R.string.settings_date_from_text_view_key),
                SettingsActivity.getDateFrom());
        Log.e(LOG_TAG, "String dateFrom value is " + dateFrom);

        String dateTo = sharedPrefs.getString(getString(R.string.settings_date_to_text_view_key),
                SettingsActivity.getDateTo());
        Log.e(LOG_TAG, "String dateTo value is " + dateTo);

        //Create Uri according to settings
        Uri baseUri = Uri.parse(URL_QUERY);
        Uri.Builder builder = baseUri.buildUpon();

        if (dateFrom != null && dateTo != null) {

            if (section.equals(getString(R.string.settings_section_value_all))) {
                builder.appendQueryParameter("show-references", "author");
                builder.appendQueryParameter("from-date", dateFrom);
                builder.appendQueryParameter("to-date", dateTo);
                builder.appendQueryParameter("api-key", "a9233c14-976c-4a3f-afb3-5d342fcccd78");
            } else {
                builder.appendQueryParameter("show-references", "author");
                builder.appendQueryParameter("section", section);
                builder.appendQueryParameter("from-date", dateFrom);
                builder.appendQueryParameter("to-date", dateTo);
                builder.appendQueryParameter("api-key", "a9233c14-976c-4a3f-afb3-5d342fcccd78");
            }
        } else {

            if (section.equals(getString(R.string.settings_section_value_all))) {
                builder.appendQueryParameter("show-references", "author");
                builder.appendQueryParameter("api-key", "a9233c14-976c-4a3f-afb3-5d342fcccd78");
            } else {
                builder.appendQueryParameter("show-references", "author");
                builder.appendQueryParameter("section", section);
                builder.appendQueryParameter("api-key", "a9233c14-976c-4a3f-afb3-5d342fcccd78");
            }
        }

        // Create a new loader for the given URL
        Log.e(LOG_TAG, "URL query: " + builder.toString());
        return new NewsLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {

        //Set text for empty screen
        emptyStateTextView.setText(R.string.no_news);

        //Set ProgressBar of downloading data screen
        ProgressBar progressBar = findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(View.GONE);

        // Clear the adapter of previous articles data
        adapter.clear();
        Log.e(LOG_TAG, "onLoadFinished is executing, adapter is cleaned");

        // If there is a valid list of {@link Article}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()){
            adapter.addAll(data);
            Log.e(LOG_TAG, "onLoadFinished is executing, eartkquakes added to adapter");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Clear the adapter of articles data
        adapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Open SettingsActivity if settings menu is picked
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

