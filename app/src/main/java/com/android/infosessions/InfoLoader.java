package com.android.infosessions;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Thao on 5/8/17.
 */

public class InfoLoader extends AsyncTaskLoader<List<Info>> {
    private static String mUrl;
    public InfoLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public List<Info> loadInBackground() {
        if(mUrl == null) return null;
        List<Info> result = QueryUtils.fetchInfos(mUrl);
        return result;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
