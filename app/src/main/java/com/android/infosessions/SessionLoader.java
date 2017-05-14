package com.android.infosessions;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Thao on 5/8/17.
 */

public class SessionLoader extends AsyncTaskLoader<List<Session>> {
    private static String mUrl;
    private static Context mContext;
    public SessionLoader(Context context, String url) {
        super(context);
        mUrl = url;
        mContext = context;
    }

    @Override
    public List<Session> loadInBackground() {
        if(mUrl == null) return null;
        List<Session> result = QueryUtils.fetchInfos(mUrl);
        return result;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
