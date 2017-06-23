package com.android.infosessions;

import android.content.Context;
import android.database.Cursor;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
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
        return LayoutInflater.from(context).inflate(R.layout.item_filter, viewGroup, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String key = cursor.getString(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_KEY));
        int value = cursor.getInt(cursor.getColumnIndexOrThrow(FilterEntry.COLUMN_FILTER_VALUE));

        TextView textView = (TextView) view.findViewById(R.id.general_division);
        CheckedTextView box = (CheckedTextView) view.findViewById(R.id.specific_division);
        box.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        box.setTag(cursor.getPosition());
        box.setText(createIndentedText(key, 0, 50));
        if (value == FilterEntry.VALUE_CHECKED)
            box.setChecked(true);
        else
            box.setChecked(false);

    }

    static SpannableString createIndentedText(String text, int marginFirstLine, int marginNextLines) {
        SpannableString result=new SpannableString(text);
        result.setSpan(new LeadingMarginSpan.Standard(marginFirstLine, marginNextLines),0,text.length(),0);
        return result;
    }
}
