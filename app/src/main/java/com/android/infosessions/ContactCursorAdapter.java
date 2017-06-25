package com.android.infosessions;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.infosessions.data.ContactContract.ContactEntry;

/**
 * Created by Thao on 5/16/17.
 */

public class ContactCursorAdapter extends CursorAdapter {
    public ContactCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in item_sessionion.xml
        return LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = (TextView) view.findViewById(R.id.name);

        TextView tvName_tag = (TextView) view.findViewById(R.id.name_tag);
        TextView tvCompany = (TextView) view.findViewById(R.id.company);
        TextView tvTitle = (TextView) view.findViewById(R.id.title);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
        String company = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.DATA));
        String title = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));

        //        tvName_tag.setText(name.charAt(0));

        // Populate fields with extracted properties
        tvName_tag.setText(name.charAt(0) + "");
        tvName.setText(name);
        tvCompany.setText(company);
        tvTitle.setText(title);
    }
}
