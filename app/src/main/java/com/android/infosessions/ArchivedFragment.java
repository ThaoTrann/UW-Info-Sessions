package com.android.infosessions;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Thao on 5/10/17.
 */

public class ArchivedFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Info>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private ListView infosListView;
    private static final String UWAPI_REQUEST_URL =
            "https://api.uwaterloo.ca/v2/resources/infosessions.json?key=123afda14d0a233ecb585591a95e0339";
    public ArchivedFragment() {
    }
    // Required empty public constructor



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.infos_list, container, false);
        infosListView = (ListView) rootView.findViewById(R.id.list);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, this).forceLoad();
        return rootView;
    }

    @Override
    public Loader<List<Info>> onCreateLoader(int id, Bundle args) {
        return new InfoLoader(getContext(), UWAPI_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Info>> loader, List<Info> data) {
        ArrayList<Info> filtered = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int cMonth = c.get(Calendar.MONTH) + 1;
        int cDay = c.get(Calendar.DAY_OF_MONTH);
        int cYear = c.get(Calendar.YEAR);

        for(int i = 0; i < data.size(); i++) {
            String[] date = data.get(i).getDate().split("-");
            int day = Integer.parseInt(date[2]);
            int month = Integer.parseInt(date[1]);
            int year = Integer.parseInt(date[0]);
            if (year < cYear || (year == cYear && month < cMonth)
                    || (year == cYear && month == cMonth && day < cDay)) {
                filtered.add(data.get(i));
            }
        }
        updateUi(filtered);
    }

    @Override
    public void onLoaderReset(Loader<List<Info>> loader) {

    }

    private void updateUi(final ArrayList<Info> infos) {
        // Find a reference to the {@link ListView} in the layout
        // Create a new {@link ArrayAdapter} of earthquakes
        InfoAdapter adapter = new InfoAdapter(getContext(), infos);

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
                ArrayList<String> toSent = new ArrayList<String>();
                toSent.add(currentInfo.toJSONString());
                toSent.add(currentInfo.getLogoString());
                Intent intent = new Intent(getContext(), DetailActivity.class)
                        .putStringArrayListExtra("EXTRA_TEXT", toSent);
                // Start the new activity
                startActivity(intent);
            }
        });
    }
}
