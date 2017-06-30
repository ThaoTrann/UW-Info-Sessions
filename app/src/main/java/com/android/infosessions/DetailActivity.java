package com.android.infosessions;

import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.infosessions.data.FilterContract;
import com.android.infosessions.data.SessionContract.SessionEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Uri mUri;
    private static final int SESSION_LOADER = 0;
    private static final int CONTACT_LOADER = 1;

    private SessionCursorAdapter mCursorAdapter;
    private String time;
    private String location;
    private String map_url;
    private String link;
    private String website;
    private String employer;
    private int id;
    private Long milliseconds;
    private LinearLayout contactLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setTitle("Details");
        Intent intent = getIntent();
        mUri = intent.getData();

        mCursorAdapter = new SessionCursorAdapter(this, null);
        getLoaderManager().initLoader(SESSION_LOADER, null, this);
        getLoaderManager().initLoader(CONTACT_LOADER, null, this);

        Button alert = (Button) findViewById(R.id.alert_button);
        Button rvsp = (Button) findViewById(R.id.rvsp_button);
        Button web = (Button) findViewById(R.id.website_button);
        Button nav = (Button) findViewById(R.id.nav_button);

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long current = Calendar.getInstance().getTimeInMillis();
                Long alertTime = milliseconds;
                Log.d("LOG_TAG current time ", current.toString());
                Log.d("LOG_TAG milliseconds ", milliseconds.toString());

                Intent alertIntent = new Intent(getApplication(), AlertReceiver.class);
                alertIntent.putExtra("VALUE", employer + "," + time + "," + location + "," + mUri + "," + id);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Log.d("alert id", id + "");

                alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                        PendingIntent.getBroadcast(getApplication(), 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
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
        contactLL = (LinearLayout) findViewById(R.id.contacts);
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
                        SessionEntry.COLUMN_SESSION_AUDIENCE};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        mUri,   // Provider content URI to query
                        projection,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
            case CONTACT_LOADER:
                String orgWhere = ContactsContract.Data.MIMETYPE + " = ?";
                String[] orgWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};

                return new CursorLoader(this,   // Parent activity context
                        ContactsContract.Data.CONTENT_URI,   // Provider content URI to query
                        null,             // Columns to include in the resulting Cursor
                        orgWhere,                   // No selection clause
                        orgWhereParams,                   // No selection arguments
                        null);                  // Default sort order
            default:
                return null;
        }
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        } else {
            cursor.moveToFirst();
            switch (loader.getId()) {
                case SESSION_LOADER:
                    if (cursor.moveToFirst()) {
                        id = cursor.getInt(cursor.getColumnIndex("_id"));
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
                        //String logo = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LOGO));
                        website = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_WEBSITE));
                        milliseconds = cursor.getLong(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_MILLISECONDS));

                        time = start_time + " - " + end_time;
                        location = building_code + " " + building_room;

                        TextView nameTextView = (TextView) findViewById(R.id.employer);
                        nameTextView.setText(employer);

                        TextView timeTextView = (TextView) findViewById(R.id.time);
                        timeTextView.setText(start_time + " - " + end_time);

                        String[] date_split = date.split("-");
                        String month = getMonthForInt(Integer.parseInt(date_split[1])-1);
                        String formated_date = month + " " + date_split[2] + " " + date_split[0];

                        TextView dateTextView = (TextView) findViewById(R.id.date);
                        dateTextView.setText(formated_date);


                        TextView detailTextView = (TextView) findViewById(R.id.description);
                        detailTextView.setText(description);

                        TextView locationTextView = (TextView) findViewById(R.id.location);
                        locationTextView.setText(building_code + " (" + building_name + ") - " + building_room);

                        TextView audienceTextView = (TextView) findViewById(R.id.audience);
                        audienceTextView.setText(audience);

                        ImageView logoView = (ImageView) findViewById(R.id.employer_logo);
                        byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LOGO));
                        Bitmap logo = getImage(image);

                        //logoView.setImageDrawable(drawable);
                        logoView.setImageBitmap(logo);

                    }
                    break;
                case CONTACT_LOADER:
                    updateContactLL(cursor);
                    break;
                default:
                    return;
            }
        }
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
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

    public void updateContactLL(final Cursor cursor) {
        cursor.moveToFirst();
        contactLL.removeAllViews();
        boolean hasContact = false;
        while (!cursor.isAfterLast()) {
            LinearLayout vll = new LinearLayout(this);
            vll.setOrientation(LinearLayout.VERTICAL);

            // Extract properties from cursor
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            String company = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.DATA));
            String title = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));

            if(employer.contains(company)) {
                if(!hasContact) {
                    TextView tv = new TextView(this);
                    tv.setText("Contacts:");
                    contactLL.addView(tv);
                    hasContact = true;
                }

                TextView name_tv = new TextView(this);
                name_tv.setText(name);
                name_tv.setTextColor(getResources().getColor(R.color.textColorEmployer));
                name_tv.setPadding(16, 8, 16, 8);

                final int id = cursor.getPosition();
                vll.addView(name_tv);

                if(title == null) {
                    title = "No tilte";
                }

                TextView title_tv = new TextView(this);
                title_tv.setText(title);
                title_tv.setPadding(16, 8, 16, 8);
                vll.addView(title_tv);
                vll.setBackgroundResource(R.drawable.contacts_border);

                vll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openContact(cursor, id);
                    }
                });
                contactLL.addView(vll);
            }

            cursor.moveToNext();
        }
    }

    public void openContact(Cursor mCursor, int pos) {
        int count = 0;
        mCursor.moveToFirst();
        while (count != pos) {
            mCursor.moveToNext();
            count ++;
        }
        int mLookupKeyIndex = mCursor.getColumnIndex(Contacts.LOOKUP_KEY);
        // Gets the lookup key value
        String mCurrentLookupKey = mCursor.getString(mLookupKeyIndex);
        // Gets the _ID column index
        int mIdIndex = mCursor.getColumnIndex(Contacts._ID);
        Long mCurrentId = mCursor.getLong(mIdIndex);
        Uri mSelectedContactUri =
                Contacts.getLookupUri(mCurrentId, mCurrentLookupKey);

        // Creates a new Intent to edit a contact
        Intent editIntent = new Intent(Intent.ACTION_VIEW);
    /*
     * Sets the contact URI to edit, and the data type that the
     * Intent must match
     */
        editIntent.setDataAndType(mSelectedContactUri,Contacts.CONTENT_ITEM_TYPE);
        startActivity(editIntent);
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
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
