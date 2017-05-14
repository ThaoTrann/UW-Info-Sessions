package com.android.infosessions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.infosessions.data.ContactDbHelper;
import com.android.infosessions.data.ContactContract.ContactEntry;
/**
 * Created by Thao on 5/10/17.
 */

public class ContactFragment extends Fragment {
    public ContactFragment() {
    }
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        displayDatabaseContact(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        displayDatabaseContact(rootView);
    }

    private void displayDatabaseContact(View view) {
        ContactDbHelper mDbHelper = new ContactDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String table = getTableAsString(db, ContactEntry.TABLE_NAME);
        TextView tv = (TextView) view.findViewById(R.id.contacts_text_view);
        tv.setText(table);
    }
    public String getTableAsString(SQLiteDatabase db, String tableName) {
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }
}
