package com.android.infosessions;

import android.app.LoaderManager;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.infosessions.data.FilterContract;
import com.android.infosessions.data.SessionContract.SessionEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Uri mUri;
    private static final int SESSION_LOADER = 0;
    private static final int CONTACT_LOADER = 1;

    private SessionCursorAdapter mCursorAdapter;
    private String map_url;
    private String link;
    private String website;
    private String employer;
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

        Button rvsp = (Button) findViewById(R.id.rvsp_button);
        Button web = (Button) findViewById(R.id.website_button);
        Button nav = (Button) findViewById(R.id.nav_button);

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

    public void updateContactLL(final Cursor cursor) {
        cursor.moveToFirst();
        contactLL.removeAllViews();
        while (!cursor.isAfterLast()) {
            LinearLayout hll = new LinearLayout(this);
            hll.setOrientation(LinearLayout.VERTICAL);

            // Extract properties from cursor
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            String company = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.DATA));
            String title = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));

            if(employer.contains(company)) {
                TextView tv = new TextView(this);
                tv.setText(name + " " + company + " " + title);
                tv.setPadding(20, 0, 20, 0);

                final int id = cursor.getPosition();
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openContact(cursor, id);
                    }
                });
                hll.addView(tv);
                contactLL.addView(hll);
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
