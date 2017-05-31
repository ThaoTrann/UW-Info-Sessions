package com.android.infosessions;

import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.android.infosessions.data.DbHelper;
import com.android.infosessions.data.FilterContract.FilterEntry;

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
        getLoaderManager().initLoader(0, null, this);
/*
        final LinearLayout outerll = new LinearLayout(this);
        outerll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(outerll);
        CheckBox cb = new CheckBox(this);
        cb.setText("Outer loop");

        final LinearLayout innerll = new LinearLayout(this);
        innerll.setOrientation(LinearLayout.VERTICAL);
        innerll.setPadding(50, 0, 0, 0);
        for(int i = 0; i < 5; i++) {
            CheckBox ch = new CheckBox(FilterActivity.this);
            ch.setText("I'm dynamic!");
            ch.getOffsetForPosition(50, 0);
            innerll.addView(ch);
        }

        outerll.addView(cb);
        outerll.addView(innerll);

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expanded) {
                    innerll.setVisibility(View.GONE);
                    expanded = false;
                } else {
                    innerll.setVisibility(View.VISIBLE);
                    expanded = true;
                }
            }
        });
        
        setContentView(sv);*/
        setTitle("Filter");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                FilterEntry._ID,
                FilterEntry.COLUMN_FILTER_KEY,
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
