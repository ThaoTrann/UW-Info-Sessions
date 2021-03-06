package com.android.infosessions;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    HorizontalScrollView hsv;
    private LinearLayout filterTabsLL;

    private static final int SESSION_LOADER = 3;
    private static final int FILTER_LOADER = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sessions_list);
        setTitle("Search");
        sessionsListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.search_empty_view);
        emptyView.setVisibility(View.VISIBLE);
        sessionsListView.setEmptyView(emptyView);

        LinearLayout ll = (LinearLayout) findViewById(R.id.filter_tab);
        ll.setVisibility(View.VISIBLE);
        hsv = (HorizontalScrollView) findViewById(R.id.filter_hsv);
        hsv.setVisibility(View.VISIBLE);
        filterTabsLL = (LinearLayout) findViewById(R.id.filter_ll);

        mCursorAdapter = new SessionCursorAdapter(this, null);
        sessionsListView.setAdapter(mCursorAdapter);


        sessionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchableActivity.this, DetailActivity.class);

                Uri currentUri = ContentUris.withAppendedId(SessionEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(SESSION_LOADER, null, this);
        getLoaderManager().initLoader(FILTER_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(SESSION_LOADER, null, this);
        getLoaderManager().restartLoader(FILTER_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(60, 60);

        Button btnFilter = new Button(this);
        btnFilter.setBackgroundResource(R.drawable.filter_button);

        android.widget.SearchView searchView = (android.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search employer...");
        //searchView.setMaxWidth(Integer.MAX_VALUE);

        ((LinearLayout) searchView.getChildAt(0)).addView(btnFilter, layoutParams);
        ((LinearLayout) searchView.getChildAt(0)).setGravity(Gravity.CENTER);
        (searchView.getChildAt(0)).setPadding(0, 0, 50, 0);
        (searchView.getChildAt(0)).setMinimumWidth(50);
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
        switch (id) {
            case SESSION_LOADER:
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
                        SessionEntry.COLUMN_SESSION_ALERTED,
                        SessionEntry.COLUMN_SESSION_NUMBER_CONTACTS,
                        SessionEntry.COLUMN_SESSION_AUDIENCE};

                if(mQuery.trim().isEmpty()) {
                    mQuery = "";
                }

                String[] p = {
                        FilterEntry._ID,
                        FilterEntry.COLUMN_FILTER_KEY,
                        FilterEntry.COLUMN_FILTER_VALUE};

                String[] a = { String.valueOf(FilterEntry.VALUE_CHECKED) };

                Cursor filterCursor = getContentResolver().query(FilterEntry.CONTENT_URI, p,
                        FilterEntry.COLUMN_FILTER_VALUE + "=?",
                        a, null);
                ArrayList<String> filters = new ArrayList<>();

                String selection = SessionEntry.COLUMN_SESSION_EMPLOYER + " LIKE ? ";
                filterCursor.moveToFirst();
                int count = filterCursor.getCount();
                if(count > 0) {
                    selection += " AND (";
                }
                for(int i = 0; i < count; i++) {
                    String audience = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_KEY));
                    filters.add(audience);
                    Log.d("LOG_TAG: filter ", "" + audience);
                    if(i != 0) {
                        selection += " OR ";
                    }
                    selection += SessionEntry.COLUMN_SESSION_AUDIENCE + " LIKE ? ";
                    filterCursor.moveToNext();
                }
                if(count > 0) {
                    selection += ")";
                }
                filterCursor.close();

                String[] selectionArgs = new String[1+filters.size()];
                selectionArgs[0] = ("%" + mQuery + "%") ;
                for(int i = 1; i < selectionArgs.length; i++) {
                    selectionArgs[i] = ("%" + filters.get(i-1) + "%");
                }
                Log.d("LOG_TAG: selection", "" + selection);
                return new CursorLoader(this,
                        SessionEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
            case FILTER_LOADER:
                String[] proj = {
                        FilterEntry._ID,
                        FilterEntry.COLUMN_FILTER_KEY,
                        FilterEntry.COLUMN_FILTER_VALUE};

                String[] argus = { String.valueOf(FilterEntry.VALUE_CHECKED) };
                return new CursorLoader(this,
                        FilterEntry.CONTENT_URI,
                        proj,
                        FilterEntry.COLUMN_FILTER_VALUE + "=?",
                        argus,
                        FilterEntry.COLUMN_FILTER_KEY + " ASC");
            default:
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SESSION_LOADER:
                mCursorAdapter.swapCursor(data);
                break;
            case FILTER_LOADER:
                updateTabs(data);
                break;
            default:
                return;
        }
    }

    public void updateTabs(final Cursor cursor) {
        cursor.moveToFirst();
        hsv.removeAllViews();
        filterTabsLL.removeAllViews();
        while (!cursor.isAfterLast()) {
            LinearLayout hll = new LinearLayout(this);
            hll.setOrientation(LinearLayout.HORIZONTAL);
            hll.setBackgroundResource(R.drawable.tags_border);

            TextView tv = new TextView(this);
            String key = cursor.getString(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_KEY));
            tv.setText(key);
            tv.setTextColor(Color.WHITE);
            tv.setPadding(20, 0, 20, 0);
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_clear_white_24dp);
            iv.setMaxWidth(10);
            iv.setMaxHeight(10);

            final int pos = cursor.getPosition();
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCursor(cursor, pos);
                }
            });
            hll.addView(tv);
            hll.addView(iv);
            filterTabsLL.addView(hll);
            cursor.moveToNext();
        }
        hsv.addView(filterTabsLL);
    }

    private void updateCursor(Cursor mCursor, int pos) {
        ContentValues values = new ContentValues();
        int count = 0;
        mCursor.moveToFirst();
        while (count != pos) {
            mCursor.moveToNext();
            count ++;
        }
        String key = mCursor.getString(mCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_KEY));
        int value = mCursor.getInt(mCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_VALUE));
        int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(FilterEntry._ID));

        Uri currentUri = ContentUris.withAppendedId(FilterEntry.CONTENT_URI, id);

        values.put(FilterEntry.COLUMN_FILTER_KEY, key);
        values.put(FilterEntry.COLUMN_FILTER_VALUE, FilterEntry.VALUE_NOT_CHECKED);
        getContentResolver().update(currentUri, values, null, null);
        getLoaderManager().restartLoader(SESSION_LOADER, null, this);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SESSION_LOADER:
                mCursorAdapter.swapCursor(null);
                break;
            case FILTER_LOADER:
                break;
            default:
                return;
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQuery = newText;
        getLoaderManager().restartLoader(SESSION_LOADER, null, this);
        getLoaderManager().restartLoader(FILTER_LOADER, null, this);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

}
