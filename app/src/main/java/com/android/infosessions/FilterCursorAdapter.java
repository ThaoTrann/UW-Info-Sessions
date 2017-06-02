package com.android.infosessions;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.infosessions.data.FilterContract.FilterEntry;

/**
 * Created by Thao on 5/16/17.
 */

public class FilterCursorAdapter extends CursorAdapter {

    public FilterCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        int code = cursor.getInt(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_IS_CODE));
        /*if(code == FilterEntry.VALUE_CODE) {
            return LayoutInflater.from(context).inflate(R.layout.item_filter_general, viewGroup, false);
        } else {
            */return LayoutInflater.from(context).inflate(R.layout.item_filter_specific, viewGroup, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String key = cursor.getString(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_KEY));
        int value = cursor.getInt(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_VALUE));
        int code = cursor.getInt(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_IS_CODE));
        /*Log.d("LOG_TAG", key + code);

        if(code == FilterEntry.VALUE_CODE) {
            TextView textView = (TextView) view.findViewById(R.id.general_division);
            textView.setText(key);
        } else {
            */Log.d("LOG_TAG inside else", key + code);
            CheckedTextView box = (CheckedTextView) view.findViewById(R.id.specific_division);
            //box.setTag(cursor.getPosition());
            box.setText(key);

            if (value == FilterEntry.VALUE_CHECKED)
                box.setChecked(true);
            else
                box.setChecked(false);

    }
}
