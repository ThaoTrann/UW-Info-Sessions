package com.android.infosessions;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.infosessions.data.ContactContract.ContactEntry;

/**
 * Created by Thao on 5/10/17.
 */

public class ContactFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private EditText mName;
    private EditText mCompany;
    private EditText mPosition;
    private EditText mEmail;
    private EditText mPhone;

    private ContactCursorAdapter mCursorAdapter;

    private static final int LOADER_ID = 0;
    public ContactFragment() {
    }
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog mBuilder = new AlertDialog.Builder(getContext()).create();
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( getContext().LAYOUT_INFLATER_SERVICE );
                final View mView = inflater.inflate(R.layout.edit_contact, null);
                mName = (EditText) mView.findViewById(R.id.etName);
                mCompany = (EditText) mView.findViewById(R.id.etCompany);
                mPosition = (EditText) mView.findViewById(R.id.etPosition);
                mEmail = (EditText) mView.findViewById(R.id.etEmail);
                mPhone = (EditText) mView.findViewById(R.id.etPhone_number);
                Button mAdd = (Button) mView.findViewById(R.id.add_btn);
                Button mCnl = (Button) mView.findViewById(R.id.cancel_btn);

                mBuilder.setView(mView);
                mBuilder.setCancelable(true);
                mAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        insertContact();
                        mBuilder.dismiss();
                    }
                });

                mCnl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBuilder.dismiss();
                    }
                });
                mBuilder.show();
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.list);

        View emptyView = rootView.findViewById(R.id.empty_view);

        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri currentUri = ContentUris.withAppendedId(ContactEntry.CONTENT_URI, id);
                final AlertDialog mBuilder = new AlertDialog.Builder(getContext()).create();
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( getContext().LAYOUT_INFLATER_SERVICE );
                final View mView = inflater.inflate(R.layout.edit_contact, null);

                mName = (EditText) mView.findViewById(R.id.etName);
                mCompany = (EditText) mView.findViewById(R.id.etCompany);
                mPosition = (EditText) mView.findViewById(R.id.etPosition);
                mEmail = (EditText) mView.findViewById(R.id.etEmail);
                mPhone = (EditText) mView.findViewById(R.id.etPhone_number);

                Button mAdd = (Button) mView.findViewById(R.id.add_btn);
                Button mCnl = (Button) mView.findViewById(R.id.cancel_btn);

                mBuilder.setView(mView);
                mBuilder.setCancelable(true);
                mAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        insertContact();
                        mBuilder.dismiss();
                    }
                });

                mCnl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBuilder.dismiss();
                    }
                });
                mBuilder.show();
            }
        });

        mCursorAdapter = new ContactCursorAdapter(getContext(), null);
        listView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;

    }

    private void insertContact() {
        String name = mName.getText().toString().trim();
        String company = mCompany.getText().toString().trim();
        String position = mPosition.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ContactEntry.COLUMN_CONTACT_NAME, name);
        values.put(ContactEntry.COLUMN_CONTACT_COMPANY, company);
        values.put(ContactEntry.COLUMN_CONTACT_POSITION, position);
        values.put(ContactEntry.COLUMN_CONTACT_EMAIL, email);
        values.put(ContactEntry.COLUMN_CONTACT_PHONE_NUMBER, phone);

        // Insert a new row for pet in the database, returning the ID of that new row.
        Uri newUri = getActivity().getContentResolver().insert(ContactEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ContactEntry._ID,
                ContactEntry.COLUMN_CONTACT_NAME,
                ContactEntry.COLUMN_CONTACT_EMAIL };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(getContext(),   // Parent activity context
                ContactEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
