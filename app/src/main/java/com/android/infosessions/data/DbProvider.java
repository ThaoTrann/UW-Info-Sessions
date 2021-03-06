package com.android.infosessions.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.android.infosessions.data.ContactContract.ContactEntry;
import com.android.infosessions.data.SessionContract.SessionEntry;
import com.android.infosessions.data.FilterContract.FilterEntry;
import com.android.infosessions.data.LogoContract.LogoEntry;
/**
 * Created by Thao on 5/16/17.
 */

public class DbProvider extends ContentProvider {
    private DbHelper mDbHelper;
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = DbHelper.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the table
     */
    private static final int CONTACTS = 100;
    private static final int SESSIONS = 200;
    private static final int LOGOS = 300;
    private static final int FILTERS = 400;

    private static final int SEARCH_SUGGEST_NONE = 0;
    private static final int SEARCH_SUGGEST = 1;

    /**
     * URI matcher code for the content URI for a single entry in table
     */
    private static final int CONTACT_ID = 101;
    private static final int SESSION_ID = 201;
    private static final int LOGO_ID = 301;
    private static final int FILTER_ID = 401;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(ContactContract.CONTENT_AUTHORITY, ContactContract.PATH_CONTACTS, CONTACTS);
        sUriMatcher.addURI(ContactContract.CONTENT_AUTHORITY, ContactContract.PATH_CONTACTS + "/#", CONTACT_ID);
        sUriMatcher.addURI(SessionContract.CONTENT_AUTHORITY, SessionContract.PATH_SESSIONS, SESSIONS);
        sUriMatcher.addURI(SessionContract.CONTENT_AUTHORITY, SessionContract.PATH_SESSIONS + "/#", SESSION_ID);
        sUriMatcher.addURI(SessionContract.CONTENT_AUTHORITY, FilterContract.PATH_FILTERS, FILTERS);
        sUriMatcher.addURI(SessionContract.CONTENT_AUTHORITY, FilterContract.PATH_FILTERS + "/#", FILTER_ID);
        sUriMatcher.addURI(SessionContract.CONTENT_AUTHORITY, LogoContract.PATH_FILTERS, LOGOS);
        sUriMatcher.addURI(SessionContract.CONTENT_AUTHORITY, LogoContract.PATH_FILTERS + "/#", LOGO_ID);
        sUriMatcher.addURI(SessionContract.CONTENT_AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST_NONE);
        sUriMatcher.addURI(SessionContract.CONTENT_AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                cursor = database.query(ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CONTACT_ID:
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ContactEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case FILTERS:
                cursor = database.query(FilterEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FILTER_ID:
                selection = FilterEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(FilterEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LOGOS:
                cursor = database.query(LogoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case LOGO_ID:
                selection = FilterEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(LogoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SESSIONS:
                cursor = database.query(SessionEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SESSION_ID:
                selection = SessionEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(SessionEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SEARCH_SUGGEST:
                String query = uri.getLastPathSegment().toLowerCase();
                selection = SessionEntry.COLUMN_SESSION_EMPLOYER + " LIKE ?";
                selectionArgs = new String[]{"%" + query + "%"};
                cursor = database.query(SessionEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                Log.d("cursor count", String.valueOf(cursor.getCount()));
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return insertContact(uri, contentValues);
            case SESSIONS:
                return insertSession(uri, contentValues);
            case FILTERS:
                return insertFilter(uri, contentValues);
            case LOGOS:
                return insertLogo(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertContact(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new data with the given values
        long id = database.insert(ContactEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertSession(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new data with the given values
        long id = database.insert(SessionEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertFilter(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new data with the given values
        long id = database.insert(FilterEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertLogo(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new data with the given values
        long id = database.insert(LogoEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {final int match = sUriMatcher.match(uri);
        switch (match) {
            case SESSIONS:
                return updateSession(uri, contentValues, selection, selectionArgs);
            case SESSION_ID:
                selection = SessionEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateSession(uri, contentValues, selection, selectionArgs);
            case CONTACTS:
                return updateContract(uri, contentValues, selection, selectionArgs);
            case CONTACT_ID:
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateContract(uri, contentValues, selection, selectionArgs);
            case FILTERS:
                return updateFilter(uri, contentValues, selection, selectionArgs);
            case FILTER_ID:
                selection = FilterEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateFilter(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    private int updateSession(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowUpdated = database.update(SessionEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }

    private int updateFilter (Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowUpdated = database.update(FilterEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }

    private int updateContract(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowUpdated = database.update(SessionEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}