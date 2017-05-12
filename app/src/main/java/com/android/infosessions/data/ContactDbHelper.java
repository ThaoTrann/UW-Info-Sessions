package com.android.infosessions.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.infosessions.data.ContactContract.ContactEntry;

/**
 * Created by Thao on 5/11/17.
 */

public class ContactDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ContactEntry.TABLE_NAME + " (" +
                        ContactEntry.COLUMN_CONTACT_NAME + " TEXT," +
                        ContactEntry.COLUMN_CONTACT_COMPANY + " TEXT," +
                        ContactEntry.COLUMN_CONTACT_POSITION + " TEXT," +
                        ContactEntry.COLUMN_CONTACT_EMAIL + " TEXT," +
                        ContactEntry.COLUMN_CONTACT_PHONE_NUMBER + " TEXT)";

        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
