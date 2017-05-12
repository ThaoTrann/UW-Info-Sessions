package com.android.infosessions.data;

import android.provider.BaseColumns;

/**
 * Created by Thao on 5/11/17.
 */

public final class ContactContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ContactContract() {}

    /* Inner class that defines the table contents */
    public static class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_CONTACT_NAME = "title";
        public static final String COLUMN_CONTACT_EMAIL = "email";
        public static final String COLUMN_CONTACT_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_CONTACT_POSITION = "position";
        public static final String COLUMN_CONTACT_COMPANY = "company";
    }
}
