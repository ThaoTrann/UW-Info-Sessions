package com.android.infosessions.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Thao on 5/12/17.
 */

public class SessionContract {

    public static final String CONTENT_AUTHORITY = "com.android.infosessions";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SESSIONS = "sessions";


    private SessionContract() {}
    /* Inner class that defines the table contents */
    public static class SessionEntry implements BaseColumns {
        /** The content URI to access the data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SESSIONS);

        public static final String TABLE_NAME = "sessions";
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_SESSION_EMPLOYER = "employer";
        public static final String COLUMN_SESSION_START_TIME = "start_time";
        public static final String COLUMN_SESSION_END_TIME = "end_time";
        public static final String COLUMN_SESSION_DATE = "date";
        public static final String COLUMN_SESSION_DAY = "day";
        public static final String COLUMN_SESSION_MILLISECONDS = "milliseconds";
        public static final String COLUMN_SESSION_WEBSITE = "website";
        public static final String COLUMN_SESSION_LINK = "link";
        public static final String COLUMN_SESSION_DESCRIPTION = "description";
        public static final String COLUMN_SESSION_BUILDING_CODE = "building_code";
        public static final String COLUMN_SESSION_BUILDING_NAME = "building_name";
        public static final String COLUMN_SESSION_BUILDING_ROOM = "building_room";
        public static final String COLUMN_SESSION_MAP_URL = "map_url";
        public static final String COLUMN_SESSION_AUDIENCE = "audience";
        public static final String COLUMN_SESSION_NUMBER_CONTACTS = "number_of_contacts";
        public static final String COLUMN_SESSION_ALERTED = "alerted";

        public static final int ALERTED = 1;
        public static final int NOT_ALERTED = 0;
        public static final int NO_CONTACT = 0;
    }
}

