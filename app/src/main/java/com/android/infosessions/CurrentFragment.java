package com.android.infosessions;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.infosessions.data.DbHelper;
import com.android.infosessions.data.SessionContract.SessionEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



/**
 * Created by Thao on 5/10/17.
 */

public class CurrentFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = MainActivity.class.getName();

    private ListView sessionsListView;
    private static final String UWAPI_REQUEST_URL =
            "https://api.uwaterloo.ca/v2/resources/infosessions.json?key=123afda14d0a233ecb585591a95e0339";
    private static final int LOADER_ID = 0;
    private SessionCursorAdapter mCursorAdapter;
    private String mQuery;
    public CurrentFragment() {
    }

    // Required empty public constructor

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sessions_list, container, false);
        sessionsListView = (ListView) rootView.findViewById(R.id.list);
        mCursorAdapter = new SessionCursorAdapter(getContext(), null);
        sessionsListView.setAdapter(mCursorAdapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionTask sessionTask = new SessionTask();
                sessionTask.execute(UWAPI_REQUEST_URL);
            }
        });

        sessionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DetailActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(SessionEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return rootView;
    }

    public class SessionTask extends AsyncTask<String, Void, ArrayList<Session>> {
        @Override
        protected ArrayList<Session> doInBackground(String... params) {
            ArrayList<Session> sessions =  QueryUtils.fetchInfos(params[0], getContext());
            return sessions;
        }

        @Override
        protected void onPostExecute(ArrayList<Session> sessions) {
            super.onPostExecute(sessions);
            insertSession(sessions);
            Toast toast = Toast.makeText(getContext(), "Updated seconds ago", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void insertSession(ArrayList<Session> sessions) {
        for(int i = 0; i < sessions.size(); i++) {
            Session session = sessions.get(i);
            String mEmployer = session.getEmployer();
            String mStartTime = session.getStartTime();
            String mEndTime = session.getEndTime();
            String mDate = session.getDate();
            String mDay = session.getDay();
            long mMinutes = dayToMilliSeconds(mDate);
            String mWebsite = session.getWebsite();
            String mLink = session.getLink();
            String mDescription = session.getDescription();
            String mLogo = session.getLogoString();
            if (mDescription.isEmpty()) {
                mDescription = "Employer's Description is not provided.";
            }
            String mBuildingName = session.getBuildingName();
            String mRoom = session.getBuildingRoom();
            String mCode = session.getBuildingCode();
            String mMapUrl = session.getMapUrl();
            String mAudience = session.getAudience();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(SessionEntry.COLUMN_SESSION_EMPLOYER, mEmployer);
            values.put(SessionEntry.COLUMN_SESSION_START_TIME, mStartTime);
            values.put(SessionEntry.COLUMN_SESSION_END_TIME, mEndTime);
            values.put(SessionEntry.COLUMN_SESSION_DATE, mDate);
            values.put(SessionEntry.COLUMN_SESSION_DAY, mDay);
            values.put(SessionEntry.COLUMN_SESSION_MILLISECONDS, mMinutes);
            values.put(SessionEntry.COLUMN_SESSION_WEBSITE, mWebsite);
            values.put(SessionEntry.COLUMN_SESSION_LINK, mLink);
            values.put(SessionEntry.COLUMN_SESSION_AUDIENCE, mAudience);
            values.put(SessionEntry.COLUMN_SESSION_DESCRIPTION, mDescription);
            values.put(SessionEntry.COLUMN_SESSION_BUILDING_CODE, mCode);
            values.put(SessionEntry.COLUMN_SESSION_BUILDING_NAME, mBuildingName);
            values.put(SessionEntry.COLUMN_SESSION_BUILDING_ROOM, mRoom);
            values.put(SessionEntry.COLUMN_SESSION_MAP_URL, mMapUrl);
            values.put(SessionEntry.COLUMN_SESSION_LOGO, mLogo);


            // Insert a new row for pet in the database, returning the ID of that new row.
            Uri newUri = getActivity().getContentResolver().insert(SessionEntry.CONTENT_URI, values);
        }
    }

    public long dayToMilliSeconds(String data) {
        String[] mDay = data.split("-");
        int day = Integer.parseInt(mDay[2]);
        int month = Integer.parseInt(mDay[1]);
        int year = Integer.parseInt(mDay[0]);
        Date date = new Date(year, month, day);
        return date.getTime();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.

        Calendar rightNow = Calendar.getInstance();
        int day = rightNow.get(rightNow.DAY_OF_MONTH);
        int month = rightNow.get(rightNow.MONTH) + 1;
        int year = rightNow.get(rightNow.YEAR);
        Date date = new Date(year, month, day);
        long milliSeconds = date.getTime();

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

        DbHelper mDbHelper = new DbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Perform a query on the pets table
        Cursor cursor = db.query(
                SessionEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);

        if(cursor.getCount() == 0) {
            SessionTask sessionTask = new SessionTask();
            sessionTask.execute(UWAPI_REQUEST_URL);
        }
        String selection = SessionEntry.COLUMN_SESSION_MILLISECONDS + ">?";
        String[] selectionArgs = { String.valueOf(milliSeconds) };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(getContext(),
                SessionEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

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
