package com.android.infosessions;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.infosessions.data.SessionContract.SessionEntry;

import java.text.DateFormatSymbols;

/**
 * Created by Thao on 5/16/17.
 */

public class SessionCursorAdapter extends CursorAdapter {

    private Context mContext;
    private int mId;
    public SessionCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in item_session.xmlxml
        return LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String employer = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_EMPLOYER));
        String start_time = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_START_TIME));
        String end_time = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_END_TIME));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DESCRIPTION));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DATE));
        String day = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_DAY));
        String map_url = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_MAP_URL));
        String audience = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_AUDIENCE));
        String building_room = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_ROOM));
        String building_code = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_CODE));
        String building_name = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_BUILDING_NAME));
        String link = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LINK));
        Integer contacts = cursor.getInt(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_NUMBER_CONTACTS));
        final Integer alerted = cursor.getInt(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_ALERTED));

        //String logo = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LOGO));

        TextView nameTextView = (TextView) view.findViewById(R.id.employer);
        TextView nameMPTextView = (TextView) view.findViewById(R.id.employer_mp);

        TextView timeTextView = (TextView) view.findViewById(R.id.time);
        timeTextView.setText(start_time + "-" + end_time);

        TextView dateTextView = (TextView) view.findViewById(R.id.date);
        dateTextView.setText(date);

        TextView detailTextView = (TextView) view.findViewById(R.id.description);
        detailTextView.setText(description);

        TextView locationTextView = (TextView) view.findViewById(R.id.location);
        locationTextView.setText(building_code);

        TextView contactTextView = (TextView) view.findViewById(R.id.contacts);
        if(contacts == 0) {
            nameTextView.setVisibility(View.GONE);
            nameMPTextView.setVisibility(View.VISIBLE);
            contactTextView.setVisibility(View.GONE);
        } else if(contacts == 1) {
            nameTextView.setVisibility(View.VISIBLE);
            nameMPTextView.setVisibility(View.GONE);
            contactTextView.setVisibility(View.VISIBLE);
            contactTextView.setText(contacts + " connection");
        } else {
            nameTextView.setVisibility(View.VISIBLE);
            nameMPTextView.setVisibility(View.GONE);
            contactTextView.setVisibility(View.VISIBLE);
            contactTextView.setText(contacts + " connections");
        }
        nameTextView.setText(employer);
        nameMPTextView.setText(employer);

        ImageView logoView = (ImageView) view.findViewById(R.id.employer_logo);
        //Drawable drawable = getImage(logoView.getContext(), logo);

        byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(SessionEntry.COLUMN_SESSION_LOGO));
        Bitmap logo = getImage(image);

        //logoView.setImageDrawable(drawable);
        logoView.setImageBitmap(logo);

        ImageView alertImage = (ImageView) view.findViewById(R.id.alert);
        if (alerted == SessionEntry.ALERTED) {
            alertImage.setVisibility(View.VISIBLE);
        } else {
            alertImage.setVisibility(View.GONE);
        }
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
