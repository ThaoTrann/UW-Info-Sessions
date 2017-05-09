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

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Info>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String UWAPI_REQUEST_URL =
            "https://api.uwaterloo.ca/v2/resources/infosessions.json?key=123afda14d0a233ecb585591a95e0339";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, this).forceLoad();
    }

    @Override
    public Loader<List<Info>> onCreateLoader(int id, Bundle args) {
        return new InfoLoader(this, UWAPI_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Info>> loader, List<Info> data) {
        updateUi((ArrayList<Info>) data);
    }

    @Override
    public void onLoaderReset(Loader<List<Info>> loader) {

    }

    private void updateUi(final ArrayList<Info> infos) {
        // Find a reference to the {@link ListView} in the layout
        final ListView infosListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        InfoAdapter adapter = new InfoAdapter(this, infos);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        infosListView.setAdapter(adapter);

        /*earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri webpage = Uri.parse(infos.get(position).getUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(webIntent);
            }
        });*/

        infosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // The code in this method will be executed when the numbers category is clicked on.
                Info currentInfo = infos.get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class)
                        .putExtra("EXTRA_TEXT", currentInfo.toJSONString());
                // Start the new activity
                startActivity(intent);
            }
        });
    }
}
