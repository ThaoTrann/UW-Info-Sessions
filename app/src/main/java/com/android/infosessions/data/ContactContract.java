package com.android.infosessions.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Thao on 5/11/17.
 */

public final class ContactContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.public static final String CONTENT_AUTHORITY = "com.example.android.infosessions";
    private ContactContract() {}

    public static final String CONTENT_AUTHORITY = "com.android.infosessions";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CONTACTS = "contacts";


    /* Inner class that defines the table contents */
    public static class ContactEntry implements BaseColumns {
        /** The content URI to access the data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CONTACTS);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        public static final String TABLE_NAME = "contacts";
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_CONTACT_NAME = "title";
        public static final String COLUMN_CONTACT_EMAIL = "email";
        public static final String COLUMN_CONTACT_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_CONTACT_POSITION = "position";
        public static final String COLUMN_CONTACT_COMPANY = "company";
    }
}
