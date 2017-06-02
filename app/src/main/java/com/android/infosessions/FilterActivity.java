package com.android.infosessions;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.infosessions.data.DbHelper;
import com.android.infosessions.data.FilterContract.FilterEntry;
import com.android.infosessions.data.SessionContract;

public class FilterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private boolean expanded = false;
    private ListView listView;
    private FilterCursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        listView = (ListView) findViewById(R.id.list);
        mCursorAdapter = new FilterCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri currentUri = ContentUris.withAppendedId(FilterEntry.CONTENT_URI, id);
                String[] projection = {
                        FilterEntry._ID,
                        FilterEntry.COLUMN_FILTER_KEY,
                        FilterEntry.COLUMN_FILTER_IS_CODE,
                        FilterEntry.COLUMN_FILTER_VALUE};
                Cursor cursor = getContentResolver().query(currentUri, projection, null, null, null);

                ContentValues values = new ContentValues();
                if (cursor.moveToFirst()) {
                    String key = cursor.getString(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_KEY));
                    int value = cursor.getInt(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_VALUE));
                    int code = cursor.getInt(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_IS_CODE));

                    values.put(FilterEntry.COLUMN_FILTER_KEY, key);
                    if(value == FilterEntry.VALUE_NOT_CODE) {
                        if (value == FilterEntry.VALUE_CHECKED) {
                            values.put(FilterEntry.COLUMN_FILTER_VALUE, FilterEntry.VALUE_NOT_CHECKED);
                        } else {
                            values.put(FilterEntry.COLUMN_FILTER_VALUE, FilterEntry.VALUE_CHECKED);
                        }
                    } else {
                        values.put(FilterEntry.COLUMN_FILTER_VALUE, FilterEntry.VALUE_NOT_CHECKED);
                    }
                    values.put(FilterEntry.COLUMN_FILTER_IS_CODE, code);
                }
                getContentResolver().update(currentUri, values, null, null);
            }
        });

        getLoaderManager().initLoader(0, null, this);
        setTitle("Filter");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                FilterEntry._ID,
                FilterEntry.COLUMN_FILTER_KEY,
                FilterEntry.COLUMN_FILTER_IS_CODE,
                FilterEntry.COLUMN_FILTER_VALUE};

        return new CursorLoader(this,
                FilterEntry.CONTENT_URI,
                projection,
                null,
                null,
                FilterEntry.COLUMN_FILTER_KEY + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
