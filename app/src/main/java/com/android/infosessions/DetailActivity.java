package com.android.infosessions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    JSONObject currentInfo;
    JSONObject building;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        try {
            currentInfo = new JSONObject(intent.getStringExtra("EXTRA_TEXT"));
            building = currentInfo.getJSONObject("building");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView nameTextView = (TextView) findViewById(R.id.name);
        TextView timeTextView = (TextView) findViewById(R.id.time);
        TextView dateTextView = (TextView) findViewById(R.id.date);
        TextView detailTextView = (TextView) findViewById(R.id.details);
        TextView locationTextView = (TextView) findViewById(R.id.location);
        try {
            nameTextView.setText(currentInfo.getString("employer"));
            timeTextView.setText(currentInfo.getString("start_time") + " - " + currentInfo.getString("end_time"));
            dateTextView.setText(currentInfo.getString("day") + " - " +currentInfo.getString("date"));
            detailTextView.setText(currentInfo.getString("description"));
            locationTextView.setText(building.getString("code") + " - " + building.getString("room"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
