package com.android.infosessions.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Thao on 5/29/17.
 */

public class FilterContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.public static final String CONTENT_AUTHORITY = "com.example.android.infosessions";
    private FilterContract() {}

    public static final String CONTENT_AUTHORITY = "com.android.infosessions";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FILTERS = "filters";


    /* Inner class that defines the table contents */
    public static class FilterEntry implements BaseColumns {
        /** The content URI to access the data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FILTERS);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FILTERS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FILTERS;

        public static final String TABLE_NAME = "filters";
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_FILTER_KEY = "key";
        public static final String COLUMN_FILTER_IS_CODE = "code";
        public static final String COLUMN_FILTER_VALUE = "value";
        public static final int VALUE_CHECKED = 1;
        public static final int VALUE_NOT_CHECKED = 0;
        public static final int VALUE_CODE = 1;
        public static final int VALUE_NOT_CODE = 0;
    }
}
