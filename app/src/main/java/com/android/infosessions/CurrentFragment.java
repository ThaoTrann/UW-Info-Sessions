package com.android.infosessions;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.infosessions.data.DbHelper;
import com.android.infosessions.data.FilterContract.FilterEntry;
import com.android.infosessions.data.LogoContract.LogoEntry;
import com.android.infosessions.data.SessionContract.SessionEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Thao on 5/10/17.
 */

public class CurrentFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = MainActivity.class.getName();

    private ListView sessionsListView;
    private TextView updateTimeTV;
    private RelativeLayout loadingRL;

    private static final String UWAPI_REQUEST_URL =
            "https://api.uwaterloo.ca/v2/resources/infosessions.json?key=123afda14d0a233ecb585591a95e0339";
    private static final int LOADER_ID = 0;
    private SessionCursorAdapter mCursorAdapter;
    private Context mContext;

    public CurrentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sessions_list, container, false);
        sessionsListView = (ListView) rootView.findViewById(R.id.list);
        updateTimeTV = (TextView) rootView.findViewById(R.id.update_time);
        updateTimeTV.setVisibility(View.VISIBLE);

        loadingRL = (RelativeLayout) rootView.findViewById(R.id.loading_spinner);

        mCursorAdapter = new SessionCursorAdapter(mContext, null);
        sessionsListView.setAdapter(mCursorAdapter);
        sessionsListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                sessionsListView.removeOnLayoutChangeListener(this);
                Log.e(LOG_TAG, "finish updated");
            }
        });

        mCursorAdapter.notifyDataSetChanged();

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

                Intent intent = new Intent(mContext, DetailActivity.class);

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
    String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
    private String audienceListSofar = "";

    public class SessionTask extends AsyncTask<String, Void, ArrayList<Session>> {
        @Override
        protected ArrayList<Session> doInBackground(String... params) {

            ArrayList<Session> sessions =  QueryUtils.fetchInfos(params[0], mContext);
            audienceListSofar = "";
            insertSession(sessions);
            return sessions;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*
            Toast toast = Toast.makeText(getContext(), "PreExecute", Toast.LENGTH_SHORT);
            toast.show();*/
            loadingRL.setVisibility(View.VISIBLE);
            sessionsListView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(ArrayList<Session> sessions) {
            super.onPostExecute(sessions);
            loadingRL.setVisibility(View.GONE);
            //Toast toast = Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT);
            //toast.show();
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(rightNow.DAY_OF_MONTH);
            int month = rightNow.get(rightNow.MONTH) + 1;
            int year = rightNow.get(rightNow.YEAR);
            updateTimeTV.setText("Updated by " + getMonthForInt(month) + " " + day + " " + year);
            sessionsListView.setVisibility(View.VISIBLE);
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

            if (mDescription.isEmpty()) {
                mDescription = "Employer's Description is not provided.";
            }
            String mBuildingName = session.getBuildingName();
            String mRoom = session.getBuildingRoom();
            String mCode = session.getBuildingCode();
            String mMapUrl = session.getMapUrl();
            String mAudience = session.getAudience();
            if(mAudience != null && !mAudience.isEmpty()) {
                ArrayList<String> mAudienceSA = session.getAudienceStringArray();
                
                for (int j = 0; j < mAudienceSA.size(); j++) {
                    String audience = mAudienceSA.get(j).trim();
                    if (!audienceListSofar.contains(audience)) {
                        ContentValues valuesAudience = new ContentValues();
                        valuesAudience.put(FilterEntry.COLUMN_FILTER_KEY, audience);
                        valuesAudience.put(FilterEntry.COLUMN_FILTER_VALUE, FilterEntry.VALUE_NOT_CHECKED);
                        mContext.getContentResolver().insert(FilterEntry.CONTENT_URI, valuesAudience);
                        audienceListSofar += mAudienceSA.get(j) + ",";
                    }
                }
            }

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


            // retrieve logo image from logo database
            byte[] mLogo;

            String[] logo_projection = {
                    LogoEntry._ID,
                    LogoEntry.COLUMN_LOGO_EMPLOYER,
                    LogoEntry.COLUMN_LOGO_IMAGE};


            String logo_selection = LogoEntry.COLUMN_LOGO_EMPLOYER + " LIKE ? ";
            String[] logo_selectionArgs = { mEmployer };

            DbHelper mDbHelper = new DbHelper(mContext);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            // Perform a query on the pets table
            Cursor cursor = db.query(
                    LogoEntry.TABLE_NAME,   // The table to query
                    logo_projection,            // The columns to return
                    logo_selection,                  // The columns for the WHERE clause
                    logo_selectionArgs,                  // The values for the WHERE clause
                    null,                  // Don't group the rows
                    null,                  // Don't filter by row groups
                    null);
            int mContacts;
            mContacts = cursor.getCount();
            if(cursor.getCount() == 0) {
                //loadingRL.setVisibility(View.VISIBLE);
                // fetch logo image from clearbit
                mLogo = getBytes(QueryUtils.fetchImage(mEmployer, mContext));

                //save image to db
                ContentValues logo_values = new ContentValues();
                logo_values.put(LogoEntry.COLUMN_LOGO_EMPLOYER, mEmployer);
                logo_values.put(LogoEntry.COLUMN_LOGO_IMAGE, mLogo);
                db.insert(LogoEntry.TABLE_NAME, null, logo_values);

            } else {
                cursor.moveToFirst();
                mLogo = cursor.getBlob(cursor.getColumnIndexOrThrow(LogoEntry.COLUMN_LOGO_IMAGE));
            }

            db.close();

            values.put(SessionEntry.COLUMN_SESSION_LOGO, mLogo);

            String[] mEmployerSplit = mEmployer.split(" ");

            // retrieve related contacts from contact database
            String orgWhere = ContactsContract.Data.MIMETYPE + " = ? AND ";
            String[] orgWhereParams = new String[ mEmployerSplit.length + 1];

            orgWhereParams[0] =  ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE;

            if (mEmployerSplit.length > 1) {
                orgWhere += "(";
            }
            for(int j = 1; j <= mEmployerSplit.length; j++) {
                orgWhere += ContactsContract.CommonDataKinds.Organization.DATA + " LIKE ? ";
                orgWhereParams[j] = mEmployerSplit[j-1];
                if(j != mEmployerSplit.length) {
                    orgWhere += " OR ";
                } else if (mEmployerSplit.length > 1) {
                    orgWhere += ")";
                }
            }

            Cursor contact_cursor = mContext.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,             // Columns to include in the resulting Cursor
                    orgWhere,                   // No selection clause
                    orgWhereParams,                   // No selection arguments
                    null);

            mContacts = contact_cursor.getCount();
            //contact_cursor.moveToFirst();

            values.put(SessionEntry.COLUMN_SESSION_NUMBER_CONTACTS, mContacts);

            // Insert a new row for pet in the database, returning the ID of that new row.
            mContext.getContentResolver().insert(SessionEntry.CONTENT_URI, values);

        }
        //Log.d("LOG_TAG", audienceListSofar);
    }
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
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
                SessionEntry.COLUMN_SESSION_NUMBER_CONTACTS,
                SessionEntry.COLUMN_SESSION_AUDIENCE};

        DbHelper mDbHelper = new DbHelper(mContext);
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
            loadingRL.setVisibility(View.VISIBLE);
            SessionTask sessionTask = new SessionTask();
            sessionTask.execute(UWAPI_REQUEST_URL);
        } else {
            loadingRL.setVisibility(View.GONE);
        }

        db.close();

        String selection = SessionEntry.COLUMN_SESSION_MILLISECONDS + ">?";
        String[] selectionArgs = { String.valueOf(milliSeconds) };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(mContext,
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
        loadingRL.setVisibility(View.GONE);
    }
}
