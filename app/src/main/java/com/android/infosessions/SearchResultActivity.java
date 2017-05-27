package com.android.infosessions;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.android.infosessions.data.DbProvider;
import com.android.infosessions.data.SessionContract.SessionEntry;

public class SearchResultActivity extends AppCompatActivity {

    private TextView mTextView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mTextView = (TextView) findViewById(R.id.text);
        mListView = (ListView) findViewById(R.id.list);
        Log.d("LOG_TAG", "create search result");
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show word
            Intent wordIntent = new Intent(this, DetailActivity.class);
            wordIntent.setData(intent.getData());
            startActivity(wordIntent);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    /**
     * Searches the dictionary and displays results for the given query.
     * @param query The search query
     */
    private void showResults(String query) {

        Cursor cursor = managedQuery(SessionEntry.CONTENT_URI, null, null,
                new String[] {query}, null);

        if (cursor == null) {
            // There are no results
            mTextView.setText(getString(R.string.no_results, new Object[] {query}));
        } else {
            // Display the number of results
            int count = cursor.getCount();
            String countString = getResources().getQuantityString(R.plurals.search_results,
                    count, new Object[] {count, query});
            mTextView.setText(countString);

            // Specify the columns we want to display in the result
            String[] from = {
                    SessionEntry._ID,
                    SessionEntry.COLUMN_SESSION_EMPLOYER,/*
                    SessionEntry.COLUMN_SESSION_START_TIME,
                    SessionEntry.COLUMN_SESSION_END_TIME,
                    SessionEntry.COLUMN_SESSION_DATE,
                    SessionEntry.COLUMN_SESSION_DAY,
                    SessionEntry.COLUMN_SESSION_MILLISECONDS,
                    SessionEntry.COLUMN_SESSION_WEBSITE,
                    SessionEntry.COLUMN_SESSION_LINK,*/
                    SessionEntry.COLUMN_SESSION_DESCRIPTION,/*
                    SessionEntry.COLUMN_SESSION_BUILDING_CODE,
                    SessionEntry.COLUMN_SESSION_BUILDING_NAME,
                    SessionEntry.COLUMN_SESSION_BUILDING_ROOM,
                    SessionEntry.COLUMN_SESSION_MAP_URL,*/
                    SessionEntry.COLUMN_SESSION_LOGO};

            //String[] from = new String[] { PetDictionaryDatabase.KEY_WORD,
              //      DictionaryDatabase.KEY_DEFINITION };

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.employer, R.id.description, R.id.employer_logo};

            // Create a simple cursor adapter for the definitions and apply them to the ListView
            SimpleCursorAdapter words = new SimpleCursorAdapter(this,
                    R.layout.sessions_list, cursor, from, to);
            mListView.setAdapter(words);

            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Build the Intent used to open WordActivity with a specific word Uri
                    Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
                    Uri data = Uri.withAppendedPath(SessionEntry.CONTENT_URI,
                            String.valueOf(id));
                    detailIntent.setData(data);
                    startActivity(detailIntent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            default:
                return false;
        }
    }
}
