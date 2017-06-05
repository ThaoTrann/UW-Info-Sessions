package com.android.infosessions;

import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.infosessions.data.SessionContract.SessionEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static com.android.infosessions.SessionAdapter.getImage;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Uri mUri;
    private static final int LOADER_ID = 0;
    private SessionCursorAdapter mCursorAdapter;
    private String employer;
    private String time;
    private String location;
    private String map_url;
    private String link;
    private String website;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setTitle("Details");
        Intent intent = getIntent();
        mUri = intent.getData();

        mCursorAdapter = new SessionCursorAdapter(this, null);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        Button alert = (Button) findViewById(R.id.alert_button);
        Button rvsp = (Button) findViewById(R.id.rvsp_button);
        Button web = (Button) findViewById(R.id.website_button);
        Button nav = (Button) findViewById(R.id.nav_button);

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long alertTime = Calendar.getInstance().getTimeInMillis() + 5*1000;
                Intent alertIntent = new Intent(getApplication(), AlertReceiver.class);
                alertIntent.putExtra("VALUE", employer + "," + time + "," + location);
                sendBroadcast(alertIntent);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(getApplication(), 1, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
            }
        });

        rvsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(webIntent);
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                startActivity(webIntent);
            }
        });

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(map_url));
                startActivity(webIntent);
            }
        });
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
            employer = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_EMPLOYER));
            String start_time = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_START_TIME));
            String end_time = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_END_TIME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DATE));
            String day = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DAY));
            map_url = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_MAP_URL));
            String audience = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_AUDIENCE));
            String building_room = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_ROOM));
            String building_code = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_CODE));
            String building_name = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_NAME));
            link = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LINK));
            String logo = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LOGO));
            website = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_WEBSITE));

            time = start_time + " - " + end_time;
            location = building_code + " " + building_room;

            TextView nameTextView = (TextView) findViewById(R.id.employer);
            nameTextView.setText(employer);

            TextView timeTextView = (TextView) findViewById(R.id.time);
            timeTextView.setText(start_time + " - " + end_time);

            TextView dateTextView = (TextView) findViewById(R.id.date);
            dateTextView.setText(day + " - " + date);

            TextView detailTextView = (TextView) findViewById(R.id.description);
            detailTextView.setText(description);

            TextView locationTextView = (TextView) findViewById(R.id.location);
            locationTextView.setText(building_code + " (" + building_name + ") - " + building_room);

            TextView audienceTextView = (TextView) findViewById(R.id.audience);
            audienceTextView.setText(audience);

            ImageView logoView = (ImageView) findViewById(R.id.employer_logo);
            Drawable drawable = getImage(logoView.getContext(), logo);

            logoView.setImageDrawable(drawable);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
