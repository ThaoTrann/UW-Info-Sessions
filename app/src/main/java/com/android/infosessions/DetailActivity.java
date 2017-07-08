package com.android.infosessions;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.infosessions.data.FilterContract;
import com.android.infosessions.data.SessionContract.SessionEntry;

import java.sql.Blob;
import java.text.DateFormatSymbols;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Uri mUri;
    private static final int SESSION_LOADER = 0;
    private static final int CONTACT_LOADER = 1;

    private SessionCursorAdapter mCursorAdapter;
    private String mTime;
    private String mLocation;
    private String mMapUrl;
    private String mWebsite;
    private String mEmployer;
    private String mStartTime;
    private String mEndTime;
    private String mLink;
    private String mDescription;
    private String mDate;
    private String mDay;
    private String mBuildingRoom;
    private String mBuildingCode;
    private String mBuildingName;
    private String mMilliseconds;
    private String mAudience;
    private byte[] mLogo;
    private String mContacts;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private int mId;
    private int mAlerted;
    private Long milliseconds;
    private ImageButton alert;
    private ImageButton rsvp;
    private ImageButton web;
    private ImageButton nav;
    private String formated_date;

    private LinearLayout contactLL;
    private TextView contact_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(mEmployer);
        toolbar.setTitle(mEmployer);
        toolbar.setTitleTextColor(Color.WHITE);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle(mEmployer);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        Intent intent = getIntent();
        mUri = intent.getData();

        mCursorAdapter = new SessionCursorAdapter(this, null);
        getLoaderManager().initLoader(SESSION_LOADER, null, this);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            getLoaderManager().destroyLoader(CONTACT_LOADER);
        } else {
            getLoaderManager().initLoader(CONTACT_LOADER, null, this);
        }

        alert = (ImageButton) findViewById(R.id.alert_button);
        rsvp = (ImageButton) findViewById(R.id.rvsp_button);
        web = (ImageButton) findViewById(R.id.website_button);
        nav = (ImageButton) findViewById(R.id.nav_button);

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Long current = Calendar.getInstance().getTimeInMillis() + 5*1000;
                final Long alertTime = milliseconds;
                /*
                Log.d("LOG_TAG current time ", current.toString());
                Log.d("LOG_TAG milliseconds ", milliseconds.toString());

                Log.d("alert id", mId + "");*/

                Intent alertIntent = new Intent(getApplication(), AlertReceiver.class);
                alertIntent.putExtra("VALUE", mEmployer + ";" + mTime + " (" + formated_date + ")"  + ";" + mLocation + ";" + mUri + ";" + mId);
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                pendingIntent = PendingIntent.getBroadcast(getApplication(), 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                if(mAlerted == SessionEntry.NOT_ALERTED) {
                    final long[] extra_time = new long[1];
                    final AlertDialog mBuilder = new AlertDialog.Builder(DetailActivity.this).create();
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE );
                    final View mView = inflater.inflate(R.layout.alert_time_preference, null);
                    //RadioButton on_time = (TextView) mView.findViewById(R.id.on_time);
                    RadioGroup radioGroup = (RadioGroup) mView.findViewById(R.id.radio_group);
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (checkedId == R.id.on_time) {
                                extra_time[0] = 0;
                            } else if (checkedId == R.id.fifteen_minutes) {
                                extra_time[0] = 900000;
                            } else if (checkedId == R.id.thirty_minutes) {
                                extra_time[0] = 1800000;
                            } else if (checkedId == R.id.aday) {
                                extra_time[0] = 86400000;
                            }
                        }
                    });

                    Button mAdd = (Button) mView.findViewById(R.id.add_btn);
                    Button mCnl = (Button) mView.findViewById(R.id.cancel_btn);

                    mBuilder.setView(mView);
                    mBuilder.setCancelable(true);
                    mAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime - extra_time[0], pendingIntent);
                            mBuilder.dismiss();
                            updateSession(mId);
                            Log.d("current ", current + "");
                            Log.d("alert extra ", alertTime + extra_time[0] + "");
                        }
                    });

                    mCnl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBuilder.dismiss();
                        }
                    });
                    mBuilder.show();
                } else {
                    alarmManager.cancel(pendingIntent);
                    updateSession(mId);
                }

            }
        });

        rsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLink));
                startActivity(webIntent);
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webpage = Uri.parse(mWebsite);

                if (!mWebsite.startsWith("http://") && !mWebsite.startsWith("https://")) {
                    webpage = Uri.parse("http://" + mWebsite);
                }
                Log.d("mWeb", mWebsite);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(webIntent);
            }
        });

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mMapUrl));
                startActivity(webIntent);
            }
        });
        contactLL = (LinearLayout) findViewById(R.id.contacts);
        contact_title = (TextView) findViewById(R.id.contacts_title);
    }

    private void updateSession(long id) {
        Uri currentUri = ContentUris.withAppendedId(SessionEntry.CONTENT_URI, id);
        ContentValues values = new ContentValues();
        if(mAlerted == SessionEntry.ALERTED) {
            values.put(SessionEntry.COLUMN_SESSION_ALERTED, SessionEntry.NOT_ALERTED);
            Toast toast = Toast.makeText(getApplicationContext(), "Notification removed", Toast.LENGTH_LONG);
            toast.show();
        } else {
            values.put(SessionEntry.COLUMN_SESSION_ALERTED, SessionEntry.ALERTED);
            Toast toast = Toast.makeText(getApplicationContext(), "Notification set", Toast.LENGTH_LONG);
            toast.show();
        }

        getContentResolver().update(currentUri, values, null, null);
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
                        SessionEntry.COLUMN_SESSION_ALERTED,
                        SessionEntry.COLUMN_SESSION_LOGO,
                        SessionEntry.COLUMN_SESSION_NUMBER_CONTACTS,
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
                        updateDetail(cursor);
                    }
                    break;
                case CONTACT_LOADER:
                    if (cursor.moveToFirst()) {
                        updateContactLL(cursor);
                    }
                    break;
                default:
                    return;
            }
        }
        Log.d("alert id", mId + "");
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

    public void updateDetail(final Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex("_id"));
        mEmployer = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_EMPLOYER));

        setTitle(mEmployer);

        mStartTime = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_START_TIME));
        mEndTime = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_END_TIME));
        mDescription = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DESCRIPTION));
        mDate = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DATE));
        mDay = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DAY));
        mMapUrl = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_MAP_URL));
        mAudience = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_AUDIENCE));
        mBuildingRoom = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_ROOM));
        mBuildingCode = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_CODE));
        mBuildingName = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_NAME));
        mLink = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LINK));
        mAlerted = cursor.getInt(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_ALERTED));

        mLogo = cursor.getBlob(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LOGO));
        mWebsite = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_WEBSITE));
        milliseconds = cursor.getLong(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_MILLISECONDS));
        mContacts = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_NUMBER_CONTACTS));

        mTime = mStartTime + " - " + mEndTime;
        mLocation = mBuildingCode + " " + mBuildingRoom;

        if(mLink.isEmpty() || mLink == null) {
            rsvp.setVisibility(View.GONE);
        } else {
            rsvp.setVisibility(View.VISIBLE);
        }
        if(mWebsite.isEmpty() || mWebsite == null) {
            web.setVisibility(View.GONE);
        } else {
            web.setVisibility(View.VISIBLE);
        }
        if(mMapUrl.isEmpty() || mMapUrl == null) {
            nav.setVisibility(View.GONE);
        } else {
            nav.setVisibility(View.VISIBLE);
        }

        TextView timeTextView = (TextView) findViewById(R.id.time);
        timeTextView.setText(mStartTime + " - " + mEndTime);

        String[] date_split = mDate.split("-");
        String month = getMonthForInt(Integer.parseInt(date_split[1])-1);
         formated_date = month + " " + date_split[2] + ", " + date_split[0];

        TextView dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText(formated_date);

        if(mAlerted == SessionEntry.ALERTED) {
            alert.setImageResource(R.drawable.ic_notifications_active_black_24dp);
        } else {
            alert.setImageResource(R.drawable.ic_notifications_off_black_24dp);
        }

        TextView detailTextView = (TextView) findViewById(R.id.description);
        detailTextView.setText(mDescription);

        TextView locationTextView = (TextView) findViewById(R.id.location);
        locationTextView.setText(mBuildingCode + " (" + mBuildingName + ") - " + mBuildingRoom);

        TextView audienceTextView = (TextView) findViewById(R.id.audience);
        audienceTextView.setText(mAudience);

        byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LOGO));
        Bitmap logo = getImage(image);

        ImageView headerImageView = (ImageView) findViewById(R.id.bgheader);
        headerImageView.setImageBitmap(logo);

    }
    public void updateContactLL(final Cursor cursor) {
        cursor.moveToFirst();
        contactLL.removeAllViews();
        boolean hasContact = false;
        while (!cursor.isAfterLast()) {
            LinearLayout vll = new LinearLayout(this);
            vll.setOrientation(LinearLayout.VERTICAL);

            // Extract properties from cursor
            String name = cursor.getString(cursor.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));
            String company = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.DATA));
            String title = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));

            if(mEmployer.contains(company)) {
                if(!hasContact) {
                    contact_title.setVisibility(View.VISIBLE);
                    hasContact = true;
                }

                TextView name_tv = new TextView(this);
                name_tv.setText(name);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    name_tv.setTextColor(getResources().getColor(R.color.colorAccent, null));
                }

                final int id = cursor.getPosition();
                vll.addView(name_tv);

                if(title == null) {
                    title = "No tilte";
                }

                TextView title_tv = new TextView(this);
                title_tv.setText(title);
                vll.addView(title_tv);
                vll.setBackgroundResource(R.drawable.contacts_border);

                int[] attrs = new int[]{R.attr.selectableItemBackground};
                TypedArray typedArray = obtainStyledAttributes(attrs);
                int backgroundResource = typedArray.getResourceId(0, 0);
                vll.setBackgroundResource(backgroundResource);

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
