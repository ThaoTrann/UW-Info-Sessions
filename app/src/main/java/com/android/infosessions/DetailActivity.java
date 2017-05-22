package com.android.infosessions;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.infosessions.data.SessionContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.infosessions.SessionAdapter.getImage;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    String mId;
    private static final int LOADER_ID = 0;
    private SessionCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mId = getIntent().getStringExtra("id");

        mCursorAdapter = new SessionCursorAdapter(this, null, 1);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = SessionContract.SessionEntry._ID + "=?";
        String[] selectionArgs = {mId};

        String[] projection = {
                SessionContract.SessionEntry._ID,
                SessionContract.SessionEntry.COLUMN_SESSION_EMPLOYER,
                SessionContract.SessionEntry.COLUMN_SESSION_START_TIME,
                SessionContract.SessionEntry.COLUMN_SESSION_END_TIME,
                SessionContract.SessionEntry.COLUMN_SESSION_DATE,
                SessionContract.SessionEntry.COLUMN_SESSION_DAY,
                SessionContract.SessionEntry.COLUMN_SESSION_MILLISECONDS,
                SessionContract.SessionEntry.COLUMN_SESSION_WEBSITE,
                SessionContract.SessionEntry.COLUMN_SESSION_LINK,
                SessionContract.SessionEntry.COLUMN_SESSION_DESCRIPTION,
                SessionContract.SessionEntry.COLUMN_SESSION_BUILDING_CODE,
                SessionContract.SessionEntry.COLUMN_SESSION_BUILDING_NAME,
                SessionContract.SessionEntry.COLUMN_SESSION_BUILDING_ROOM,
                SessionContract.SessionEntry.COLUMN_SESSION_MAP_URL,
                SessionContract.SessionEntry.COLUMN_SESSION_LOGO,
                SessionContract.SessionEntry.COLUMN_SESSION_AUDIENCE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                SessionContract.SessionEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                selection,                   // No selection clause
                selectionArgs,                   // No selection arguments
                null);                  // Default sort order

    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
