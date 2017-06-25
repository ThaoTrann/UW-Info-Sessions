package com.android.infosessions;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.infosessions.data.ContactContract.ContactEntry;
import android.provider.ContactsContract;

/**
 * Created by Thao on 5/10/17.
 */

public class ContactFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {
    private ContactCursorAdapter mCursorAdapter;
    private static final int LOADER_ID = 0;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    public ContactFragment() {
    }
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        /*FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditorActivity.class);
                startActivity(intent);
            }
        });*/

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(getContext(), EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(ContactEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });*/


        ListView listView = (ListView) rootView.findViewById(R.id.list);

        View emptyView = rootView.findViewById(R.id.empty_view);

        mCursorAdapter = new ContactCursorAdapter(getContext(), null);
        listView.setAdapter(mCursorAdapter);

        listView.setEmptyView(emptyView);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            /*// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }*/
        }

        return rootView;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

/*

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ContactEntry._ID,
                ContactEntry.COLUMN_CONTACT_NAME,
                ContactEntry.COLUMN_CONTACT_COMPANY,
                ContactEntry.COLUMN_CONTACT_POSITION,
                ContactEntry.COLUMN_CONTACT_EMAIL,
                ContactEntry.COLUMN_CONTACT_PHONE_NUMBER };

        return new CursorLoader(getContext(),   // Parent activity context
                ContactEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }
*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String orgWhere = ContactsContract.Data.MIMETYPE + " = ?";
        String[] orgWhereParams = new String[]{
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};

        return new CursorLoader(getContext(),   // Parent activity context
                ContactsContract.Data.CONTENT_URI,   // Provider content URI to query
                null,             // Columns to include in the resulting Cursor
                orgWhere,                   // No selection clause
                orgWhereParams,                   // No selection arguments
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
