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

public class ArchivedFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Session>> {

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
    public Loader<List<Session>> onCreateLoader(int id, Bundle args) {
        return new SessionLoader(getContext(), UWAPI_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Session>> loader, List<Session> data) {
        ArrayList<Session> filtered = new ArrayList<>();
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
    public void onLoaderReset(Loader<List<Session>> loader) {

    }

    private void updateUi(final ArrayList<Session> sessions) {
        // Find a reference to the {@link ListView} in the layout
        // Create a new {@link ArrayAdapter} of earthquakes
        SessionAdapter adapter = new SessionAdapter(getContext(), sessions);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        infosListView.setAdapter(adapter);

        infosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // The code in this method will be executed when the numbers category is clicked on.
                Session currentSession = sessions.get(position);
                ArrayList<String> toSent = new ArrayList<String>();
                toSent.add(currentSession.toJSONString());
                toSent.add(currentSession.getLogoString());
                Intent intent = new Intent(getContext(), DetailActivity.class)
                        .putStringArrayListExtra("EXTRA_TEXT", toSent);
                // Start the new activity
                startActivity(intent);
            }
        });
    }
}
