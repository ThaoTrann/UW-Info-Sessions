package com.android.infosessions.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Thao on 5/29/17.
 */

public class LogoContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.public static final String CONTENT_AUTHORITY = "com.example.android.infosessions";
    private LogoContract() {}

    public static final String CONTENT_AUTHORITY = "com.android.infosessions";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FILTERS = "logo_image";


    /* Inner class that defines the table contents */
    public static class LogoEntry implements BaseColumns {
        /** The content URI to access the data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FILTERS);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FILTERS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FILTERS;

        public static final String TABLE_NAME = "logo_image";
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_LOGO_EMPLOYER = "employer";
        public static final String COLUMN_LOGO_IMAGE = "image";
    }
}
