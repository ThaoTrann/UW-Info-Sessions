package com.android.infosessions;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.infosessions.data.DbHelper;
import com.android.infosessions.data.SessionContract.SessionEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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

    public CurrentFragment() {
    }

    // Required empty public constructor


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast toast = Toast.makeText(getContext(), "Created", Toast.LENGTH_SHORT);
        toast.show();
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

        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    public class SessionTask extends AsyncTask<String, Void, ArrayList<Session>> {
        @Override
        protected ArrayList<Session> doInBackground(String... params) {
            ArrayList<Session> sessions =  QueryUtils.fetchInfos(params[0], getContext());
            Log.d("LOG_TAG", String.valueOf(sessions.size()));
            return sessions;
        }
    }


    public void fetchData(Loader<List<Session>> loader, List<Session> data) {
        ArrayList<Session> filtered = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int cMonth = c.get(Calendar.MONTH) + 1;
        int cDay = c.get(Calendar.DAY_OF_MONTH);
        int cYear = c.get(Calendar.YEAR);

        for(int i = 0; i < data.size(); i++) {
            String[] date = data.get(i).getDate().split("-");
            int day = Integer.parseInt(date[2]);
            int month = Integer.parseInt(date[1]);
            int year = Integer.parseInt(date[0]);
            if (year > cYear || (year == cYear && month > cMonth)
                    || (year == cYear && month == cMonth && day >= cDay)) {
                filtered.add(data.get(i));
            }
        }
        Toast toast = Toast.makeText(getContext(), "Finished updating", Toast.LENGTH_SHORT);
        toast.show();
        getActivity().findViewById(R.id.loading_spinner).setVisibility(View.GONE);
        updateUi(filtered);
    }


    private void updateUi(final ArrayList<Session> sessions) {
        // Find a reference to the {@link ListView} in the layout
        // Create a new {@link ArrayAdapter} of earthquakes
        SessionAdapter adapter = new SessionAdapter(getContext(), sessions);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        sessionsListView.setAdapter(adapter);
/*
        infosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // The code in this method will be executed when the numbers category is clicked on.
                Session currentSession = sessions.get(position);
                Cursor cursor = mCursorAdapter.getCursor()
                ArrayList<String> toSent = new ArrayList<String>();
                toSent.add(currentSession.toJSONString());
                toSent.add(currentSession.getLogoString());
                Intent intent = new Intent(getContext(), DetailActivity.class)
                        .putStringArrayListExtra("EXTRA_TEXT", toSent);
                // Start the new activity
                startActivity(intent);
            }
        });*/
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        ArrayList<Session> filtered = new ArrayList<>();
        Calendar rightNow = Calendar.getInstance();
        int day = rightNow.get(rightNow.DAY_OF_MONTH);
        int month = rightNow.get(rightNow.MONTH) + 1;
        int year = rightNow.get(rightNow.YEAR);
        Date date = new Date(year, month, day);
        long milliSeconds = date.getTime();
        Log.d("LOG_TAG current", String.valueOf(month));

        String selection = SessionEntry.COLUMN_SESSION_MILLISECONDS + ">?";
        String[] selectionArgs = { String.valueOf(milliSeconds) };

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
        return new CursorLoader(getContext(),   // Parent activity context
                SessionEntry.CONTENT_URI,   // Provider content URI to query
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
