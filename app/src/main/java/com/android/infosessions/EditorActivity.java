package com.android.infosessions;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.infosessions.data.ContactContract.ContactEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText mNameET;
    private EditText mCompanyET;
    private EditText mPositionET;
    private EditText mEmailET;
    private EditText mPhoneET;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        mUri = intent.getData();
        if(mUri != null) {
            setTitle("Edit Contact");
        } else {
            setTitle("Add Contact");
        }

        mNameET = (EditText) findViewById(R.id.etName);
        mCompanyET = (EditText) findViewById(R.id.etCompany);
        mPositionET = (EditText) findViewById(R.id.etPosition);
        mEmailET = (EditText) findViewById(R.id.etEmail);
        mPhoneET = (EditText) findViewById(R.id.etPhone_number);

        getLoaderManager().initLoader(0, null, this);
    }
    private void saveContact() {
        String name = mNameET.getText().toString().trim();
        String company = mCompanyET.getText().toString().trim();
        String position = mPositionET.getText().toString().trim();
        String email = mEmailET.getText().toString().trim();
        String phone = mPhoneET.getText().toString().trim();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ContactEntry.COLUMN_CONTACT_NAME, name);
        values.put(ContactEntry.COLUMN_CONTACT_COMPANY, company);
        values.put(ContactEntry.COLUMN_CONTACT_POSITION, position);
        values.put(ContactEntry.COLUMN_CONTACT_EMAIL, email);
        values.put(ContactEntry.COLUMN_CONTACT_PHONE_NUMBER, phone);

        if(mUri == null) {
            // Insert the new row, returning the primary key value of the new row
            Uri newUri = getContentResolver().insert(ContactEntry.CONTENT_URI, values);
            if (newUri != null) {
                Toast toast = Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            int newUri = getContentResolver().update(mUri, values,  null, null);
            if (newUri != 0) {
                Toast toast = Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                saveContact();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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


        return new CursorLoader(this,   // Parent activity context
                mUri,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            String name = cursor.getString(cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_NAME));
            String company = cursor.getString(cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_COMPANY));
            String position = cursor.getString(cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_POSITION));
            String email = cursor.getString(cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_EMAIL));
            String phone = cursor.getString(cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_PHONE_NUMBER));

            Log.d("LOG_TAG", name + " " + email);
            // Update the views on the screen with the values from the database
            mNameET.setText(name);
            mCompanyET.setText(company);
            mPositionET.setText(position);
            mEmailET.setText(email);
            mPhoneET.setText(phone);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameET.setText("");
        mCompanyET.setText("");
        mEmailET.setText("");
        mPositionET.setText("");
        mPhoneET.setText("");
    }
}
