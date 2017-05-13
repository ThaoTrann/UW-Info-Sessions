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
    private EditText mName;
    private EditText mCompany;
    private EditText mPosition;
    private EditText mEmail;
    private EditText mPhone;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( getContext().LAYOUT_INFLATER_SERVICE );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = inflater.inflate(R.layout.add_contact, null);
                mName = (EditText) mView.findViewById(R.id.etName);
                mCompany = (EditText) mView.findViewById(R.id.etCompany);
                mPosition = (EditText) mView.findViewById(R.id.etPosition);
                mEmail = (EditText) mView.findViewById(R.id.etEmail);
                mPhone = (EditText) mView.findViewById(R.id.etPhone_number);
                Button mAdd = (Button) mView.findViewById(R.id.add_btn);

                mAdd.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        insertContact();
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
        displayDatabaseContact(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    private void insertContact() {
        String name = mName.getText().toString().trim();
        String company = mCompany.getText().toString().trim();
        String position = mPosition.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();

        ContactDbHelper mDbHelper = new ContactDbHelper(getContext());

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ContactEntry.COLUMN_CONTACT_NAME, name);
        values.put(ContactEntry.COLUMN_CONTACT_COMPANY, company);
        values.put(ContactEntry.COLUMN_CONTACT_POSITION, position);
        values.put(ContactEntry.COLUMN_CONTACT_EMAIL, email);
        values.put(ContactEntry.COLUMN_CONTACT_PHONE_NUMBER, phone);

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(ContactEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(getContext(), "Error with saving pet", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(getContext(), "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
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
    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo(View root) {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        ContactDbHelper mDbHelper = new ContactDbHelper(getContext());

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + ContactEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) root.findViewById(R.id.contacts_text_view);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
