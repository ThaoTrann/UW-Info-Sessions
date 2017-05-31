package com.android.infosessions;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.infosessions.data.FilterContract.FilterEntry;

import org.json.JSONException;

import static com.android.infosessions.SessionAdapter.getImage;

/**
 * Created by Thao on 5/16/17.
 */

public class FilterCursorAdapter extends CursorAdapter {

    public FilterCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in item_session.xmlxml
        return LayoutInflater.from(context).inflate(R.layout.item_filter, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String key = cursor.getString(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_KEY));
        int value = cursor.getInt(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_VALUE));

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setText(key);
        if(value == FilterEntry.VALUE_CHECKED) {checkBox.setChecked(true);}
        else {checkBox.setChecked(false);}
    }
}
