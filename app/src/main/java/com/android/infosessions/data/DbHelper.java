package com.android.infosessions.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.infosessions.data.ContactContract.ContactEntry;
import com.android.infosessions.data.SessionContract.SessionEntry;
import com.android.infosessions.data.FilterContract.FilterEntry;
import com.android.infosessions.data.LogoContract.LogoEntry;

/**
 * Created by Thao on 5/11/17.
 */

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mDatabase.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_CONTACT_ENTRIES =
                "CREATE TABLE " + ContactEntry.TABLE_NAME + " (" +
                        ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ContactEntry.COLUMN_CONTACT_NAME + " TEXT, " +
                        ContactEntry.COLUMN_CONTACT_COMPANY + " TEXT, " +
                        ContactEntry.COLUMN_CONTACT_POSITION + " TEXT, " +
                        ContactEntry.COLUMN_CONTACT_EMAIL + " TEXT, " +
                        ContactEntry.COLUMN_CONTACT_PHONE_NUMBER + " TEXT);";

        db.execSQL(SQL_CREATE_CONTACT_ENTRIES);

        String SQL_CREATE_FILTER_ENTRIES =
                "CREATE TABLE " + FilterEntry.TABLE_NAME + " (" +
                        FilterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FilterEntry.COLUMN_FILTER_KEY + " TEXT, " +
                        FilterEntry.COLUMN_FILTER_VALUE + " INTEGER);";

        db.execSQL(SQL_CREATE_FILTER_ENTRIES);

        String SQL_CREATE_LOGO_ENTRIES =
                "CREATE TABLE " + LogoEntry.TABLE_NAME + " (" +
                        LogoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        LogoEntry.COLUMN_LOGO_EMPLOYER + " TEXT, " +
                        LogoEntry.COLUMN_LOGO_IMAGE + " BLOB);";

        db.execSQL(SQL_CREATE_LOGO_ENTRIES);

        String SQL_CREATE_SESSION_ENTRIES =
                "CREATE TABLE " + SessionEntry.TABLE_NAME + " (" +
                        SessionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SessionEntry.COLUMN_SESSION_ALERTED + " INTEGER, " +
                        SessionEntry.COLUMN_SESSION_EMPLOYER + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_START_TIME + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_END_TIME + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_DATE + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_DAY + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_MILLISECONDS + " REAL, " +
                        SessionEntry.COLUMN_SESSION_WEBSITE + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_LINK + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_DESCRIPTION + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_BUILDING_CODE + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_BUILDING_NAME + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_BUILDING_ROOM + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_MAP_URL + " TEXT, " +
                        SessionEntry.COLUMN_SESSION_NUMBER_CONTACTS + " INTEGER, " +
                        SessionEntry.COLUMN_SESSION_AUDIENCE + " TEXT);";

        db.execSQL(SQL_CREATE_SESSION_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
