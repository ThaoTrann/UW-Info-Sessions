package com.android.infosessions.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.infosessions.data.SessionContract.SessionEntry;

/**
 * Created by Thao on 5/12/17.
 */

public class SessionDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Session.db";

    public SessionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SessionEntry.TABLE_NAME + " (" +
                    SessionEntry.COLUMN_SESSION_EMPLOYER + " TEXT," +
                    SessionEntry.COLUMN_SESSION_START_TIME + " TEXT," +
                    SessionEntry.COLUMN_SESSION_END_TIME + " TEXT," +
                    SessionEntry.COLUMN_SESSION_DATE + " TEXT," +
                    SessionEntry.COLUMN_SESSION_DAY + " TEXT," +
                    SessionEntry.COLUMN_SESSION_WEBSITE + " TEXT," +
                    SessionEntry.COLUMN_SESSION_LINK + " TEXT," +
                    SessionEntry.COLUMN_SESSION_DESCRIPTION + " TEXT," +
                    SessionEntry.COLUMN_SESSION_BUILDING_CODE + " TEXT," +
                    SessionEntry.COLUMN_SESSION_BUILDING_NAME + " TEXT," +
                    SessionEntry.COLUMN_SESSION_BUILDING_ROOM + " TEXT," +
                    SessionEntry.COLUMN_SESSION_MAP_URL + " TEXT," +
                    SessionEntry.COLUMN_SESSION_LOGO + " INTEGER," +
                    SessionEntry.COLUMN_SESSION_AUDIENCE + " TEXT)";

        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
