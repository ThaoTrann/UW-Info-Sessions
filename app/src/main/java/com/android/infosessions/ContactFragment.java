package com.android.infosessions;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Thao on 5/10/17.
 */

public class ContactFragment extends Fragment {
    public ContactFragment() {
    }
    // Required empty public constructor



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        return rootView;
    }
}
