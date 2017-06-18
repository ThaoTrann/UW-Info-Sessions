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
    private String mAudience;
    private Bitmap mLogo;
    private ArrayList<String> mAudienceStringArray;

    public Session(JSONObject info, Bitmap logo) throws JSONException {
        mInfo = info;
        mName = info.getString("employer");
        mStartTime = info.getString("start_time");
        mEndTime = info.getString("end_time");
        mDate = info.getString("date");
        mDay = info.getString("day");
        mWebsite = info.getString("website");
        mLink = info.getString("link");
        mDetail = info.getString("description");
        mLogo = logo;
        if(mDetail.isEmpty()) {
            mDetail = "Employer's Description is not provided.";
        }
        JSONObject building = info.getJSONObject("building");
        mCode = building.getString("code");
        mBuildingName = building.getString("name");
        mRoom = building.getString("room");
        mMapUrl = building.getString("map_url");


        JSONArray mAudienceArray = info.getJSONArray("audience");
        mAudience = "Audience: \n";
        mAudienceStringArray = new ArrayList<>();
        for (int i = 0; i < mAudienceArray.length(); i++) {
            mAudience += "\n" + mAudienceArray.getString(i);
            mAudienceStringArray.add(mAudienceArray.getString(i));
        }

    }

    public String getEmployer() {
        return mName;
    }
    public String getStartTime() {
        return mStartTime ;
    }
    public String getEndTime() {
        return mEndTime ;
    }
    public String getDay() {
        return mDay;
    }
    public String getDate() {
        return mDate;
    }
    public String getDescription() {
        return mDetail;
    }

    public String getWebsite() {
        return mWebsite;
    }
    public String getLink() {return mLink;}

    public String getBuildingRoom() {
        return mRoom;
    }
    public String getBuildingName() {
        return mBuildingName;
    }
    public String getBuildingCode() {
        return mCode;
    }
    @Override
    public String toString() {
        return "Session: " + mName;
    }
    public String toJSONString() {
        return mInfo.toString();
    }
    public Bitmap getLogoBitmap() {
        return mLogo;
    }
    public String getMapUrl() {
        return mMapUrl;
    }
    public String getAudience() {return mAudience;}
    public ArrayList<String> getAudienceStringArray() {return mAudienceStringArray;}
}
