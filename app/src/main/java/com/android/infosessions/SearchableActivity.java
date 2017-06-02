package com.android.infosessions;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.infosessions.data.FilterContract.FilterEntry;
import com.android.infosessions.data.SessionContract.SessionEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchableActivity extends AppCompatActivity implements android.widget.SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private SessionCursorAdapter mCursorAdapter;
    private String mQuery = "";
    private ListView sessionsListView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sessions_list);
        setTitle("Search");
        sessionsListView = (ListView) findViewById(R.id.list);
        textView = (TextView) findViewById(R.id.text);
        textView.setVisibility(View.VISIBLE);

        mCursorAdapter = new SessionCursorAdapter(this, null);
        sessionsListView.setAdapter(mCursorAdapter);

        sessionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchableActivity.this, DetailActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(SessionEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(60, 60);

        Button btnFilter = new Button(this);
        btnFilter.setBackgroundResource(R.drawable.ic_filter_list_white_24dp);

        android.widget.SearchView searchView = (android.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search employer...");
        searchView.setMaxWidth(Integer.MAX_VALUE);

        ((LinearLayout) searchView.getChildAt(0)).addView(btnFilter, layoutParams);
        ((LinearLayout) searchView.getChildAt(0)).setGravity(Gravity.CENTER);
        (searchView.getChildAt(0)).setPadding(0, 0, 50, 0);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(SearchableActivity.this, FilterActivity.class);
                startActivity(intent2);
            }
        });

        // Assumes current activity is the searchable activity
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                SessionEntry._ID,
                SessionEntry.COLUMN_SESSION_EMPLOYER,
                SessionEntry.COLUMN_SESSION_START_TIME,
                SessionEntry.COLUMN_SESSION_END_TIME,
                SessionEntry.COLUMN_SESSION_DATE,
                SessionEntry.COLUMN_SESSION_DAY,
                SessionEntry.COLUMN_SESSION_MILLISECONDS,
                SessionEntry.COLUMN_SESSION_WEBSITE,
                SessionEntry.COLUMN_SESSION_LINK,
                SessionEntry.COLUMN_SESSION_DESCRIPTION,
                SessionEntry.COLUMN_SESSION_BUILDING_CODE,
                SessionEntry.COLUMN_SESSION_BUILDING_NAME,
                SessionEntry.COLUMN_SESSION_BUILDING_ROOM,
                SessionEntry.COLUMN_SESSION_MAP_URL,
                SessionEntry.COLUMN_SESSION_LOGO,
                SessionEntry.COLUMN_SESSION_AUDIENCE};

        if(mQuery.trim().isEmpty()) {
            return new CursorLoader(this,
                    SessionEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }

        String[] p = {
                FilterEntry._ID,
                FilterEntry.COLUMN_FILTER_KEY,
                FilterEntry.COLUMN_FILTER_IS_CODE,
                FilterEntry.COLUMN_FILTER_VALUE};

        String[] a = { String.valueOf(FilterEntry.VALUE_CHECKED) };

        Cursor filterCursor = getContentResolver().query(FilterEntry.CONTENT_URI, p,
                FilterEntry.COLUMN_FILTER_VALUE + "=?",
                a, null);

        ArrayList<String> filters = new ArrayList<>();

        String selection = SessionEntry.COLUMN_SESSION_EMPLOYER + " LIKE ? ";

        while(filterCursor.moveToNext()) {
            String audience = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_KEY));
            filters.add(audience);
            selection += " OR " + SessionEntry.COLUMN_SESSION_AUDIENCE + " LIKE ? ";
        }


        String[] selectionArgs = new String[1+filters.size()];
        selectionArgs[0] = ("%" + mQuery + "%") ;
        for(int i = 1; i < filters.size(); i++) {
            selectionArgs[i] = ("%" + filters.get(i) + "%");
        }
        return new CursorLoader(this,
                SessionEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        textView.setText("Return " + String.valueOf(data.getCount()) + "results:");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        mQuery = newText;
        getLoaderManager().restartLoader(0, null, this);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

}
