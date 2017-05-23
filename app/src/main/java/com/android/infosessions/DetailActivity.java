package com.android.infosessions;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.infosessions.data.SessionContract.SessionEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.infosessions.SessionAdapter.getImage;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Uri mUri;
    private static final int LOADER_ID = 0;
    private SessionCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setTitle("Details");
        Intent intent = getIntent();
        mUri = intent.getData();

        mCursorAdapter = new SessionCursorAdapter(this, null);
        getLoaderManager().initLoader(LOADER_ID, null, this);
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

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mUri,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        if (cursor.moveToFirst()) {
            String employer = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_EMPLOYER));
            String start_time = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_START_TIME));
            String end_time = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_END_TIME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DATE));
            String day = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DAY));
            String map_url = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_MAP_URL));
            String audience = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_AUDIENCE));
            String building_room = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_ROOM));
            String building_code = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_CODE));
            String building_name = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_NAME));
            String link = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LINK));
            String logo = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LOGO));

            TextView nameTextView = (TextView) findViewById(R.id.employer);
            nameTextView.setText(employer);

            TextView timeTextView = (TextView) findViewById(R.id.time);
            timeTextView.setText(start_time + " - " + end_time);

            TextView dateTextView = (TextView) findViewById(R.id.date);
            dateTextView.setText(day + " - " + date);

            TextView detailTextView = (TextView) findViewById(R.id.description);
            detailTextView.setText(description);

            TextView locationTextView = (TextView) findViewById(R.id.location);
            locationTextView.setText(building_code + " - " + building_name + " " + building_room);

            TextView audienceTextView = (TextView) findViewById(R.id.audience);
            audienceTextView.setText("Audience:/n" + audience);

            ImageView logoView = (ImageView) findViewById(R.id.employer_logo);
            Drawable drawable = getImage(logoView.getContext(), logo);

            logoView.setImageDrawable(drawable);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
