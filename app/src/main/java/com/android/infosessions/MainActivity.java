package com.android.infosessions;
/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.infosessions.data.ContactContract;
import com.android.infosessions.data.ContactDbHelper;


public class MainActivity extends AppCompatActivity{

    private EditText mName;
    private EditText mCompany;
    private EditText mPosition;
    private EditText mEmail;
    private EditText mPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getFragmentManager());
        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.add_contact, null);
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
    }

    private void insertContact() {
        String name = mName.getText().toString().trim();
        String company = mCompany.getText().toString().trim();
        String position = mPosition.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();

        ContactDbHelper mDbHelper = new ContactDbHelper(this);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.COLUMN_CONTACT_NAME, name);
        values.put(ContactContract.ContactEntry.COLUMN_CONTACT_COMPANY, company);
        values.put(ContactContract.ContactEntry.COLUMN_CONTACT_POSITION, position);
        values.put(ContactContract.ContactEntry.COLUMN_CONTACT_EMAIL, email);
        values.put(ContactContract.ContactEntry.COLUMN_CONTACT_PHONE_NUMBER, phone);

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(ContactContract.ContactEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }
}
