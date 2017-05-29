package com.android.infosessions;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.infosessions.data.SessionContract.SessionEntry;

import java.util.Calendar;
import java.util.Date;

public class SearchableActivity extends AppCompatActivity implements android.widget.SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private SessionCursorAdapter mCursorAdapter;
    private String mQuery = "";
    private ListView sessionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sessions_list);
        sessionsListView = (ListView) findViewById(R.id.list);
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

        android.widget.SearchView searchView = (android.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search employer...");
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
        String selection = SessionEntry.COLUMN_SESSION_EMPLOYER + " LIKE ? AND " + SessionEntry.COLUMN_SESSION_AUDIENCE + " LIKE ?";
        String[] selectionArgs = { "%" + mQuery + "%" , "%MATH%"};
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
