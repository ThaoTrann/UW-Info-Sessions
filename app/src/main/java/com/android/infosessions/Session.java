package com.android.infosessions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Thao on 5/4/17.
 */

public class Session implements Serializable{
    private JSONObject mInfo;
    private String mName;
    private String mStartTime;
    private String mEndTime;
    private String mDate;
    private String mDay;
    private String mWebsite;
    private String mLink;
    private String mDetail;
    private String mCode;
    private String mBuildingName;
    private String mRoom;
    private String mMapUrl;
    private String mLogoString;
    private ArrayList<String> mAudience;

    public Session(JSONObject info, String logoString) throws JSONException {
        mInfo = info;
        mName = info.getString("employer");
        mStartTime = info.getString("start_time");
        mEndTime = info.getString("end_time");
        mDate = info.getString("date");
        mDay = info.getString("day");
        mWebsite = info.getString("website");
        mLink = info.getString("link");
        mDetail = info.getString("description");
        mLogoString = logoString;
        if(mDetail.isEmpty()) {
            mDetail = "Employer's Description is not provided.";
        }
        JSONObject building = info.getJSONObject("building");
        mCode = building.getString("code");
        mBuildingName = building.getString("name");
        mRoom = building.getString("room");
        mMapUrl = building.getString("map_url");
    }

    public String getName() {
        return mName;
    }
    public String getTime() {
        return mStartTime + " - " + mEndTime;
    }
    public String getDay() {
        return mDay;
    }
    public String getDate() {
        return mDate;
    }
    public String getDetail() {
        return mDetail;
    }

    public String getUrl() {
        return mWebsite;
    }
    public String getBuilding() {
        return mCode;
    }
    @Override
    public String toString() {
        return "Session: " + mName;
    }
    public String toJSONString() {
        return mInfo.toString();
    }
    public String getLogoString() {
        return mLogoString;
    }
}