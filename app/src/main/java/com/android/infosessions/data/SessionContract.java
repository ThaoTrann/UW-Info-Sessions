package com.android.infosessions.data;

import android.graphics.drawable.Drawable;
import android.provider.BaseColumns;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Thao on 5/12/17.
 */

public class SessionContract {
    private SessionContract() {}
    /* Inner class that defines the table contents */
    public static class SessionEntry implements BaseColumns {
        public static final String TABLE_NAME = "sessions";
        public static final String COLUMN_SESSION_EMPLOYER = "employer";
        public static final String COLUMN_SESSION_START_TIME = "start_time";
        public static final String COLUMN_SESSION_END_TIME = "end_time";
        public static final String COLUMN_SESSION_DATE = "date";
        public static final String COLUMN_SESSION_DAY = "day";
        public static final String COLUMN_SESSION_WEBSITE = "website";
        public static final String COLUMN_SESSION_LINK = "link";
        public static final String COLUMN_SESSION_DESCRIPTION = "description";
        public static final String COLUMN_SESSION_BUILDING_CODE = "building_code";
        public static final String COLUMN_SESSION_BUILDING_NAME = "building_name";
        public static final String COLUMN_SESSION_BUILDING_ROOM = "building_room";
        public static final String COLUMN_SESSION_MAP_URL = "map_url";
        public static final String COLUMN_SESSION_AUDIENCE = "audience";
        public static final int COLUMN_SESSION_LOGO = 0;
    }
}

