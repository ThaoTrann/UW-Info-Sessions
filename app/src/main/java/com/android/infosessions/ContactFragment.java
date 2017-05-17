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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.infosessions.data.ContactDbHelper;
import com.android.infosessions.data.ContactContract.ContactEntry;

import static com.android.infosessions.R.color.colorPrimaryDark;

/**
 * Created by Thao on 5/10/17.
 */

public class ContactFragment extends Fragment {
    private EditText mName;
    private EditText mCompany;
    private EditText mPosition;
    private EditText mEmail;
    private EditText mPhone;

    public ContactFragment() {
    }
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        displayDatabaseContact();
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
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        displayDatabaseContact();
    }

    private void displayDatabaseContact() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ContactEntry._ID,
                ContactEntry.COLUMN_CONTACT_NAME,
                ContactEntry.COLUMN_CONTACT_EMAIL,
                ContactEntry.COLUMN_CONTACT_PHONE_NUMBER,
                ContactEntry.COLUMN_CONTACT_COMPANY,
                ContactEntry.COLUMN_CONTACT_POSITION};

        // Perform a query on the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to access the pet data.
        Cursor cursor = getActivity().getContentResolver().query(
                ContactEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        ContactCursorAdapter contactCursorAdapter = new ContactCursorAdapter(getContext(), cursor);
        listView.setAdapter(contactCursorAdapter);
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
}
